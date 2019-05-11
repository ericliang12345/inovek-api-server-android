package bms.device.webapi.api.nfc;

import android.content.Context;
import android.util.Log;

// serial
import com.licheedev.serialtool.comn.Device;
import com.licheedev.serialtool.comn.SerialPortManager;

final class NFC_Status {
    boolean reader; // true: NFC reader is working, false: NFC reader is not working
    boolean card;   // true: card is exist; false: no card
    String data;    // Data in the NFC Card

    NFC_Status(boolean reader, boolean card, String data){ this.reader = reader; this.card = card; this.data = data; }

}

public class NFCManager {

    private static NFCManager instance = null;
    public static NFCManager getInstance(Context context) {
        if (instance == null) {
            instance = new NFCManager(context);
        }
        return instance;
    }

    public static NFCManager getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        }
        return instance;
    }

    private static final String TAG = "NFCManage";
    private Device mDevice;
    private boolean mOpened = false;
    private NFC_Status mNFC_Status;
    private String mNFCReadCmd = "50000222105232";
    private String mNoCardReturn = "f00001228457";

    private NFCManager(Context context) {
        initDevice();
    }

    private void initDevice() {

        mDevice = new Device("/dev/ttyS3", "115200");
        mOpened = SerialPortManager.instance().open(mDevice) != null;
        if (mOpened) {
            mNFC_Status = new NFC_Status(true, false, "");
            Log.i(TAG, "Success to open /dev/ttyS3");
        } else {
            Log.i(TAG, "Failed to open /dev/ttyS3");
            mNFC_Status = new NFC_Status(false, false, "");

        }

    }


    public NFC_Status getNFC_Status() {

        if( mNFC_Status.reader == false ) return mNFC_Status;

        StringBuilder str = new StringBuilder(mNFCReadCmd); // Query NFC Command

        String sendData = str.toString();
        Log.i(TAG, sendData);
        SerialPortManager.instance().sendCommand(sendData);
        String recvData = SerialPortManager.instance().getReceiveData();
        try {
            Thread.sleep(1000); // wait 1 second
        } catch (InterruptedException e) {

        }
        // Wait a 10 ms wait NFC reader to reply its data
        if(recvData.equals(mNoCardReturn)) {
            mNFC_Status.card = false;
            mNFC_Status.data = "";
        }else {
            mNFC_Status.card = true;
            mNFC_Status.data = recvData;
        }
        return mNFC_Status;
    }




}
