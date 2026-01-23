package de.feli490.utils.hytale.sql;

import de.feli490.utils.core.sql.SQLConnection;

public class SQLConnectionProvider {
    private static SqlInitializer INSTANCE;

    public static boolean hasConnection() {
        return INSTANCE != null;
    }

    public static SQLConnection get() {
        return SQLConnectionProvider.INSTANCE.loadConnection();
    }

    static void set(SqlInitializer sqlInitializer) {
        SQLConnectionProvider.INSTANCE = sqlInitializer;
    }
}
