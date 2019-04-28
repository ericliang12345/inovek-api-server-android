package bms.device.webapi.api.info;


import bms.device.webapi.api.Api;





public final class ApiPublicInfo extends Api {



    public static String name() {
        return "/public/info";
    }

    @Override
    public boolean isAuthRequired() {
        return false;
    }

    @Override
    public Output execute(Action action, String uri, String json) {
        if (action == Action.READ) {
            PublicInfo publicInfo = DevInfoManager.getInstance().getPublicInfo();
            return new Output(Result.OK, gson.toJson(new GetJSONResultsResponse(publicInfo)));
        }  else {
            return new Output(Result.BAD_REQUEST);
        }
    }
}
