package org.logannelson.filesystem.ui;

import org.logannelson.filesystem.service.FileSystemService;
import org.logannelson.filesystem.service.FileSystemServiceImpl;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Path;

public class MainFrame extends JFrame {

    private final FileSystemService fileSystemService;
    private final StatusBarPanel statusBarPanel;

    private FileBrowserPanel browserPanel;
    private FileContentPanel contentPanel;

    public MainFrame() {
        super("File Management System");

        this.fileSystemService = new FileSystemServiceImpl();
        this.statusBarPanel = new StatusBarPanel();

        initFrameSettings();
        initLayout(); //Now create the panels first
        initMenuBar(); //Then wire menu actions
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

        //WIRE ACTIONS
        newFileItem.addActionListener(e -> createNewFile());
        newFolderItem.addActionListener(e -> createNewFolder());
        exitItem.addActionListener(e -> dispose());

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

    }

    private void initLayout() {
        // Right side: content panel gets the service and now status reporter
        this.contentPanel = new FileContentPanel(
                fileSystemService,
                statusBarPanel::setStatusMessage
        );

        // Left side: browser panel; provide callback for when a file is opened
        this.browserPanel =
                new FileBrowserPanel(
                        fileSystemService,
                        statusBarPanel::setStatusMessage,
                        fileItem -> {
                            try {
                                String content = fileSystemService.readFile(fileItem.getPath());
                                statusBarPanel.setStatusMessage(
                                        "Opened file: " + fileItem.getPath() +
                                                " (length: " + content.length() + ")"
                                );
                                contentPanel.displayFile(fileItem.getPath(), content);
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

    //Helper for createNewFile
    private void createNewFile() {
        if (browserPanel == null) {
            statusBarPanel.setStatusMessage("File browser not ready.");
            return;
        }

        Path currentDir = browserPanel.getCurrentDirectory();
        if (currentDir == null) {
            statusBarPanel.setStatusMessage("No directory selected.");
            return;
        }

        String name = JOptionPane.showInputDialog(
                this,
                "Enter new file name:",
                JOptionPane.PLAIN_MESSAGE
        );

        if (name == null) {
            //If user cancels
            statusBarPanel.setStatusMessage("File creation cancelled.");
            return;
        }

        name = name.trim();
        if (name.isEmpty()) {
            statusBarPanel.setStatusMessage("File name cannot be empty.");
            return;
        }

        try {
            fileSystemService.createFile(currentDir, name, "");
            browserPanel.reloadCurrentDirectory();
            statusBarPanel.setStatusMessage("Created file: " + currentDir.resolve(name));
        } catch (IOException e) {
            statusBarPanel.setStatusMessage("Error creating file: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to create file:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }


    }

    private void createNewFolder() {
        if (browserPanel == null) {
            statusBarPanel.setStatusMessage("File browser not ready.");
            return;
        }

        Path currentDir = browserPanel.getCurrentDirectory();
        if (currentDir == null) {
            statusBarPanel.setStatusMessage("No directory selected.");
            return;
        }

        String name = JOptionPane.showInputDialog(
                this,
                "Enter new folder name:",
                "New Folder",
                JOptionPane.PLAIN_MESSAGE
        );

        if (name == null) {
            // User cancelled
            statusBarPanel.setStatusMessage("Folder creation cancelled.");
            return;
        }

        name = name.trim();
        if (name.isEmpty()) {
            statusBarPanel.setStatusMessage("Folder name cannot be empty.");
            return;
        }

        try {
            fileSystemService.createDirectory(currentDir, name);
            browserPanel.reloadCurrentDirectory();
            statusBarPanel.setStatusMessage("Created folder: " + currentDir.resolve(name));
        } catch (IOException e) {
            statusBarPanel.setStatusMessage("Error creating folder: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to create folder:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
