package br.org.projeto.vigilante.business.database;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.org.projeto.vigilante.business.vo.ReportVO;
import br.org.projeto.vigilante.rest.json.CorporationJson;
import br.org.projeto.vigilante.rest.json.TypeJson;

@EBean(scope = EBean.Scope.Singleton)
public class DatabaseManager {

    private final String DATABASE_NAME = "VigilanteDB";

    private final String REPORT_TABLE = "REPORT";

    private final String REPORT_ID = "ID";

    private final String REPORT_DATE = "DATE";

    private final String REPORT_OCCURRENCE = "OCCURRENCE";

    private final String REPORT_ADDRESS_COMPLETE = "ADDRESS_COMPLETE";

    private final String REPORT_ADDRESS_LATITUDE = "ADDRESS_LATITUDE";

    private final String REPORT_ADDRESS_LONGITUDE = "ADDRESS_LONGITUDE";

    private final String REPORT_ADDRESS_COUNTRY = "ADDRESS_COUNTRY";

    private final String REPORT_ADDRESS_STATE = "ADDRESS_STATE";

    private final String REPORT_ADDRESS_CITY = "ADDRESS_CITY";

    private final String REPORT_ADDRESS_SUBLOCALITY = "ADDRESS_SUBLOCALITY";

    private final String REPORT_ADDRESS_STREET = "ADDRESS_STREET";

    private final String REPORT_ADDRESS_NUMBER = "ADDRESS_NUMBER";

    private final String REPORT_ADDRESS_CEP = "ADDRESS_CEP";

    private final String REPORT_CORPORATION = "CORPORATION";

    private final String REPORT_TYPE = "TYPE";

    private final String REPORT_IMAGE_PATH = "IMAGE_PATH";

    private final String REPORT_VIDEO_PATH = "VIDEO_PATH";

    private final String REPORT_AUDIO_PATH = "AUDIO_PATH";

    private final String REPORT_NAME = "NAME";

    private final String REPORT_CONTACT = "CONTACT";

    private final String REPORT_WIFI_ONLY = "WIFI_ONLY";

    private final String REPORT_CREATE_DATE = "REPORT_CREATE_DATE";

    @RootContext
    protected Context mContext;

    private SQLiteDatabase db;

    @AfterInject
    protected void afterInject() {
        startDatabase();
    }

    public synchronized void startDatabase() {
        createDatabase();
        createTable();
    }

    private void createDatabase() {
        if (db == null) {
            db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        }
    }

    private void createTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + REPORT_TABLE + " ("
                + REPORT_ID + " INTEGER primary key, "
                + REPORT_DATE + " INTEGER not null,"
                + REPORT_OCCURRENCE + " TEXT not null,"
                + REPORT_ADDRESS_COMPLETE + " TEXT not null,"
                + REPORT_ADDRESS_LATITUDE + " INTEGER,"
                + REPORT_ADDRESS_LONGITUDE + " INTEGER,"
                + REPORT_ADDRESS_COUNTRY + " TEXT,"
                + REPORT_ADDRESS_STATE + " TEXT,"
                + REPORT_ADDRESS_CITY + " TEXT,"
                + REPORT_ADDRESS_SUBLOCALITY + " TEXT,"
                + REPORT_ADDRESS_STREET + " TEXT,"
                + REPORT_ADDRESS_NUMBER + " INTEGER,"
                + REPORT_ADDRESS_CEP + " INTEGER,"
                + REPORT_CORPORATION + " INTEGER,"
                + REPORT_TYPE + " INTEGER,"
                + REPORT_IMAGE_PATH + " TEXT,"
                + REPORT_VIDEO_PATH + " TEXT,"
                + REPORT_AUDIO_PATH + " TEXT,"
                + REPORT_NAME + " TEXT,"
                + REPORT_CONTACT + " TEXT,"
                + REPORT_WIFI_ONLY + " INTEGER not null,"
                + REPORT_CREATE_DATE + " INTEGER not null"
                + ");");
    }

    @Background
    public void insertTable(ReportVO vo) {
        if (db == null) {
            startDatabase();
        }

        if (db != null) {
            ContentValues values = new ContentValues();
            values.put(REPORT_DATE, vo.getDateTime().getTime());
            values.put(REPORT_OCCURRENCE, vo.getOccurrence());
            values.put(REPORT_ADDRESS_COMPLETE, vo.getAddressComplete());
            if (vo.getAddress() != null) {
                values.put(REPORT_ADDRESS_LATITUDE, vo.getAddress().getLatitude());
                values.put(REPORT_ADDRESS_LONGITUDE, vo.getAddress().getLongitude());
                values.put(REPORT_ADDRESS_COUNTRY, vo.getAddress().getCountryName());
                values.put(REPORT_ADDRESS_STATE, vo.getAddress().getAdminArea());
                values.put(REPORT_ADDRESS_CITY, vo.getAddress().getLocality());
                values.put(REPORT_ADDRESS_SUBLOCALITY, vo.getAddress().getSubLocality());
                values.put(REPORT_ADDRESS_STREET, vo.getAddress().getThoroughfare());
                values.put(REPORT_ADDRESS_NUMBER, vo.getAddress().getSubThoroughfare());
                values.put(REPORT_ADDRESS_CEP, vo.getAddress().getPostalCode());
            }
            if (vo.getCorporation() != null) {
                values.put(REPORT_CORPORATION, vo.getCorporation().getId());
            }
            if (vo.getType() != null) {
                values.put(REPORT_TYPE, vo.getType().getId());
            }
            values.put(REPORT_IMAGE_PATH, vo.getImagePath());
            values.put(REPORT_VIDEO_PATH, vo.getVideoPath());
            values.put(REPORT_AUDIO_PATH, vo.getAudioPath());
            values.put(REPORT_NAME, vo.getName());
            values.put(REPORT_CONTACT, vo.getContact());
            values.put(REPORT_WIFI_ONLY, vo.isOnlyWifi() ? 1 : 0);
            values.put(REPORT_CREATE_DATE, new Date().getTime());
            db.insert(REPORT_TABLE, null, values);
        }
    }

    public List<ReportVO> getReports() {

        if (db == null) {
            startDatabase();
        }

        List<ReportVO> reports = null;
        if (db != null) {
            Cursor cursor = db.query(REPORT_TABLE,
                    new String[]{REPORT_ID, REPORT_DATE, REPORT_OCCURRENCE, REPORT_ADDRESS_COMPLETE,
                            REPORT_ADDRESS_LATITUDE, REPORT_ADDRESS_LONGITUDE, REPORT_ADDRESS_COUNTRY,
                            REPORT_ADDRESS_STATE,
                            REPORT_ADDRESS_CITY, REPORT_ADDRESS_SUBLOCALITY, REPORT_ADDRESS_STREET,
                            REPORT_ADDRESS_NUMBER,
                            REPORT_ADDRESS_CEP, REPORT_CORPORATION, REPORT_TYPE, REPORT_IMAGE_PATH, REPORT_VIDEO_PATH,
                            REPORT_AUDIO_PATH, REPORT_NAME, REPORT_CONTACT, REPORT_WIFI_ONLY, REPORT_CREATE_DATE}, null,
                    null, null, null, null);

            reports = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {

                ReportVO vo = new ReportVO(cursor.getString(3), new Date(cursor.getLong(1)), cursor.getString(2));

                vo.setId(cursor.getInt(0));

                Address address = new Address(Locale.getDefault());
                address.setLatitude(cursor.getDouble(4));
                address.setLongitude(cursor.getDouble(5));
                address.setCountryName(cursor.getString(6));
                address.setAdminArea(cursor.getString(7));
                address.setLocality(cursor.getString(8));
                address.setSubLocality(cursor.getString(9));
                address.setThoroughfare(cursor.getString(10));
                address.setSubThoroughfare(cursor.getString(11));
                address.setPostalCode(cursor.getString(12));
                vo.setAddress(address);
                vo.setCorporation(new CorporationJson(cursor.getInt(13), ""));
                vo.setType(new TypeJson(cursor.getInt(14), ""));
                vo.setImagePath(cursor.getString(15));
                vo.setVideoPath(cursor.getString(16));
                vo.setAudioPath(cursor.getString(17));
                vo.setName(cursor.getString(18));
                vo.setContact(cursor.getString(19));
                vo.setOnlyWifi(cursor.getInt(20) == 1);
                vo.setCreationDate(new Date(cursor.getLong(21)));

                reports.add(vo);
            }

            cursor.close();
        } else {
            reports = new ArrayList<>(0);
        }
        return reports;
    }

    @Background
    public void deleteReport(final ReportVO vo) {
        String where = REPORT_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(vo.getId())};
        db.delete(REPORT_TABLE, where, whereArgs);
    }


}
