package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.component.Ref;

import com.hypixel.hytale.component.Store;

import com.hypixel.hytale.server.core.command.system.CommandContext;

import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;

import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import com.hypixel.hytale.server.core.universe.world.World;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.player.PlayerStats;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;


import javax.annotation.Nonnull;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.LM;


public class PlayerInfoCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final OptionalArg<String> targetPlayer;
    public PlayerInfoCommand(HytaleFoundations hytaleFoundations) {
        super("playerinfo", "Shows various info of a player.");
        this.addAliases("pinfo");
        this.requirePermission(Permissions.PLAYER_INFO.getPermission());
        this.hytaleFoundations = hytaleFoundations;
        targetPlayer = withOptionalArg("target", "Target player whose info will be showed.", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(playerRef);
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = null;
        Map<String, String> profileInfo;
        Map<String, String> statsInfo;
        if(commandContext.get(this.targetPlayer) == null) {
            profileInfo = getPlayerProfileInfo(hytaleFoundations.getPlayerDataManager().getPlayerProfile(senderUsername));
            statsInfo = getPlayerStatInfo(hytaleFoundations.getPlayerDataManager().getPlayerStats(senderUsername));
            hytaleFoundations.getPlayerDataManager().savePlayTime(senderUsername);
        } else {
            targetUsername = commandContext.get(this.targetPlayer);
            if(hytaleFoundations.getPlayerDataManager().doesPlayerExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return;
            }
            profileInfo = getPlayerProfileInfo(hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername));
            statsInfo = getPlayerStatInfo(hytaleFoundations.getPlayerDataManager().getPlayerStats(targetUsername));
            hytaleFoundations.getPlayerDataManager().savePlayTime(targetUsername);
        }
        if(profileInfo.isEmpty() || statsInfo.isEmpty()) {
            if(targetUsername == null) commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_INFO_LOAD_FAIL, senderUsername));
            else commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_INFO_LOAD_FAIL, targetUsername));
        }
        commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_INFO, senderUsername));
        commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_INFO_PROFILE));
        for(Map.Entry<String, String> entry : profileInfo.entrySet()) {
            if(!isAdmin && entry.getValue().equals("Admin")) continue;
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.LIST_ITEM, entry.getKey()));
        }
        commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_INFO_STATS));
        for(Map.Entry<String, String> entry : statsInfo.entrySet()) {
            if(!isAdmin && entry.getValue().equals("Admin")) continue;
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.LIST_ITEM, entry.getKey()));
        }
    }

    private Map<String, String> getPlayerProfileInfo(PlayerProfile profile) {
        Map<String, String> profileInfo = new LinkedHashMap<>();
        profileInfo.put("&e&lUsername(s): &r&f" + String.join(", ", profile.getUsername()), "Player");
        profileInfo.put("&b&lUUID: &r&f" + profile.getUuid(), "Admin");
        profileInfo.put("&e&lRank: " + (profile.getRankId() == null || profile.getRankId().isEmpty() ? "&r&f&oNone": " &r&f")
                + (profile.getRankId() != null ? hytaleFoundations.getRankManager().getRankDisplay(profile.getRankId()) : "None"), "Player");
        profileInfo.put("&b&lLanguage: &r&f" + (profile.getLanguage() != null ? profile.getLanguage() : "Default"), "Admin");
        profileInfo.put("&e&lNickname: " + (profile.getNickname() != null  && !profile.getNickname().isEmpty() ? profile.getNickname() : "&r&f&oNone"), "Player");
        profileInfo.put("&b&lName Color Code: " + (profile.getUsernameColorCode() == null || profile.getUsernameColorCode().isEmpty() ? "": "&r&-")
                + (profile.getUsernameColorCode() != null ? profile.getUsernameColorCode() : "Default"), "Admin");
        profileInfo.put("&e&lActive Prefixes: &r&f" + profile.getActivePrefix().size() + " / " + profile.getMaxPrefix(), "Player");
        profileInfo.put("&e&lActive Suffixes: &r&f" + profile.getActiveSuffix().size() + " / " + profile.getMaxSuffix(), "Player");
        profileInfo.put("&b&lShow Nickname: &r&f" + profile.isShowNickname(), "Admin");
        profileInfo.put("&b&lShow Prefix: &r&f" + profile.isShowPrefix(), "Admin");
        profileInfo.put("&b&lShow Suffix: &r&f" + profile.isShowSuffix(), "Admin");
        profileInfo.put("&c&lIs Muted: &r&f" + profile.isMuted(), "Player");
        if (profile.isMuted()) profileInfo.put("&c&lMute Duration: &r&f" + profile.getMuteDuration() + "ms", "Admin");
        profileInfo.put("&c&lIs Silenced: &r&f" + profile.isSilenced(), "Admin");
        profileInfo.put("&b&lBlocked Players: &r&f" + profile.getBlockedPlayers().size(), "Admin");
        profileInfo.put("&b&lIgnored Players: &r&f" + profile.getIgnoredPlayers().size(), "Admin");
        profileInfo.put("&c&lGod Mode: &r&f" + profile.isGodModeEnabled(), "Admin");
        profileInfo.put("&b&lVanished: &r&f" + profile.isVanished(), "Admin");
        profileInfo.put("&b&lFlying: &r&f" + profile.isFlying(), "Admin");
        profileInfo.put("&b&lAnonymous: &r&f" + profile.isAnonymous(), "Admin");
        profileInfo.put("&b&lHomes Count: &r&f" + profile.getHomes().size(), "Admin");
        profileInfo.put("&b&lBalances Tracked: &r&f" + profile.getBalances().size(), "Admin");
        return profileInfo;
    }

    private Map<String, String> getPlayerStatInfo(PlayerStats stats) {
        Map<String, String> statsInfo = new LinkedHashMap<>();
        statsInfo.put("&e&lFirst Joined: &r&f" + TimeUtils.getDate(stats.getFirstJoined()), "Player");
        statsInfo.put("&b&lLast Joined: &r&f" + TimeUtils.getDate(stats.getLastJoined()), "Admin");
        statsInfo.put("&e&lTotal Playtime: &r&f" + TimeUtils.formatDuration(stats.getPlayTimeMillis()), "Player");
        statsInfo.put("&b&lCollect Stats: &r&f" + stats.isCollectStats(), "Admin");
        statsInfo.put("&c&lPlayer Kills: &r&f" + stats.getPlayerKills(), "Player");
        statsInfo.put("&c&lMob Kills: &r&f" + stats.getMobKills(), "Player");
        statsInfo.put("&c&lTotal Deaths: &r&f" + stats.getTotalDeaths(), "Player");
        statsInfo.put("&c&lDamage Given (PVP): &r&f" + stats.getDamageGivenPvp(), "Player");
        statsInfo.put("&c&lDamage Taken (PVP): &r&f" + stats.getDamageTakenPvp(), "Player");
        statsInfo.put("&c&lDamage Given (PVE): &r&f" + stats.getDamageGivenPve(), "Player");
        statsInfo.put("&c&lDamage Taken (PVE): &r&f" + stats.getDamageTakenPve(), "Player");
        statsInfo.put("&a&lBlocks Broken: &r&f" + stats.getBlocksBroken(), "Player");
        statsInfo.put("&a&lBlocks Placed: &r&f" + stats.getBlocksPlaced(), "Player");
        statsInfo.put("&a&lItems Crafted: &r&f" + stats.getItemsCrafted(), "Player");
        statsInfo.put("&a&lItems Broken: &r&f" + stats.getItemsBroken(), "Player");
        statsInfo.put("&a&lDistance Walked: &r&f" + String.format("%.2f", stats.getDistanceWalked()), "Player");
        statsInfo.put("&b&lMessages Sent: &r&f" + stats.getTotalMessagesSent(), "Player");
        statsInfo.put("&c&lReported By Players: &r&f" + stats.getTotalTimesReportedByPlayers(), "Admin");
        return statsInfo;
    }
}