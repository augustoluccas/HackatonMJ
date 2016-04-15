package br.org.projeto.vigilante.ui.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.org.projeto.vigilante.R;
import br.org.projeto.vigilante.business.vo.ReportVO;
import br.org.projeto.vigilante.events.DateEvent;
import br.org.projeto.vigilante.events.TimeEvent;
import br.org.projeto.vigilante.rest.VigilanteAPI;
import br.org.projeto.vigilante.rest.json.CorporationJson;
import br.org.projeto.vigilante.rest.json.TypeJson;
import br.org.projeto.vigilante.ui.fragments.DatePickerFragment;
import br.org.projeto.vigilante.ui.fragments.TimePickerFragment;
import br.org.projeto.vigilante.ui.view.ReportEditText;
import br.org.projeto.vigilante.util.AppUtil;
import br.org.projeto.vigilante.util.FormatUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_report)
public class ReportActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String REPORT_MAP_ADDRESS = "REPORT_MAP_ADDRESS";

    public static final String REPORT_ATTACH_AUDIO = "REPORT_ATTACH_AUDIO";

    static final int REPORT_IMAGE_REQUEST_CODE = 134;

    static final int REPORT_VIDEO_REQUEST_CODE = 136;

    private final int REPORT_MAP_REQUEST_CODE = 132;

    private final int REPORT_GALLERY_PERMISSION = 131;

    private final int REPORT_GALLERY_REQUEST_CODE = 133;

    private final int REPORT_IMAGE_PERMISSION = 135;

    private final int REPORT_VIDEO_PERMISSION = 139;

    private final int REPORT_AUDIO_REQUEST_CODE = 137;

    private final int REPORT_AUDIO_PERMISSION = 138;

    @ViewById(R.id.report_toolbar)
    protected Toolbar mToolbar;

    @ViewById(R.id.report_scrollview)
    protected ScrollView mScrollView;

    @ViewById(R.id.report_address)
    protected ReportEditText mAddressEditText;

    @ViewById(R.id.report_state)
    protected ReportEditText mStateEditText;

    @ViewById(R.id.report_city)
    protected ReportEditText mCityEditText;

    @ViewById(R.id.report_date)
    protected TextView mDateText;

    @ViewById(R.id.report_time)
    protected TextView mTimeText;

//    @ViewById(R.id.report_corporation)
//    protected Spinner mCorporationSpinner;

    @ViewById(R.id.report_type)
    protected Spinner mTypeSpinner;

    @ViewById(R.id.report_occurrence)
    protected ReportEditText mOccurrence;

    @ViewById(R.id.report_attach)
    protected LinearLayout mAttachLayout;

    @ViewById(R.id.report_preview)
    protected ImageView mImagePreview;

    @ViewById(R.id.report_preview_layout)
    protected RelativeLayout mImagePreviewLayout;

    @ViewById(R.id.report_name)
    protected EditText mReportName;

    @ViewById(R.id.report_contact)
    protected EditText mReportContact;

    @ViewById(R.id.report_identify_layout)
    protected LinearLayout mIdentifyLayout;

    @ViewById(R.id.report_identify_button)
    protected Button mIdentifyButton;

    @ViewById(R.id.report_anonymous_button)
    protected Button mAnonymousButton;

    private LatLng mLatLng;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private Address mAddress;

    private Calendar mDateTime;

//    private List<CorporationJson> mCorporationList;

    private List<TypeJson> mTypeList;

    private String mImagePath;

    private String mVideoPath;

    private String mAudioPath;

    private Resources mResources;

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
        }

        mResources = getResources();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void updateDate(DateEvent event) {
        mDateTime.set(event.getYear(), event.getMonth(), event.getDay());
        mDateText.setText(FormatUtil.formatDate((mDateTime.getTime())));
    }

    @Subscribe
    public void updateTime(TimeEvent event) {
        mDateTime.set(Calendar.HOUR_OF_DAY, event.getHour());
        mDateTime.set(Calendar.MINUTE, event.getMinute());
        mTimeText.setText(FormatUtil.formatTime((mDateTime.getTime())));
    }

    @AfterViews
    protected void afterViews() {
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateTime = Calendar.getInstance();
        mDateText.setText(FormatUtil.formatDate(mDateTime.getTime()));
        mTimeText.setText(FormatUtil.formatTime(mDateTime.getTime()));

        //updateSpinners();

        //noinspection deprecation
        mImagePreview.setAlpha(127);
    }

//    @UiThread
//    protected void updateSpinners() {
//        VigilanteAPI.getServices(this).fetchCorporation(new Callback<List<CorporationJson>>() {
//            @Override
//            public void success(List<CorporationJson> corporationJsons, Response response) {
//                setCorporationSpinner(corporationJsons);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                setCorporationSpinner(new ArrayList<CorporationJson>(0));
//            }
//        });
//
//        VigilanteAPI.getServices(this).fetchType(new Callback<List<TypeJson>>() {
//            @Override
//            public void success(List<TypeJson> typeJsons, Response response) {
//                setTypeSpinner(typeJsons);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                setTypeSpinner(new ArrayList<TypeJson>(0));
//            }
//        });
//    }


//    @UiThread
//    protected void setCorporationSpinner(List<CorporationJson> list) {
//        mCorporationList = list;
//        mCorporationList.add(0, new CorporationJson(0, getString(R.string.report_spinner_zero)));
//        ArrayAdapter<CorporationJson> corporationAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
//                mCorporationList);
//        corporationAdapter.setDropDownViewResource(R.layout.spinner_item);
//        mCorporationSpinner.setAdapter(corporationAdapter);
//    }


    @UiThread
    protected void setTypeSpinner(List<TypeJson> list) {
        mTypeList = list;
        mTypeList.add(0, new TypeJson(0, getString(R.string.report_spinner_zero)));
        ArrayAdapter<TypeJson> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mTypeList);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        mTypeSpinner.setAdapter(typeAdapter);

    }

    @Click(R.id.report_map_button)
    protected void reportMapClick() {
        AppUtil.closeKeyboard(this, mToolbar);
        startActivityForResult(new Intent(this, MapsActivity.class), REPORT_MAP_REQUEST_CODE);

    }

    @OnActivityResult(REPORT_MAP_REQUEST_CODE)
    protected void onMapAddress(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mAddress = new Gson().fromJson(data.getStringExtra(REPORT_MAP_ADDRESS), Address.class);
            mAddressEditText.setText(AppUtil.getAddressFormatted(mAddress));
            mStateEditText.setText(AppUtil.getStateCityNull(mAddress.getAdminArea()));
            mCityEditText.setText(AppUtil.getStateCityNull(mAddress.getLocality()));
        }
    }

    @Click(R.id.report_date)
    protected void dateClick() {
        AppUtil.closeKeyboard(this, mToolbar);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Click(R.id.report_time)
    protected void timeClick() {
        AppUtil.closeKeyboard(this, mToolbar);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Click(R.id.report_attach_gallery)
    protected void attachFromGallery() {
        AppUtil.closeKeyboard(this, mToolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            startActivityForResult(
                    new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                    REPORT_GALLERY_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REPORT_GALLERY_PERMISSION);
        }
    }

    @OnActivityResult(REPORT_GALLERY_REQUEST_CODE)
    protected void onGalleryResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && null != data) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                mImagePath = picturePath;
                updatePreview(BitmapFactory.decodeFile(picturePath));
            }
        }
    }

    @UiThread
    protected void updatePreview(Bitmap bitmap) {
        mImagePreview.setImageBitmap(bitmap);
        mAttachLayout.setVisibility(View.GONE);
        mImagePreviewLayout.setVisibility(View.VISIBLE);
    }

    @Click(R.id.report_attach_image)
    protected void attachImage() {
        AppUtil.closeKeyboard(this, mToolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            callPhotoCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REPORT_IMAGE_PERMISSION);
        }
    }

    private void callPhotoCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REPORT_IMAGE_REQUEST_CODE);
        }
    }

    @OnActivityResult(REPORT_IMAGE_REQUEST_CODE)
    protected void onPhotoCameraResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            updatePreview(bitmap);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile(imageFileName, ".jpg", storageDir);

                mImagePath = file.getPath();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REPORT_GALLERY_PERMISSION:
                // If user authorized, ask know to turn on GPS.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(
                            new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                            REPORT_GALLERY_REQUEST_CODE);
                } else {
                    AppUtil.showToast(this, R.string.report_error_attach_permission);
                }
                break;
            case REPORT_IMAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    callPhotoCamera();
                } else {
                    AppUtil.showToast(this, R.string.report_error_attach_permission);
                }
                break;
            case REPORT_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    AudioCaptureAcitivity_.intent(this).startForResult(REPORT_AUDIO_REQUEST_CODE);
                } else {
                    AppUtil.showToast(this, R.string.report_error_attach_permission);
                }
                break;
            case REPORT_VIDEO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    callVideo();
                } else {
                    AppUtil.showToast(this, R.string.report_error_attach_permission);
                }
                break;
        }
    }

    @Click(R.id.report_attach_video)
    protected void attachVideo() {
        AppUtil.closeKeyboard(this, mToolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            callVideo();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REPORT_VIDEO_PERMISSION);
        }
    }

    private void callVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REPORT_VIDEO_REQUEST_CODE);
        }
    }

    @OnActivityResult(REPORT_VIDEO_REQUEST_CODE)
    protected void onVideoCameraResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            Uri videoUri = data.getData();
            mVideoPath = getRealPathFromURI(this, videoUri);

            updatePreview(MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(),
                    Long.parseLong(videoUri.getLastPathSegment()), MediaStore.Video.Thumbnails.MINI_KIND,
                    new BitmapFactory.Options()));
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Click(R.id.report_preview)
    protected void onPreviewTouch() {
        AppUtil.closeKeyboard(this, mToolbar);

        mAttachLayout.setVisibility(View.VISIBLE);
        mImagePreviewLayout.setVisibility(View.GONE);

        // Cleaning memory.
        mImagePreview.setImageBitmap(null);
        mImagePath = null;
        mVideoPath = null;
        mAudioPath = null;
    }

    @Click(R.id.report_attach_audio)
    protected void attachAudio() {
        AppUtil.closeKeyboard(this, mToolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            AudioCaptureAcitivity_.intent(this).startForResult(REPORT_AUDIO_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REPORT_AUDIO_PERMISSION);
        }
    }


    @OnActivityResult(REPORT_AUDIO_REQUEST_CODE)
    protected void onAudioResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mAudioPath = new Gson().fromJson(data.getStringExtra(REPORT_ATTACH_AUDIO), String.class);
            updatePreview(BitmapFactory.decodeResource(mResources, R.drawable.ic_music_video_black_48dp_1));
        }
    }


    @Click(R.id.report_report)
    protected void reportButtonClick() {
        AppUtil.closeKeyboard(this, mToolbar);

        if (TextUtils.isEmpty(mAddressEditText.getText().toString())) {
            AppUtil.showToast(this, R.string.report_error_address);
            return;
        }

        if (TextUtils.isEmpty(mStateEditText.getText().toString())) {
            AppUtil.showToast(this, R.string.report_error_state);
            return;
        }

        if (TextUtils.isEmpty(mCityEditText.getText().toString())) {
            AppUtil.showToast(this, R.string.report_error_city);
            return;
        }

        if (mDateTime.after(Calendar.getInstance())) {
            AppUtil.showToast(this, R.string.report_error_future);
            return;
        }

        if (TextUtils.isEmpty(mOccurrence.getText().toString())) {
            AppUtil.showToast(this, R.string.report_error_occurrence);
            return;
        }

//        if (mCorporationSpinner.getSelectedItemPosition() < 1) {
//            AppUtil.showToast(this, R.string.report_error_corporation);
//            return;
//        }

        // Success
        makeVoObject();
    }

    private void makeVoObject() {

        ReportVO reportVO = new ReportVO(mAddressEditText.getText().toString(),
                mDateTime.getTime(), mOccurrence.getText().toString());
        reportVO.setAddress(mAddress);
        reportVO.setVideoPath(mVideoPath);
        reportVO.setAudioPath(mAudioPath);

        if (!TextUtils.isEmpty(mReportName.getText().toString())) {
            reportVO.setName(mReportName.getText().toString());
        }

        if (!TextUtils.isEmpty(mReportContact.getText().toString())) {
            reportVO.setContact(mReportContact.getText().toString());
        }

//        if (mCorporationSpinner.getSelectedItemPosition() > 0) {
//            reportVO.setCorporation((CorporationJson) mCorporationSpinner.getSelectedItem());
//        }
        if (mTypeSpinner.getSelectedItemPosition() > 0) {
            reportVO.setType((TypeJson) mTypeSpinner.getSelectedItem());
        }
        reportVO.setOnlyWifi(((CheckBox) findViewById(R.id.config_wifi_switch)).isChecked());

        if (!TextUtils.isEmpty(mImagePath)) {
            reportVO.setImagePath(mImagePath);
        } else if (mVideoPath != null) {
            reportVO.setVideoPath(mVideoPath);
        } else if (!TextUtils.isEmpty(mAudioPath)) {
            reportVO.setAudioPath(mAudioPath);
        }

        ConfirmActivity_.intent(this).mVO(new Gson().toJson(reportVO)).start();

    }

    @Click(R.id.report_identify_button)
    protected void onIdentifyClick() {
        updateIdentifyState(true);
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Click(R.id.report_anonymous_button)
    protected void onAnonymousClick() {
        updateIdentifyState(false);
        mReportName.setText("");
        mReportContact.setText("");
    }

    @SuppressWarnings("deprecation")
    @UiThread
    private void updateIdentifyState(boolean identify) {
        mIdentifyButton.setBackgroundDrawable(
                mResources.getDrawable(identify ? R.drawable.main_button_selector : R.drawable.second_button_selector));
        mAnonymousButton.setBackgroundDrawable(
                mResources.getDrawable(identify ? R.drawable.second_button_selector : R.drawable.main_button_selector));
        mIdentifyButton.setTextColor(
                mResources.getColor(identify ? R.color.main_button_text_color : R.color.second_button_text_color));
        mAnonymousButton.setTextColor(
                mResources.getColor(identify ? R.color.second_button_text_color : R.color.main_button_text_color));
        mIdentifyLayout.setVisibility(identify ? View.VISIBLE : View.GONE);

    }

    @OptionsItem(android.R.id.home)
    protected void home() {
        supportFinishAfterTransition();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                mAddressEditText.setHint(R.string.report_address_hint);
            }
        } else {
            mAddressEditText.setHint(R.string.report_address_hint);
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                getAddress();
            } else {
                mAddressEditText.setHint(R.string.report_address_hint);
            }
        } else {
            mAddressEditText.setHint(R.string.report_address_hint);
        }
    }

    private void getAddress() {
        try {
            final List<Address> addresses = new Geocoder(this).getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
            if (addresses.size() > 0) {
                mAddress = addresses.get(0);
                mAddressEditText.setText(AppUtil.getAddressFormatted(mAddress));
                mStateEditText.setText(AppUtil.getStateCityNull(mAddress.getAdminArea()));
                mCityEditText.setText(AppUtil.getStateCityNull(mAddress.getLocality()));
            }
        } catch (Exception e) {
            mAddressEditText.setHint(R.string.report_address_hint);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        mAddressEditText.setHint(R.string.report_address_hint);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mAddressEditText.setHint(R.string.report_address_hint);
    }

}

