package org.logannelson.filesystem.model;
//Represents a file or directory in the system.
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public class FileItem {
    private final Path path;
    private final String name;
    private final boolean directory;
    private final long size;
    private final FileTime lastModifiedTime;

    public FileItem(Path path, String name, boolean directory, long size, FileTime lastModifiedTime) {
        this.path = path;
        this.name = name;
        this.directory = directory;
        this.size = size;
        this.lastModifiedTime = lastModifiedTime;
    }

    //Getters
    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public long getSize() {
        return size;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    //Methods//
    @Override
    public String toString() {
        //Placeholder
        return directory ? "[DIR] " + name : name;
    }
}

