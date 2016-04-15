package br.org.projeto.vigilante.ui.activities;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import br.org.projeto.vigilante.R;
import br.org.projeto.vigilante.business.database.DatabaseManager;
import br.org.projeto.vigilante.business.singletons.SyncHelper;
import br.org.projeto.vigilante.business.vo.ReportVO;
import br.org.projeto.vigilante.util.FormatUtil;

@EActivity(R.layout.activity_confirm)
public class ConfirmActivity extends AppCompatActivity {

    private final static String WARNING_ALREADY_CONFIRM = "WARNING_ALREADY_CONFIRM";

    @ViewById(R.id.confirm_toolbar)
    protected Toolbar mToolbar;

    @ViewById(R.id.confirm_address)
    protected TextView mAddress;

    @ViewById(R.id.confirm_date)
    protected TextView mDateTime;

    @ViewById(R.id.confirm_occcurrence)
    protected TextView mOccurrence;

    @ViewById(R.id.confirm_convenio)
    protected TextView mConvenio;

    @ViewById(R.id.confirm_type)
    protected TextView mType;

    @ViewById(R.id.confirm_name)
    protected TextView mName;

    @ViewById(R.id.confirm_contact)
    protected TextView mContact;

    @ViewById(R.id.confirm_attach_title)
    protected TextView mAttachTitle;

    @ViewById(R.id.confirm_attach)
    protected ImageButton mAttach;

    @ViewById(R.id.confirm_warning)
    protected TextView mWarning;

    @ViewById(R.id.confirm_send)
    protected Button mSend;

    @Extra
    protected String mVO;

    @Bean
    protected DatabaseManager mDatabaseManager;

    @Bean
    protected SyncHelper mSyncHelper;

    private ReportVO mReportVO;

    private SharedPreferences mPreferences;

    private View.OnClickListener mImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(mReportVO.getImagePath())), "image/png");
            startActivity(intent);
        }
    };

    private View.OnClickListener mVideoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(mReportVO.getVideoPath())), "video/mp4");
            startActivity(intent);
        }
    };

    private View.OnClickListener mAudioClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(mReportVO.getAudioPath())), "video/3gpp");
            startActivity(intent);
        }
    };

    @AfterViews
    protected void afterViews() {
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReportVO = new Gson().fromJson(mVO, ReportVO.class);

        setText(mAddress, R.string.confirm_address, mReportVO.getAddressComplete());
        setText(mDateTime, R.string.confirm_date, FormatUtil.formatDateTime(mReportVO.getDateTime()));
        setText(mOccurrence, R.string.confirm_occcurrence, mReportVO.getOccurrence());
        setText(mConvenio, R.string.confirm_convenio, mReportVO.getCorporation().toString());
//        if (mReportVO.getCorporation() != null) {
//            setText(mCorporation, R.string.confirm_corporation, mReportVO.getCorporation().toString());
//        } else {
//            mCorporation.setVisibility(View.GONE);
//        }
        if (mReportVO.getType() != null) {
            setText(mType, R.string.confirm_type, mReportVO.getType().toString());
        } else {
            mType.setVisibility(View.GONE);
        }
        setText(mName, R.string.confirm_name, mReportVO.getName());
        setText(mContact, R.string.confirm_contact, mReportVO.getContact());

        if (!TextUtils.isEmpty(mReportVO.getImagePath())) {
            setImage();
        } else if (!TextUtils.isEmpty(mReportVO.getAudioPath())) {
            setAudio();
        } else if (mReportVO.getVideoPath() != null) {
            setVideo();
        } else {
            mAttachTitle.setVisibility(View.GONE);
            mAttach.setVisibility(View.GONE);
        }

        mPreferences = getPreferences(Context.MODE_PRIVATE);

        if (mPreferences.getBoolean(WARNING_ALREADY_CONFIRM, false)) {
            mWarning.setVisibility(View.VISIBLE);
        } else {
            updateButtonLayout();
        }
    }

    @UiThread
    private void updateButtonLayout() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSend.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mSend.setLayoutParams(params);
    }

    @UiThread
    protected void setImage() {
        setAttachVisible(BitmapFactory.decodeFile(mReportVO.getImagePath()));
        mAttach.setOnClickListener(mImageClick);
    }

    @UiThread
    protected void setVideo() {
        setAttachVisible(
                ThumbnailUtils.createVideoThumbnail(mReportVO.getVideoPath(), MediaStore.Video.Thumbnails.MICRO_KIND));
        mAttach.setOnClickListener(mVideoClick);
    }

    @UiThread
    protected void setAudio() {
        setAttachVisible(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_video_black_48dp_1));
        mAttach.setOnClickListener(mAudioClick);
    }

    @UiThread
    protected void setAttachVisible(Bitmap attachBitmap) {
        mAttachTitle.setVisibility(View.VISIBLE);
        mAttach.setVisibility(View.VISIBLE);
        mAttach.setImageBitmap(attachBitmap);
    }

    @UiThread
    protected void setText(TextView textView, int resource, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(FormatUtil.setBold(this, getString(resource, text)));
        }
    }

    @Click(R.id.confirm_send)
    protected void onSendClick() {
        if (mPreferences.getBoolean(WARNING_ALREADY_CONFIRM, false)) {
            showConfirmAlert();
        } else {
            showWarningAlert();
        }

    }

    @UiThread
    private void showWarningAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_dialog_warning_message)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm_dialog_warning_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPreferences.edit().putBoolean(WARNING_ALREADY_CONFIRM, true).apply();
                        showConfirmAlert();
                    }
                })
                .setNegativeButton(R.string.confirm_dialog_warning_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @UiThread
    protected void showConfirmAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_dialog_sent_title)
                .setMessage(R.string.confirm_dialog_sent_message)
                .setCancelable(false)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveOnDatabase();
                        sendToServer();
                        MainActivity_.intent(ConfirmActivity.this)
                                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                                .start();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Background
    protected void saveOnDatabase() {
        mDatabaseManager.insertTable(mReportVO);
    }

    @Background(delay = 200)
    protected void sendToServer() {
        mSyncHelper.save();
    }

    @OptionsItem(android.R.id.home)
    protected void home() {
        supportFinishAfterTransition();
    }
}
