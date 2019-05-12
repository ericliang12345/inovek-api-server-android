package bms.device.webapi.api.wifi;

import android.net.LinkAddress;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import bms.device.webapi.api.Api;
import bms.device.webapi.api.net.IpConfiguration;

import java.util.ArrayList;
import java.util.List;

import bms.device.webapi.api.Api;

final class MyWifiConfiguration {
    String BSSID;
    String FQDN;
    String SSID;
    long allowedAuthAlgorithms;
    long allowedGroupCiphers;
    long allowedKeyManagement;
    long allowedPairwiseCiphers;
    long allowedProtocols;
    boolean hiddenSSID;
    int networkId;
    int priority;
    int status;
    int wepTxKeyIndex;

    String eap_method;
    String eap_phase2_method;
    String eap_identity;
    String eap_anonymous;

    IpConfiguration advanced;
}

final class GetWiFiNetworkResponse {
    Object[] results;

    GetWiFiNetworkResponse(Object[] results) {
        this.results = results;
    }
}

final class WiFiStatus {
    String value;

    WiFiStatus(String state){ this.value = state; }

}

final class ScanResult {
    String BSSID;
    String SSID;
    String capabilities;
    int frequency;
    int level;
    long timestamp;
}

final class ScanResultsResponse {
    Object[] results;

    ScanResultsResponse(Object[] results) {
        this.results = results;
    }
}

final class PostWiFiNetworkRequest {
    String method;
    String ssid;
    String password;
    String security;

    String eap_method;
    String eap_phase2_method;
    String eap_identity;
    String eap_anonymous;

    IpConfiguration advanced;
}

public final class ApiWiFiNetwork extends Api {

    public static String name() {
        return "/wifi";
    }

    @Override
    public boolean isAuthRequired() {
        return false;
    }

    @Override
    public Output execute(Action action, String uri, String json) {
        if (action == Action.READ) {
            if(uri.equals("/wifi/network"))
                return readWifiConfigurations();
            else if( uri.equals("/wifi/state")) {
                if( MyWiFiManager.getInstance().getWiFiState() == true )
                    return new Output(Result.OK, gson.toJson(new WiFiStatus("enable")));
                else
                    return new Output(Result.OK, gson.toJson(new WiFiStatus("disable")));
            }else if( uri.equals("/wifi/scan_results")) {
                MyWiFiManager.getInstance().startWiFiScan();

                List<ScanResult> results = new ArrayList<>();

                try {
                    Thread.sleep(10000); // wait 10 second
                } catch (InterruptedException e) {

                }


                for (android.net.wifi.ScanResult scanResult: MyWiFiManager.getInstance().getScanResults()) {
                    ScanResult result = new ScanResult();
                    result.BSSID = scanResult.BSSID;
                    result.SSID = scanResult.SSID;
                    result.capabilities = scanResult.capabilities;
                    result.frequency = scanResult.frequency;
                    result.level = scanResult.level;
                    result.timestamp = scanResult.timestamp;

                    results.add(result);
                }

                return new Output(Result.OK, gson.toJson(new ScanResultsResponse(results.toArray())));
            }

        } else if (action == Action.WRITE) {
            //return new Output(Result.BAD_REQUEST);
        }

        return new Output(Result.BAD_REQUEST);
    }

    private String toEapMethodString(int method) {
        switch (method) {
            case WifiEnterpriseConfig.Eap.PEAP:
                return "peap";
            case WifiEnterpriseConfig.Eap.TLS:
                return "tls";
            case WifiEnterpriseConfig.Eap.TTLS:
                return "ttls";
            case WifiEnterpriseConfig.Eap.PWD:
                return "pwd";
            case WifiEnterpriseConfig.Eap.SIM:
                return "sim";
            case WifiEnterpriseConfig.Eap.AKA:
                return "aka";
            case WifiEnterpriseConfig.Eap.AKA_PRIME:
                return "aka_prime";
            case WifiEnterpriseConfig.Eap.UNAUTH_TLS:
                return "unauth_tls";

            case WifiEnterpriseConfig.Eap.NONE:
            default:
                return "none";
        }
    }

    private String toEapPhase2MethodString(int method) {
        switch (method) {
            case WifiEnterpriseConfig.Phase2.PAP:
                return "pap";
            case WifiEnterpriseConfig.Phase2.MSCHAP:
                return "mschap";
            case WifiEnterpriseConfig.Phase2.MSCHAPV2:
                return "mschapv2";
            case WifiEnterpriseConfig.Phase2.GTC:
                return "gtc";
            case WifiEnterpriseConfig.Phase2.SIM:
                return "sim";
            case WifiEnterpriseConfig.Phase2.AKA:
                return "aka";
            case WifiEnterpriseConfig.Phase2.AKA_PRIME:
                return "aka_prime";

            case WifiEnterpriseConfig.Eap.NONE:
            default:
                return "none";
        }
    }

    private long bitset2long(BitSet bits) {
        long[] array = bits.toLongArray();
        if (array.length > 0) {
            return array[0];
        } else {
            return 0;
        }
    }

    private Output readWifiConfigurations() {
        ArrayList<MyWifiConfiguration> responses = new ArrayList<>();
        List<WifiConfiguration> configurations = MyWiFiManager.getInstance().getConfiguredNetworks();
        for (WifiConfiguration configuration: configurations) {
            MyWifiConfiguration myWiFi = new MyWifiConfiguration();
            myWiFi.BSSID = configuration.BSSID;
            myWiFi.FQDN = configuration.FQDN;
            myWiFi.SSID = configuration.SSID;
            myWiFi.allowedAuthAlgorithms = bitset2long(configuration.allowedAuthAlgorithms);
            myWiFi.allowedGroupCiphers = bitset2long(configuration.allowedGroupCiphers);
            myWiFi.allowedKeyManagement = bitset2long(configuration.allowedKeyManagement);
            myWiFi.allowedPairwiseCiphers = bitset2long(configuration.allowedPairwiseCiphers);
            myWiFi.allowedProtocols = bitset2long(configuration.allowedProtocols);
            myWiFi.hiddenSSID = configuration.hiddenSSID;
            myWiFi.networkId = configuration.networkId;
            myWiFi.priority = configuration.priority;
            myWiFi.status = configuration.status;
            myWiFi.wepTxKeyIndex = configuration.wepTxKeyIndex;

            WifiEnterpriseConfig eap = configuration.enterpriseConfig;
            if (eap.getEapMethod() != WifiEnterpriseConfig.Eap.NONE) {
                myWiFi.eap_anonymous = eap.getAnonymousIdentity();
                myWiFi.eap_identity = eap.getIdentity();
                myWiFi.eap_method = toEapMethodString(eap.getEapMethod());
                myWiFi.eap_phase2_method = toEapPhase2MethodString(eap.getPhase2Method());
            }

            try {
                if (getIpAssignment(configuration).toLowerCase().equals("static")) {
                    myWiFi.advanced = new IpConfiguration();
                    myWiFi.advanced.ip_assignment = "static";

                    LinkAddress la = getIpAddress(configuration);
                    myWiFi.advanced.static_ip = la.getAddress().getHostAddress();
                    myWiFi.advanced.network_prefix_length = la.getPrefixLength();

                    RouteInfo gateway = getGateway(configuration);
                    myWiFi.advanced.gateway = gateway.getGateway().getHostAddress();

                    List<InetAddress> dns = getDNS(configuration);
                    myWiFi.advanced.dns = dns.get(0).getHostAddress();
                    if (dns.size() > 1) {
                        myWiFi.advanced.dns2 = dns.get(1).getHostAddress();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            responses.add(myWiFi);
        }

        GetWiFiNetworkResponse response = new GetWiFiNetworkResponse(responses.toArray());
        return new Output(Result.OK, gson.toJson(response));
    }

    private void setIpAssignment(String assign , WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign.toUpperCase(), "ipAssignment");
    }

    private void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)
            return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);

        ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    private void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);

        ArrayList mRoutes = (ArrayList)getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    private void setDNS(InetAddress dns, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;

        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); //or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    private Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        return f.get(obj);
    }

    private Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    private void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    private String getIpAssignment(WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return getEnumField(wifiConf, "ipAssignment");
    }

    private LinkAddress getIpAddress(WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return null;

        return (LinkAddress) ((ArrayList) getDeclaredField(linkProperties, "mLinkAddresses")).get(0);
    }

    private RouteInfo getGateway(WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)
            return null;

        return (RouteInfo) ((ArrayList) getDeclaredField(linkProperties, "mRoutes")).get(0);
    }

    private List<InetAddress> getDNS(WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return null;

        return (List<InetAddress>) getDeclaredField(linkProperties, "mDnses");
    }

    private String getEnumField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        return f.get(obj).toString();
    }
}
