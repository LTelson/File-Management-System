package org.logannelson.filesystem.service;

import org.logannelson.filesystem.model.FileItem;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public Path getStartDirectory() {
        //Start in the user's home directory
        return Path.of(System.getProperty("user.home"));
    }

    @Override
    public List<FileItem> listDirectory(Path directory) throws IOException {
        List<FileItem> items = new ArrayList<>();

        //Use a stream to list directory contents
        try (Stream<Path> stream = Files.list(directory)) {
            stream.forEach(path -> {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    FileItem item = new FileItem(
                            path,
                            path.getFileName().toString(),
                            attrs.isDirectory(),
                            attrs.isDirectory() ? 0L : attrs.size(),
                            attrs.lastModifiedTime()
                    );
                    items.add(item);
                } catch (IOException e) {
                    //If one file fails, skip it for now. Log later.
                }
            });
        }

        return items;
    }

    @Override
    public String readFile(Path file) throws IOException {
        return java.nio.file.Files.readString(file);
    }

    @Override
    public void writeFile(Path file, String content) throws IOException{
        //Simple overwrite.
        Files.writeString(file, content);
    }

    @Override
    public Path createDirectory(Path parentDirectory, String name) throws IOException {
        Path newDir = parentDirectory.resolve(name);
        return Files.createDirectory(newDir);
    }

    @Override
    public Path createFile(Path parentDirectory, String name, String initialContent) throws IOException{
        Path newFile = parentDirectory.resolve(name);
        Path created = Files.createFile(newFile);

        if (initialContent != null && !initialContent.isEmpty()){
            Files.writeString(created, initialContent);
        }
        return created;
    }
}
