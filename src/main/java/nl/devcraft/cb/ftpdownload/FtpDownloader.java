package nl.devcraft.cb.ftpdownload;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FtpDownloader {

  private final FtpConnector ftpConnector;

  @ConfigProperty(name = "ftp.user")
  String user;

  @ConfigProperty(name = "ftp.password")
  String password;

  @ConfigProperty(name = "ftp.host")
  String host;

  @ConfigProperty(name = "ftp.port")
  String port;


  FtpDownloader() {
    ftpConnector = new FtpConnector();
  }

  public void download(String remotePath, String localPath) {
    try {
      FTPClient ftpClient = ftpConnector.connect(host, user, password, Integer.parseInt(port));
      goToDir(ftpClient, remotePath);
      FTPFile[] files = ftpClient.listFiles();
      Stream.of(files)
          .filter(onyxfFilePredicate())
          .forEach(file -> downloadFile(file, localPath, ftpClient));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void downloadFile(FTPFile file, String localPath, FTPClient ftpClient) {
    try {
      Path localFilePath = Path.of(localPath, file.getName());
      File fileObj = localFilePath.toFile();
      if (!fileObj.exists()) {
        fileObj.createNewFile();
      }

      try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileObj))) {
        boolean succesfullyRetrieved = ftpClient.retrieveFile(file.getName(), outputStream);
        System.out.printf("%s file is downloaded : %s", file.getName(), succesfullyRetrieved);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Predicate<FTPFile> onyxfFilePredicate() {
    return file -> !file.isDirectory() && file.getName().endsWith(".xml");
  }

  private static void goToDir(FTPClient ftpClient, String remotePath) throws IOException {
    if (!ftpClient.changeWorkingDirectory(remotePath)) {
      throw new RuntimeException("Remote directory not found.");
    }
  }
}
