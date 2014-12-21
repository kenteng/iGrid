package ui;

import javax.swing.*;

/**
 * Created by kteng on 2014/12/21.
 */
public class Hello extends JFrame {
    private JLabel jLabel;
    public Hello(){
        super();
    }
    private javax.swing.JLabel getJLabel() {
        if(jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setBounds(34, 49, 53, 18);
            jLabel.setText("Name:");
        }
        return jLabel;
    }
}
