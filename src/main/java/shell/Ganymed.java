package shell;

import ch.ethz.ssh2.*;

import java.io.*;

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
    private Session session;

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

    public boolean execCommand(String cmd) {
        try {
            session = conn.openSession();
            int pid = has("selenium-server-standalone-2.40.0.jar");
            if (pid != 0) {
                killProcess(pid);
            }
            session = conn.openSession();
            session.execCommand(cmd);
            pid = has("selenium-server-standalone-2.40.0.jar");
            if (pid != 0)
                return true;
            else
                return false;

        } catch (IOException e) {
            return false;
        }
    }


    public boolean execCommandWithStdOut(String cmd) {
        try {
            session = conn.openSession();
            int pid = has("selenium-server-standalone-2.40.0.jar");
            if (pid != 0) {
                killProcess(pid);
            }
            session = conn.openSession();
            session.execCommand(cmd);
            InputStream stdout = new StreamGobbler(session.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
            System.out.println("ExitCode: " + session.getExitStatus());
            if (session.getExitStatus() == 0)
                session.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error? " + session.getExitSignal());
            session.close();
            e.printStackTrace();
            return false;
        }
    }

    private int has(String pName) {
        int pid = 0;
        try {
            session = conn.openSession();
            session.execCommand("jps");
            InputStream stdout = new StreamGobbler(session.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                if (line.contains(pName)) {
                    String[] temp = line.split(" ");
                    pid = Integer.parseInt(temp[0]);
                    break;
                }
            }
            session.close();
        } catch (IOException e) {

        }
        return pid;
    }

    private void killProcess(int pid) {
        try {

            if (pid != 0) {
                session = conn.openSession();
                session.execCommand("Kill " + pid);
                session.close();
            }
        } catch (IOException e) {
        }
    }

    public boolean connect() {
        conn = new Connection(hostName, port);
        try {
            conn.connect();
            if (conn.authenticateWithPassword(userName, pwd)) {
                return true;
            } else {
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
            }
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
