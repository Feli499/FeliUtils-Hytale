package de.feli490.utils.hytale.sql;

import de.feli490.utils.core.sql.SQLConnection;

public class SQLConnectionService {
    private static SqlInitializer INSTANCE;

    public static boolean hasConnection() {
        return INSTANCE != null;
    }

    public static SQLConnection get() {
        if (!hasConnection())
            return null;
        return SQLConnectionService.INSTANCE.loadConnection();
    }

    static void set(SqlInitializer sqlInitializer) {
        SQLConnectionService.INSTANCE = sqlInitializer;
    }
}
