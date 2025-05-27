package nl.devcraft.cb;

import picocli.CommandLine;

@io.quarkus.picocli.runtime.annotations.TopCommand
@CommandLine.Command(name = "top", description = "main command", subcommands = {ParseCommand.class})
public class TopCommand {}
