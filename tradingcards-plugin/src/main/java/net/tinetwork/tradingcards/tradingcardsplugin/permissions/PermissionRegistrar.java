package net.tinetwork.tradingcards.tradingcardsplugin.permissions;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PermissionRegistrar {
    private PermissionRegistrar() {
        throw new UnsupportedOperationException();
    }

    public static void register(@NotNull String parentPermission, @NotNull Class<?> permissionClass) throws IllegalAccessException {
        final Permission permission = new Permission(parentPermission);
        for (String permissionNode : collectNodes(permissionClass)) {
            if (!permissionNode.equalsIgnoreCase(parentPermission)) {
                permission.getChildren().put(permissionNode, true);
            }
        }
        permission.recalculatePermissibles();
        Bukkit.getPluginManager().addPermission(permission);
    }

    public static @NotNull List<String> collectNodes(@NotNull Class<?> permissionClass) throws IllegalAccessException {
        if (!permissionClass.isMemberClass() && !permissionClass.isLocalClass()) {
            return Collections.emptyList();
        }

        final List<String> permissionNodes = new ArrayList<>();
        for (Field field : permissionClass.getDeclaredFields()) {
            if (field.getType() == String.class && Modifier.isStatic(field.getModifiers())) {
                permissionNodes.add((String) field.get(null));
            }
        }

        for (Class<?> innerClass : permissionClass.getDeclaredClasses()) {
            permissionNodes.addAll(collectNodes(innerClass));
        }

        return permissionNodes;
    }
}
