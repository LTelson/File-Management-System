package org.logannelson.filesystem.ui;

import org.logannelson.filesystem.service.FileSystemService;
import org.logannelson.filesystem.service.FileSystemServiceImpl;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.io.IOException;

public class MainFrame extends JFrame {

    private final FileSystemService fileSystemService;
    private final StatusBarPanel statusBarPanel;

    public MainFrame() {
        super("File Management System");

        this.fileSystemService = new FileSystemServiceImpl();
        this.statusBarPanel = new StatusBarPanel();

        initFrameSettings();
        initMenuBar();
        initLayout();
    }

    private void initFrameSettings() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
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

        //Wiring menu actions later.
    }

    private void initLayout() {
        // Right side: content panel
        FileContentPanel contentPanel = new FileContentPanel();

        // Left side: browser panel; provide callback for when a file is opened
        FileBrowserPanel browserPanel =
                new FileBrowserPanel(
                        fileSystemService,
                        statusBarPanel::setStatusMessage,
                        fileItem -> {
                            try {
                                String content = fileSystemService.readFile(fileItem.getPath());
                                contentPanel.displayFile(fileItem.getPath(), content);
                                statusBarPanel.setStatusMessage("Opened file: " + fileItem.getPath());
                            } catch (IOException e) {
                                statusBarPanel.setStatusMessage("Error reading file: " + e.getMessage());
                            }
                        }
                );

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                browserPanel,
                contentPanel
        );
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);
        add(statusBarPanel, BorderLayout.SOUTH);
    }
}
