package nl.devcraft.cb;

import nl.devcraft.cb.ftpdownload.FtpDownloader;
import nl.devcraft.cb.unzip.Unzipper;
import picocli.CommandLine;

@CommandLine.Command(name = "download", description = "download fies to be parsed")
class DownloadCommand implements Runnable {

  private final FtpDownloader ftpDownloader;
  private final Unzipper unzipper;
  private String inputDir;
  private String outputDir;
  private String unzipDir;

  public DownloadCommand(FtpDownloader ftpDownloader, Unzipper unzipper) {
    this.ftpDownloader = ftpDownloader;
    this.unzipper = unzipper;
  }

  @CommandLine.Option(names = "-i", defaultValue = "/", description = "Input directory on ftp")
  public void setInputDir(String inputDir) {
    this.inputDir = inputDir;
  }

  @CommandLine.Option(names = "-o", defaultValue = "download/", description = "Output directory to download to")
  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  @CommandLine.Option(names = "-u", defaultValue = "unzip/", description = "Output directory to unzip to")
  public void setUnzipDir(String unzipDir) {
    this.unzipDir = unzipDir;
  }

  @Override
  public void run() {
    ftpDownloader.download(inputDir, outputDir);
    unzipper.unzipDir(outputDir, unzipDir);
  }

}