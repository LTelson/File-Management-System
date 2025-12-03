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

    /*
    Writes the given text content into selected file.
    Overwrites existing content!!!
    * */
    void writeFile(Path file, String content) throws IOException;

    /*
    * Creates a new empty directory with the given name in the parent directory
    * Returns path to created directory
    * */
    Path createDirectory(Path parentDirectory, String name) throws IOException;

    /*
    * Creates a new file with the given name and initial content in the parent directory
    * Returns the path to the created file
    * */
    Path createFile(Path parentDirectory, String name, String initialContent) throws IOException;

    /*
    * Renamed a file or directory to the given name
    * Returns to new path
    * */
    Path rename(Path target, String newName) throws  IOException;

    /*
    * Deletes a file or directory. Directories deleted recursively.
    * */
    void delete(Path target) throws IOException;

}

