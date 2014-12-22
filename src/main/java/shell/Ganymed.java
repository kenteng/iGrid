package shell;

import ch.ethz.ssh2.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by kteng on 2014/12/21.
 */
public class Ganymed {
   /* String hostName = "10.10.10.204";
    String hostIp = "10.10.10.204";
    String userName = "root";
    String pwd = "7_gmausd3_3te";
    int proxyPort = 3128;*/

    private String hostName;
    private String userName;
    private String pwd;
    private Connection conn;
    private int port;

    public Ganymed(String hostName, String userName, String pwd) {
        this.hostName = hostName;
        this.userName = userName;
        this.pwd = pwd;
        this.port = 22;
    }

    public Ganymed(String hostName, String userName, String pwd, int port) {
        this.hostName = hostName;
        this.userName = userName;
        this.pwd = pwd;
        this.port = port;
    }

    public void execCommand(String cmd) {
        try {
            Session sess = conn.openSession();
            sess.execCommand(cmd);
            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
            System.out.println("ExitCode: " + sess.getExitStatus());

            sess.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean connect() {
        conn = new Connection(hostName, port);
        try {
            conn.connect();
            boolean isAvailable = conn.isAuthMethodAvailable(userName, pwd);
            if (isAvailable)
                isAvailable = conn.authenticateWithPassword(userName, pwd);
            else {
                try {
                    isAvailable = conn.authenticateWithKeyboardInteractive(userName, new InteractiveCallback() {
                        public String[] replyToChallenge(String name, String instruction, int numPrompts,
                                                         String[] prompt, boolean[] echo)
                                throws Exception {
                            String[] responses = new String[numPrompts];
                            for (int x = 0; x < numPrompts; x++) {
                                responses[x] = pwd;
                            }
                            return responses;
                        }
                    });
                } catch (IOException ex) {
                }
            }
            if (isAvailable == false)
                throw new IOException("Authentication failed.");
            return true;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
        return false;
    }

    private void close() {
        conn.close();
    }
}
