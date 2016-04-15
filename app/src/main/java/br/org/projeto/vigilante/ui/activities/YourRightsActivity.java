package br.org.projeto.vigilante.ui.activities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import br.org.projeto.vigilante.R;
import br.org.projeto.vigilante.rest.VigilanteAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_your_rights)
public class YourRightsActivity extends AppCompatActivity {

    @ViewById(R.id.your_rights_toolbar)
    protected Toolbar mToolbar;

    @ViewById(R.id.your_rights_text)
    protected TextView mTextView;

    @AfterViews
    protected void afterViews() {
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        VigilanteAPI.getServices(this).fetchYourRights(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                mTextView.setText(new String(((TypedByteArray) response.getBody()).getBytes()));
            }

            @Override
            public void failure(RetrofitError error) {
                mTextView.setText(R.string.your_right_text);
            }
        });
    }

    @OptionsItem(android.R.id.home)
    protected void home() {
        supportFinishAfterTransition();
    }
}
