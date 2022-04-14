import java.sql.*;
public class CreateTable {
    public static void createTable(String DB_URL) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS weather " +
                    "(id integer PRIMARY KEY, " +
                    "localDate text NOT NULL, " +
                    "dayText text NOT NULL, " +
                    "nightText text NOT NULL, " +
                    "minTemperature double NOT NULL, " +
                    "maxTemperature double NOT NULL);");
        } catch (SQLException e) {
            System.out.println("Ошибка создания БД: " + e.getMessage());
        }
    }
}