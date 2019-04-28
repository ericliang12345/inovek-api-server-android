package bms.device.webapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import bms.device.webapi.api.info.DevInfoManager;
import bms.device.webapi.api.led.MyLedManager;

import bms.device.webapi.api.wifi.MyWiFiManager;
import bms.device.webapi.test.FunctionTestActivity;
import bms.device.webapi.user.User;
import bms.device.webapi.user.UserManager;
import bms.device.webapi.user.UserStorage;


// wifi
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.dev.lungyu.wifi_dection.PermissionManagement;
import java.util.List;


final class UserStorageV1 implements UserStorage {

    private SharedPreferences sharedPreferences;
    private String name;
    private String password;
    private String privilege;

    UserStorageV1(Activity activity) {
        sharedPreferences = activity.getSharedPreferences("BMS", Activity.MODE_PRIVATE);
        name = sharedPreferences.getString("username", "admin");
        password = sharedPreferences.getString("password", "12345678");
        privilege = sharedPreferences.getString("privilege", "Administrator");
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public User load(int index) {
        return new User(name, password, User.Privilege.valueOf(privilege));
    }

    @Override
    public boolean changePassword(String name, String newPassword) {
        if (name.equals(this.name)) {
            this.password = newPassword;
            sharedPreferences.edit().putString("password", this.password).apply();
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String name) {
        return changePassword(name, "12345678");
    }

}

public class MainActivity extends Activity {

    // wifi
    private static final int REQUEST_CODE_ASK = 1055;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DevInfoManager.getInstance(this);
        MyWiFiManager.getInstance(this);
        MyLedManager.getInstance(this);

        UserManager userManager = UserManager.getInstance();
        userManager.setStorage(new UserStorageV1(this));

        try {
            new WebServer(8080).start();
            new WebServer(8443, loadSSL()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkAllowAllPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, FunctionTestActivity.class);
        startActivity(intent);
    }

    private SSLContext loadSSL() {
        try {
            /* command to build a key:                                                            */
            /* keytool -genkey -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.pfx */
            InputStream inputStream = getAssets().open("keystore.pfx");
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, "bmsapi".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "bmsapi".toCharArray());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // wifi
    private void checkAllowAllPermission(){
        PermissionManagement permissionManagement = new PermissionManagement(this);
        List<String> list = permissionManagement.getNotAllowPermission();

        if(list.size() == 0){
            directActivity();
            return;
        }

        String[] reqs = new String[list.size()];
        for(int i=0;i<reqs.length;i++)
            reqs[i] = list.get(i);


        ActivityCompat.requestPermissions(
                this,
                reqs,
                REQUEST_CODE_ASK
        );
    }

    private void directActivity(){
        //Intent intent = new Intent(this,LoginActivity.class);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }




}
