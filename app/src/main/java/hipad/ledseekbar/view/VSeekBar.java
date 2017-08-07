package hipad.ledseekbar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.SeekBar;

/**
 * 自定义竖直摆放seekbar，可滑动，双击改变滑块位置【0.2s内连续双击】
 */
public class VSeekBar extends SeekBar {
    private float oldx, oldy;
    float mx;
    float my;
    private boolean mIsDragging;
    private float mTouchDownY;
    private int mScaledTouchSlop;
    private boolean isInScrollingContainer = false;
    private final String TAG = VSeekBar.class.getSimpleName();

    public interface OnSeekBarChangeListener {
        void onProgressChanged(VSeekBar VerticalBar, int progress, boolean fromUser);

        void onStartTrackingTouch(VSeekBar VerticalBar);

        void onStopTrackingTouch(VSeekBar VerticalBar);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    public boolean isInScrollingContainer() {
        return isInScrollingContainer;
    }

    public void setInScrollingContainer(boolean isInScrollingContainer) {
        this.isInScrollingContainer = isInScrollingContainer;
    }

    /**
     * on touch, this offset plus the scaled value from the position of the
     * touch will form the progress value. Usually 0.
     */
    float mTouchProgressOffset;

    public VSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    public VSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VSeekBar(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    //记录用户首次按下的时间
    private long firstTime = 0;
    //可操作；
    private boolean longTime = false;
    //设置一个保护，用户处理过界坐标
    private long oldy_min, old_max;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        float _y = event.getY();
        //设置一个保护，用户处理过界坐标

//        float _yy = event.getRawY();
        if (oldy < 0) {
            oldy = 0;
        }
        if (oldy > getHeight()) {
            oldy = getHeight();
        }


        int h = getMax() - (int) (getMax() * _y / getHeight());// 现在的刻度值
        int h2 = getMax() - (int) (getMax() * oldy / getHeight());// 之前的刻度值
//        Log.d(TAG, "onTouchEvent,,,,: _y=" + _y + ",oldy=" + oldy + ",h=" + h + ",h2=" + h2);
        if (oldx == 0 && oldy == 0) {
            /* int */
            h2 = getProgress();
        }
        float ky = oldy;
        oldy = _y;


        //每次点击的时候当前值会变成之前的旧值，这个时候加一个判断，只有在规定时间之内，才能够赋值，负责没有效果


        Log.d(TAG, "onTouchEvent: _y=" + _y + ",oldy=" + oldy + ",h=" + h + ",h2=" + h2);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                if (System.currentTimeMillis() - firstTime < 200) {//old需要恢复到之前的数据
//                    longTime = true;
                    h = getMax() - (int) (getMax() * _y / getHeight());// 现在的刻度值
                    h2 = getMax() - (int) (getMax() * my / getHeight());// 之前的刻度值
                    Log.d(TAG, "onTouchEvent: xxxx<500");

                } else {
                    oldy = ky;
                    Log.d(TAG, "onTouchEvent: xxxx>500");

                }
                my = _y;
                firstTime = System.currentTimeMillis();
                if (Math.abs(h - h2) > 200) {
                    Log.d(TAG, "onTouchEvent: h=" + h + ", h2=" + h2 + "," + " h-h2=" + (h - h2));
                    //如果点击的位置是0或者最大附近位置
                    return false;
                }


                if (isInScrollingContainer()) {
                    mTouchDownY = event.getY();
                } else {
                    setPressed(true);
                    invalidate();
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    attemptClaimDrag();
                    onSizeChanged(getWidth(), getHeight(), 0, 0);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    trackTouchEvent(event);
                } else {
                    final float y = event.getY();
                    if (Math.abs(y - mTouchDownY) > mScaledTouchSlop) {
                        setPressed(true);

                        invalidate();
                        onStartTrackingTouch();
                        trackTouchEvent(event);
                        attemptClaimDrag();

                    }
                }
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should
                    // be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();

                }
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                invalidate();
                break;
        }
        return true;

    }

    private void trackTouchEvent(MotionEvent event) {
        final int height = getHeight();
        final int top = getPaddingTop();
        final int bottom = getPaddingBottom();
        final int available = height - top - bottom;

        int y = (int) event.getY();

        float scale;
        float progress = 0;

        if (y > height - bottom) {
            scale = 0.0f;
        } else if (y < top) {
            scale = 1.0f;
        } else {
            scale = 1 - (float) (y) / (float) available;
            progress = mTouchProgressOffset;
        }

        final int max = getMax();
        progress += scale * max;

        setProgress((int) progress);

    }

    /**
     * This is called when the user has started touching this widget.
     */
    public void onStartTrackingTouch() {
        mIsDragging = true;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    public void onStopTrackingTouch() {
        mIsDragging = false;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    private void attemptClaimDrag() {
        ViewParent p = getParent();
        if (p != null) {
            p.requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, getProgress(), isPressed());
        }
    }

}
