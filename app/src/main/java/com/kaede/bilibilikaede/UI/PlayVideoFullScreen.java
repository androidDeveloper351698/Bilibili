package com.kaede.bilibilikaede.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.View.media.AndroidMediaController;
import com.kaede.bilibilikaede.View.media.IjkVideoView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayVideoFullScreen extends AppCompatActivity {
    @InjectView(R.id.videoView) IjkVideoView videoView;
    @InjectView(R.id.danmaView) DanmakuView danmaView;

    private AndroidMediaController mediaController;
    private DanmakuContext mContext;
    private BaseDanmakuParser parser;
    private boolean onBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_full_screen);
        ButterKnife.inject(this);

        init();
        initPlay();
    }

    private void init(){
        mediaController = new AndroidMediaController(this);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        videoView.setMediaController(mediaController);
        videoView.setDanmakuView(danmaView);

        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); //滚动弹幕最大显示5行
        //设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        danmaView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                Log.i("json","prepared()");
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
                Log.i("json","danmakuShown():"+danmaku.text);
            }

            @Override
            public void drawingFinished() {

            }
        });
    }

    private void initPlay(){
        String cid = getIntent().getStringExtra("cid");
        if(TextUtils.isEmpty(cid)){
            Toast.makeText(getBaseContext(),"网络异常",Toast.LENGTH_SHORT).show();
        }else {
            String videoPath = getIntent().getStringExtra("videoPath");
            byte[] danma = getIntent().getByteArrayExtra("danma");
            initVideoSource(videoPath);
            initDanmakuSource(danma);
            videoView.start();
            danmaView.start();
        }
    }

    private void initVideoSource(String videoPath) {
       videoView.setVideoPath(videoPath);
    }

    private void initDanmakuSource(byte[] bs){
        parser = createParser(new ByteArrayInputStream(bs));
        danmaView.prepare(parser,mContext);
        danmaView.enableDanmakuDrawingCache(true);
    }

//    private byte[] decompressDanMu(InputStream is) throws Exception{
//        StringBuilder sb = new StringBuilder();
//        byte [] buffer = new byte[1024];
//        int length = 0;
//        while((length = is.read(buffer))!= -1){
//            sb.append(new String(buffer,0,length));
//        }
//        Log.i("json","xml = "+sb.toString());
//        return sb.toString().getBytes("utf-8");
//    }
//
//    private Video parseXml(String xmlStr) throws Exception{
//        XmlPullParser parser = Xml.newPullParser();
//        parser.setInput(new StringReader(xmlStr));
//        int eventType = parser.getEventType();
//        Video video = new Video();
//        ArrayList<VideoDownloadUrl> urlList = new ArrayList<>();
//        video.setUrlList(urlList);
//        while (eventType != XmlPullParser.END_DOCUMENT) {
//            if(eventType == XmlPullParser.START_TAG) {
//                String tagName = parser.getName();
//                if (tagName.equals("result")) {
//                    video.setResults(parser.nextText());
//                } else if (tagName.equals("timelength")) {
//                    video.setTimelength(parser.nextText());
//                } else if (tagName.equals("format")) {
//                    video.setFormat(parser.nextText());
//                } else if (tagName.equals("accept_format")) {
//                    video.setAccept_format(parser.nextText());
//                } else if (tagName.equals("accept_quality")) {
//                    video.setAccept_quality(parser.nextText());
//                } else if (tagName.equals("from")) {
//                    video.setFrom(parser.nextText());
//                } else if (tagName.equals("seek_param")) {
//                    video.setSeek_param(parser.nextText());
//                } else if (tagName.equals("seek_type")) {
//                    video.setSeek_type(parser.nextText());
//                } else if (tagName.equals("src")) {
//                    video.setSrc(parser.nextText());
//                } else if (tagName.equals("durl")) {
//                    VideoDownloadUrl durl = new VideoDownloadUrl();
//                    while (eventType != XmlPullParser.END_TAG || !tagName.equals("durl")) {
//                        eventType = parser.next();
//                        tagName = parser.getName();
//                        if (eventType == XmlPullParser.START_TAG) {
//                            if (tagName.equals("order")) {
//                                durl.setOrder(parser.nextText());
//                            } else if (tagName.equals("length")) {
//                                durl.setLength(parser.nextText());
//                            } else if (tagName.equals("size")) {
//                                durl.setSize(parser.nextText());
//                            } else if (tagName.equals("url")) {
//                                durl.setUrl(parser.nextText());
//                            } else if (tagName.equals("backup_url")) {
//                                ArrayList<String> backup_url = new ArrayList<>();
//                                while (eventType != XmlPullParser.END_TAG || !tagName.equals("backup_url")) {
//                                    eventType = parser.next();
//                                    tagName = parser.getName();
//                                    if (eventType == XmlPullParser.START_TAG) {
//                                        if (tagName.equals("url")) {
//                                            backup_url.add(parser.nextText());
//                                        }
//                                    }
//                                }
//                                durl.setBackup_url(backup_url);
//                            }
//                        }
//                    }
//                    urlList.add(durl);
//                }
//            }
//            eventType = parser.next();
//        }
//        return video;
//    }

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
        onBackPressed = true;
        super.onBackPressed();
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
}
