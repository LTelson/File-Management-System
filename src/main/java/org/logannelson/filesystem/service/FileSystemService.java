package org.logannelson.filesystem.service;
//Handles core file operations: CRUD, list directories.
import org.logannelson.filesystem.model.FileItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileSystemService {

    // Returns the starting directory.
    Path getStartDirectory();

    /** Returns the children of the given directory.
     */
    List<FileItem> listDirectory(Path directory) throws IOException;

    //Reads the entire contents of a text file as a String
    String readFile(Path file) throws IOException;
}

