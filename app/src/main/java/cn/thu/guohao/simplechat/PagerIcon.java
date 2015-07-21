package cn.thu.guohao.simplechat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


/**
 * Viewpager icon with text.
 * Can change color.
 */
public class PagerIcon extends View {
    private String mText = getResources().getString(R.string.tab_default);
    private int mColor = Color.RED;
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            12, getResources().getDisplayMetrics());
    private Drawable mIcon = getResources().getDrawable(R.drawable.tab_chats);

    private Paint mTextPaint;
    private Rect mTextRect;
    private Rect mIconRect;
    float mTextWidth;
    float mTextHeight;

    public PagerIcon(Context context) {
        super(context);
        init(null, 0);
    }

    public PagerIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PagerIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PagerIcon, defStyle, 0);

        mText = a.getString(
                R.styleable.PagerIcon_pi_text);
        mColor = a.getColor(
                R.styleable.PagerIcon_pi_color,
                mColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mTextSize = a.getDimension(
                R.styleable.PagerIcon_pi_text_size,
                mTextSize);

        mIcon = a.getDrawable(
                R.styleable.PagerIcon_pi_icon);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.parseColor("#ff666666"));

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColor);
        mTextWidth = mTextPaint.measureText(mText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mText,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mIcon != null) {
            mIcon.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mIcon.draw(canvas);
        }
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param Text The example string attribute value to use.
     */
    public void setText(String Text) {
        mText = Text;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param Color The example color attribute value to use.
     */
    public void setColor(int Color) {
        mColor = Color;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param TextSize The example dimension attribute value to use.
     */
    public void setTextSize(float TextSize) {
        mTextSize = TextSize;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getIcon() {
        return mIcon;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param Icon The example drawable attribute value to use.
     */
    public void setIcon(Drawable Icon) {
        mIcon = Icon;
    }
}
