package bms.device.webapi.api.audio.nfc;


import bms.device.webapi.api.Api;
import bms.device.webapi.api.audio.MyAudioManager;


final class VolumeReply {
    int value;
    VolumeReply(int volume){
        this.value = volume;
    }

}

class setVolumeResponse {
    String message = "Failed to set Volume";

    setVolumeResponse(Api.Result ret) {
        if( ret == Api.Result.OK)
            this.message = "Success to set Volume";
    }
}

public final class ApiAudioManager extends Api {



    public static String name() {
        return "/audio";
    }


    @Override
    public boolean isAuthRequired() {
        return false;
    }


    @Override
    public Output execute(Action action, String uri, String json) {
        if (action == Action.READ) {
            if(uri.equals("/audio/volume/stream_system")) {
                return new Output(Result.OK, gson.toJson(new VolumeReply(MyAudioManager.getInstance().getSysVolume()))); // 1
            } else if(uri.equals("/audio/volume/stream_music")) {
                return new Output(Result.OK, gson.toJson(new VolumeReply(MyAudioManager.getInstance().getMusicVolume()))); // 3
            } else if(uri.equals("/audio/volume/stream_notification")) {
                return new Output(Result.OK, gson.toJson(new VolumeReply(MyAudioManager.getInstance().getNotifyVolume()))); // 4
            } else if(uri.equals("/audio/volume/stream_alarm")) {
                return new Output(Result.OK, gson.toJson(new VolumeReply(MyAudioManager.getInstance().getAlarmVolume()))); // 5
            } else if(uri.equals("/audio/volume")) // { "stream_system": 18, "stream_music": 100, "stream_notification": 0, "stream_alarm": 0 }
                return new Output(Result.OK, gson.toJson( MyAudioManager.getInstance().getAudioStatus()));
            else
                    return new Output(Result.NOT_FOUND);
        } else if ( action == Action.WRITE ) {
            VolumeReply setVolume = gson.fromJson(json, VolumeReply.class);
            int type = -1;
            if(uri.equals("/audio/volume/stream_system"))
                type = 1;
            else if(uri.equals("/audio/volume/stream_music"))
                type = 3;
            else if(uri.equals("/audio/volume/stream_notification"))
                type = 4;
            else if(uri.equals("/audio/volume/stream_alarm"))
                type = 5;

            if( type != -1 ) {
                MyAudioManager.getInstance().setVolume(type, setVolume.value);
                return new Output(Result.OK, gson.toJson(new setVolumeResponse(Result.OK)));
            } else {
                return new Output(Result.BAD_REQUEST);
            }

        } else
            return new Output(Result.BAD_REQUEST);
    }
}
