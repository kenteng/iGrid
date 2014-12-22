package ui;

import shell.Ganymed;

import javax.swing.*;
import java.awt.event.*;

public class iGrid extends JDialog {
    private JPanel contentPane;
    private JButton StartHub;
    private JButton NewNode;
    private JTable table1;

    public iGrid() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(StartHub);
        setTable();

        StartHub.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (startHub())
                    table1.setValueAt(lib.getHubIP(), 0, 0);
                table1.setValueAt("true", 0, 2);
            }
        });

//        NewNode.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                setNewNode("10.10.10.204","root","7_gmausd3_3te");
//               /* node1.setVisible(true);
//                node1.setText("1.1.1.1");
//                if(setNewNode()){
//                }else {
//                }*/
//            }
//        });

        NewNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setNewNode("192.168.3.18", "root", "admaster", 1622);
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void setTable() {
        table1.setShowGrid(true);
        table1.setModel(new TableModel());
        table1.setVisible(true);
        table1.setValueAt(lib.getHubIP(), 0, 0);
        table1.setValueAt("hub", 0, 1);
        table1.setValueAt("unknown", 0, 2);
    }

    private boolean setNewNode(String hostName, String userName, String pwd) {
        Ganymed ganymed = new Ganymed(hostName, userName, pwd);
        if (ganymed.connect()) {
            int i = table1.getRowCount();
            table1.setValueAt(hostName, i, 0);
            table1.setValueAt("node", i, 1);
            table1.setValueAt("true", i, 2);
            return true;
        }
        return false;
    }

    private boolean setNewNode(String hostName, String userName, String pwd, int port) {
        Ganymed ganymed = new Ganymed(hostName, userName, pwd, port);
        if (ganymed.connect()) {
            int i = table1.getRowCount();
            table1.setValueAt(hostName, i, 0);
            table1.setValueAt("node", i, 1);
            table1.setValueAt("true", i, 2);
            return true;
        }
        return false;
    }

    private boolean startHub() {
        Ganymed ganymed = new Ganymed(lib.getHubIP(), "shockwave", "admaster");
        if (ganymed.connect()) {
            ganymed.execCommand("ls");
            return true;
        }
        return false;
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        iGrid dialog = new iGrid();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
