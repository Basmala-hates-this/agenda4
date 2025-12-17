package com.agenda.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    private static final Gson gson;
    
    static {
        // Build Gson with custom adapters for Java 8 time API
        GsonBuilder gsonBuilder = new GsonBuilder();
        
        // Register LocalDate adapter
        gsonBuilder.registerTypeAdapter(LocalDate.class, 
            (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        
        gsonBuilder.registerTypeAdapter(LocalDate.class,
            (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE));
        
        // Register LocalDateTime adapter
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
            (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
                new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
            (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> 
                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }
    
    /**
     * Read a list of objects from a JSON file
     */
    public static <T> List<T> readListFromFile(String filePath, Type type) {
        File file = new File(filePath);
        
        // Create directory if it doesn't exist
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        
        // If file doesn't exist, return empty list
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (Reader reader = new FileReader(file)) {
            List<T> result = gson.fromJson(reader, type);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error reading from file: " + filePath);
            e.printStackTrace();
            return new ArrayList<>();
        } catch (JsonParseException e) {
            System.err.println("Error parsing JSON from file: " + filePath);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Write a list of objects to a JSON file
     */
    public static <T> void writeListToFile(String filePath, List<T> list) {
        File file = new File(filePath);
        
        // Create directory if it doesn't exist
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            e.printStackTrace();
        }
    }
    
    /**
     * Read a single object from a JSON file
     */
    public static <T> T readObjectFromFile(String filePath, Class<T> clazz) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            return null;
        }
        
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            System.err.println("Error reading object from file: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Write a single object to a JSON file
     */
    public static <T> void writeObjectToFile(String filePath, T object) {
        File file = new File(filePath);
        
        // Create directory if it doesn't exist
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            System.err.println("Error writing object to file: " + filePath);
            e.printStackTrace();
        }
    }
    
    /**
     * Convert object to JSON string
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }
    
    /**
     * Convert JSON string to object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
    
    /**
     * Convert JSON string to list of objects
     */
    public static <T> List<T> fromJsonList(String json, Type type) {
        return gson.fromJson(json, type);
    }
 

/**
 * Read an object from a JSON file
 */
public static <T> T readFromFile(String filePath, Type type) {
    File file = new File(filePath);
    
    if (!file.exists()) {
        return null;
    }
    
    try (Reader reader = new FileReader(file)) {
        return gson.fromJson(reader, type);
    } catch (IOException e) {
        System.err.println("Error reading from file: " + filePath);
        e.printStackTrace();
        return null;
    } catch (JsonParseException e) {
        System.err.println("Error parsing JSON from file: " + filePath);
        e.printStackTrace();
        return null;
    }
}

/**
 * Write an object to a JSON file
 */
public static <T> void writeToFile(String filePath, T object) {
    File file = new File(filePath);
    
    // Create directory if it doesn't exist
    if (file.getParentFile() != null) {
        file.getParentFile().mkdirs();
    }
    
    try (Writer writer = new FileWriter(file)) {
        gson.toJson(object, writer);
    } catch (IOException e) {
        System.err.println("Error writing to file: " + filePath);
        e.printStackTrace();
    }
}
}