package net.tinetwork.tradingcards.tradingcardsplugin.permissions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionRegistrarTest {

    @Test
    void collectNodesIncludesNestedPermissionConstants() throws IllegalAccessException {
        final List<String> nodes = PermissionRegistrar.collectNodes(TestPermissions.Root.class);

        assertEquals(
                List.of(
                        "cards.root",
                        "cards.root.read",
                        "cards.root.write",
                        "cards.root.write.own",
                        "cards.root.write.all"
                ),
                nodes
        );
    }

    @Test
    void collectNodesReturnsEmptyForTopLevelClass() throws IllegalAccessException {
        assertTrue(PermissionRegistrar.collectNodes(TestPermissions.class).isEmpty());
    }

    private static final class TestPermissions {
        private static final class Root {
            private static final String ROOT = "cards.root";

            private static final class Read {
                private static final String READ = "cards.root.read";
            }

            private static final class Write {
                private static final String WRITE = "cards.root.write";

                private static final class Scope {
                    private static final String OWN = "cards.root.write.own";
                    private static final String ALL = "cards.root.write.all";
                }
            }
        }
    }
}
