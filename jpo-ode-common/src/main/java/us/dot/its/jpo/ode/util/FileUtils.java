package us.dot.its.jpo.ode.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Contains file and directory manipulation code
 */
public class FileUtils {

   private FileUtils() {
   }

   /**
    * Attempts to create a directory given a String path.
    * 
    * @param dir
    * @throws IOException
    */
   public static void createDirectoryRecursively(Path dir) throws IOException {

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
    * @param file
    *           Path of the file being moved
    * @param backup
    *           Path of backup directory
    * @throws IOException
    */
   public static void backupFile(Path file, Path backupDir) throws IOException {

      // Check that the destination directory actually exists before moving the
      // file
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
   
   /**
    * Attempts to move and overwrite a file to destination directory. Throws exception if directory doesn't exist or move failed.
    * @param file
    * @param destination
    * @throws IOException 
    */
   public static void moveFile(Path file, Path destination) throws IOException {
   // Check that the destination directory actually exists before moving the
      // file
      if (!destination.toFile().exists()) {
         throw new IOException("Directory does not exist: " + destination);
      }

      Path targetPath = Paths.get(destination.toString(), file.getFileName().toString());

      // Attempt to move the file to the backup directory
      try {
         Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
         throw new IOException("Unable to move file to backup: " + e);
      }
   }
}
