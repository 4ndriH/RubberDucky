package services;

import java.util.*;
import java.io.*;

public class StorageHandler {
    public static void writeData(String path, String file, ArrayList<String> data) {
        ArrayList<String> oldData = readData(path, file);
        try (PrintStream writer = new PrintStream(path + file + ".rd")){
            for (String s : oldData)
                writer.println(s);
            for (String s : data)
                writer.println(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readData(String path, String file) {
        ArrayList<String> data = new ArrayList<>();
        if (new File(path, file + ".rd").exists()) {
            try (Scanner scanner = new Scanner(new File(path + file + ".rd"))) {
                while (scanner.hasNextLine())
                    data.add(scanner.nextLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static void deleteLine(String path, String file, int idx) {
        ArrayList<String> data = readData(path, file);
        data.remove(idx);
        writeData(path, file, data);
    }

    public static void writeLine(String path, String file, String value, int idx) {
        ArrayList<String> data = readData(path, file);
        data.add(idx, value);
        writeData(path, file, data);
    }

    public static String readLine(String path, String file, int idx){
        ArrayList<String> data = readData(path, file);
        return data.size() > 0 ? data.get(idx) : null;
    }

    public static void replaceLine(String path, String file, String line, int idx) {
        ArrayList<String> data = readData(path, file);
        data.remove(idx);
        data.add(idx, line);
        writeData(path, file, data);
    }
}
