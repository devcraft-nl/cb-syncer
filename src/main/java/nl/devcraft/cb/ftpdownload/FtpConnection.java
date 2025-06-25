package nl.devcraft.cb.ftpdownload;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record FtpConnection(String host, String user, String password, int port, boolean debug) {}