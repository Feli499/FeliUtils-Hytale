package de.feli490.utils.hytale.sql;

import com.hypixel.hytale.logger.HytaleLogger;
import de.feli490.utils.core.sql.MySQLConnection;
import de.feli490.utils.core.sql.SQLConfig;
import de.feli490.utils.core.sql.SQLConnection;
import de.feli490.utils.hytale.FeliUtilsConfig;
import de.feli490.utils.hytale.utils.HytaleLoggerAdapter;
import java.io.IOException;
import java.sql.SQLException;

public class SqlInitializer {

    private final HytaleLogger hytaleLogger;
    private final FeliUtilsConfig config;

    private MySQLConnection mySQLConnection = null;

    public SqlInitializer(HytaleLogger hytaleLogger, FeliUtilsConfig config) {

        this.hytaleLogger = hytaleLogger.getSubLogger("SqlInitializer");
        this.config = config;
    }

    public void init() throws SQLException {

        if (!config.isEnableSQLProvider() && !config.getStorageToUse()
                                                    .equalsIgnoreCase("mysql")) {
            return;
        }

        SQLConfig sqlConfig = config.getSQLConfig();
        if (sqlConfig == null) {
            return;
        }

        if (config.isEnableSQLProvider())
            SQLConnectionService.set(this);

        mySQLConnection = new MySQLConnection(HytaleLoggerAdapter.createLoggerAdapter(this.hytaleLogger), sqlConfig);
    }

    public void shutdown() throws IOException {

        if (mySQLConnection == null)
            return;

        mySQLConnection.close();
    }

    public SQLConnection loadConnection() {
        return mySQLConnection;
    }

    public boolean hasConnection() {
        return mySQLConnection != null;
    }
}
