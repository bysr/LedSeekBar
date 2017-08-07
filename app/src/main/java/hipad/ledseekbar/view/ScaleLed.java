package hipad.ledseekbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;



import java.util.ArrayList;
import java.util.List;

import hipad.ledseekbar.R;

/**
 * Created by wangyawen on 2017/7/31 0031.
 */

public class ScaleLed extends ViewGroup{

    //设置每种LED灯的显示数量，childNum总数为3*LedNum
    private int LedNumMax;

    private int LedNum;//LED显示数量

    //设置三种类型的图片，用于显示LED
    private int[] constImageIds={R.mipmap.green,R.mipmap.yellow,R.mipmap.red};

    //子view的宽高
    private int childWidth = 0;
    private int childHeight = 0;
    /**
     * 图片大小
     */
    private int childViewwidth=0;


    private List<ImageView> mImageViewList = new ArrayList<>();
    //显示图片集合
    private List<Integer> mImgDataList=new ArrayList<>();
    private List<Integer> oldImgDataList;


    public ScaleLed(Context context) {
        this(context,null);
    }

    public ScaleLed(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ScaleLed);
        LedNumMax=t.getInteger(R.styleable.ScaleLed_showNumber,6);
        LedNum=t.getInteger(R.styleable.ScaleLed_showDefault,0);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), constImageIds[0]);
        childViewwidth=bitmap.getWidth();
        setLedNum(LedNum);

    }


    public void setLedNumMax(int ledNumMax) {
        LedNumMax = ledNumMax;
    }

    /**
     * 设置各个种类样式
     * @param constImageIds
     */
    public void setConstImageIds(int[] constImageIds) {
        this.constImageIds = constImageIds;
    }

    /**
     * 外界设置入口
     * @param percent 百分比
     */
    public void showLED(float percent){
        this.LedNum=Math.round(percent*constImageIds.length*LedNumMax);
        setLedNum(LedNum);

    }

    /**
     *
     * @param ledNum 显示数量
     */
    public void setLedNum(int ledNum) {
        this.LedNum=ledNum;
        if (!mImgDataList.isEmpty()){
            mImgDataList.clear();
        }


        //目前总共三种图片，有太多可以在这儿进行扩展
        for (int i = 0; i <ledNum ; i++) {
            if (i<LedNumMax){
                mImgDataList.add(constImageIds[0]);
            }else if (i>=2*LedNumMax){
                mImgDataList.add(constImageIds[2]);
            }else{
                mImgDataList.add(constImageIds[1]);
            }

        }

        setImagesData(mImgDataList);
    }


    /**
     * 设置图片数据
     *
     * @param lists    显示图片集合
     */
    public void setImagesData(List<Integer> lists) {
        int newShowCount = lists.size();

        if (oldImgDataList == null) {//第一次添加图片
            oldImgDataList=new ArrayList<>();
            int i = 0;
            while (i < newShowCount) {
                ImageView iv = getImageView(i);
                if (iv == null) {
                    return;
                }
                addView(iv, generateDefaultLayoutParams());
                i++;
            }
        } else {
            int oldShowCount = oldImgDataList.size();
            if (oldShowCount > newShowCount) {
                removeViews(newShowCount, oldShowCount - newShowCount);
            } else if (oldShowCount < newShowCount) {
                for (int i = oldShowCount; i < newShowCount; i++) {
                    ImageView iv = getImageView(i);
                    if (iv == null) {
                        return;
                    }
                    addView(iv, generateDefaultLayoutParams());
                }
            }
        }

        //新值赋给旧的
        oldImgDataList.clear();
        oldImgDataList.addAll(lists);
        requestLayout();

    }



    /**
     * 获得 ImageView
     * 保证了 ImageView 的重用
     *
     * @param position 位置
     */
    private ImageView getImageView(final int position) {
        if (position < mImageViewList.size()) {
            return mImageViewList.get(position);
        } else {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            mImageViewList.add(imageView);
            return imageView;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int rw = MeasureSpec.getSize(widthMeasureSpec);
        int rh = MeasureSpec.getSize(heightMeasureSpec);


        int totalHeight = rh - getPaddingTop() - getPaddingBottom();//实际区域的高度值
        int totalWidth=rw-getPaddingLeft()-getPaddingRight();
        childHeight=totalHeight/(constImageIds.length*LedNumMax);//子view显示高度
        if (totalWidth==0){
            if (childWidth!=0){
                totalWidth=childWidth;}
            else{
           /*wrap_content情况下直接测量图片给出显示宽度*/

            totalWidth=childViewwidth+5;}
        }

        setMeasuredDimension(totalWidth,totalHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int num=getChildCount();
        int  left=0,top=0,right=0,bottom=0;
        for (int i = 0; i <num ; i++) {

            ImageView childrenView = (ImageView) getChildAt(i);
            //设置每一个子view的高度大小
            //1 控件本生的长度

            top=getHeight()-(i+1)*childHeight;
            right=r-l;
            bottom=top+childHeight;

            left=right-childViewwidth+10;


            //这儿都是相对位置

            /**
             * left: 0
             * top:
             * right:
             * bottom:
             */


            childrenView.layout(left, top, right, bottom);

            childrenView.setImageResource(oldImgDataList.get(i));


        }
        }

}
