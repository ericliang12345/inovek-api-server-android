package bms.device.webapi.api.audio;

import android.content.Context;

import android.media.AudioManager;


// serial

final class Audio_Status {
    int stream_system; // true: NFC reader is working, false: NFC reader is not working
    int stream_music;
    int stream_notification;
    int stream_alarm;

    Audio_Status(int system, int music, int notification, int alarm )
    { this.stream_system = system; this.stream_music = music; this.stream_notification = notification; this.stream_alarm = alarm; }

}

public class MyAudioManager {

    private static MyAudioManager instance = null;
    public static MyAudioManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyAudioManager(context);
        }
        return instance;
    }

    public static MyAudioManager getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        }
        return instance;
    }

    private static final int STREAM_SYSTEM = 1;
    private static final int STREAM_MUSIC  = 3;
    private static final int STREAM_ALARM  = 4;
    private static final int STREAM_NOTIFICATION = 5;

    private Audio_Status mAudio_Status;
    private AudioManager mAudioManager;
    private Context context;

    private MyAudioManager(Context context) {

        this.context = context;

        this.mAudioManager = (AudioManager) context
                .getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
    }



    public Audio_Status getAudioStatus() {
        int sys_volume = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
        int music_volume = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        int alarm_volume = mAudioManager.getStreamVolume( AudioManager.STREAM_ALARM );
        int notif_volume = mAudioManager.getStreamVolume( AudioManager.STREAM_NOTIFICATION );
        mAudio_Status = new Audio_Status(sys_volume,music_volume,alarm_volume,notif_volume);

        return mAudio_Status;
    }

    public int getSysVolume(){
        return mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
    }

    public int getMusicVolume(){
        return mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
    }

    public int getAlarmVolume(){
        return mAudioManager.getStreamVolume( AudioManager.STREAM_ALARM );
    }

    public int getNotifyVolume(){
        return mAudioManager.getStreamVolume( AudioManager.STREAM_NOTIFICATION );
    }

    public void setVolume(int index, int volume ){
        mAudioManager.setStreamVolume(index, volume, 0 );
    }




}
