package net.tinetwork.tradingcards.tradingcardsplugin.drop;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobDropPoolTest {

    @Test
    void matchesName() {
        MobDropPool pool = new MobDropPool(
                EntityType.ZOMBIE,
                "Jeffrey",
                true,
                1,
                1,
                -1,
                List.of(new DropPoolEntry(DropPoolEntryType.CARD, "zombie", 1))
        );

        assertTrue(pool.matchesName("Jeffrey"));
        assertTrue(pool.matchesName(ChatColor.GREEN + "Jeffrey"));
        assertFalse(pool.matchesName("Another"));
    }

    @Test
    void getDropAmountWhenRangeIsStatic() {
        MobDropPool pool = new MobDropPool(
                EntityType.ZOMBIE,
                null,
                true,
                2,
                2,
                -1,
                List.of(new DropPoolEntry(DropPoolEntryType.CARD, "zombie", 1))
        );

        assertEquals(2, pool.getDropAmount(new Random(1)));
    }

    @Test
    void getEntryForRollUsesWeights() {
        DropPoolEntry zombie = new DropPoolEntry(DropPoolEntryType.CARD, "zombie", 99);
        DropPoolEntry common = new DropPoolEntry(DropPoolEntryType.RARITY, "common", 1);
        MobDropPool pool = new MobDropPool(EntityType.ZOMBIE, null, true, 1, 1, -1, List.of(zombie, common));

        assertEquals(Optional.of(zombie), pool.getEntryForRoll(1));
        assertEquals(Optional.of(zombie), pool.getEntryForRoll(99));
        assertEquals(Optional.of(common), pool.getEntryForRoll(100));
        assertTrue(pool.getEntryForRoll(101).isEmpty());
    }

    @Test
    void getRandomEntryEmptyWhenNoEntries() {
        MobDropPool pool = new MobDropPool(EntityType.ZOMBIE, null, true, 1, 1, -1, List.of());
        assertTrue(pool.getRandomEntry(new Random(1)).isEmpty());
    }
}
