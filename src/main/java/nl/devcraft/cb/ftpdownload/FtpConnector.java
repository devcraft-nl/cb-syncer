package nl.devcraft.cb.ftpdownload;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FtpConnector {

  public FTPClient connect(FtpConnection ftpConnection) throws IOException {
    FTPClient ftpClient = new FTPClient();
    ftpClient.setControlEncoding("UTF-8");
    ftpClient.setAutodetectUTF8(true);

    if (ftpConnection.debug()) {
      ftpClient
          .addProtocolCommandListener(
              new PrintCommandListener(new PrintWriter(System.out, false, UTF_8)));
    }

    ftpClient.connect(ftpConnection.host(), ftpConnection.port());
    FTPClientConfig config = new FTPClientConfig();
    config.setUnparseableEntries(true);
    ftpClient.configure(config);

    int replyCode = ftpClient.getReplyCode();
    if (!FTPReply.isPositiveCompletion(replyCode)) {
      ftpClient.disconnect();
      throw new RuntimeException("Operation failed. Server reply code:" + replyCode);
    }

    // login to ftp server with username and password
    boolean success = ftpClient.login(ftpConnection.user(), ftpConnection.password());
    if (!success) {
      ftpClient.disconnect();
      throw new RuntimeException("Could not login to server!");
    }
    // assign file type according to the server.
    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    ftpClient.enterLocalPassiveMode();

    return ftpClient;
  }
}
