package br.org.projeto.vigilante.rest.json;

import com.google.gson.annotations.SerializedName;

public class TypeJson {

    @SerializedName("id")
    private final int mId;

    @SerializedName("naturezaDenuncia")
    private final String mName;

    public TypeJson(int id, String name) {
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
