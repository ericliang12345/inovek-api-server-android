package com.dev.lungyu.wifi_dection;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lungyu on 10/10/17.
 */

public class PermissionManagement {
    private Context context;

    public PermissionManagement(Context context){
        this.context = context;
    }

    //for example, permission can be "android.permission.WRITE_EXTERNAL_STORAGE"
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean hasPermission(String permission)
    {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    Log.d("requestedPermissions",p);
                    if (p.equals(permission)) {
                        return true;
                    }
                }

                for (int ps : info.requestedPermissionsFlags) {
                    Log.d("requestedPermissions",ps+"");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<String> getNotAllowPermission(){

        PackageInfo info = null;
        List<String> notAllowList = new LinkedList<String>();
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {

                for (String permissionName : info.requestedPermissions) {
                    Log.d("requestedPermissions",permissionName);
                    if(!IsAllowPermission(permissionName)){
                        notAllowList.add(permissionName);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return notAllowList;
    }

    private boolean IsAllowPermission(String permissionName){
        int permission = ActivityCompat.checkSelfPermission(context, permissionName);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            return false;
        }
        return true;
    }
}
