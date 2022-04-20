package org.flareon.alisa.utils;

import org.flareon.alisa.FLAlisa;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileUtil {
    public static ArrayList<String> readProjectFileLines(final String name) {
        final ArrayList<String> result = new ArrayList<>();
        final File f1 = new File(FLAlisa.getInstance().getDataFolder(), name);
        if (f1.exists()) {
            return readFileFromDataFolderToArray(name);
        }
        BufferedReader reader = null;
        try {
            final InputStream in = FLAlisa.getInstance().getClass().getResourceAsStream("/" + name);
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    result.add(line);
                }
            }
        }
        catch (Exception e2) {
            FLAlisa.getInstance().getLogger().info("error reading project file");
            return result;
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static ArrayList<String> readFileFromDataFolderToArray(final String filename) {
        final ArrayList<String> result = new ArrayList<>();
        BufferedReader reader = null;
        try {
            final InputStream in = new FileInputStream(new File(FLAlisa.getInstance().getDataFolder(), filename));
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    result.add(line);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return result;
    }
}

