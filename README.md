# WaveView-master
动画+定时器+贝塞尔曲线绘制水波纹加载球

用法
layout
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#000"
    tools:context=".MainActivity">

    <com.ycy.waveview.WaveView
        android:id="@+id/waveView"
        android:layout_width="67dp"
        android:layout_height="67dp"
        android:layout_centerInParent="true"/>


</RelativeLayout>

activity

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WaveView waveView=findViewById(R.id.waveView);
        waveView.start();
        waveView.setLoadStateListener(new WaveView.LoadStateListener() {
            @Override
            public void isLoad(boolean loadState) {
                 //loadState == true 加载完成
            }
        });
    }
