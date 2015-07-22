package cn.thu.guohao.simplechat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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
    private Bitmap mLineBitmap;
    private Paint mTextPaint;
    private Rect mIconRect;
    private Rect mTextRect;

    private float mAlpha = 1.0f;
    private Bitmap mColorBitmap;
    private Canvas mColorCanvas;
    private Paint mIconPaint;
    final static private PorterDuffXfermode XFERMODE = new PorterDuffXfermode(
            PorterDuff.Mode.DST_IN);

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

        Drawable mIcon = a.getDrawable(
                R.styleable.PagerIcon_pi_icon);
        BitmapDrawable bd = (BitmapDrawable)mIcon;
        if (bd != null)
            mLineBitmap = bd.getBitmap();

        a.recycle();

        mTextRect = new Rect();
        mIconRect = new Rect();
        // Set up a default TextPaint object
        mTextPaint = new TextPaint();

        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(Color.parseColor("#ff666666"));
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);

        mIconPaint = new Paint();
        mIconPaint.setColor(mColor);
        mIconPaint.setAntiAlias(true);
        mIconPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int iconLength = Math.min(getMeasuredWidth() - paddingLeft - paddingRight,
                getMeasuredHeight() - paddingTop - paddingBottom - mTextRect.height());
        int left = (getMeasuredWidth() - iconLength) / 2;
        int top = (getMeasuredHeight() - iconLength - mTextRect.height()) / 2;
        mIconRect.set(left, top, left + iconLength, top + iconLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int alpha = (int) Math.ceil(mAlpha * 255);
        if (mColorBitmap == null) {
            mColorBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            mColorCanvas = new Canvas(mColorBitmap);
        }

        //DST
        mIconPaint.setAlpha(alpha);
        mColorCanvas.drawRect(mIconRect, mIconPaint);
        //SRC
        mIconPaint.setXfermode(XFERMODE); // DST_IN
        mIconPaint.setAlpha(255);
        mColorCanvas.drawBitmap(mLineBitmap, null, mIconRect, mIconPaint);

        // color
        canvas.drawBitmap(mColorBitmap, 0, 0, null);
        // bounder
        canvas.drawBitmap(mLineBitmap, null, mIconRect, null);
        // text
        int x = getMeasuredWidth() / 2;
        int y = mIconRect.bottom + mTextRect.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

}
