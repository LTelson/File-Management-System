package org.logannelson.filesystem.ui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.nio.file.Path;

public class FileContentPanel extends JPanel {

    private  final JLabel filePathLabel;
    private final JTextArea textArea;
    private Path currentFile;

    public FileContentPanel() {
        super(new BorderLayout());

        setBorder(BorderFactory.createTitledBorder("File Content"));

        filePathLabel = new JLabel("No file selected:");
        add(filePathLabel, BorderLayout.NORTH);


        textArea = new JTextArea();
        textArea.setEditable(true); //TO-DO: add editing support
        add(new JLabel("File content viewer/editor goes here"), BorderLayout.CENTER);
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
}
