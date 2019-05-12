package com.dev.lungyu.wifi_dection;

//import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK = 1055;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        checkAllowAllPermission();
    }

    private void checkAllowAllPermission(){
        PermissionManagement permissionManagement = new PermissionManagement(this);
        List<String> list = permissionManagement.getNotAllowPermission();

        /*if(list.size() == 0){
            directActivity();
            return;
        }*/

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
        //Intent intent = new Intent(this,MainActivity.class);
        //startActivity(intent);
       // finish();
    }
}
