package db.migration;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Decks;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Users;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.UUID;


/**
 * @author sarhatabaot
 */
//!!! Should not close connection
public class V6__Generate_users_from_decks extends BaseJavaMigration {
    @Override
    public void migrate(final Context context) throws Exception {
        DSLContext dslContext = DSL.using(context.getConnection());
        Result<Record> result = dslContext.select()
                .from(Decks.DECKS).fetch();

        for(Record recordResult: result) {
            final String uuid = recordResult.getValue(Users.USERS.UUID);
            boolean noUser = !dslContext.fetchExists(dslContext.selectFrom(Users.USERS).where(Users.USERS.UUID.eq(uuid)));
            if(noUser) {
                final String name = resolvePlayerName(uuid);
                dslContext.insertInto(Users.USERS)
                        .set(Users.USERS.UUID, uuid)
                        .set(Users.USERS.USERNAME,name)
                        .execute();
            }
        }
    }

    private String resolvePlayerName(final String uuid) {
        final OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid));
        if(player.hasPlayedBefore()) {
            return player.getName();
        }
        return "";
    }
}
