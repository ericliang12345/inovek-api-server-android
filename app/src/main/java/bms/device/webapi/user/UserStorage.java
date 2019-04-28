package bms.device.webapi.user;

public interface UserStorage {
    int getCount();
    User load(int index);
    boolean changePassword(String name, String newPassword);
    boolean delete(String name);
}
