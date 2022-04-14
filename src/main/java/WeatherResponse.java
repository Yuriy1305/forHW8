import javax.json.JsonArray;
import javax.json.JsonObject;
import java.sql.*;

public class WeatherResponse {
    private String date;
    private double minimumTemperature;
    private double maximumTemperature;
    private String dayTextDescription;
    private String nightTextDescription;

    // немного сократил и упростил для своего понимания:
    public WeatherResponse(JsonObject jsonObject, String DB_URL) throws SQLException {
        System.out.println("Погода на 5 дней в городе Saint Petersburg из JSON:");
// сначала выделяем отдельные дни:
        JsonArray jsonDailyForecastsArray = jsonObject.getJsonArray("DailyForecasts");
        for (int forecast = 0; forecast < jsonDailyForecastsArray.size(); forecast++) {
            JsonObject jsonForecast = jsonDailyForecastsArray.getJsonObject(forecast);
// потом разбираем каждый день по ключам:
            date = jsonForecast.getString("Date");
            minimumTemperature = jsonForecast
                    .getJsonObject("Temperature")
                    .getJsonObject("Minimum")
                    .getJsonNumber("Value")
                    .doubleValue();
            maximumTemperature = jsonForecast
                    .getJsonObject("Temperature")
                    .getJsonObject("Maximum")
                    .getJsonNumber("Value")
                    .doubleValue();
            dayTextDescription = jsonForecast
                    .getJsonObject("Day")
                    .getString("IconPhrase");
            nightTextDescription = jsonForecast
                    .getJsonObject("Night")
                    .getString("IconPhrase");
// выводим прочитанное в удобном виде:
            System.out.println(date.substring(0, 10) + "-го ожидается днём " + dayTextDescription +
                    ", ночью " + nightTextDescription +
                    " при минимальной температауре " + minimumTemperature +
                    " и максимальной " + maximumTemperature + "°C.");
// для внесения данных в БД используем эти же переменные, после выполнения запроса  выведем дублями из DB:
            try (Connection connection = DriverManager.getConnection(DB_URL);
                 PreparedStatement preparedStatement = connection.prepareStatement
                         ("INSERT INTO weather(localDate, dayText, nightText, minTemperature, maxTemperature) VALUES(?,?,?,?,?);")
            ) {
// вносим данные из JSON как в БД:
                preparedStatement.setString(1, date.substring(0, 10));
                preparedStatement.setString(2, dayTextDescription);
                preparedStatement.setString(3, nightTextDescription);
                preparedStatement.setDouble(4, minimumTemperature);
                preparedStatement.setDouble(5, maximumTemperature);
                preparedStatement.execute();
            } catch (SQLException ex) {
                System.out.println("Ошибка заполнения БД: " + ex.getMessage());
            }
        }
// а теперь выводим данные из БД:
        System.out.println("\nПогода на 5 дней в городе Saint Petersburg из БД:");
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM weather");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("localDate") + "-го ожидается днём "
                        + resultSet.getString("dayText") +
                        ", ночью " + resultSet.getString("nightText") +
                        " при минимальной температауре " + resultSet.getDouble("minTemperature") +
                        " и максимальной " + resultSet.getDouble("maxTemperature") + "°C.");
            }
// стираем данные, чтобы не плодить строки в БД:
//                        statement.executeQuery("DELETE FROM weather");
        } catch (SQLException e) {
            System.out.println("Ошибка чтения из БД: " + e.getMessage());
        }
// стираем данные, чтобы не плодить строки в БД:
        Connection connection = DriverManager.getConnection(DB_URL);
        Statement statement = connection.createStatement();
        statement.executeQuery("DELETE FROM weather"); // вроде стирает...
    }
}




