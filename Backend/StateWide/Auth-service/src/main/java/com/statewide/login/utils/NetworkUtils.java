package com.statewide.login.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkUtils {
    public static String getHostName() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName(); // machine hostname
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "UnknownHost";
        }
    }

    public static String getIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress(); // machine IP
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "UnknownIP";
        }
    }

    public static String getHostNameFromConfig(String serverUrl) {
        try {
            URI uri = new URI(serverUrl);
            return uri.getHost();
        } catch (Exception e) {
            return getHostName();
        }
    }

    public static String getMACAddress() {
        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder macAddress = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        macAddress.append(String.format("%02X", mac[i]));
                        if (i != mac.length - 1) {
                            macAddress.append("-");
                        }
                    }
                    return macAddress.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "MAC not available";
    }

}
