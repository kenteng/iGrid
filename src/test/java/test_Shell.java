import shell.Shell;

/**
 * Created by kteng on 2014/12/21.
 */
public class test_Shell {
    public static void main(String[] args){
        //new shell.Shell("192.168.3.18",1522,"root","admater");
        Shell shell = new Shell("10.10.10.204",22,"root","7_gmausd3_3te");
        String[] cmd = new String[1];
        cmd[0] = "ls";
        shell.executeCommands(cmd);
    }
}
