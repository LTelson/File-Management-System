package org.logannelson.filesystem.ui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("File Management System");

        initFrameSettings();
        initMenuBar();
        initLayout();
    }

    private void initFrameSettings() {
        // Basic window behavior
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); // center on screen
        setLayout(new BorderLayout());
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newFileItem = new JMenuItem("New File");
        JMenuItem newFolderItem = new JMenuItem("New Folder");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(newFileItem);
        fileMenu.add(newFolderItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem renameItem = new JMenuItem("Rename");
        JMenuItem deleteItem = new JMenuItem("Delete");
        editMenu.add(renameItem);
        editMenu.add(deleteItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        // Wiring actions will come later
    }

    private void initLayout() {
        // Left: file browser (placeholder for now)
        FileBrowserPanel browserPanel = new FileBrowserPanel();

        // Right: file content (placeholder for now)
        FileContentPanel contentPanel = new FileContentPanel();

        // Split pane between browser (left) and content (right)
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                browserPanel,
                contentPanel
        );
        splitPane.setDividerLocation(300);

        // Status bar at the bottom
        StatusBarPanel statusBarPanel = new StatusBarPanel();

        add(splitPane, BorderLayout.CENTER);
        add(statusBarPanel, BorderLayout.SOUTH);
    }
}
