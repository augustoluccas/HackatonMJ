package br.org.projeto.vigilante.business.vo;

import android.content.Context;
import android.location.Address;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Date;

import br.org.projeto.vigilante.push.QuickstartPreferences;
import br.org.projeto.vigilante.rest.json.CorporationJson;
import br.org.projeto.vigilante.rest.json.ReportJson;
import br.org.projeto.vigilante.rest.json.TypeJson;

public class ReportVO {

    private final String mAddressComplete;

    private final Date mDateTime;

    private final String mOccurrence;

    private int mId;

    private Address mAddress;

    private CorporationJson mCorporation;

    private TypeJson mType;

    private String mImagePath;

    private String mVideoPath;

    private String mAudioPath;

    private boolean mOnlyWifi;

    private String mName;

    private String mContact;

    private Date mCreationDate;

    public ReportVO(String addressComplete, Date dateTime, String occurrence) {
        mAddressComplete = addressComplete;
        mDateTime = dateTime;
        mOccurrence = occurrence;
    }

    public String getAddressComplete() {
        return mAddressComplete;
    }

    public Date getDateTime() {
        return mDateTime;
    }

    public String getOccurrence() {
        return mOccurrence;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public CorporationJson getCorporation() {
        return mCorporation;
    }

    public void setCorporation(CorporationJson corporation) {
        mCorporation = corporation;
    }

    public TypeJson getType() {
        return mType;
    }

    public void setType(TypeJson type) {
        mType = type;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
    }

    public String getAudioPath() {
        return mAudioPath;
    }

    public void setAudioPath(String audioPath) {
        mAudioPath = audioPath;
    }

    public boolean isOnlyWifi() {
        return mOnlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        mOnlyWifi = onlyWifi;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getContact() {
        return mContact;
    }

    public void setContact(String contact) {
        mContact = contact;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(Date creationDate) {
        mCreationDate = creationDate;
    }

    public ReportJson toJson(Context context) {

        final Address address = getAddress();

        final String token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(QuickstartPreferences.PUSH_TOKEN, "token");

        return new ReportJson(mOccurrence, address.getLatitude(), address.getLongitude(),
                mCorporation.getId(), mType.getId(), address.getLocality(), address.getAdminArea(),
                address.getSubLocality(), address.getThoroughfare(), address.getSubThoroughfare(),
                mName, mContact, mDateTime.getTime(), token, mAddressComplete, address.getPostalCode(), getAtach());
    }

    private String getAtach() {
        String attach = "";
        if (!TextUtils.isEmpty(mImagePath)) {
            attach = "IMAGE";
        } else if (!TextUtils.isEmpty(mVideoPath)) {
            attach = "VIDEO";
        } else if (!TextUtils.isEmpty(mAudioPath)) {
            attach = "AUDIO";
        }
        return attach;
    }

}
