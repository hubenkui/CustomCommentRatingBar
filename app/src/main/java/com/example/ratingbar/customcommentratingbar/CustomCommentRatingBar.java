package com.example.ratingbar.customcommentratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bkhu on 17/9/22.
 */

public class CustomCommentRatingBar extends View {

    private static final int NO_RATING = 0;
    private static final int MAX_RATE = 5;
    private boolean isSliding;
    private float slidePosition;
    private PointF[] points;
    private float itemWidth;
    private Drawable[] ratingSmiles;
    private Drawable defaultSmile;
    private OnRatingSliderChangeListener listener;
    private int currentRating = NO_RATING;
    private int smileWidth, smileHeight;
    private int horizontalSpacing;
    private boolean isEnabled;
    private int rating = NO_RATING;

    public CustomCommentRatingBar(Context context) {
        super(context);
        init();
    }

    public CustomCommentRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomCommentRatingBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init() {
        init(null);
    }

    private void init(AttributeSet attrs) {
        isSliding = false;

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RatingBar, 0, 0);
            try {
                smileWidth = ta.getDimensionPixelSize(R.styleable.RatingBar_Width, 0);
                smileHeight = ta.getDimensionPixelSize(R.styleable.RatingBar_Height, 0);
                horizontalSpacing = ta.getDimensionPixelSize(R.styleable.RatingBar_horizontalSpacing, 0);
                isEnabled = ta.getBoolean(R.styleable.RatingBar_enabled, true);
                rating = ta.getInt(R.styleable.RatingBar_rating, NO_RATING);
                int resDefault = ta.getResourceId(R.styleable.RatingBar_Default, R.drawable.emojiblank);
                int emojiBlack = ta.getResourceId(R.styleable.RatingBar_RateBlank, R.drawable.emojiblank);
                int emojiSad = ta.getResourceId(R.styleable.RatingBar_RateSad, R.drawable.emojisad);
                int emojiHappy = ta.getResourceId(R.styleable.RatingBar_RateHappy, R.drawable.emojihappy);
                defaultSmile = ResourcesCompat.getDrawable(getResources(), resDefault, null);
                ratingSmiles = new Drawable[]{
                        ResourcesCompat.getDrawable(getResources(), emojiBlack, null),
                        ResourcesCompat.getDrawable(getResources(), emojiSad, null),
                        ResourcesCompat.getDrawable(getResources(), emojiHappy, null),
                };

                if (smileWidth == 0)
                    smileWidth = defaultSmile.getIntrinsicWidth();

                if (smileHeight == 0)
                    smileHeight = defaultSmile.getIntrinsicHeight();
            } finally {
                ta.recycle();
            }
        }

        points = new PointF[MAX_RATE];
        for (int i = 0; i < MAX_RATE; i++) {
            points[i] = new PointF();
        }
        if (rating != NO_RATING)
            setRating(rating);
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        super.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Set a listener that will be invoked whenever the users interacts with the SmileBar.
     *
     * @param listener Listener to set.
     */
    public void setOnRatingSliderChangeListener(OnRatingSliderChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                isSliding = true;
                slidePosition = getRelativePosition(event.getX());
                rating = (int) Math.ceil(slidePosition);
                if (listener != null && rating != currentRating) {
                    currentRating = rating;
                    listener.onBeginRating(this, rating);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                currentRating = NO_RATING;
                if (listener != null)
                    listener.onFinishRating(this, (int) Math.ceil(slidePosition));
                rating = (int) Math.ceil(slidePosition);
                break;
            case MotionEvent.ACTION_CANCEL:
                currentRating = NO_RATING;
                if (listener != null)
                    listener.onCancelRating();
                isSliding = false;
                break;
            default:
                break;
        }

        invalidate();
        return true;
    }

    private float getRelativePosition(float x) {
        float position = x / itemWidth;
        position = Math.max(position, 0);
        return Math.min(position, MAX_RATE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        itemWidth = w / (float) MAX_RATE;
        updatePositions();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = smileWidth * MAX_RATE + horizontalSpacing * (MAX_RATE - 1) +
                getPaddingLeft() + getPaddingRight();
        int height = smileHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < MAX_RATE; i++) {
            PointF pos = points[i];
            canvas.save();
            canvas.translate(pos.x, pos.y);
            drawSmile(canvas, i);
            canvas.restore();
        }
    }

    private void drawSmile(Canvas canvas, int position) {
        if (isSliding && position <= slidePosition) {
            Drawable[] smiles = ratingSmiles;
            int rating = (int) Math.ceil(slidePosition);
            if (rating > 0)
                if (rating <= 3) {
                    drawSmile(canvas, smiles[1]);
                } else {
                    drawSmile(canvas, smiles[2]);
                }
            else
                drawSmile(canvas, defaultSmile);
        } else {
            drawSmile(canvas, defaultSmile);
        }
    }

    private void drawSmile(Canvas canvas, Drawable smile) {
        canvas.save();
        canvas.translate(-smileWidth / 2, -smileHeight / 2);
        smile.setBounds(0, 0, smileWidth, smileHeight);
        smile.draw(canvas);
        canvas.restore();
    }

    private void updatePositions() {
        float left = 0;
        for (int i = 0; i < MAX_RATE; i++) {
            float posY = getHeight() / 2;
            float posX = left + smileWidth / 2;
            left += smileWidth;
            if (i > 0) {
                posX += horizontalSpacing;
                left += horizontalSpacing;
            } else {
                posX += getPaddingLeft();
                left += getPaddingLeft();
            }

            points[i].set(posX, posY);

        }
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > MAX_RATE){
            return;
        }
        this.rating = rating;
        slidePosition = (float) (rating - 0.1);
        isSliding = true;
        invalidate();
        if (listener != null)
            listener.onFinishRating(this, rating);
    }


    public interface OnRatingSliderChangeListener {

        void onBeginRating(CustomCommentRatingBar smileBar, int rating);

        void onFinishRating(CustomCommentRatingBar smileBar, int rating);

        void onCancelRating();
    }
}
