package com.raeden.hytale.modules.chat.commands.affix;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.AffixManager;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.langManager;

public class AffixListCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public AffixListCommand(HytaleFoundations hytaleFoundations) {
        super("list", "Show list of all available affixes");
        this.hytaleFoundations = hytaleFoundations;
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
        if(affixManager == null) return;
        if(affixManager.getAffixMap() == null || affixManager.getAffixMap().isEmpty()) return;
        String playerUsername = commandContext.sender().getDisplayName();
        commandContext.sender().sendMessage(langManager.getMessage(playerUsername, LangKey.GENERAL_LIST, false, "affix(s)"));
        for(Map.Entry<String, AffixManager.PlayerAffix> entry : affixManager.getAffixMap().entrySet()) {
            commandContext.sender().sendMessage(langManager.getMessage(playerUsername, LangKey.GENERAL_LIST_ITEM, false,
                    entry.getValue().getDisplayText() + " &r&e&l[ID: " + entry.getKey() + "]"));
        }
    }
}
