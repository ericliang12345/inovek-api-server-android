package bms.device.webapi.user;

import java.util.ArrayList;
import java.util.List;

public final class UserManager {
    private static final UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }

    private List<User> users = new ArrayList<>();

    private UserStorage storage = null;
    public void setStorage(UserStorage storage) throws RuntimeException {
        if ((this.storage == null) && (storage != null)) {
            this.storage = storage;

            reload();
        } else {
            throw new RuntimeException("Only once"); // FIXME:
        }
    }

    public User.Privilege check(String name, String password) {
        for (User user: users) {
            if (name.equals(user.name) && password.equals(user.password)) {
                return user.privilege;
            }
        }

        return User.Privilege.Unauthorized;
    }

    public boolean changePassword(String name, String newPassword) {
        boolean result = storage.changePassword(name, newPassword);
        reload();
        return result;
    }

    public boolean delete(String name) {
        boolean result = storage.delete(name);
        reload();
        return result;
    }

    private void reload() {
        users.clear();

        int count = storage.getCount();
        for (int i = 0; i < count; i++) {
            users.add(storage.load(i));
        }
    }
}
