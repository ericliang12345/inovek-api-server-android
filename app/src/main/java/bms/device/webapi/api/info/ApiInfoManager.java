package bms.device.webapi.api.info;


import bms.device.webapi.api.Api;




final class GetSingleValueResponse {
    Object value;

    GetSingleValueResponse(Object data) { this.value = data; }
}



public final class ApiInfoManager extends Api {



    public static String name() {
        return "/info";
    }


    @Override
    public Output execute(Action action, String uri, String json) {
        if (action == Action.READ) {
            DevInfo devInfo = DevInfoManager.getInstance().getDevInfo();
            if(uri.equals(this.name()))
                return new Output(Result.OK, gson.toJson(new GetJSONResultsResponse(devInfo)));
            else if( uri.equals("/info/fw_version")){
                return new Output(Result.OK, gson.toJson(new GetSingleValueResponse(devInfo.fw_version)));
            }
            return new Output(Result.NOT_FOUND);
        }  else {
            return new Output(Result.BAD_REQUEST);
        }
    }
}
