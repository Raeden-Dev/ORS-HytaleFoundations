package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.LM;

public class NicknameCommand extends AbstractCommandCollection {
    private final HytaleFoundations hytaleFoundations;
    public NicknameCommand(HytaleFoundations hytaleFoundations) {
        super("nick", "Argument to access rest of /nick functions");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(PermissionNodes.NICK.getPermission());
        this.setAllowsExtraArguments(true);
        this.addSubCommand(new NicknameSetCommand(hytaleFoundations));
        this.addSubCommand(new NicknameClearCommand(hytaleFoundations));
    }
    public static class NicknameSetCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> nickname;
        public NicknameSetCommand(HytaleFoundations hytaleFoundations) {
            super("set", "Set nickname of other players");
            this.hytaleFoundations = hytaleFoundations;
            this.requirePermission(PermissionNodes.ADMIN.getPermission());
            targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
            nickname = withRequiredArg("Nickname", "Nickname to set on target.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetUsername = commandContext.get(this.targetPlayer);
            String nickname = commandContext.get(this.nickname);
            PlayerProfile targetProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
            if(targetProfile == null) {
                commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG,false, targetUsername));
                return;
            }
            if(!hytaleFoundations.getChatManager().validateNickname(playerRef, nickname)) {
                return;
            }
            targetProfile.setNickname(nickname);
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.NICKNAME_SET,false, targetUsername, nickname));
        }
    }
    public static class NicknameClearCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public NicknameClearCommand(HytaleFoundations hytaleFoundations) {
            super("clear", "Set nickname of other players");
            this.hytaleFoundations = hytaleFoundations;
            this.requirePermission(PermissionNodes.ADMIN.getPermission());
            this.setAllowsExtraArguments(true);
            targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayer = commandContext.get(this.targetPlayer);
            String senderUsername = commandContext.sender().getDisplayName();
            if(targetPlayer == null || targetPlayer.isEmpty()) {
                PlayerProfile senderProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(senderUsername);
                if(senderProfile != null) {
                    senderProfile.setNickname("");
                }
            } else {
                PlayerProfile targetProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetPlayer);
                if(targetProfile == null) {
                    commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG,false, targetPlayer));
                    return;
                }
                targetProfile.setNickname("");
                commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.NICKNAME_CLEARED,false, targetPlayer));
            }
        }
    }

}
// ~DEPRECATED
//@Override
//protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
//    String senderUsername = commandContext.sender().getDisplayName();
//    String[] rawMessage = commandContext.getInputString().split("\\s+", 2);
//    if(rawMessage.length == 1) return;
//    String nicknameContent = rawMessage[1];
//    if(!hytaleFoundations.getChatManager().validateNickname(playerRef, nicknameContent)) {
//        return;
//    }
//    PlayerProfile senderProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(senderUsername);
//    if(senderProfile != null) {
//        senderProfile.setNickname(nicknameContent);
//    }
//}