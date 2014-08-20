package com.yookos.notifyserver.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jome on 2014/07/02.
 */
public class NotificationHelper {
    public static InetAddress getServerAddress(){
        try {
            InetAddress host = InetAddress.getLocalHost();
            return host;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
