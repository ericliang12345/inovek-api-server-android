package bms.device.webapi;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import bms.device.webapi.api.Api;
import bms.device.webapi.api.ApiHello;
import bms.device.webapi.api.info.ApiInfoManager;
import bms.device.webapi.api.led.ApiLedManager;
import bms.device.webapi.api.nfc.ApiNFC;
import bms.device.webapi.api.ApiOAuth2Token;
import bms.device.webapi.api.ApiUserPassword;
import bms.device.webapi.api.wifi.ApiScanResults;
import bms.device.webapi.api.wifi.ApiWiFiNetwork;
import bms.device.webapi.user.SessionManager;
import bms.device.webapi.user.User;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD;

final class WebServer extends RouterNanoHTTPD {

    WebServer(int port) {
        super(port);
        addMappings();
    }

    WebServer(int port, SSLContext sslContext) throws RuntimeException {
        super(port);
        addMappings();
        if (sslContext != null) {
            makeSecure(sslContext.getServerSocketFactory(), null);
        } else {
            throw new RuntimeException("SSL Context is invalid");
        }
    }

    @Override
    public void addMappings() {
        setNotImplementedHandler(NotImplementedHandler.class);
        setNotFoundHandler(Error404UriHandler.class);

        addRoute(ApiV1Nanolet.name + "/(.)+", ApiV1Nanolet.class);
    }

}

final class ErrorMessage {
    String message;

    ErrorMessage(Status status) {
        message = status.getDescription();
    }
}

final class ApiV1Nanolet implements RouterNanoHTTPD.UriResponder {

    public static final String name = "/v1";

    public ApiV1Nanolet() {
        addApi(ApiHello.name(), ApiHello.class);
        addApi(ApiOAuth2Token.name(), ApiOAuth2Token.class);
        addApi(ApiUserPassword.name(), ApiUserPassword.class);
        addApi(ApiScanResults.name(), ApiScanResults.class);
        addApi(ApiWiFiNetwork.name(), ApiWiFiNetwork.class);
        addApi(ApiInfoManager.name(), ApiInfoManager.class);
        addApi(ApiLedManager.name(), ApiLedManager.class);
        addApi(ApiNFC.name(), ApiNFC.class);
    }

    private Api find(String uri) {
        for (Map.Entry<String, Class<?>> entry: apis.entrySet()) {
            String key = entry.getKey();
            Class<?> clz = entry.getValue();
            if (uri.startsWith(key)) {
                try {
                    return (Api) clz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    private Map<String, Class<?>> apis = new HashMap<>();
    private <T> void addApi(String uri, Class<T> clz) {
        apis.put(uri, clz);
    }

    private final String mimeType = "application/json";
    private Gson gson = new Gson();

    private Response errorMessage(Status status) {
        return NanoHTTPD.newFixedLengthResponse(status, mimeType, gson.toJson(new ErrorMessage(status)));
    }

    private Response badRequest() {
        return errorMessage(Status.BAD_REQUEST);
    }

    private Response unauthorized() {
        return errorMessage(Status.UNAUTHORIZED);
    }

    private Response notFound() {
        return errorMessage(Status.NOT_FOUND);
    }

    private Response notImplemented() {
        return errorMessage(Status.NOT_IMPLEMENTED);
    }

    private Response internalError() {
        return errorMessage(Status.INTERNAL_ERROR);
    }

    private String readContent(InputStream inputStream) {
        try {
            int size = inputStream.available();
            byte[] data = new byte[size];
            inputStream.read(data, 0, size);
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Response handle(Api.Action action, NanoHTTPD.IHTTPSession session) {
        if (session != null) {
            String uri = session.getUri().substring(name.length());
            String json = readContent(session.getInputStream());
            Api api = find(uri);
            if (api != null) {
                /* check authorization of each API */
                if (api.isAuthRequired()) {
                    String authorization = session.getHeaders().get("authorization");
                    if (authorization.startsWith("Bearer ")) {
                        String accessToken = authorization.substring("Bearer ".length());
                        User.Privilege privilege = SessionManager.getInstance().checkAccessToken(accessToken);
                        if (privilege.ordinal() > api.privilegeRequired().ordinal()) {
                            return unauthorized();
                        }
                    }
                }

                Api.Output output = api.execute(action, uri, json);
                if (output == null) {
                    // FIXME: Internal ERROR!
                    return internalError();
                }

                switch (output.result) {
                    case OK:
                        return NanoHTTPD.newFixedLengthResponse(Status.OK, mimeType, output.json);
                    case BAD_REQUEST:
                        return badRequest();
                    case UNAUTHORIZED:
                        return unauthorized();
                    case NOT_FOUND:
                        return notFound();
                    case NOT_IMPLEMENT:
                        return notImplemented();
                }
            }
        }

        return badRequest();
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        return handle(Api.Action.READ, session);
    }

    @Override
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        return handle(Api.Action.WRITE, session);
    }

    @Override
    public NanoHTTPD.Response delete(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        return handle(Api.Action.REMOVE, session);
    }

    @Override
    public NanoHTTPD.Response put(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        return notImplemented();
    }

    @Override
    public NanoHTTPD.Response other(String method, RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        return notImplemented();
    }

}
