package com.example.ratingbar.customcommentratingbar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by bkhu on 17/9/23.
 */

public class CustomCommentRatingBarActivity extends Activity implements CustomCommentRatingBar.OnRatingSliderChangeListener  {

    CustomCommentRatingBar smileBar1 = null;
    CustomCommentRatingBar smileBar2 = null;
    CustomCommentRatingBar smileBar3 = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_comment_rating_bar);
        smileBar1 = (CustomCommentRatingBar) findViewById(R.id.starBar1);
        smileBar2 = (CustomCommentRatingBar) findViewById(R.id.starBar2);
        smileBar3 = (CustomCommentRatingBar) findViewById(R.id.starBar3);
        smileBar1.setOnRatingSliderChangeListener(this);
        smileBar2.setOnRatingSliderChangeListener(this);
        smileBar3.setOnRatingSliderChangeListener(this);
    }

    @Override
    public void onBeginRating(CustomCommentRatingBar smileBar, int rating) {

    }

    @Override
    public void onFinishRating(CustomCommentRatingBar smileBar, int rating) {

    }

    @Override
    public void onCancelRating() {

    }
}
