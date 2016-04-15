package br.org.projeto.vigilante;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

import android.app.Application;

import br.org.projeto.vigilante.business.database.DatabaseManager;
import br.org.projeto.vigilante.business.singletons.SyncHelper;
import io.fabric.sdk.android.Fabric;

@EApplication
public class VigilanteApplication extends Application {

    @Bean
    protected SyncHelper mSyncHelper;

    @Bean
    protected DatabaseManager mDatabaseManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO remove
//        if (BuildConfig.REPORT_CRASH) {
        if (true) {
            Fabric.with(this, new Crashlytics());
            Crashlytics.setString("Build time", BuildConfig.BUILD_TIME);
            Crashlytics.setString("Git SHA", BuildConfig.GIT_SHA);
        }

        // Workaound to open database and then request report to send.
        mDatabaseManager.startDatabase();
        checkIfHasDataToSend();
    }

    @Background(delay = 2000)
    protected void checkIfHasDataToSend() {
        mSyncHelper.save();
    }
}
