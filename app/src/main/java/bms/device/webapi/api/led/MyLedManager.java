package bms.device.webapi.api.led;

import android.content.Context;
import android.util.Log;

// serial
import com.licheedev.serialtool.comn.Device;
import com.licheedev.serialtool.comn.SerialPortManager;

final class LedStatus {
    int red;
    int green;
    int blue;

    LedStatus(int r, int g, int b){ this.red = r; this.green = g; this.blue = b; }

}

public class MyLedManager {

    private static MyLedManager instance = null;
    public static MyLedManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyLedManager(context);
        }
        return instance;
    }

    public static MyLedManager getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        }
        return instance;
    }

    public static final String TAG = "LedManage";
    private Device mDevice;
    private boolean mOpened = false;
    private String mData = "";
    private LedStatus mLedStatus;
    private String mLedCmd = "ff550124";

    private MyLedManager(Context context) {
        initDevice();
    }

    private void initDevice() {

        mLedStatus = new LedStatus(0,0,0);

        mDevice = new Device("/dev/ttyS1", "115200");
        mOpened = SerialPortManager.instance().open(mDevice) != null;
        if (mOpened) {
            Log.i(TAG, "成功打开串口");
        } else {
            Log.i(TAG, "打开串口失败");
        }
        setLed(mLedStatus);

    }


    public void setLed(LedStatus status) {

        String redHex=Integer.toHexString(status.red);
        String greenHex=Integer.toHexString(status.green);
        String blueHex=Integer.toHexString(status.blue);
        String ledHex = new StringBuilder(redHex).append(greenHex).append(blueHex).toString();

        StringBuilder str = new StringBuilder("ff550124"); // LED Start Code & Cmd
        for(int i=0; i<12; i++)
            str.append(ledHex);

        int sum = 1 + 36 + 12 * ( status.red + status.green + status.blue );
        if( sum > 255 ) sum = 255;
        String sumHex = Integer.toHexString(sum);
        str.append(sumHex); // checksum

        String sendData = str.toString();
        Log.i("LED", sendData);
        SerialPortManager.instance().sendCommand(sendData);
    }

    public LedStatus getLedStatus() {
        return mLedStatus;
    }


}
