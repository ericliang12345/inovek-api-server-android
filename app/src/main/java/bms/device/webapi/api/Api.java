package bms.device.webapi.api;

import com.google.gson.Gson;

import bms.device.webapi.user.User;







abstract public class Api {

    public enum Action {
        READ,   // map to HTTP GET
        WRITE,  // map to HTTP PUT
        REMOVE, // map to HTTP DELETE
    }

    public enum Result {
        OK,
        BAD_REQUEST,
        UNAUTHORIZED,
        NOT_FOUND,
        NOT_IMPLEMENT
    }

    public class Output {
        public Result result;
        public String json = null;

        public Output(Result result) {
            this.result = result;
        }

        public Output(Result result, String json) {
            this.result = result;
            this.json = json;
        }
    }

    protected Gson gson = new Gson();

    public boolean isAuthRequired() {
        return true;
    }

    public User.Privilege privilegeRequired() {
        return User.Privilege.Administrator;
    }

    abstract public Output execute(Action action, String uri, String json);

    final String trimUri(String uri, String name) {
        return uri.substring(name.length());
    }
}
