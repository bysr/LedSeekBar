package hipad.ledseekbar.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import hipad.ledseekbar.R;


/**
 * 自定义带LED SeekBar
 */
public class SeekBarLED extends LinearLayout {

    private VSeekBar seekBar;
    private Context context;
    //----LED
    private ScaleLed scaleLed;
    //-----刻度
    private ScaleView scaleView;

    //监听回调
    private SeekBarChangeListener seekBarChangeListener;



    public void setEnabled(boolean enable) {
        seekBar.setEnabled(enable);
    }

    public SeekBarLED(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        initView(context);
    }

    public SeekBarLED(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        initView(context);
    }

    public SeekBarLED(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView(context);
    }

    /**
     * 更换背景资源
     *
     * @param drawId
     */
    public void setBackgroundSeekBar(int drawId) {
        seekBar.setBackgroundResource(drawId);
    }

    public void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.seekbar_led, this, false);
        scaleLed = (ScaleLed) view.findViewById(R.id.scaleLED);
        seekBar = (VSeekBar) view.findViewById(R.id.seekBar);
        scaleView = (ScaleView) view.findViewById(R.id.scaleView);
        seekBar.setMax(100);
        addView(view);
        initEvent();
    }

    /**
     * 设置隐藏刻度
     *
     * @param isShow
     */
    public void setScaleView(boolean isShow) {
        if (!isShow) {
            scaleView.setVisibility(View.GONE);
        }
    }


    /**
     * 设置LED是否显示
     *
     * @param able
     */
    public void switchLED(boolean able) {
        if (!able) {
            scaleLed.setVisibility(View.GONE);
        } else
            scaleLed.setVisibility(View.VISIBLE);

    }

    /**
     * 设置滑块，也即修改滑块
     *
     * @param drawable
     */
    public void setThumb(Drawable drawable) {

        seekBar.setThumb(drawable);

    }

    /**
     * 设置seekBar进度值
     *
     * @param MAX
     */
    public void setMAX(int MAX) {
        seekBar.setMax(MAX);
    }

    /**
     * 设置图片
     *
     * @param its
     */
    public void setConstImageIds(int[] its) {
        scaleLed.setConstImageIds(its);
    }


    /**
     * 设置每种图片数量
     */

    public void setLedNumMax(int num) {

        scaleLed.setLedNumMax(num);

    }


    /**
     * 取消seekBar进度值
     *
     * @return
     */
    public int getMAX() {
        return seekBar.getMax();
    }

    public void initEvent() {

        seekBar.setOnSeekBarChangeListener(new VSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VSeekBar VerticalBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

                ShowLED(progress);



                if (seekBarChangeListener != null) {
                    seekBarChangeListener.CallBack(SeekBarLED.this, seekBar);
                }

            }


            @Override
            public void onStartTrackingTouch(VSeekBar VerticalBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.CallStart(SeekBarLED.this, seekBar);
                }

            }

            @Override
            public void onStopTrackingTouch(VSeekBar VerticalBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.CallEnd(SeekBarLED.this, seekBar);
                }
            }
        });

        scaleView.setOnProgress(new ScaleView.OnProgressPloy() {
            @Override
            public int onProgress(double master) {
                if (seekBarChangeListener != null)
                    return seekBarChangeListener.ScalePloy(master);
                return (int) master;
            }
        });


    }

    public void ShowLED(int progress) {
        scaleLed.showLED((float) progress / getMAX());


    }

    /**
     * 单独给seekbar设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }


    public void setSeekBarChangeListener(SeekBarChangeListener seekBarChangeListener) {
        this.seekBarChangeListener = seekBarChangeListener;
    }

    public interface SeekBarChangeListener {

        void CallBack(View view, SeekBar seekBar);

        void CallEnd(View view, SeekBar seekBar);

        void CallStart(View view, SeekBar seekBar);

        int ScalePloy(double progress);


    }


}
