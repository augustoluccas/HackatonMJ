package br.org.projeto.vigilante.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import br.org.projeto.vigilante.R;

/**
 * Created by fernando on 4/14/16.
 */
@EActivity(R.layout.activity_info)
public class InfoActivity extends AppCompatActivity {

    @ViewById(R.id.info_toolbar)
    protected Toolbar mToolbar;

    @AfterViews
    protected void afterViews() {
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OptionsItem(android.R.id.home)
    protected void home() {
        supportFinishAfterTransition();
    }
}
