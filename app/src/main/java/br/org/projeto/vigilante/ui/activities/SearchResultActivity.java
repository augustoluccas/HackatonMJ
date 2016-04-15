package br.org.projeto.vigilante.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import br.org.projeto.vigilante.R;

/**
 * Created by fernando on 4/13/16.
 */
@EActivity(R.layout.activity_search_result)
public class SearchResultActivity extends AppCompatActivity {

    @ViewById(R.id.search_result_toolbar)
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

    @Click(R.id.results)
    protected void search() {
        InfoActivity_.intent(this).start();
    }
}
