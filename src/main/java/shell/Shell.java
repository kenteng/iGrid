package shell;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;

/**
 * Created by kteng on 2014/12/21.
 */


public class Shell {

    private String ip;

    private String user;

    private String password;

    private int port;

    private Session session;

    public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;

    private static Expect4j expect = null;

    private ChannelShell channel;

    private static final long defaultTimeOut = 1000;
    private StringBuffer buffer=new StringBuffer();

    public static String[] linuxPromptRegEx = new String[] { "~]#", "~#", "#",
            ":~#", "/$", ">" };

    public static String[] errorMsg=new String[]{"could not acquire the config lock "};

    public Shell(String ip,int port,String user,String password) {
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.password=password;
        expect = getExpect();
    }

    private Expect4j getExpect() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, ip, port);
            session.setPassword(password);
            Hashtable<String, String> config = new Hashtable<String, String>();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            localUserInfo ui = new localUserInfo();
            session.setUserInfo(ui);
            session.connect();
            channel = (ChannelShell) session.openChannel("shell");
            Expect4j expect = new Expect4j(channel.getInputStream(), channel
                    .getOutputStream());
            channel.connect();
            return expect;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 执行配置命令
     * @param commands 要执行的命令，为字符数组
     * @return 执行是否成功
     */
    public boolean executeCommands(String[] commands) {
        //如果expect返回为0，说明登入没有成功
        if(expect==null){
            return false;
        }

        System.out.println("----------Running commands are listed as follows:----------");
        for(String command:commands){
            System.out.println(command);
        }
        System.out.println("----------End----------");

        Closure closure = new Closure() {
            public void run(ExpectState expectState) throws Exception {
                buffer.append(expectState.getBuffer());// buffer is string
                // buffer for appending
                // output of executed
                // command
                expectState.exp_continue();

            }
        };
        List<Match> lstPattern = new ArrayList<Match>();
        String[] regEx = linuxPromptRegEx;
        if (regEx != null && regEx.length > 0) {
            synchronized (regEx) {
                for (String regexElement : regEx) {// list of regx like, :>, />
                    // etc. it is possible
                    // command prompts of your
                    // remote machine
                    try {
                        RegExpMatch mat = new RegExpMatch(regexElement, closure);
                        lstPattern.add(mat);
                    } catch (MalformedPatternException e) {
                        return false;
                    } catch (Exception e) {
                        return false;
                    }
                }
                lstPattern.add(new EofMatch(new Closure() { // should cause
                    // entire page to be
                    // collected
                    public void run(ExpectState state) {
                    }
                }));
                lstPattern.add(new TimeoutMatch(defaultTimeOut, new Closure() {
                    public void run(ExpectState state) {
                    }
                }));
            }
        }
        try {
            boolean isSuccess = true;
            for (String strCmd : commands){
                isSuccess = isSuccess(lstPattern, strCmd);
            }
            //防止最后一个命令执行不了
            isSuccess = !checkResult(expect.expect(lstPattern));

            //找不到错误信息标示成功
            String response=buffer.toString().toLowerCase();
            for(String msg:errorMsg){
                if(response.indexOf(msg)>-1){
                    return false;
                }
            }

            return isSuccess;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //检查执行是否成功
    private boolean isSuccess(List<Match> objPattern, String strCommandPattern) {
        try {
            boolean isFailed = checkResult(expect.expect(objPattern));
            if (!isFailed) {
                expect.send(strCommandPattern);
                expect.send("\r");
                return true;
            }
            return false;
        } catch (MalformedPatternException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    //检查执行返回的状态
    private boolean checkResult(int intRetVal) {
        if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
            return true;
        }
        return false;
    }

    //登入SSH时的控制信息
    //设置不提示输入密码、不显示登入信息等
    public static class localUserInfo implements UserInfo {
        String passwd;

        public String getPassword() {
            return passwd;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {

        }
    }

}
