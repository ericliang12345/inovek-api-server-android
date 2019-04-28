package bms.device.webapi.api;

class Hello {
    String message = "Hello World";

    Hello(String name) {
        if ((name != null) && (name.length() > 1)) {
            this.message = "Hello " + name.substring(1);
        }
    }
}

public final class ApiHello extends Api {

    public static String name() {
        return "/hello1";
    }

    @Override
    public boolean isAuthRequired() {
        return false;
    }

    @Override
    public Output execute(Action action, String uri, String json) {
        if (action == Action.READ) {
            return new Output(Result.OK, gson.toJson(new Hello(trimUri(uri, name()))));
        } else {
            return new Output(Result.BAD_REQUEST);
        }
    }

}
