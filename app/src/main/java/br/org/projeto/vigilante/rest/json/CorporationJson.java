package br.org.projeto.vigilante.rest.json;

import com.google.gson.annotations.SerializedName;

public class CorporationJson {

    @SerializedName("id")
    private final int mId;

    @SerializedName("nomeCorporacao")
    private final String mName;

    public CorporationJson(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    @Override
    public String toString() {
        return mName;
    }
}

