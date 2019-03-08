package com.example.weizhongbin.lunbo;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private RadioGroup group;
    //图片资源
    private int[] imageIds = {
            R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,R.mipmap.img4,R.mipmap.img5,R.mipmap.img6,R.mipmap.img7,R.mipmap.img8,R.mipmap.img9,R.mipmap.img10,
            R.mipmap.img11,R.mipmap.img12,R.mipmap.img13,R.mipmap.img14,R.mipmap.img15,R.mipmap.img16,R.mipmap.img17,R.mipmap.img18,R.mipmap.img19,R.mipmap.img20,
            R.mipmap.img21,R.mipmap.img22,R.mipmap.img23,R.mipmap.img24,R.mipmap.img25,R.mipmap.img26,R.mipmap.img27,R.mipmap.img28,R.mipmap.img29,R.mipmap.img30,
    };
    //存放图片的数组
    private List<ImageView> mList;
    //当前索引位置以及上一个索引位置
    private int index = 0,preIndex = 0;
    //是否需要轮播标志
    private boolean isContinue = true;
    //定时器，用于实现轮播
    private Timer timer;
    Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    index++;
                    System.out.println("==========index: "+index);
                    viewPager.setCurrentItem(index);
            }
        }
    };
    private Button btnStart;
    private Button btnStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        group = (RadioGroup) findViewById(R.id.group);
        mList = new ArrayList<>();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setOnTouchListener(onTouchListener);
        btnStart = (Button) findViewById(R.id.start);
        btnStop = (Button) findViewById(R.id.stop);
        initRadioButton(imageIds.length);
        btnStop.setEnabled(false);
    }
    public void click(View view){
        switch (view.getId()){
            case R.id.start:
                timer = new Timer();//创建Timer对象
                //执行定时任务
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //首先判断是否需要轮播，是的话我们才发消息
                        if (isContinue) {
                            mHandler.sendEmptyMessage(1);
                        }
                    }
                },2000,100);//延迟2秒，每隔2秒发一次消息
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                break;
            case R.id.stop:
                timer.cancel();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                break;
        }
    }

    /**
     * 根据图片个数初始化按钮
     * @param length
     */
    private void initRadioButton(int length) {
        for(int i = 0;i<length;i++){
            ImageView imageview = new ImageView(this);
            imageview.setImageResource(R.drawable.rg_selector);//设置背景选择器
            imageview.setPadding(20,0,0,0);//设置每个按钮之间的间距
            //将按钮依次添加到RadioGroup中
            group.addView(imageview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //默认选中第一个按钮，因为默认显示第一张图片
            group.getChildAt(0).setEnabled(false);
        }
    }

    /**
     * 根据当前触摸事件判断是否要轮播
     */
    View.OnTouchListener onTouchListener  = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                //手指按下和划动的时候停止图片的轮播
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isContinue = false;
                    break;
                default:
                    isContinue = true;
            }
            return false;
        }
    };
    /**
     *根据当前选中的页面设置按钮的选中
     */
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            index = position;//当前位置赋值给索引
            setCurrentDot(index%imageIds.length);
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 设置对应位置按钮的状态
     * @param i 当前位置
     */
    private void setCurrentDot(int i) {
        if(group.getChildAt(i)!=null){
            group.getChildAt(i).setEnabled(false);//当前按钮选中
        }
        if(group.getChildAt(preIndex)!=null){
            group.getChildAt(preIndex).setEnabled(true);//上一个取消选中
            preIndex = i;//当前位置变为上一个，继续下次轮播
        }
    }
    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            //返回一个比较大的值，目的是为了实现无限轮播
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position%imageIds.length;
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource(imageIds[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);
            mList.add(imageView);
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }
    };
}
