package org.logannelson.filesystem.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class FileBrowserPanel extends JPanel {

    public FileBrowserPanel() {
        super(new BorderLayout());

        setBorder(BorderFactory.createTitledBorder("File Browser"));

        //Placeholder
        add(new JLabel("Directory tree / list goes here"), BorderLayout.CENTER);
    }
}
