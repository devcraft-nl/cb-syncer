package nl.devcraft.cb;

import nl.devcraft.cb.ftpdownload.FtpDownloader;
import picocli.CommandLine;

@CommandLine.Command(name = "download", description = "download fies to be parsed")
class DownloadCommand implements Runnable {

  private final FtpDownloader ftpDownloader;
  private String inputDir;
  private String outputDir;

  public DownloadCommand(FtpDownloader ftpDownloader) {
    this.ftpDownloader = ftpDownloader;
  }

  @CommandLine.Option(names = "-i", defaultValue = "/", description = "Input directory on ftp")
  public void setInputDir(String inputDir) {
    this.inputDir = inputDir;
  }

  @CommandLine.Option(names = "-o", defaultValue = "download/", description = "Output directory to download to")
  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  @Override
  public void run() {
    ftpDownloader.download(inputDir, outputDir);
  }

}