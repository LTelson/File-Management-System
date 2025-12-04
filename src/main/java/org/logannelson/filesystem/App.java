package org.logannelson.filesystem;

import org.logannelson.filesystem.ui.MainFrame;

import javax.swing.SwingUtilities;

public class App {


    /*
    * Section 1
    * This is the application entry point.
    * This launches the main Swing window and initializes the application.
    * */
    public static void main(String[] args) {
        //Always start Swing apps on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
