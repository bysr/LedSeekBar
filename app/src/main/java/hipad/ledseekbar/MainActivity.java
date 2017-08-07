package hipad.ledseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import hipad.ledseekbar.view.SeekBarLED;

/**
 * 带刻度，LED灯的显示显示进度
 */
public class MainActivity extends AppCompatActivity {


    private SeekBarLED seekBarLED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBarLED = (SeekBarLED) findViewById(R.id.seekBar);

        seekBarLED.setSeekBarChangeListener(new SeekBarLED.SeekBarChangeListener() {
            @Override
            public void CallBack(View view, SeekBar seekBar) {

            }

            @Override
            public void CallEnd(View view, SeekBar seekBar) {

            }

            @Override
            public void CallStart(View view, SeekBar seekBar) {

            }

            @Override
            public int ScalePloy(double progress) {//设置进度策略
                return (int) progress;//返回默认值
            }
        });


    }
}
