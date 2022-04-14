import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

// 12/04/2022, Yuriy Lomtev
//1. Добавить поддержку SQLite в проект.
//2. Создать класс-репозиторий, отвечающий за взаимодействие с базой данных.
//3. Организовать запись данных в базу при каждом успешном API запросе. Формат - String localDate, String dayText, String nightText, Double minTemperature, Double maxTemperatue.
//4. Организовать чтение из базы всех данных
//5. Учесть, что соединение всегда нужно закрывать
    public class Main {
        public static void main(String[] args) {
            try {
// запрос по Питеру 5 дней с полученым ключом и в градусах Цельсия:
                URL weatherUrl = new URL("http://dataservice.accuweather.com/forecasts/v1/daily/5day/295212?apikey=Q9xCitbhbtPpwbQrHTPnvmm1VNa7XrWY&metric=true");
// связываемся с сервером:
                HttpURLConnection urlConnection = (HttpURLConnection) weatherUrl.openConnection();
// при правильном ответе на наш запрос (проверил на странице браузера, см. HW6):
                if (urlConnection.getResponseCode() == 200) {
// пытаемся читать поток и записываем содержание:
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                        StringBuilder responseContent = new StringBuilder();
                        String line = "";
// читаем до конца, т.к. не файл, то ждем null:
                        while ((line = reader.readLine()) != null) {
                            responseContent.append(line);
                        }
// закрываем чтение:
//                        reader.close(); Совет: В try-with-resources закрывать этот reader необязательно,
// поскольку эта синтаксическая конструкция, при объявлении в ней объектов потока (в круглых скобках), при выходе из нее, их автоматом закрывает.
// превращаем наш прочитанный JSON в объект для разбора:
                        StringReader forecastJsonReader = new StringReader(responseContent.toString());
                        JsonReader jsonReader = Json.createReader(forecastJsonReader);
                        JsonObject weatherResponseJson = jsonReader.readObject();
// создали БД:
                        final String DB_URL = "jdbc:sqlite:myDB.db";
                        CreateTable.createTable(DB_URL);
// отправляем на разбор и вывод в нужной форме, записываем в БД, потом читаем и выводим:
                        WeatherResponse weatherResponse =new WeatherResponse(weatherResponseJson, DB_URL);
                    } catch (IOException e) {
                        System.out.println("Ошибка1: " + e.getMessage());
// не забываем закончить связь с сервером:
                    } finally {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception ex) {
                System.out.println("Ошибка2: " + ex.getMessage());
// ошибка после стирания данных в таблице БВ
            }
        }
    }

