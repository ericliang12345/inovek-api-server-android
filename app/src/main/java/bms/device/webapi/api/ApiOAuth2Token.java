package bms.device.webapi.api;

import java.util.UUID;

import bms.device.webapi.user.SessionManager;
import bms.device.webapi.user.User;
import bms.device.webapi.user.UserManager;

final class TokenRequest {
    String grant_type;
    String username;
    String password;
    String refresh_token;
}

final class TokenResponse {
    final String token_type = "Bearer";
    String access_token;
    String refresh_token;

    TokenResponse(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
}

public final class ApiOAuth2Token extends Api {

    public static String name() {
        return "/oauth2/token";
    }

    @Override
    public boolean isAuthRequired() {
        return false;
    }

    @Override
    public Output execute(Action action, String uri, String json) {
        if (action != Action.WRITE) {
            return new Output(Result.BAD_REQUEST);
        }

        User.Privilege privilege = User.Privilege.Unauthorized;
        boolean isRefresh = false;

        TokenRequest request = gson.fromJson(json, TokenRequest.class);
        switch (request.grant_type) {
            case "password":
                privilege = checkPassword(request.username, request.password);
                break;
            case "refresh_token":
                privilege = checkRefreshToken(request.refresh_token);
                isRefresh = true;
                break;
        }

        if (privilege != User.Privilege.Unauthorized) {
            String accessToken = generateToken();
            String refreshToken = isRefresh ? request.refresh_token : generateToken();
            if (!isRefresh) {
                SessionManager.getInstance().add(accessToken, refreshToken, privilege);
            } else {
                SessionManager.getInstance().refresh(refreshToken, accessToken);
            }
            return new Output(Result.OK, gson.toJson(new TokenResponse(accessToken, refreshToken)));
        } else {
            return new Output(Result.BAD_REQUEST);
        }
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private User.Privilege checkPassword(String username, String password) {
        return UserManager.getInstance().check(username, password);
    }

    private User.Privilege checkRefreshToken(String refreshToken) {
        return SessionManager.getInstance().checkRefreshToken(refreshToken);
    }

}
