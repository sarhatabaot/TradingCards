package net.tinetwork.tradingcards.tradingcardsplugin.permissions;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
                field.setAccessible(true);
                permissionNodes.add((String) field.get(null));
            }
        }

        if (permissionNodes.isEmpty()) {
            return Collections.emptyList();
        }

        final Class<?>[] declaredClasses = permissionClass.getDeclaredClasses();
        Arrays.sort(declaredClasses, Comparator.comparing(Class::getSimpleName));
        for (Class<?> innerClass : declaredClasses) {
            permissionNodes.addAll(collectNodes(innerClass));
        }

        return permissionNodes;
    }
}
