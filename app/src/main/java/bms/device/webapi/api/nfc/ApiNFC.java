package bms.device.webapi.api.nfc;


import bms.device.webapi.api.Api;



public final class ApiNFC extends Api {



    public static String name() {
        return "/nfc";
    }



    @Override
    public Output execute(Action action, String uri, String json) {
        if (action == Action.READ) {
            // need to read from
            NFC_Status nfcStatus = NFCManager.getInstance().getNFC_Status();
            return new Output(Result.OK, gson.toJson(nfcStatus));

        } else {
            return new Output(Result.BAD_REQUEST);
        }
    }
}
