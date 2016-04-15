package br.org.projeto.vigilante.ui.activities;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.org.projeto.vigilante.R;
import br.org.projeto.vigilante.util.AppUtil;

@EActivity(R.layout.activity_audio_capture_acitivity)
public class AudioCaptureAcitivity extends AppCompatActivity {

    @ViewById(R.id.audio_capture_button)
    protected Button mButton;

    boolean mStartRecording = false;

    private String mFileName = null;

    private MediaRecorder mRecorder = null;

    @AfterViews
    protected void afterViews() {
        mFileName = Environment.getExternalStorageDirectory().getPath();
    }


    @OptionsItem(android.R.id.home)
    protected void home() {
        supportFinishAfterTransition();
    }

    @Click(R.id.audio_capture_button)
    protected void onClick() {
        if (!mStartRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        if (validateMicAvailability()) {
            mRecorder = new MediaRecorder();
            mRecorder.reset();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            mFileName += "/media/audio/3GP_" + timeStamp + "_.3gp";

            mRecorder.setOutputFile(mFileName);

            try {
                mRecorder.prepare();
            } catch (Exception e) {
                AppUtil.showToast(this, R.string.report_error_no_sdcard);
                supportFinishAfterTransition();
                return;
            }

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            mButton.startAnimation(animation);
            mButton.setText(R.string.audio_capture_stop);
            mStartRecording = true;
            mRecorder.start();
        } else {
            AppUtil.showToast(this, R.string.report_error_mic_not_read);
        }
    }

    private boolean validateMicAvailability() {
        boolean canUse = true;
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_DEFAULT, 44100);
        try {
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                canUse = false;
            }

            recorder.startRecording();
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop();
                canUse = false;
            }
            recorder.stop();
        } finally {
            recorder.release();
            recorder = null;
            return canUse;
        }
    }

    private void stopRecording() {

        mButton.setEnabled(false);

        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mStartRecording = false;

            final Intent output = new Intent();
            output.putExtra(ReportActivity.REPORT_ATTACH_AUDIO, new Gson().toJson(mFileName));
            setResult(RESULT_OK, output);
            supportFinishAfterTransition();
        } else {
            supportFinishAfterTransition();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
