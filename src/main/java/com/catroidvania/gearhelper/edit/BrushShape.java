package com.catroidvania.gearhelper.edit;

public enum BrushShape {
    EMPTY,
    CUBE,
    SPHERE,
    NOISE;

    public String toString() {
        return switch (this) {
            case EMPTY -> "Void";
            case CUBE -> "Cube";
            case SPHERE -> "Sphere";
            case NOISE -> "Noise";
        };
    }
}
