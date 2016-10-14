package test.util;

import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by nyapparova on 11.10.2016.
 */
public class DBUtil {

    private static Properties connectionProps;

    static {
        try {
            connectionProps = getProperties();
            Class.forName(connectionProps.getProperty("DRIVER_CLASS"));
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    private static DataSource dataSource;

    public static synchronized DataSource getDataSource() throws SQLException {
        if (dataSource == null) {
            OracleDataSource ds = new OracleDataSource();
            ds.setURL(connectionProps.getProperty("URL"));
            ds.setUser(connectionProps.getProperty("USERNAME"));
            ds.setPassword(connectionProps.getProperty("PASSWORD"));
            dataSource = ds;
        }
        return dataSource;
    }

    private static Properties getProperties() throws IOException {
        Properties connectionProps = new Properties();
        try (InputStream stream = new FileInputStream("db.properties")) {
            connectionProps.load(stream);
        }
        return connectionProps;
    }
}
