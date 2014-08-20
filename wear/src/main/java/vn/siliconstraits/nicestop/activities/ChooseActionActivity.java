package vn.siliconstraits.nicestop.activities;

import android.view.View;

import harmony.android.library.base.BaseActivity;
import vn.siliconstraits.nicestop.R;

/**
 * Created by HarmonyLee on 8/20/14.
 */
public class ChooseActionActivity extends BaseActivity {
    // Control
    private View viewGoToNiceStop;
    private View viewGoToMyFavoriteList;

    private final View.OnClickListener onClickFavoriteList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // go to favorite list
            goToActivity(CategoriesActivity.class);
        }
    };
    private final View.OnClickListener onClickSeeNiceStop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // go to show current location
            goToActivity(ShowLocationMapActivity.class);
        }
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_choose_action;
    }

    @Override
    protected void setUpControl() {
        viewGoToNiceStop = findViewById(R.id.textViewSeeNiceStop);
        viewGoToMyFavoriteList = findViewById(R.id.textViewFavoriteList);
    }

    @Override
    protected void setUpActionForControl() {
        viewGoToMyFavoriteList.setOnClickListener(onClickFavoriteList);
        viewGoToNiceStop.setOnClickListener(onClickSeeNiceStop);
    }
}
