package org.logannelson.filesystem.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class StatusBarPanel extends JPanel {

    private final JLabel statusLabel;

    public StatusBarPanel() {
        super(new BorderLayout());

        statusLabel = new JLabel("Ready");
        setBorder(BorderFactory.createEtchedBorder());

        add(statusLabel, BorderLayout.WEST);
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
}
