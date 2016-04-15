package br.org.projeto.vigilante.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;

import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import br.org.projeto.vigilante.BuildConfig;
import br.org.projeto.vigilante.util.ConnectionUtil;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class VigilanteAPI {

    private static final String ENDPOINT = "http://server-vigilante.rhcloud.com";

    private static long SIZE_OF_CACHE = 32 * 1024 * 1024; //32mb

    public static VigilanteInterface getServices(final Context context) {

        HttpURLConnection.setFollowRedirects(false);
        final OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Cache responseCache = new Cache(context.getCacheDir(), SIZE_OF_CACHE);
            okHttpClient.setCache(responseCache);
        } catch (Exception e) {
            Log.d("RETROFIT", "Unable to set http cache", e);
        }
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectionPool(new ConnectionPool(0, 5 * 60 * 1000));

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(getGson()))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(createInterceptor(context))
                .build();
        return adapter.create(VigilanteInterface.class);
    }

    private static RequestInterceptor createInterceptor(final Context context) {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (ConnectionUtil.isOnWifi(context)) {
                    int maxAge = 0; // force cache expire
                    request.addHeader("Cache-Control", "public, max-age=" + maxAge);
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    request.addHeader("Cache-Control",
                            "public, only-if-cached, max-stale=" + maxStale);
                }
            }
        };
    }

    private static Gson getGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }
}
