package media.xen.tradingcards.db;

import media.xen.tradingcards.TradingCards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class Database {
	protected final TradingCards plugin;
	protected Connection connection;

	public abstract Connection getSQLConnection();

	public abstract void load();

	public Database(final TradingCards plugin) {
		this.plugin = plugin;
	}

	public void initialize() {
		this.connection = this.getSQLConnection();

		try {
			PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM cards");
			ResultSet rs = ps.executeQuery();
			this.close(ps, rs);
		} catch (SQLException var3) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", var3);
		}

	}

	public Boolean executeStatement(String statement) {
		Connection conn = null;
		PreparedStatement ps = null;

		Boolean var5;
		try {
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			Boolean var4 = !ps.execute();
			return var4;
		} catch (SQLException var15) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), var15);
			var5 = false;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (SQLException var14) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), var14);
				return false;
			}

		}

		return var5;
	}

	public Object queryValue(String statement, String row) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			if (rs.next()) {
				Object var6 = rs.getObject(row);
				return var6;
			}
		} catch (SQLException var17) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), var17);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (SQLException var16) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), var16);
			}

		}

		return null;
	}

	public void insert(String query) {
		String sql = query;

		try {
			Connection conn = this.getSQLConnection();
			Throwable var4 = null;

			try {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				Throwable var6 = null;

				try {
					pstmt.executeUpdate();
				} catch (Throwable var31) {
					var6 = var31;
					throw var31;
				} finally {
					if (pstmt != null) {
						if (var6 != null) {
							try {
								pstmt.close();
							} catch (Throwable var30) {
								var6.addSuppressed(var30);
							}
						} else {
							pstmt.close();
						}
					}

				}
			} catch (Throwable var33) {
				var4 = var33;
				throw var33;
			} finally {
				if (conn != null) {
					if (var4 != null) {
						try {
							conn.close();
						} catch (Throwable var29) {
							var4.addSuppressed(var29);
						}
					} else {
						conn.close();
					}
				}

			}
		} catch (SQLException var35) {
			System.out.println(var35.getMessage());
		}

	}

	public List<Object> queryRow(String statement, String row) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList objects = new ArrayList();

		try {
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();

			while(rs.next()) {
				objects.add(rs.getObject(row));
			}

			ArrayList var7 = objects;
			return var7;
		} catch (SQLException var17) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), var17);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (SQLException var16) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), var16);
			}

		}

		return null;
	}

	public Map<String, List<Object>> queryMultipleRows(String statement, String... row) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Object> objects = new ArrayList<>();
		HashMap<String, List<Object>> map = new HashMap<>();

		try {
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();

			while(rs.next()) {
				String[] var8 = row;
				int var9 = row.length;

				int var10;
				String singleRow;
				for(var10 = 0; var10 < var9; ++var10) {
					singleRow = var8[var10];
					objects.add(rs.getObject(singleRow));
				}

				var8 = row;
				var9 = row.length;

				for(var10 = 0; var10 < var9; ++var10) {
					singleRow = var8[var10];
					map.put(singleRow, objects);
				}
			}

			return map;
		} catch (SQLException var20) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), var20);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (SQLException var19) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), var19);
			}

		}

		return null;
	}

	public void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}

			if (rs != null) {
				rs.close();
			}
		} catch (SQLException var4) {
			Error.close(plugin, var4);
		}

	}

	public void closeConnection() {
		try {
			this.connection.close();
		} catch (SQLException var2) {
			Error.close(plugin, var2);
		}

	}
}