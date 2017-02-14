package us.dot.its.jpo.ode.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class ImporterFileService implements ImporterService {
    
    /**
     * Attempts to create a directory given a String path.
     * 
     * @param dir
     * @throws IOException
     */
    public void createDirectoryRecursively(Path dir) throws IOException {

        // Attempt to create a directory
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IOException("Exception while trying to create directory: " + e);
        }

        // Verify the directory was successfully created
        if (!dir.toFile().exists()) {
            throw new IOException("Failed to verify directory creation - directory does not exist.");
        }

    }

    /**
     * Attempts to move a file to a backup directory. Prepends the filename with
     * the current time and changes the file extension to 'pbo'.
     * 
     * @param file Path of the file being moved
     * @param backup Path of backup directory
     * @throws IOException
     */
    public void backupFile(Path file, Path backupDir) throws IOException {

        // Check that the destination directory actually exists before moving the file
        if (!backupDir.toFile().exists()) {
            throw new IOException("Backup directory does not exist: " + backupDir);
        }

        // Prepend file name with time and change extension to 'pbo'
        String processedFileName = Integer.toString((int) System.currentTimeMillis()) + "-"
                + file.getFileName().toString().replaceFirst("uper", "pbo");
        Path targetPath = Paths.get(backupDir.toString(), processedFileName);

        // Attempt to move the file to the backup directory
        try {
            Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Unable to move file to backup: " + e);
        }

    }

}
