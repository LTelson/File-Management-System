package org.logannelson.filesystem.ui;

import org.logannelson.filesystem.model.FileItem;
import org.logannelson.filesystem.service.FileSystemService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class FileBrowserPanel extends JPanel {

    private final FileSystemService fileSystemService;
    private final Consumer<String> statusConsumer;
    private final Consumer<FileItem> fileOpenConsumer;

    private final JLabel currentPathLabel;
    private final DefaultListModel<FileItem> listModel;
    private final JList<FileItem> fileList;

    private Path currentDirectory;

    public FileBrowserPanel(FileSystemService fileSystemService,
                            Consumer<String> statusConsumer,
                            Consumer<FileItem> fileOpenConsumer) {
        super(new BorderLayout());
        this.fileSystemService = fileSystemService;
        this.statusConsumer = statusConsumer;
        this.fileOpenConsumer = fileOpenConsumer;

        setBorder(BorderFactory.createTitledBorder("File Browser"));

        //Top bar: "Current path" and Up button
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));

        JButton upButton = new JButton("Up");
        currentPathLabel = new JLabel();

        pathPanel.add(new JLabel("Path:"));
        pathPanel.add(currentPathLabel);

        topPanel.add(pathPanel, BorderLayout.CENTER);
        topPanel.add(upButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //Center: list of files/directories
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        add(new JScrollPane(fileList), BorderLayout.CENTER);

        //Initially load the start directory
        this.currentDirectory = fileSystemService.getStartDirectory();
        loadDirectory(currentDirectory);

        //Up directory navigation
        upButton.addActionListener(e -> navigateUp());

        //Double-click navigation / open
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Left mouse double click
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    int index = fileList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        FileItem selected = listModel.getElementAt(index);
                        handleItemDoubleClick(selected);
                    }
                }
            }
        });
    }

    private void handleItemDoubleClick(FileItem item) {
        if (item.isDirectory()) {
            loadDirectory(item.getPath());
        } else {
            setStatus("Opening file: " + item.getName());
            if (fileOpenConsumer != null) {
                fileOpenConsumer.accept(item);
            }
        }
    }

    private void loadDirectory(Path directory) {
        try {
            List<FileItem> items = fileSystemService.listDirectory(directory);

            listModel.clear();
            for (FileItem item : items) {
                listModel.addElement(item);
            }

            currentDirectory = directory;
            currentPathLabel.setText(directory.toString());
            setStatus("Opened: " + directory);
        } catch (IOException e) {
            setStatus("Error reading directory: " + e.getMessage());
        }
    }

    private void navigateUp() {
        Path parent = currentDirectory.getParent();
        if (parent != null) {
            loadDirectory(parent);
        } else {
            setStatus("Already at top-level directory.");
        }
    }

    private void setStatus(String message) {
        if (statusConsumer != null) {
            statusConsumer.accept(message);
        }
    }
}
