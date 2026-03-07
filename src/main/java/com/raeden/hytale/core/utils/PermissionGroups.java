package com.raeden.hytale.core.utils;

import java.util.Set;

public enum PermissionGroups {
    OWNER(Set.of("hytalefoundations.*")),
    ADMIN(Set.of("hytalefoundations.admin.*")),
    MANAGER(Set.of("hytalefoundations.admin.*")),
    HELPER(Set.of("hytalefoundations.helper.*")),
    PLAYER(Set.of("hytalefoundations.player.*"));

    private final Set<String> permissionSet;
    PermissionGroups (Set<String> permissionSet) {
        this.permissionSet = permissionSet;
    }
    public Set<String> getPermissionSet() {return permissionSet;}
}
