package org.logannelson.filesystem.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class FileContentPanel extends JPanel {

    public FileContentPanel() {
        super(new BorderLayout());

        setBorder(BorderFactory.createTitledBorder("File Content"));

        //Placeholder content for now
        add(new JLabel("File content viewer/editor goes here"), BorderLayout.CENTER);
    }
}
