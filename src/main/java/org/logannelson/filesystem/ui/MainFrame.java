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

        renameItem.addActionListener(e -> renameSelectedItem());
        deleteItem.addActionListener(e -> deleteSelectedItem());

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

    private void renameSelectedItem() {
        if (browserPanel == null) {
            statusBarPanel.setStatusMessage("File browser not ready.");
            return;
        }

        var selected = browserPanel.getSelectedItem();
        if (selected == null) {
            statusBarPanel.setStatusMessage("No file or folder selected to rename.");
            return;
        }

        String oldName = selected.getName();
        String newName = JOptionPane.showInputDialog(
                this,
                "Enter new name:",
                oldName
        );

        if (newName == null) {
            statusBarPanel.setStatusMessage("Rename cancelled.");
            return;
        }

        newName = newName.trim();
        if (newName.isEmpty()) {
            statusBarPanel.setStatusMessage("New name cannot be empty.");
            return;
        }

        try {
            Path oldPath = selected.getPath();
            Path newPath = fileSystemService.rename(oldPath, newName);
            statusBarPanel.setStatusMessage("Renamed to: " + newPath);

            //If this file is currently open in the content panel, update the label/content path
            Path openFile = contentPanel.getCurrentFile();
            if (openFile != null && openFile.equals(oldPath)) {
                //Just update the label path. Content stays the same
                contentPanel.displayFile(newPath, contentPanel.getCurrentContent());
            }

            browserPanel.reloadCurrentDirectory();
        } catch (IOException e) {
            statusBarPanel.setStatusMessage("Error renaming: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to rename:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSelectedItem() {
        if (browserPanel == null) {
            statusBarPanel.setStatusMessage("File browser not ready.");
            return;
        }

        var selected = browserPanel.getSelectedItem();
        if (selected == null) {
            statusBarPanel.setStatusMessage("No file or folder selected to delete.");
            return;
        }

        Path target = selected.getPath();
        String message = selected.isDirectory()
                ? "Delete this folder and all its contents?\n" + target
                : "Delete this file?\n" + target;

        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            statusBarPanel.setStatusMessage("Delete cancelled.");
            return;
        }

        try {
            //If the open file is being deleted, clear the content panel
            Path openFile = contentPanel.getCurrentFile();
            if (openFile != null && openFile.equals(target)) {
                contentPanel.clearContent();
            }

            fileSystemService.delete(target);
            browserPanel.reloadCurrentDirectory();
            statusBarPanel.setStatusMessage("Deleted: " + target);
        } catch (IOException e) {
            statusBarPanel.setStatusMessage("Error deleting: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
