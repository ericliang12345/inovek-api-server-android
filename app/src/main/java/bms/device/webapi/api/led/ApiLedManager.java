package bms.device.webapi.api.led;


import bms.device.webapi.api.Api;


final class LEDInfoResultsResponse {
    String[] results;

    LEDInfoResultsResponse(String[] results) {
        this.results = results;
    }
}

final class LedStatusResponse {
    int red;
    int green;
    int blue;

    LedStatusResponse(int red, int green, int blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

}



class setLedResponse {
    String message = "Failed to set LED";

    setLedResponse(Api.Result ret) {
        if( ret == Api.Result.OK)
            this.message = "Success to set LED";
    }
}

public final class ApiLedManager extends Api {



    public static String name() {
        return "/led";
    }

    public static String[] led_list = {"front_led"};
    @Override
    public boolean isAuthRequired() {
        return false;
    }

    @Override
    public Output execute(Action action, String uri, String json) {
        if (action == Action.READ) {

            if(uri.equals(this.name())) // {"results": [ "front_led" ] }
                return new Output(Result.OK, gson.toJson( new LEDInfoResultsResponse(led_list)));
            // need to read from
            else if(uri.equals("/led/front_led")) {
                LedStatus ledStatus = MyLedManager.getInstance().getLedStatus();
                return new Output(Result.OK, gson.toJson(new LedStatusResponse(ledStatus.red, ledStatus.green, ledStatus.blue)));
            } else
                return new Output(Result.NOT_FOUND);
        } else if ( action == Action.WRITE ) {
            LedStatus request = gson.fromJson(json, LedStatus.class);

            if(request.red < 0 || request.red > 255 || request.green < 0 || request.green > 255 || request.blue < 0 || request.blue > 255 )
                return new Output(Result.BAD_REQUEST, gson.toJson(new setLedResponse(Result.BAD_REQUEST)));

            MyLedManager.getInstance().setLed(request);

            return new Output(Result.OK, gson.toJson(new setLedResponse(Result.OK)));

        } else {
            return new Output(Result.BAD_REQUEST);
        }
    }
}
