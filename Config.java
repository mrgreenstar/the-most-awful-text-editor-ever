package com.company.awful;

import java.awt.*;
import java.io.*;
import java.util.Scanner;

class Config {
    private String fontName;
    private int fontSize;
    private String themeName;
    private File configFile;

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String name) {
        fontName = name;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int size) {
        fontSize = size;
    }

    public Font getFont() {
        return new Font(fontName, Font.PLAIN, fontSize);
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String name) {
        themeName = name;
    }

    public void saveConfig() {
        configFile = new File("testConfig");
        try (FileWriter fw = new FileWriter(configFile)) {
            fw.flush();
            fw.write("FontName: " + fontName + "\n");
            fw.write("FontSize: " + fontSize + "\n");
            fw.write("Theme: " + themeName + "\n");
        }
        catch (IOException exc) {
            System.out.println("Ошибка при сохранении конфига");
        }
    }

    public boolean readConfig() {
        configFile = new File("testConfig");
        if (!configFile.exists()) {
            return false;
        }
        try (FileReader fr = new FileReader(configFile)) {
            Scanner fileOutput = new Scanner(fr);
            while (fileOutput.hasNextLine()) {
                String str = fileOutput.nextLine();
                if (str.charAt(0) == '#') {
                    // # == comment
                    continue;
                }
                if (str.lastIndexOf("FontName") != -1) {
                    fontName = str.split(": ")[1];
                }
                if (str.lastIndexOf("FontSize") != -1) {
                    fontSize = Integer.parseInt(str.split(": ")[1]);
                }
                if (str.lastIndexOf("Theme") != -1) {
                    themeName = str.split(": ")[1];
                }
            }
        }
        catch (IOException exc) {}
        return true;
    }
}