package bms.device.webapi.user;

public final class User {
    String name;
    String password;
    Privilege privilege;

    public User(String name, String password, Privilege privilege) {
        this.name = name;
        this.password = password;
        this.privilege = privilege;
    }

    public enum Privilege {
        Administrator,
        /*PowerUser,
        User,
        Guest,*/
        Unauthorized
    }
}
