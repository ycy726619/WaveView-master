package com.ycy.waveview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.graphics.Shader;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.ycy.waveview.util.ArithmeticUtils;
import com.ycy.waveview.util.MeasureUtils;


/**
 * 水波纹加载球
 * 2020/11/19
 * ycy
 */
public class WaveView extends View implements View.OnClickListener {
    private static final String TAG = "WaveView";
    private final float mCircleOutWidth = 4f;//圆环画笔宽度
    private Paint mPaint;
    private float mOutStrokeWidth = 20;//外圈宽度
    private Paint mCirclePaint;//内圆画笔
    private Paint mWaterCirclePaint;//内圆画笔加载完成
    private Paint mOutCirclePaint;//外圆画笔
    private Paint mCircleOutPaint; //圆环画笔
    private int mCircleColor = Color.parseColor("#181F2D");//背景内圆颜色
    private int mOutStrokeColor = Color.parseColor("#B8D86A");//外圆颜色
    private int mWaterColor = Color.parseColor("#04DD98");//水波颜色
    private int mCircleOutColor = Color.parseColor("#FFFFFF");//圆环
    private float mWidth;
    private float mHeight;
    private int mWaveHeight;
    private int mWaveDx;
    int mOutRadius = 0;//外圆半径
    int mRadius = 0;//内圆半径
    private int dx;
    private Point mCenterPoint; //圆的中心点
    private float currentHeight = 0;
    private CountDownTimer countDownTimer;
    private ValueAnimator valueAnimator;
    private LoadStateListener loadStateListener;

    private long millisInFuture = 1000 * 60;
    private long countDownInterval = 50;
    /**
     * 是否加载完毕
     */
    private boolean isLoad = false;


    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setLoadStateListener(LoadStateListener loadStateListener) {
        this.loadStateListener = loadStateListener;
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mPaint.setDither(true);
        mPaint.setColor(mWaterColor);
        mPaint.setStyle(Paint.Style.FILL);

        mWaveDx = 165;
        //水波的高度
        mWaveHeight = 3;
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(false);

        mCircleOutPaint = new Paint();
        mCircleOutPaint.setColor(mCircleOutColor);
        mCircleOutPaint.setStrokeWidth(mCircleOutWidth);
        mCircleOutPaint.setStyle(Paint.Style.STROKE);
        mCircleOutPaint.setAntiAlias(false);


        mWaterCirclePaint = new Paint();
        mWaterCirclePaint.setColor(mWaterColor);
        mWaterCirclePaint.setStyle(Paint.Style.FILL);
        mWaterCirclePaint.setAntiAlias(false);

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setColor(mOutStrokeColor);
        mOutCirclePaint.setStyle(Paint.Style.FILL);
        mOutCirclePaint.setAntiAlias(false);
        LinearGradient gradient = new LinearGradient(mWidth / 2, 0, mWidth / 2, mHeight, new int[]{Color.parseColor("#00FFC9"), Color.parseColor("#00F7FF")}, null, Shader.TileMode.MIRROR);
        mOutCirclePaint.setShader(gradient);

        valueAnimator = ValueAnimator.ofInt(0, mWaveDx);
        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                final double height = mHeight - 2 - mOutStrokeWidth;
                final double heightDiff = millisInFuture / countDownInterval;
                currentHeight =
                        Float.parseFloat(ArithmeticUtils.sub(
                                String.valueOf(currentHeight),
                                String.valueOf(ArithmeticUtils.div(
                                        height,
                                        heightDiff
                                )),5));

                if (currentHeight <= 0) {
                    countDownTimer.onFinish();
                    countDownTimer.cancel();
                }

            }

            @Override
            public void onFinish() {
                isLoad = true;
                valueAnimator.cancel();
                invalidate();
                Log.e(TAG, "onFinish: currentHeight = "+currentHeight );
            }
        };
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureUtils.measureView(widthMeasureSpec, widthMeasureSpec);
        mHeight = MeasureUtils.measureView(heightMeasureSpec, heightMeasureSpec);

        mOutRadius = (int) (mWidth / 2);
        mRadius = (int) (0.5 * (mWidth - 2 - mOutStrokeWidth));
        currentHeight = mWidth - 2 - mOutStrokeWidth;
        mCenterPoint = new Point((int) (mWidth / 2), (int) (mHeight / 2));

    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas);
    }


    private void drawWave(Canvas canvas) {
        if (canvas == null)
            return;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mWaterCirclePaint.setColor(mCircleColor);
        if (isLoad) {
            //加载完成操作
            mWaterCirclePaint.setColor(mWaterColor);
            mOutCirclePaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));//设置发光
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mOutRadius, mOutCirclePaint);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mOutRadius - 8, mCircleOutPaint);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadius, mWaterCirclePaint);
            return;
        }
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mOutRadius, mOutCirclePaint);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mOutRadius - 8, mCircleOutPaint);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadius, mWaterCirclePaint);
        Path path = new Path();
        path.reset();
        path.moveTo(-mWaveDx + dx, currentHeight);
        //绘制贝塞尔
        for (int i = -mWaveDx; i < mWaveDx; i += (mWaveDx / 2)) {
            path.rQuadTo(mWaveDx / 8, -mWaveHeight, mWaveDx / 4, 0);
            path.rQuadTo(mWaveDx / 8, mWaveHeight, mWaveDx / 4, 0);
        }
        path.lineTo(mWidth, mHeight);
        path.lineTo(0, mHeight);
        path.close();
        Path pc = new Path();
        pc.addCircle(mCenterPoint.x, mCenterPoint.y, mRadius, Path.Direction.CCW);
        canvas.clipPath(pc, Region.Op.INTERSECT);//切割画布
        canvas.drawPath(path, mPaint);

    }



    public void start() {
        requestLayout();

        isLoad = false;
        mOutCirclePaint.setMaskFilter(null);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //水平方向的偏移量
                dx = (int) animation.getAnimatedValue();
                invalidate();

            }

        });
        valueAnimator.start();
        countDownTimer.start();
    }

    @Override
    public void onClick(View v) {
        if (null != loadStateListener) {
            loadStateListener.isLoad(isLoad);
        }
    }

    public interface LoadStateListener {
        /**
         * 加载状态回调
         *
         * @param loadState true 加载完成 | false 正在加载
         */
        void isLoad(boolean loadState);
    }


}
