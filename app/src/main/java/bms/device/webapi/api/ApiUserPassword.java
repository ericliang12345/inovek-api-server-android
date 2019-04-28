package bms.device.webapi.api;

import bms.device.webapi.user.UserManager;

final class PasswordRequest {
    String value;
}

public final class ApiUserPassword extends Api {

    public static String name() {
        return "/user/password";
    }

    @Override
    public Output execute(Action action, String uri, String json) {
        String name = "admin"; // TODO: get name from session
        UserManager userManager = UserManager.getInstance();

        Result result = Result.BAD_REQUEST;
        switch (action) {
            case WRITE:
                PasswordRequest request = gson.fromJson(json, PasswordRequest.class);
                if (userManager.changePassword(name, request.value)) {
                    result = Result.OK;
                }
                break;

            case REMOVE:
                if (userManager.delete(name)) {
                    result = Result.OK;
                }
                break;
        }
        return new Output(result);
    }
}
