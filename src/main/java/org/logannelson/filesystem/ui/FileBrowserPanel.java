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


/*
* Section 7
* Displays directory contents, handles double-clock navigation and "up" button functionality.
* */
public class FileBrowserPanel extends JPanel {

    private final FileSystemService fileSystemService;
    private final Consumer<String> statusConsumer;
    private final Consumer<FileItem> fileOpenConsumer;

    private final JLabel currentPathLabel;
    private final DefaultListModel<FileItem> listModel;
    private final JList<FileItem> fileList;
    private final Path rootDirectory; //Guard rails
    private Path currentDirectory;

    public FileBrowserPanel(FileSystemService fileSystemService,
                            Consumer<String> statusConsumer,
                            Consumer<FileItem> fileOpenConsumer) {
        super(new BorderLayout());
        this.fileSystemService = fileSystemService;
        this.statusConsumer = statusConsumer;
        this.fileOpenConsumer = fileOpenConsumer;

        setBorder(BorderFactory.createTitledBorder("File Browser"));

        //Start directory for the browser
        this.rootDirectory = fileSystemService.getStartDirectory();

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

        //Initially load the root/start directory
        this.currentDirectory = rootDirectory; //Use new sandbox root
        loadDirectory(currentDirectory);

        //Up directory navigation, but not above guard rail
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

    /*
    * Section 8
    * Loads file/folder list into JList for given directory
    * */
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

    /*
    * Section 9
    * Prevents navigation above the created sandbox root.
    * */
    private void navigateUp() {
        //Make sure user does not go above safe root
        if (currentDirectory == null || currentDirectory.equals(rootDirectory)) {
            setStatus("Already at top-level directory.");
            return;
        }

        Path parent = currentDirectory.getParent();
        if (parent != null && parent.toAbsolutePath().normalize().startsWith(rootDirectory)) {
            loadDirectory(parent); //User stays within sandbox root.
        } else {
            setStatus("Cannot go above root directory."); //Safety message
        }
    }

    public Path getCurrentDirectory() {
        return currentDirectory;
    }

    public void reloadCurrentDirectory() {
        if (currentDirectory != null){
            loadDirectory(currentDirectory);
        }
    }

    public FileItem getSelectedItem(){
        return fileList.getSelectedValue();
    }

    private void setStatus(String message) {
        if (statusConsumer != null) {
            statusConsumer.accept(message);
        }
    }
}
