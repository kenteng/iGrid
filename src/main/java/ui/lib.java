package ui;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by shockwave on 14/12/22.
 */
public class lib {

    public static String getHubIP() {
        InetAddress i = null;
        try {
            i = InetAddress.getLocalHost();
            System.out.println("Get local ip =" + i.getHostAddress()); // IP address only
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return i.getHostAddress();
    }
}
