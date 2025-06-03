package nl.devcraft.cb;

import picocli.CommandLine;

@io.quarkus.picocli.runtime.annotations.TopCommand
@CommandLine.Command(name = "cb-sync", description = "main command", subcommands = {ParseCommand.class,
                                                                                    UpdateCommand.class,
                                                                                    DownloadCommand.class})
public class TopCommand {}
