package br.org.projeto.vigilante.rest.json;

import com.google.gson.annotations.SerializedName;

public class ReportJson {

    @SerializedName("mensagem")
    private final String mOccurrence;

    @SerializedName("latitude")
    private final double mLatitude;

    @SerializedName("longitude")
    private final double mLongitude;

    @SerializedName("idCorporacao")
    private final int mCorporation;

    @SerializedName("idNaturezaDenuncia")
    private final int mType;

    @SerializedName("nomeCidade")
    private final String mLocality;

    @SerializedName("estado")
    private final String mAdminArea;

    @SerializedName("bairro")
    private final String mSubLocality;

    @SerializedName("rua")
    private final String mThoroughfare;

    @SerializedName("numero")
    private final String mSubThoroughfare;

    @SerializedName("nome")
    private final String mName;

    @SerializedName("contato")
    private final String mContact;

    @SerializedName("dataOcorrencia")
    private final long mOcurrenceDate;

    @SerializedName("token")
    private final String mToken;

    @SerializedName("enderecoCompleto")
    private final String mAddressComplete;

    @SerializedName("cep")
    private final String mPostalCode;

    @SerializedName("tipoAnexo")
    private final String mAttachType;

    public ReportJson(String occurrence, double latitude, double longitude, int corporation, int type,
            String locality, String adminArea, String subLocality, String thoroughfare, String subThoroughfare,
            String name, String contact, long ocurrenceDate, String token,
            String addressComplete, String postalCode, String attachType) {
        mOccurrence = occurrence;
        mLatitude = latitude;
        mLongitude = longitude;
        mCorporation = corporation;
        mType = type;
        mLocality = locality;
        mAdminArea = adminArea;
        mSubLocality = subLocality;
        mThoroughfare = thoroughfare;
        mSubThoroughfare = subThoroughfare;
        mName = name;
        mContact = contact;
        mOcurrenceDate = ocurrenceDate;
        mToken = token;
        mAddressComplete = addressComplete;
        mPostalCode = postalCode;
        mAttachType = attachType;
    }

    public String getOccurrence() {
        return mOccurrence;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public int getCorporation() {
        return mCorporation;
    }

    public int getType() {
        return mType;
    }

    public String getLocality() {
        return mLocality;
    }

    public String getAdminArea() {
        return mAdminArea;
    }

    public String getSubLocality() {
        return mSubLocality;
    }

    public String getThoroughfare() {
        return mThoroughfare;
    }

    public String getSubThoroughfare() {
        return mSubThoroughfare;
    }

    public String getName() {
        return mName;
    }

    public String getContact() {
        return mContact;
    }

    public long getOcurrenceDate() {
        return mOcurrenceDate;
    }

    public String getToken() {
        return mToken;
    }

    public String getAddressComplete() {
        return mAddressComplete;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    public String getAttachType() {
        return mAttachType;
    }
}
