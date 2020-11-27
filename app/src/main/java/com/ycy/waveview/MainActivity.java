package com.ycy.waveview;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


@SuppressLint("DefaultLocale")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WaveView waveView=findViewById(R.id.waveView);
        waveView.start();
        waveView.setLoadStateListener(new WaveView.LoadStateListener() {
            @Override
            public void isLoad(boolean loadState) {
                Toast.makeText(MainActivity.this,"是否加载完成 ==> " + loadState,Toast.LENGTH_SHORT).show();

                if (loadState) {
                    Toast.makeText(MainActivity.this,"再次加载" ,Toast.LENGTH_SHORT).show();
                    waveView.start();
                }
            }
        });
    }
}
