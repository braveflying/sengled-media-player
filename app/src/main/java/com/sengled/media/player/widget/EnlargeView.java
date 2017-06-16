package com.sengled.media.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by admin on 2017/6/9.
 */
public class EnlargeView extends View {
    private final Path mPath = new Path();
    private final Matrix matrix = new Matrix();
    private Bitmap bitmap =null;

    private static final int RADIUS = 100; // 放大镜的半径
    private static final int FACTOR = 2; // 放大倍数
    private float mCurrentX, mCurrentY;

    public EnlargeView(Context context) {
        super(context);
        init();
    }

    public EnlargeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        //setBackgroundColor(Color.GRAY);
        mPath.addCircle(RADIUS, RADIUS, RADIUS, Path.Direction.CW);
        matrix.setScale(FACTOR, FACTOR);
    }

    public void startEnlarge(Bitmap bitmap, float  posX, float posY) {
        this.bitmap = bitmap;
        this.mCurrentX = posX;
        this.mCurrentY = posY;
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        if (bitmap == null){
            return;
        }
        super.onDraw(canvas);

        // 底图
        //canvas.drawBitmap(bitmap, 0, 0, null);

        // 剪切
        canvas.translate(mCurrentX - RADIUS, 0);
        canvas.clipPath(mPath);
        canvas.drawColor(Color.WHITE);

        // 画放大后的图
        canvas.translate(RADIUS - mCurrentX * FACTOR, RADIUS - mCurrentY
                * FACTOR);

        canvas.drawBitmap(bitmap, matrix, null);
    }
}
