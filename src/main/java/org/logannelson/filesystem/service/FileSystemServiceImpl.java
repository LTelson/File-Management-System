package org.logannelson.filesystem.service;

import org.logannelson.filesystem.model.FileItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileSystemServiceImpl implements FileSystemService {

    /**
     * All file operations are restricted to this root directory.
     * This prevents the app from modifying system files or anything
     * outside the sandbox.
     */
    private final Path rootDirectory;

    public FileSystemServiceImpl() {
        //Sandbox root: <user.home>/FileSystemSandbox
        this.rootDirectory = Path
                .of(System.getProperty("user.home"), "FileSystemSandbox")
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize sandbox root: " + rootDirectory, e);
        }
    }

    /**
     * Ensures the given path stays under the sandbox root.
     * Returns a normalized absolute path if allowed, otherwise throws.
     */
    private Path ensureUnderRoot(Path path) throws IOException {
        Path normalizedRoot = rootDirectory;
        Path normalizedPath = path.toAbsolutePath().normalize();

        if (!normalizedPath.startsWith(normalizedRoot)) {
            throw new IOException("Operation outside sandbox is not allowed: " + normalizedPath);
        }
        return normalizedPath;
    }

    @Override
    public Path getStartDirectory() {
        // Start in the sandbox root
        return rootDirectory;
    }

    @Override
    public List<FileItem> listDirectory(Path directory) throws IOException {
        Path dir = ensureUnderRoot(directory);

        List<FileItem> items = new ArrayList<>();

        try (Stream<Path> stream = Files.list(dir)) {
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
        Path safeFile = ensureUnderRoot(file);
        return Files.readString(safeFile);
    }

    @Override
    public void writeFile(Path file, String content) throws IOException {
        Path safeFile = ensureUnderRoot(file);
        Files.writeString(safeFile, content);
    }

    @Override
    public Path createDirectory(Path parentDirectory, String name) throws IOException {
        Path safeParent = ensureUnderRoot(parentDirectory);
        Path newDir = safeParent.resolve(name);
        return Files.createDirectory(newDir);
    }

    @Override
    public Path createFile(Path parentDirectory, String name, String initialContent) throws IOException {
        Path safeParent = ensureUnderRoot(parentDirectory);
        Path newFile = safeParent.resolve(name);
        Path created = Files.createFile(newFile);

        if (initialContent != null && !initialContent.isEmpty()){
            Files.writeString(created, initialContent);
        }

        return created;
    }

    @Override
    public Path rename(Path target, String newName) throws IOException {
        Path safeTarget = ensureUnderRoot(target);
        Path parent = safeTarget.getParent();
        if (parent == null) {
            throw new IOException("Cannot rename root path: " + safeTarget);
        }
        Path newPath = parent.resolve(newName).toAbsolutePath().normalize();
        ensureUnderRoot(newPath);
        return Files.move(safeTarget, newPath);
    }

    @Override
    public void delete(Path target) throws IOException {
        Path safeTarget = ensureUnderRoot(target);

        if (Files.isDirectory(safeTarget)) {
            //Recursively delete directory contents first
            try (var walk = Files.walk(safeTarget)) {
                walk
                        // delete children before parents
                        .sorted((p1, p2) -> p2.getNameCount() - p1.getNameCount())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException io) {
                    throw io;
                }
                throw e;
            }
        } else {
            Files.delete(safeTarget);
        }
    }
}
