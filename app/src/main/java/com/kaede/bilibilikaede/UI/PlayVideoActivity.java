package com.kaede.bilibilikaede.UI;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaede.bilibilikaede.API.APIService;
import com.kaede.bilibilikaede.Domain.Video.Video;
import com.kaede.bilibilikaede.Domain.Video.VideoDownloadUrl;
import com.kaede.bilibilikaede.Fragment.Play.VideoCommentFragment;
import com.kaede.bilibilikaede.Fragment.Play.VideoProfileFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.RxBus;
import com.kaede.bilibilikaede.RxBus.VideoEvent.PassCidEvent;
import com.kaede.bilibilikaede.RxBus.VideoEvent.PassTitleEvent;
import com.kaede.bilibilikaede.Utils.Constant;
import com.kaede.bilibilikaede.Utils.SizeUtils;
import com.kaede.bilibilikaede.View.media.AndroidMediaController;
import com.kaede.bilibilikaede.View.media.IjkVideoView;
import com.squareup.picasso.Picasso;


import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.functions.Action1;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayVideoActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.play_video_back) ImageView back;
    @InjectView(R.id.av_number) TextView avNumer;
    @InjectView(R.id.tabs) TabLayout tabs;
    @InjectView(R.id.video_view) IjkVideoView videoView;
    @InjectView(R.id.video_banner) ImageView videoBanner;
    @InjectView(R.id.danmaView) DanmakuView danmaView;
    @InjectView(R.id.viewpager_play_video) ViewPager viewPager;
    @InjectView(R.id.appbar_play_video) AppBarLayout appBarLayout;
    @InjectView(R.id.tabContainer) LinearLayout tabContainer;
    @InjectView(R.id.fab_play_video) FloatingActionButton fab;
    @InjectView(R.id.root) CoordinatorLayout root;
    @InjectView(R.id.fullscreenContainer) FrameLayout fullscreenContainer;
    @InjectView(R.id.colorInflater) CircleImageView colorInflater;
    private RxBus rxBus;

    private String[] titles = {"简介", "评论"};//评论是动态的...

    private DanmakuContext mContext;
    private BaseDanmakuParser parser;
    private boolean onBackPressed = false;
    private Video mVideo;//储存了正在播放中剧集的信息

    private VideoProfileFragment mProfileFragment;
    private VideoCommentFragment mCommentFragment;

    private int mCurrentVideoOrder = 0; //当前正在播放的视频段(一般6分钟一段)
    private int mDurationOffset = 0;
    public static int POSSIBLE_OFFSET = 500;//弹幕容许的前后延迟

    private boolean isDanmaPrepared = false;
    private boolean isVideoPrepared = false;
    private boolean isBuffering = false;
    private boolean isDanmaShutdown = false; //是否人为关闭了弹幕
    private boolean isFullScreen = false;
    private boolean isToolbarShowing = true;

    private HideToolbarWorker mHideToolbarWorker = new HideToolbarWorker();
    private ShowHideHandler mHandler = new ShowHideHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        ButterKnife.inject(this);

        init();
    }

    private void init(){
        setSupportActionBar(toolbar);
        mProfileFragment = new VideoProfileFragment();
        mCommentFragment = new VideoCommentFragment();
        PlayPagerAdapter mAdapter = new PlayPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Picasso.with(getBaseContext()).load(getIntent().getStringExtra("picPath")).placeholder(R.drawable.bili_default_image_tv).error(R.drawable.video_pic_load_error).into(videoBanner);
        setToolbarTitle("av"+getIntent().getStringExtra("aid"));
        onRxBuxEvent();

        //播放器的初始化
        AndroidMediaController mediaController = new AndroidMediaController(this,false);//不使用快进
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        videoView.setMediaController(mediaController);
        videoView.setDanmakuView(danmaView);

        //弹幕View的初始化
        //设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); //滚动弹幕最大显示5行
        //设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(0.8f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        videoBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPlayingAnimation();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPlayingAnimation();
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!isToolbarShowing) {
                            toolbar.setVisibility(View.VISIBLE);
                            mHandler.postDelayed(mHideToolbarWorker,3000);
                            isToolbarShowing = true;
                        }else {
                            mHandler.removeCallbacks(mHideToolbarWorker);
                            toolbar.setVisibility(View.INVISIBLE);
                            isToolbarShowing = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isToolbarShowing) {
                            mHandler.removeCallbacks(mHideToolbarWorker);
                            mHandler.postDelayed(mHideToolbarWorker, 3000);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        syncDanmakuWithVideo();
        initFragmentData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initFragmentData(){
        Bundle cidArgs = new Bundle();
        cidArgs.putString("aid",getIntent().getStringExtra("aid"));
        mProfileFragment.setArguments(cidArgs);
        mCommentFragment.setArguments(cidArgs);

    }

    private void performPlayingAnimation(){
        appBarLayout.setExpanded(true);
        appBarLayout.scrollTo(0,0);
        int targetDp = SizeUtils.dp2px(this, 35);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,0,Animation.ABSOLUTE,0,Animation.ABSOLUTE,0,Animation.ABSOLUTE,-targetDp);
        translateAnimation.setDuration(200);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        translateAnimation.setFillAfter(true);

        final ScaleAnimation scaleAnimation = new ScaleAnimation(1,20,1,20, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(500);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                root.removeView(fab);
                colorInflater.setVisibility(View.VISIBLE);
                colorInflater.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                colorInflater.setVisibility(View.GONE);
                videoBanner.setVisibility(View.GONE);
                toolbar.setVisibility(View.INVISIBLE);
                isToolbarShowing = false;
                videoView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fab.startAnimation(translateAnimation);
    }

    private void syncDanmakuWithVideo(){
     videoView.setOnBufferingStateChangeListener(new IMediaPlayer.OnBufferingStateChangeListener() {
         @Override
         public void onBufferingStateChange(IMediaPlayer mp, Message msg) {
             //msg.arg1 缓冲总进度 单位为毫秒
             //msg.arg2 缓冲百分比 单位% 但是会超过100
             int currentPosition = videoView.getCurrentPosition()+mDurationOffset; //大部分视频都被强行分段 计算的时候要加上这个偏移量
             int loadingPercent = Math.min(100,msg.arg2);
             if(loadingPercent<100){
                 isBuffering = true;
                 if(!danmaView.isPaused()) {
                     danmaView.pause();
                 }
             }else if(loadingPercent==100&&videoView.isPlaying()) {
                 isBuffering =false;
                 long currentTime = danmaView.getCurrentTime();
                 if((Math.abs(currentPosition-currentTime)>POSSIBLE_OFFSET||!danmaView.isShown())&&!isDanmaShutdown){
                     danmaView.start(currentPosition);
                 }
             }
         }
     });

        videoView.setOnControllerButtonClickListener(new IjkVideoView.OnControllerButtonClickListener() {
            @Override
            public void onControllerButtonClick(int buttonType, int extra) {
                switch (buttonType){
                    case IjkVideoView.BUTTON_TYPE_PAUSE:
                        if(extra == IjkVideoView.BUTTON_PAUSE_PAUSING){//按下了停止按钮
                            Log.i("json","pause pressed");
                            if(!danmaView.isPaused()){
                                danmaView.pause();
                            }
                        }else if(extra == IjkVideoView.BUTTON_PAUSE_PLAYING){//按下了开始播放按钮
                            Log.i("json","play pressed");
                            if (!isDanmaShutdown) {
                                int currentPosition = videoView.getCurrentPosition() + mDurationOffset;
                                danmaView.start(currentPosition);
                            }
                        }
                        break;
                    case IjkVideoView.BUTTON_TYPE_SEEKBAR:
                        if(isBuffering) {
                            danmaView.seekTo((long) extra + mDurationOffset);
                        }else if(!isDanmaShutdown){
                            danmaView.start((long) extra + mDurationOffset);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                mDurationOffset += mp.getDuration();
                danmaView.pause();
                ArrayList<VideoDownloadUrl> urlList = mVideo.getUrlList();
                if(urlList.size()>mCurrentVideoOrder){
                    videoView.setVideoPath(urlList.get(mCurrentVideoOrder).getUrl());
                    mCurrentVideoOrder++;
                }
            }
        });

        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                Log.i("json","videoView prepared");
                isVideoPrepared = true;
                if(mCurrentVideoOrder == 1){
                    if(isDanmaPrepared){
                        videoView.start();
                    }
                }else {//分段视频的新起点 自动帮用户开始播放
                    mp.start();
                    if(!isDanmaShutdown) {
                        danmaView.start(mDurationOffset);
                    }
                }
            }
        });

        danmaView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                Log.i("json","danmaku prepared");
                isDanmaPrepared = true;
                if(isVideoPrepared){
                    videoView.start();
                }
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
//                Log.i("json","danmakuShown():"+danmaku.text);
            }

            @Override
            public void drawingFinished() {

            }
        });
    }

    private void initVideoSource(String cid) {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(Constant.BASE_URL_VIDEO).build();
        APIService service = retrofit.create(APIService.class);
        Call<ResponseBody> call = service.getVideoSource(Constant.appkey, cid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String xmlStr = response.body().string();
                    //开始XML解析
                    mVideo = parseXml(xmlStr);
                    Log.i("json", xmlStr);
                    Log.i("json",mVideo.toString());
                    if(mVideo.getUrlList().size()!=0){//初始化播放地址
                        VideoDownloadUrl vdu = mVideo.getUrlList().get(0);//播放第一个视频(可能有一组视频)
                        videoView.setVideoPath(vdu.getUrl());
                        mCurrentVideoOrder = 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void initDanmakuSource(final String cid){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(Constant.BASE_URL_DANMA+cid+".xml");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    InputStream is = new InflaterInputStream(conn.getInputStream(),new Inflater(true));
                    byte[] bs = decompressDanMu(is);
                    parser = createParser(new ByteArrayInputStream(bs));
                    danmaView.clear();
                    danmaView.release();
                    danmaView.prepare(parser,mContext);
                    danmaView.enableDanmakuDrawingCache(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private byte[] decompressDanMu(InputStream is) throws Exception{
        StringBuilder sb = new StringBuilder();
        byte [] buffer = new byte[1024];
        int length;
        while((length = is.read(buffer))!= -1){
            sb.append(new String(buffer,0,length));
        }
        Log.i("json","xml = "+sb.toString());
        return sb.toString().getBytes("utf-8");
    }
    public RxBus getRxBusSingleton() {
        if (rxBus == null) {
            synchronized (this) {
                if (rxBus == null) {
                    rxBus = new RxBus();
                }
            }
        }
        return rxBus;
    }

    private void onRxBuxEvent() {
        getRxBusSingleton().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof PassCidEvent) {
                    String cid = ((PassCidEvent) o).getCid();
                    boolean playImmediate = ((PassCidEvent) o).isPlayImmediate();
                    isDanmaPrepared =false;
                    isVideoPrepared =false;
                    isBuffering = false;
                    mDurationOffset = 0;
                    mCurrentVideoOrder = 0;
                    appBarLayout.scrollTo(0,0);
                    initVideoSource(cid);
                    initDanmakuSource(cid);
                    if(playImmediate&&videoBanner.getVisibility()==View.VISIBLE){
                        videoBanner.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                    }
                    Log.i("json","cid = "+cid);
                }else if(o instanceof PassTitleEvent){
                    String title = ((PassTitleEvent) o).getTitle();
                    titles[1] = title;
                    TabLayout.Tab tab = tabs.getTabAt(1);
                    if(tab!=null){
                        tab.setText(title);
                    }
                }
            }
        });
    }

    public void setToolbarTitle(String text) {
        avNumer.setText(text);
    }


    /**
     * 解析Video下载地址的XML文件
     * @param xmlStr xml的字符串
     * @return 解析完毕的Video对象
     * @throws Exception
     */
    private Video parseXml(String xmlStr) throws Exception{
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xmlStr));
        int eventType = parser.getEventType();
        Video video = new Video();
        ArrayList<VideoDownloadUrl> urlList = new ArrayList<>();
        video.setUrlList(urlList);
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                switch (tagName) {
                    case "result":
                        video.setResult(parser.nextText());
                        break;
                    case "timelength":
                        video.setTimelength(parser.nextText());
                        break;
                    case "format":
                        video.setFormat(parser.nextText());
                        break;
                    case "accept_format":
                        video.setAccept_format(parser.nextText());
                        break;
                    case "accept_quality":
                        video.setAccept_quality(parser.nextText());
                        break;
                    case "from":
                        video.setFrom(parser.nextText());
                        break;
                    case "seek_param":
                        video.setSeek_param(parser.nextText());
                        break;
                    case "seek_type":
                        video.setSeek_type(parser.nextText());
                        break;
                    case "src":
                        video.setSrc(parser.nextText());
                        break;
                    case "durl":
                        VideoDownloadUrl durl = new VideoDownloadUrl();
                        while (eventType != XmlPullParser.END_TAG || !tagName.equals("durl")) {
                            eventType = parser.next();
                            tagName = parser.getName();
                            if (eventType == XmlPullParser.START_TAG) {
                                switch (tagName) {
                                    case "order":
                                        durl.setOrder(parser.nextText());
                                        break;
                                    case "length":
                                        durl.setLength(parser.nextText());
                                        break;
                                    case "size":
                                        durl.setSize(parser.nextText());
                                        break;
                                    case "url":
                                        durl.setUrl(parser.nextText());
                                        break;
                                    case "backup_url":
                                        ArrayList<String> backup_url = new ArrayList<>();
                                        while (eventType != XmlPullParser.END_TAG || !tagName.equals("backup_url")) {
                                            eventType = parser.next();
                                            tagName = parser.getName();
                                            if (eventType == XmlPullParser.START_TAG) {
                                                if (tagName.equals("url")) {
                                                    backup_url.add(parser.nextText());
                                                }
                                            }
                                        }
                                        durl.setBackup_url(backup_url);
                                        break;
                                }
                            }
                        }
                        urlList.add(durl);
                        break;
                }
            }
            eventType = parser.next();
        }
        return video;
    }

    private void changeFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)appBarLayout.getChildAt(0);
        FrameLayout frameLayout = (FrameLayout) collapsingToolbarLayout.getChildAt(0);
        ViewGroup.LayoutParams lp = frameLayout.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        appBarLayout.removeView(collapsingToolbarLayout);
        appBarLayout.setVisibility(View.GONE);
        tabContainer.setVisibility(View.GONE);
        root.removeView(fab);
        fullscreenContainer.addView(collapsingToolbarLayout);
        fullscreenContainer.setVisibility(View.VISIBLE);
    }


    private void changeNormalScreen(){
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)fullscreenContainer.getChildAt(0);
        FrameLayout frameLayout = (FrameLayout) collapsingToolbarLayout.getChildAt(0);
        ViewGroup.LayoutParams lp = frameLayout.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = SizeUtils.dp2px(getBaseContext(),202.5);
        fullscreenContainer.removeView(collapsingToolbarLayout);
        fullscreenContainer.setVisibility(View.GONE);
        tabContainer.setVisibility(View.VISIBLE);
        root.addView(fab);
        appBarLayout.addView(collapsingToolbarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_video_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.full_screen:
                if(isFullScreen){
                    isFullScreen = false;
                    changeNormalScreen();
                }else {
                    isFullScreen = true;
                    changeFullScreen();
                }
                break;
            case R.id.danma:
                if(isDanmaShutdown){
                    isDanmaShutdown = false;
                    danmaView.show();
                }else {
                    isDanmaShutdown = true;
                    danmaView.hide();
                }
                break;
            case R.id.setting:
            case R.id.about:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);//解析B站格式的弹幕

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }

    @Override
    public void onBackPressed() {
        if(isFullScreen){
            changeNormalScreen();
            isFullScreen = false;
            return;
        }else {
            onBackPressed = true;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isVideoPrepared&&isDanmaPrepared){
            videoView.start();
            danmaView.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(onBackPressed||!videoView.isBackgroundPlayEnabled()){
            videoView.stopPlayback();
            videoView.release(true);
            videoView.stopBackgroundPlay();
        }else {
            videoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();

        if(onBackPressed){
            danmaView.release();
            danmaView = null;
        }else {
            danmaView.pause();
        }
    }

    class PlayPagerAdapter extends FragmentPagerAdapter {

        public PlayPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mProfileFragment;
            } else {
                return mCommentFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public static class ShowHideHandler extends Handler{
        WeakReference<Activity> weakRefer;
        public ShowHideHandler(Activity activity){
            weakRefer = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public class HideToolbarWorker implements Runnable{
        @Override
        public void run() {
            toolbar.setVisibility(View.INVISIBLE);
            isToolbarShowing = false;
        }
    }
}
