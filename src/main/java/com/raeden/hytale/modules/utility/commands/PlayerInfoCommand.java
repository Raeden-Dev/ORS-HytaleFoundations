package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.component.Ref;

import com.hypixel.hytale.component.Store;

import com.hypixel.hytale.server.core.command.system.CommandContext;

import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;

import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

import com.hypixel.hytale.server.core.entity.UUIDComponent;

import com.hypixel.hytale.server.core.entity.entities.Player;

import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import com.hypixel.hytale.server.core.universe.world.World;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.raeden.hytale.HytaleFoundations;


import javax.annotation.Nonnull;


public class PlayerInfoCommand extends AbstractPlayerCommand {

    private HytaleFoundations plugin;



    private final OptionalArg<PlayerRef> targetPlayerArg = this.withOptionalArg("player", "Check player's stats", ArgTypes.PLAYER_REF);



    public PlayerInfoCommand() {

        super("pinfo", "Checks player info.");

    }



    @Override

    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        Player player = store.getComponent(ref, Player.getComponentType());

        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());


        PlayerRef targetPlayerRef = this.targetPlayerArg.get(commandContext);

        Ref<EntityStore> targetRef = targetPlayerRef.getReference();


    }

}