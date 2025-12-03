package org.logannelson.filesystem.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.nio.file.Path;

public class FileContentPanel extends JPanel {

    private final JLabel filePathLabel;
    private final JTextArea textArea;
    private Path currentFile;

    public FileContentPanel() {
        super(new BorderLayout());

        setBorder(BorderFactory.createTitledBorder("File Content"));

        //Top: file path label
        filePathLabel = new JLabel("No file selected");
        add(filePathLabel, BorderLayout.NORTH);

        //Center: text area inside scroll pane
        textArea = new JTextArea();
        textArea.setEditable(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayFile(Path file, String content) {
        this.currentFile = file;
        filePathLabel.setText(file.toString());

        //DEBUG
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
