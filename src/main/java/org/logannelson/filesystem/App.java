package org.logannelson.filesystem;

import org.logannelson.filesystem.ui.MainFrame;

import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        //Always start Swing apps on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
