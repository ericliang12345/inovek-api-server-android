package bms.device.webapi.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

final class Session {
    String accessToken;
    String refreshToken;
    User.Privilege privilege;
    long timestamp;

    Session(String accessToken, String refreshToken, User.Privilege privilege) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.privilege = privilege;
        this.timestamp = SessionManager.now();
    }
}

public class SessionManager {
    private static final SessionManager ourInstance = new SessionManager();

    public static SessionManager getInstance() {
        return ourInstance;
    }

    private static final long TIMEOUT = 300;
    private static final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    static long now() {
        return calendar.getTimeInMillis() / 1000;
    }

    private SessionManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sessions.size() > 0) {
                    long now = now();
                    for (Session session : sessions) {
                        if (now - session.timestamp > TIMEOUT) {
                            sessions.remove(session);
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private List<Session> sessions = new ArrayList<>();
    public void add(String accessToken, String refreshToken, User.Privilege privilege) {
        sessions.add(new Session(accessToken, refreshToken, privilege));
    }

    public User.Privilege checkAccessToken(String accessToken) {
        for (Session session : sessions) {
            if (session.accessToken.equals(accessToken)) {
                session.timestamp = now();
                return session.privilege;
            }
        }

        return User.Privilege.Unauthorized;
    }

    public User.Privilege checkRefreshToken(String refreshToken) {
        for (Session session : sessions) {
            if (session.refreshToken.equals(refreshToken)) {
                session.timestamp = now();
                return session.privilege;
            }
        }

        return User.Privilege.Unauthorized;
    }

    public void refresh(String refreshToken, String newAccessToken) {
        for (Session session : sessions) {
            if (session.refreshToken.equals(refreshToken)) {
                session.accessToken = newAccessToken;
                session.timestamp = now();
            }
        }
    }

}
