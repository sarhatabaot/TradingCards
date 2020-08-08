package media.xen.tradingcards;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class Database {

    protected Connection connection;

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM cards");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);

        } catch (SQLException ex) {
            TradingCards.getInstance().getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }
    
    public Boolean executeStatement(String statement) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);
            return !ps.execute();
        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                return false;
            }
        }
    }

    public Object queryValue(String statement, String row) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);

            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getObject(row);
            }
        } catch (SQLException ex) {
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
            }
        }
        return null;
    }

    public void insert(String query) {
        String sql = query;

        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Object> queryRow(String statement, String row) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Object> objects = new ArrayList<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);

            rs = ps.executeQuery();
            while (rs.next()) {
                objects.add(rs.getObject(row));
            }
            return objects;
        } catch (SQLException ex) {
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
            }
        }
        return null;
    }

    public Map<String, List<Object>> queryMultipleRows(String statement, String... row) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Object> objects = new ArrayList<>();
        Map<String, List<Object>> map = new HashMap<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);

            rs = ps.executeQuery();
            while (rs.next()) {
                for (String singleRow : row) {
                    objects.add(rs.getObject(singleRow));
                }

                for (String singleRow : row) {
                    map.put(singleRow, objects);
                }

            }
            return map;
        } catch (SQLException ex) {
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
            }
        }
        return null;
    }

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
        }
    }


    /**
     * Close the current connection to the database. The database will need to be
     * re-initialized if this is used. When intializing using the main class, it
     * will delete this current object and create a new object connected to the db.
     * If you'd like to reload this db without trashing the database object, invoke
     * the load() method through the global map of databases. E.g
     * getDatabase("name").load();.
     *
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
        }
    }
}