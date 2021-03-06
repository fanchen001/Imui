package com.fanchen.message.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Arrays;

import com.fanchen.ui.R;

public class ShapeImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint;
    private Shape mShape;

    private float mRadius;

    private Path mCorners = new Path();

    private Bitmap mBitmap;

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShapeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MessageListView);
            mRadius = a.getDimensionPixelSize(R.styleable.MessageListView_photoMessageRadius, context.getResources().getDimensionPixelSize(R.dimen.aurora_radius_photo_message));
            a.recycle();
        }
        mPaint = new Paint();
        if (Build.VERSION.SDK_INT >= 28) {
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mShape == null) {
            float[] radius = new float[8];
            Arrays.fill(radius, mRadius);
            mShape = new RoundRectShape(radius, null, null);
        }
        mShape.resize(getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= 28) {
            Drawable drawable = getDrawable();
            if(drawable instanceof BitmapDrawable){
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if(mBitmap != bitmap){
                    mBitmap = bitmap;
                    Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    mPaint.setShader(shader);
                    float[] radius = new float[8];
                    Arrays.fill(radius, mRadius);
                    mCorners.addRoundRect(new RectF(0,0,getMeasuredWidth(),getMeasuredHeight()), radius, Path.Direction.CW);
                    Log.e("ShapeImageView","new bitmap draw");
                }else{
                    Log.e("ShapeImageView","bitmap draw ago");
                }
                canvas.drawPath(mCorners, mPaint);
            }else{
                super.onDraw(canvas);
            }
        } else {
            int saveCount = canvas.getSaveCount();
            canvas.save();
            super.onDraw(canvas);
            if (mShape != null) {
                mShape.draw(canvas, mPaint);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    public void setBorderRadius(int radius) {
        this.mRadius = radius;
        invalidate();
    }

}
