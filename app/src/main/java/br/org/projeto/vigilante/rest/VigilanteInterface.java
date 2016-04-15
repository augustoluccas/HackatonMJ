package br.org.projeto.vigilante.rest;

import java.util.List;

import br.org.projeto.vigilante.rest.json.CorporationJson;
import br.org.projeto.vigilante.rest.json.ReportJson;
import br.org.projeto.vigilante.rest.json.TypeJson;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface VigilanteInterface {

    @GET("/vigilante/direitos")
    void fetchYourRights(Callback<Response> callback);

    @GET("/vigilante/corporacao")
    void fetchCorporation(Callback<List<CorporationJson>> callback);

    @GET("/vigilante/natureza")
    void fetchType(Callback<List<TypeJson>> callback);

    @Multipart
    @POST("/vigilante/denuncia")
    void sendReport(@Part("dadosDenuncia") ReportJson reportJson, @Part("dadosAnexo") TypedFile videoFile,
            @Part("content-type") String contentType, Callback<Response> callback);

}
