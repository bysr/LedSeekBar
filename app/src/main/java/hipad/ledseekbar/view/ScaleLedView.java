package hipad.ledseekbar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import hipad.ledseekbar.R;

/**
 * Created by wangyawen on 2017/7/31 0031.
 */

public class ScaleLedView extends View {

    //设置每种LED灯的显示数量，childNum总数为3*LedNum
    private int LedNumMax = 5;

    private int LedNum = 2;//LED显示数量

    //设置三种类型的图片，用于显示LED
    private int[] constImageIds = {R.mipmap.green, R.mipmap.yellow, R.mipmap.red};

    //子view的宽高
    private int childWidth = 60;
    private int childHeight = 0;

    private Paint tPaint;


    private List<ImageView> mImageViewList = new ArrayList<>();
    //显示图片集合
    private List<Integer> mImgDataList = new ArrayList<>();
    private List<Integer> oldImgDataList;


    public ScaleLedView(Context context) {
        super(context);
        initPaint();
        setLedNum(6);

    }

    public ScaleLedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPaint();
        setLedNum(6);

    }

    /**
     * 0-LEDMAX
     *
     * @param ledNum
     */
    public void setLedNum(int ledNum) {
        this.LedNum = ledNum;
        if (!mImgDataList.isEmpty()) {
            mImgDataList.clear();
        }
        for (int i = 0; i < ledNum; i++) {
            if (i < LedNumMax) {
                mImgDataList.add(constImageIds[0]);
            } else if (i >= 2 * LedNumMax) {
                mImgDataList.add(constImageIds[2]);
            } else {
                mImgDataList.add(constImageIds[1]);
            }

        }

        setImagesData(mImgDataList);
    }


    /**
     * 设置图片数据
     *
     * @param lists 显示图片集合
     */
    public void setImagesData(List<Integer> lists) {
        //新值赋给旧的
        postInvalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int rw = MeasureSpec.getSize(widthMeasureSpec);
        int rh = MeasureSpec.getSize(heightMeasureSpec);


        int totalHeight = rh - getPaddingTop() - getPaddingBottom();//实际区域的高度值
        int totalWidth = rw - getPaddingLeft() - getPaddingRight();
        childHeight = totalHeight / (3 * LedNumMax);//子view显示高度
        if (totalWidth == 0) {
            totalWidth = childWidth;
        }

        int width = MeasureSpec.makeMeasureSpec(60, MeasureSpec.AT_MOST);

//        setMeasuredDimension(totalWidth,totalHeight);
        super.onMeasure(width, heightMeasureSpec);

    }


    private void initPaint() {
        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.STROKE);
        tPaint.setColor(Color.GREEN);
        tPaint.setTextSize(12);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int top = 0, bottom;

        int viewHeight = getBottom() - getTop() - getPaddingBottom() - getPaddingTop();
        for (int i = 0; i < mImgDataList.size(); i++) {

            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), mImgDataList.get(i));

            bottom = /*getBottom()*/viewHeight - i * childHeight - getPaddingBottom();
            top = bottom - childHeight;
            canvas.drawBitmap(bitmap, 0, top, tPaint);


        }


    }
}
