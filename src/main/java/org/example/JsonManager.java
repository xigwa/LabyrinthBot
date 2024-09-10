//package org.example;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.HashMap;
//import java.util.Map;
//
//public class JsonManager {
//    private static final String PROGRESS_FILE = "labyrinth_progress.json";
//    private Gson gson = new Gson();
//
//    // Сохранение прогресса игры в JSON
//    public void saveProgress(long chatId, Labyrinth labyrinth) {
//        Map<Long, Labyrinth> progressMap = loadProgressFile(); // Загружаем все сохраненные игры
//        progressMap.put(chatId, labyrinth);  // Обновляем или добавляем прогресс для данного пользователя
//
//        try (FileWriter writer = new FileWriter(PROGRESS_FILE)) {
//            gson.toJson(progressMap, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Загрузка прогресса для конкретного chatId
//    public Labyrinth loadProgress(long chatId) {
//        Map<Long, Labyrinth> progressMap = loadProgressFile();
//        return progressMap.getOrDefault(chatId, null);  // Возвращаем лабиринт для данного пользователя, если он существует
//    }
//
//    // Метод для загрузки всех данных из JSON-файла
//    private Map<Long, Labyrinth> loadProgressFile() {
//        try (FileReader reader = new FileReader(PROGRESS_FILE)) {
//            Type type = new TypeToken<Map<Long, Labyrinth>>() {}.getType();
//            return gson.fromJson(reader, type);  // Читаем и возвращаем данные
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new HashMap<>();  // Если файл не существует, возвращаем пустую карту
//    }
//}
