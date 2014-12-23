package ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by shockwave on 14/12/22.
 */
public class lib {

    public static final String HostIP = getHubIP();

    static String getHubIP() {
        InetAddress i = null;
        try {
            i = InetAddress.getLocalHost();
            //System.out.println("Get local ip =" + i.getHostAddress()); // IP address only
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return i.getHostAddress();
    }


    public static Map<Integer, List<String>> nodesList() {
        Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        List<String> temp = new ArrayList<String>();
        temp.add("192.168.3.18");
        temp.add("root");
        temp.add("admaster");
        temp.add("1622");
        map.put(1, temp);
        List<String> temp1 = new ArrayList<String>();
        temp1.add("192.168.3.18");
        temp1.add("root");
        temp1.add("admaster");
        temp1.add("1722");
        map.put(2, temp1);
        return map;
    }
}
