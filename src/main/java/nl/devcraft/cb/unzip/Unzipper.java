package nl.devcraft.cb.unzip;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@ApplicationScoped
public class Unzipper {

  public void unzipDir(String sourceDir, String outputDir) {
    File sourceDirFile = new File(sourceDir);
    File outputDirFile = new File(outputDir);

    Stream.of(Objects.requireNonNull(sourceDirFile.listFiles()))
        .filter(file -> !file.isDirectory() && file.getName().endsWith(".zip"))
        .forEach(file -> {
          try {
            unzipFile(file, outputDirFile);
          } catch (IOException e) {
            System.out.println("Error unzipping file: " + file.getAbsolutePath());
          }
        });
  }

  private void unzipFile(File file, File outputDir) throws IOException {
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        if (checkIfFileWasAlreadyProcessed(outputDir, zipEntry)) {
          System.out.println("Zipped contents already processed: " + file.getAbsolutePath());
          return;
        }
        writeUnzippedFile(zis, zipEntry, outputDir);
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
    }
  }

  private static boolean checkIfFileWasAlreadyProcessed(File outputDir, ZipEntry zipEntry) {
    File fileToWrite = new File(outputDir, zipEntry.getName());
    File fileToWriteAsProcessed = new File(outputDir, zipEntry.getName() + ".processed");
    return fileToWrite.exists() || fileToWriteAsProcessed.exists();
  }

  private void writeUnzippedFile(ZipInputStream zis, ZipEntry zipEntry, File outputDir) throws IOException {
    File newFile = newFile(outputDir, zipEntry);
    try (FileOutputStream fos = new FileOutputStream(newFile)) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }
}
