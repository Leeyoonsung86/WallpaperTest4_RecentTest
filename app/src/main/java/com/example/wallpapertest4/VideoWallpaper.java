package com.example.wallpapertest4;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.CallSuper;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.PopupMenu;
import android.net.Uri;
import android.widget.VideoView;

import java.io.IOException;

import static com.example.wallpapertest4.Constant.ACTION_VOICE_NORMAL;
import static com.example.wallpapertest4.Constant.ACTION_VOICE_SILENCE;

public class VideoWallpaper extends WallpaperService {

    private static final String TAG = VideoWallpaper.class.getName();
    private static String sVideoPath;

    public static void setVoiceSilence(Context context) {
        Intent intent = new Intent(Constant.Video_PARAMS_CONTROL_ACTION);
        intent.putExtra(Constant.ACTION, ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);

    }

    public static void setVoiceNormal(Context context) {
        Intent intent = new Intent(Constant.Video_PARAMS_CONTROL_ACTION);
        intent.putExtra(Constant.ACTION, ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    public static void setToWallPaper(Context context, String videoPath) {

        try {
            context.clearWallpaper();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sVideoPath = videoPath;

         //alert.setTitle("AlertDialog Title");
         //alert.setMessage("AlertDialog Content");

        Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        //intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, VideoWallpaper.class));
        context.startActivity(intent);
    }

    @Override
    public Engine onCreateEngine() {
        try {
            return new VideoWallpaperEngine();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    class VideoWallpaperEngine extends Engine {

        private SurfaceHolder holder;
        private MediaPlayer mMediaPlayer;
        private BroadcastReceiver mVideoVoiceControlReceiver;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {

            super.onCreate(surfaceHolder);
            this.holder = getSurfaceHolder();
            IntentFilter intentFileter = new IntentFilter(Constant.Video_PARAMS_CONTROL_ACTION);
            mVideoVoiceControlReceiver = new VideoVoiceControlReceiver();
            registerReceiver(mVideoVoiceControlReceiver,intentFileter);
            //onSurfaceCreated(this.holder);
        }


        public void onDestory() {
            unregisterReceiver(mVideoVoiceControlReceiver);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {

           // builder.setTitle("AlertDialog Title");
           // builder.setMessage("AlertDialog Content");

            super.onSurfaceCreated(holder);
            //Surface surface;
            if (TextUtils.isEmpty(sVideoPath)) {
                throw new NullPointerException("videoPath must not be null");
            } else {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setSurface(holder.getSurface());

                try {

                    mMediaPlayer.setDataSource("https://manifest.googlevideo.com/api/manifest/hls_playlist/expire/1573470570/ei/Cu3IXYezONOT4gLv7IPIDQ/ip/163.152.3.186/id/KGEekP1102g.1/itag/95/source/yt_live_broadcast/requiressl/yes/ratebypass/yes/live/1/goi/160/sgoap/gir%3Dyes%3Bitag%3D140/sgovp/gir%3Dyes%3Bitag%3D136/hls_chunk_host/r2---sn-n3cgv5qc5oq-bh2lk.googlevideo.com/playlist_duration/30/manifest_duration/30/playlist_type/DVR/initcwndbps/36980/mm/44/mn/sn-n3cgv5qc5oq-bh2lk/ms/lva/mv/m/mvi/1/pcm2cms/yes/pl/16/dover/11/keepalive/yes/fexp/23842630/mt/1573448844/disable_polymer/true/sparams/expire,ei,ip,id,itag,source,requiressl,ratebypass,live,goi,sgoap,sgovp,playlist_duration,manifest_duration,playlist_type/sig/ALgxI2wwRQIgX3e7Nd82Cb58uzhUzehSlq33COmhsERmpvIGQSstxI8CIQD1UTmOZkvmtqHnxR6Y7Tooovo9GhOXBzlDK0ZuhJePBw%3D%3D/lsparams/hls_chunk_host,initcwndbps,mm,mn,ms,mv,mvi,pcm2cms,pl/lsig/AHylml4wRAIgGssq8-PiAojllz6D0EfVK12nRyAguERxUWm_551wmdQCIBuBAIDrifinoN2rYl5zjjmZf1U8flzclvPeMqE_ekrH/playlist/index.m3u8");
                    //mMediaPlayer.setDataSource("http://nmxlive.akamaized.net/hls/live/529965/Live_1/index.m3u8");
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setVolume(0f, 0f);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        public void onSurfaceDestoryed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mMediaPlayer != null)
                mMediaPlayer.release();
            mMediaPlayer = null;
        }

        class VideoVoiceControlReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                int action = intent.getIntExtra(Constant.ACTION, -1);
                switch (action) {
                    case ACTION_VOICE_NORMAL:
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                        break;

                    case ACTION_VOICE_SILENCE:
                        mMediaPlayer.setVolume(0, 0);
                        break;
                }
            }
        }
    }
}
