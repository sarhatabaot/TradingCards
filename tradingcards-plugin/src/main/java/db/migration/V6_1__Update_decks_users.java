package db.migration;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Decks;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Users;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

/**
 * @author sarhatabaot
 */
/*
 Will update existing decks uuids with new id for users.
 */
public class V6_1__Update_decks_users extends BaseJavaMigration {
    @Override
    public void migrate(final Context context) throws Exception {
        DSLContext dslContext = DSL.using(context.getConnection());
        Result<Record> userResult = dslContext.select()
                .from(Users.USERS).fetch();
        for(Record recordResult: userResult) {
            final String uuid = recordResult.getValue(Users.USERS.UUID);
            dslContext.alterTable(Decks.DECKS)
                    .addColumn()
        }
        // we could add a new column,
        // set the id for that column,
        // then remove the old column
    }
}
