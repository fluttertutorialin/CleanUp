package com.jdkgroup.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.jdkgroup.cleanup.R;

public class VerificationCodeEditText extends android.support.v7.widget.AppCompatEditText implements VerificationAction, TextWatcher {

    private int mFigures;
    private int mVerCodeMargin;
    private int mBottomSelectedColor;
    private int mBottomNormalColor;
    private float mBottomLineHeight;
    private int mSelectedBackgroundColor;

    private OnVerificationCodeChangedListener onCodeChangedListener;
    private int mCurrentPosition = 0;
    private int mEachRectLength = 0;
    private Paint mSelectedBackgroundPaint;
    private Paint mNormalBackgroundPaint;
    private Paint mBottomSelectedPaint;
    private Paint mBottomNormalPaint;

    public VerificationCodeEditText(Context context) {
        this(context, null);
    }

    public VerificationCodeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        initPaint();
        setFocusableInTouchMode(true);
        super.addTextChangedListener(this);
    }

    private void initPaint() {
        mSelectedBackgroundPaint = new Paint();
        mSelectedBackgroundPaint.setColor(mSelectedBackgroundColor);
        mNormalBackgroundPaint = new Paint();
        mNormalBackgroundPaint.setColor(getColor(android.R.color.transparent));

        mBottomSelectedPaint = new Paint();
        mBottomNormalPaint = new Paint();
        mBottomSelectedPaint.setColor(mBottomSelectedColor);
        mBottomNormalPaint.setColor(mBottomNormalColor);
        mBottomSelectedPaint.setStrokeWidth(mBottomLineHeight);
        mBottomNormalPaint.setStrokeWidth(mBottomLineHeight);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.VerCodeEditText);
        mFigures = ta.getInteger(R.styleable.VerCodeEditText_figures, 4);
        mVerCodeMargin = (int) ta.getDimension(R.styleable.VerCodeEditText_verCodeMargin, 0);
        mBottomSelectedColor = ta.getColor(R.styleable.VerCodeEditText_bottomLineSelectedColor, getCurrentTextColor());
        mBottomNormalColor = ta.getColor(R.styleable.VerCodeEditText_bottomLineNormalColor, getColor(android.R.color.darker_gray));
        mBottomLineHeight = ta.getDimension(R.styleable.VerCodeEditText_bottomLineHeight, dp2px(5));
        mSelectedBackgroundColor = ta.getColor(R.styleable.VerCodeEditText_selectedBackgroundColor, getColor(android.R.color.darker_gray));
        ta.recycle();
    }

    @Override
    final public void setCursorVisible(boolean visible) {
        super.setCursorVisible(false);//隐藏光标的显示
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthResult = 0, heightResult = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            widthResult = widthSize;
        } else {
            widthResult = getScreenWidth(getContext());
        }
        mEachRectLength = (widthResult - (mVerCodeMargin * (mFigures - 1))) / mFigures;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            heightResult = heightSize;
        } else {
            heightResult = mEachRectLength;
        }
        setMeasuredDimension(widthResult, heightResult);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCurrentPosition = getText().length();
        int width = mEachRectLength - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        for (int i = 0; i < mFigures; i++) {
            canvas.save();
            int start = width * i + i * mVerCodeMargin;
            int end = width + start;
            if (i == mCurrentPosition) {
                canvas.drawRect(start, 0, end, height, mSelectedBackgroundPaint);
            } else {
                canvas.drawRect(start, 0, end, height, mNormalBackgroundPaint);
            }
            canvas.restore();
        }

        String value = getText().toString();
        for (int i = 0; i < value.length(); i++) {
            canvas.save();
            int start = width * i + i * mVerCodeMargin;
            float x = start + width / 2;
            TextPaint paint = getPaint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(getCurrentTextColor());
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2
                    - fontMetrics.top;
            canvas.drawText(String.valueOf(value.charAt(i)), x, baseline, paint);
            canvas.restore();
        }

        for (int i = 0; i < mFigures; i++) {
            canvas.save();
            float lineY = height - mBottomLineHeight / 2;
            int start = width * i + i * mVerCodeMargin;
            int end = width + start;
            if (i < mCurrentPosition) {
                canvas.drawLine(start, lineY, end, lineY, mBottomSelectedPaint);
            } else {
                canvas.drawLine(start, lineY, end, lineY, mBottomNormalPaint);
            }
            canvas.restore();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mCurrentPosition = getText().length();
        postInvalidate();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCurrentPosition = getText().length();
        postInvalidate();
        if (onCodeChangedListener != null) {
            onCodeChangedListener.onVerCodeChanged(getText(), start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        mCurrentPosition = getText().length();
        postInvalidate();
        if (getText().length() == mFigures) {
            if (onCodeChangedListener != null) {
                onCodeChangedListener.onInputCompleted(getText());
            }
        } else if (getText().length() > mFigures) {
            getText().delete(mFigures, getText().length());
        }
    }

    @Override
    public void setFigures(int figures) {
        mFigures = figures;
        postInvalidate();
    }

    @Override
    public void setVerCodeMargin(int margin) {
        mVerCodeMargin = margin;
        postInvalidate();
    }

    @Override
    public void setBottomSelectedColor(@ColorRes int bottomSelectedColor) {
        mBottomSelectedColor = getColor(bottomSelectedColor);
        postInvalidate();
    }

    @Override
    public void setBottomNormalColor(@ColorRes int bottomNormalColor) {
        mBottomSelectedColor = getColor(bottomNormalColor);
        postInvalidate();
    }

    @Override
    public void setSelectedBackgroundColor(@ColorRes int selectedBackground) {
        mSelectedBackgroundColor = getColor(selectedBackground);
        postInvalidate();
    }

    @Override
    public void setBottomLineHeight(int bottomLineHeight) {
        this.mBottomLineHeight = bottomLineHeight;
        postInvalidate();
    }

    @Override
    public void setOnVerificationCodeChangedListener(OnVerificationCodeChangedListener listener) {
        this.onCodeChangedListener = listener;
    }

    private int getColor(@ColorRes int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
