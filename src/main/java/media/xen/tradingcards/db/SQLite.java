package media.xen.tradingcards.db;

import media.xen.tradingcards.TradingCards;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends Database {
	private final String databaseName;
	private final String createStatement;
	private final File dataFolder;

	public SQLite(final TradingCards plugin, String databaseName, String createStatement, File dataFolder) {
		super(plugin);
		this.databaseName = databaseName;
		this.createStatement = createStatement;
		this.dataFolder = dataFolder;
	}

	public Connection getSQLConnection() {
		File folder = new File(this.dataFolder, this.databaseName + ".db");
		if (!folder.exists()) {
			try {
				folder.createNewFile();
			} catch (IOException var5) {
				plugin.getLogger().log(Level.SEVERE, "File write error: " + this.databaseName + ".db");
			}
		}

		try {
			if (this.connection != null && !this.connection.isClosed()) {
				return this.connection;
			}

			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + folder);
			return this.connection;
		} catch (SQLException var3) {
			plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", var3);
		} catch (ClassNotFoundException var4) {
			plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}

		return null;
	}

	public void load() {
		this.connection = this.getSQLConnection();

		try (Statement s = this.connection.createStatement()){ ;
			final String createTestTable = "CREATE TABLE IF NOT EXISTS test (`test` varchar(32) NOT NULL,PRIMARY KEY (`test`));";
			s.executeUpdate(createTestTable);
			s.executeUpdate(this.createStatement);
		} catch (SQLException var2) {
			var2.printStackTrace();
		}

		this.initialize();
	}

	public File getDataFolder() {
		return this.dataFolder;
	}
}
