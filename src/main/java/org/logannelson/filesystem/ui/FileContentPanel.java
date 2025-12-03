package org.logannelson.filesystem.ui;

import org.logannelson.filesystem.service.FileSystemService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class FileContentPanel extends JPanel {

    private final FileSystemService fileSystemService;
    private final Consumer<String> statusConsumer;

    private final JLabel filePathLabel;
    private final JTextArea textArea;
    private Path currentFile;

    public FileContentPanel(FileSystemService fileSystemService, Consumer<String> statusConsumer) {
        super(new BorderLayout());
        this.fileSystemService = fileSystemService;
        this.statusConsumer = statusConsumer;

        setBorder(BorderFactory.createTitledBorder("File Content"));

        //Top panel: file path + Save button
        JPanel topPanel = new JPanel(new BorderLayout());

        filePathLabel = new JLabel("No file selected");
        topPanel.add(filePathLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        JButton saveButton = new JButton("Save");
        buttonPanel.add(saveButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //Center: text area inside scroll pane
        textArea = new JTextArea();
        textArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        //Save button behavior
        saveButton.addActionListener(e -> saveCurrentFile());
    }

    public void displayFile(Path file, String content) {
        this.currentFile = file;
        filePathLabel.setText(file.toString());
        textArea.setText(content);
        textArea.setCaretPosition(0); // scroll to top
    }

    public void clearContent() {
        this.currentFile = null;
        filePathLabel.setText("No file selected");
        textArea.setText("");
    }

    public Path getCurrentFile() {
        return currentFile;
    }

    public String getCurrentContent() {
        return textArea.getText();
    }

    private void saveCurrentFile() {
        if (currentFile == null) {
            setStatus("No file selected to save.");
            return;
        }

        try {
            String content = textArea.getText();
            fileSystemService.writeFile(currentFile, content);
            setStatus("Saved: " + currentFile);
        } catch (IOException e) {
            setStatus("Error saving file: " + e.getMessage());
        }
    }

    private void setStatus(String message) {
        if (statusConsumer != null) {
            statusConsumer.accept(message);
        }
    }
}
