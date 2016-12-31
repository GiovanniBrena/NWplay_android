package com.neverwasradio.neverwasplayer.UI.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.neverwasradio.neverwasplayer.R;

/**
 * TODO: document your custom view class.
 */
public class MenuSection extends LinearLayout {
    private String mLabel; // TODO: use a default from R.string...
    private int mBackgroundColor = Color.RED; // TODO: use a default from R.color...
    private float mDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mIcon;

    private Paint mPaint;
    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private boolean mSquare =true;


    public MenuSection(Context context) {
        super(context);
        init(null, 0);
    }

    public MenuSection(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MenuSection(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setmSquare(boolean value) {
        mSquare =value;}


    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MenuSection, defStyle, 0);

        mLabel = a.getString(
                R.styleable.MenuSection_menuSectionLabel);
        if(mLabel==null) {mLabel="titlo";}
        mBackgroundColor = a.getColor(
                R.styleable.MenuSection_menuSectionBackground,
                mBackgroundColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mDimension = a.getDimension(
                R.styleable.MenuSection_menuSectionDimension,
                mDimension);

        mSquare = a.getBoolean(
                R.styleable.MenuSection_menuSectionSquare,true);

        if (a.hasValue(R.styleable.MenuSection_menuSectionIcon)) {
            mIcon = a.getDrawable(
                    R.styleable.MenuSection_menuSectionIcon);
            mIcon.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mDimension);
        mTextPaint.setColor(mBackgroundColor);
        mTextWidth = mTextPaint.measureText(mLabel);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;


        int textAreaHeight = getHeight()/5;
        int textAreaTop = getHeight() - textAreaHeight;
        int iconMarginTB = getHeight()/8;
        int iconMarginLR = getWidth()/2 - textAreaTop/2 + iconMarginTB;


        mPaint.setColor(Color.argb(255, 217, 217, 217));
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);


        // Draw the text.
        mTextPaint.setColor(Color.argb(255, 56, 56, 56));
        canvas.drawText(mLabel,
                getWidth() / 2,
                textAreaTop + textAreaHeight/2 + mTextHeight / 2,
                mTextPaint);



        // Draw the example drawable on top of the text.
        if (mIcon != null) {
            mIcon.setBounds(iconMarginLR,iconMarginTB,
                    getWidth()-iconMarginLR, textAreaTop-iconMarginTB);
            mIcon.draw(canvas);
        }
    }

    public void setmIcon(int resId) {
        mIcon=getResources().getDrawable(resId);
    }

    public void setmLabel(String text) { mLabel=text; }
}
