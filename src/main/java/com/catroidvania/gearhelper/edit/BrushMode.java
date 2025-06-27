package com.catroidvania.gearhelper.edit;

public enum BrushMode {
    DISABLED,
    STAMP,
    BRUSH,
    PAINT,
    EXCAVATE,
    FOLIAGE;

    public String toString() {
        return switch (this) {
            case DISABLED -> "Off";
            case STAMP -> "Stamp";
            case BRUSH -> "Brush";
            case PAINT -> "Paint";
            case EXCAVATE -> "Erase";
            case FOLIAGE -> "Foliage";
        };
    }
}
