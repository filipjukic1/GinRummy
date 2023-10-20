package com.ginrummy.NetworkSettingsJNDI;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * The NetworkSettings class provides utility methods to retrieve network-related information
 * such as the IPv4 address of the local machine.
 */
public class NetworkSettings {

    /**
     * Retrieves the IPv4 address of the local machine.
     *
     * @return The IPv4 address of the local machine as a String, or null if it cannot be determined.
     */
    public static String getIPv4() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            String ipv4Address;
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) {
                        ipv4Address = inetAddress.getHostAddress();
                        System.out.println("Interface Name: " + networkInterface.getName());
                        System.out.println("Display Name: " + networkInterface.getDisplayName());
                        System.out.println("IPv4 Address: " + ipv4Address);
                        return ipv4Address;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
