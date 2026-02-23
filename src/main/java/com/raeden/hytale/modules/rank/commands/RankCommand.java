package com.raeden.hytale.modules.rank.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;

public class RankCommand extends AbstractCommandCollection {
    private final HytaleFoundations hytaleFoundations;
    public RankCommand(HytaleFoundations hytaleFoundations) {
        super("rank", "Agrument for all rank related commands.");
        this.hytaleFoundations = hytaleFoundations;
    }
}
