package hipad.ledseekbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import hipad.ledseekbar.R;

import static android.content.ContentValues.TAG;

/**
 * Created by wangyawen on 2017/7/25 0025.
 */

public class ScaleView extends View {
    //滑块
    private Bitmap mProgressHintBG;
    //滑块高度
    private float BGlength;
    //刻度上显示的文字
    private CharSequence[] mTextArray;
    //设置一个文字画笔
    private Paint tPaint = new Paint();
    //刻度文字大小
    //Scale text and prompt text size
    private int mTextSize;
    //刻度文字颜色
    private int mTextColor;
    /*刻度文字宽度*/
    private float widthNeeded;

    //真实的最大值和最小值[x坐标值]
    //True maximum and minimum values
    private float mMin, mMax;
    //刻度与进度条间的间距
    //The spacing between the scale and the progress bar
    private int textPadding;


    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        //得到刻度值
        mTextArray = t.getTextArray(R.styleable.ScaleView_markTextArrays);
        /*刻度值颜色*/
        mTextColor = t.getColor(R.styleable.ScaleView_markTextColor, 0);
        /*刻度值文字大小*/
        mTextSize = (int) t.getDimension(R.styleable.ScaleView_markTextSize, dp2px(context, 8));
        mMin = t.getFloat(R.styleable.ScaleView_minS, 0);
        mMax = t.getFloat(R.styleable.ScaleView_maxS, 100);
        textPadding = (int) t.getDimension(R.styleable.ScaleView_textPaddingS, dp2px(context, 7));
        initPaint();
        initBitmap();


    }

    /**
     * 设置获取滑块bitmap
     */
    private void initBitmap() {

        mProgressHintBG = BitmapFactory.decodeResource(getResources(), R.mipmap.tuiniu);
        BGlength = mProgressHintBG.getWidth();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        widthNeeded = tPaint.measureText("100");//-infinite
        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();

        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = MeasureSpec.makeMeasureSpec((int) widthNeeded, MeasureSpec.AT_MOST);
        } else {
            width = MeasureSpec.makeMeasureSpec(
                    (int) widthNeeded, MeasureSpec.EXACTLY);
        }
        super.onMeasure(width, heightMeasureSpec);


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: w=" + w + ",h=" + h + ",oldw=" + oldw + ",oldh=" + oldh);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewHeight = getBottom() - getTop() - getPaddingBottom() - getPaddingTop();
        Log.d(TAG, "onDraw: bottom=" + getBottom() + ",top=" + getTop());
        if (mTextArray != null) {
            for (int i = 0; i < mTextArray.length; i++) {
                //要显示的文字
                final String textDraw = mTextArray[i].toString();
                float num = Float.parseFloat(textDraw);/*显示的值，需要转化成标准的进度值*/
                //计算出x，y坐标，涉及到计算位置
                float y = viewHeight - viewHeight * (Progress(num) - mMin) / (mMax - mMin);
                canvas.drawText(textDraw, 0, y, tPaint);

            }


        }

    }


    /**
     * 对于自定义非线性进度，可以从这儿考虑
     *
     * @param master y坐标显示值
     * @return
     */
    public int Progress(double master) {
        //1.先转化为实际的刻度值，即发送数据的刻度值【0-116】

        if (listener != null)
            return listener.onProgress(master);
        return (int) master;

    }

    public interface OnProgressPloy {
        int onProgress(double master);
    }

    OnProgressPloy listener;

    public void setOnProgress(OnProgressPloy l) {
        this.listener = l;
    }


    private void initPaint() {
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(mTextColor);
        tPaint.setTextSize(mTextSize);

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
