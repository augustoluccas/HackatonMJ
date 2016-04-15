package br.org.projeto.vigilante.ui.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import br.org.projeto.vigilante.R;

/**
 * Created by fernando on 4/13/16.
 */
@EActivity(R.layout.activity_main_follow)
public class MainFollowActivity extends AppCompatActivity {

    @ViewById(R.id.follow_toolbar)
    protected Toolbar mToolbar;
    @ViewById(R.id.map)
    protected ImageView mMapImage;
    @ViewById(R.id.follow_scrollview)
    protected ScrollView mScrollView;
    @ViewById(R.id.advanced_search)
    protected Button mAdvancedSearchButton;
    @ViewById(R.id.follow_advanced_search)
    protected ImageView mAdvancedSearchImage;

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

    @Click(R.id.map)
    protected void changePicture() {
        mMapImage.setImageResource(R.drawable.bg_follow_more_info);
    }

    @Click(R.id.advanced_search)
    protected void advancedSearch() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mScrollView.smoothScrollTo(0, mAdvancedSearchButton.getBottom());
            }
        });
    }

    @Click(R.id.search)
    protected void search() {
        SearchResultActivity_.intent(this).start();
    }
}
