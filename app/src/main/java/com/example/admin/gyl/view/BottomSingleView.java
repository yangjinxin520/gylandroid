package com.example.admin.gyl.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.ylfcf.gyl.R;


/**
 * 底部导航
 * Created by Administrator on 2018/2/1.
 */

public class BottomSingleView extends View {
    public BottomSingleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttri(context, attrs, defStyle);
        init(context);
    }

    public BottomSingleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttri(context, attrs, 0);
        init(context);
    }

    public BottomSingleView(Context context) {
        super(context);
        init(context);
    }

    private void initAttri(Context context, AttributeSet attrs, int defStyle) {
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ylf_gyl, defStyle, 0);
//         changeColor = typedArray.getColor(R.styleable.ylf_ppp_textColorP,
//         R.color.defult_text_color);
        changeColor = getResources().getColor(R.color.common_color);
        textSize = typedArray.getDimensionPixelSize(
                R.styleable.ylf_gyl_textSizeP,
                resources.getDimensionPixelSize(R.dimen.common_measure_12dp));//
        text = typedArray.getString(R.styleable.ylf_gyl_text);
        normalBitmap = ((BitmapDrawable) typedArray
                .getDrawable(R.styleable.ylf_gyl_normalBitmap)).getBitmap();
        pitchBitmap = ((BitmapDrawable) typedArray
                .getDrawable(R.styleable.ylf_gyl_pitchBitmap)).getBitmap();
        boolean alpha = typedArray.getBoolean(R.styleable.ylf_gyl_alphaP, false);
        typedArray.recycle();
        currentAlpha = alpha ? 255 : 0;
        textColor = alpha ? changeColor : getResources().getColor(
                R.color.defult_text_color);
    }

    private Bitmap normalBitmap;
    private Bitmap pitchBitmap;
    private int textColor;
    private int textSize;
    private String text;

    private void init(Context context) {
        if (normalBitmap == null || pitchBitmap == null) {
            throw new RuntimeException(
                    "please give normalBitmap  and pitchBitmap  values ");
        }
        bitmapPaint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.SANS_SERIF);

        initTextColorAnimator();
    }

    private int width;
    private int height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();

        if (srcRect == null) {
            srcRect = new Rect();
            srcRect.left = 0;
            srcRect.right = normalBitmap.getWidth();
            srcRect.top = 0;
            srcRect.bottom = normalBitmap.getHeight();
        }
        if (dstRect == null) {
            dstRect = new RectF();
            double ratio = 0.1;
            if (text == null) {
                ratio = 0.2;
                dstRect.top = (float) (height * ratio);
                dstRect.bottom = (float) (height * (1 - ratio));
            } else {
                dstRect.top = (float) (height / 8f);
                dstRect.bottom = (float) (height * 9 / 16);
            }
            int bitmapWidth = (int) (normalBitmap.getWidth()
                    * (dstRect.bottom - dstRect.top) / normalBitmap.getHeight());
            dstRect.left = (width - bitmapWidth) / 2;
            dstRect.right = dstRect.left + bitmapWidth;
        }

        if (text != null) {
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            int textWidth = rect.width();
            int textHeight = rect.height();
            textX = (int) ((width - textWidth) / 2);
            textY = (int) ((height * 0.2 - textHeight) / 2 + height * 0.8);
        }
    }

    private Rect srcRect;
    private RectF dstRect;
    private int textX;
    private int textY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (currentAlpha != 255) {
            bitmapPaint.setAlpha(255 - currentAlpha);
            canvas.drawBitmap(normalBitmap, srcRect, dstRect, bitmapPaint);
        }
        if (currentAlpha != 0) {
            bitmapPaint.setAlpha(currentAlpha);
            canvas.drawBitmap(pitchBitmap, srcRect, dstRect, bitmapPaint);
        }
        if (text != null) {
            textPaint.setColor(textColor);
            canvas.drawText(text, textX, textY, textPaint);
        }
        canvas.restore();
    }

    private Paint bitmapPaint;

    private int currentAlpha = 0;
    private Paint textPaint;

    public void setProgress(float progress) {
        if (progress > 100) {
            throw new RuntimeException("progress do not > 100");
        }
        currentAlpha = (int) (255 * progress);
        colorAnimator.setCurrentPlayTime((long) (progress * 100));
        invalidate();
    }

    private ValueAnimator colorAnimator;
    private int DEFULT_TEXT_COLOR = getResources().getColor(
            R.color.defult_text_color);
    private int changeColor;

    private void initTextColorAnimator() {
        colorAnimator = ValueAnimator.ofInt(DEFULT_TEXT_COLOR, changeColor);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textColor = (Integer) animation.getAnimatedValue();
                Log.i("bottom", textColor + " =  ");
            }
        });
        colorAnimator.setDuration(100);
        colorAnimator.setInterpolator(new LinearInterpolator());

    }
}
