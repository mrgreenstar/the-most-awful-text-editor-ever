package com.company.awful;

import java.awt.*;

class Theme {
    private Color backgroundColor;
    private Color fontColor;

    Theme(Color backgroundColor, Color fontColor) {
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public Color getFontColor() {
        return fontColor;
    }
}

class Themes {
    private static Theme lightTheme;
    private static Theme darkTheme;

    public static Theme getLightTheme() {
        lightTheme = new Theme(Color.WHITE, Color.BLACK);
        return lightTheme;
    }

    public static Theme getDarkTheme() {
        darkTheme = new Theme(Color.DARK_GRAY, Color.WHITE);
        return darkTheme;
    }
}