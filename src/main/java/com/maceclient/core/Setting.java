package com.maceclient.core;

import java.util.Locale;

public final class Setting {
    public enum Type { BOOL, NUMBER, MODE }

    public final String name;
    public final Type type;
    public boolean bool;
    public double num, min, max, step;
    public String[] modes;
    public int mode;

    private Setting(String name, Type type) { this.name = name; this.type = type; }

    public static Setting bool(String name, boolean v) {
        Setting s = new Setting(name, Type.BOOL); s.bool = v; return s;
    }

    public static Setting number(String name, double v, double min, double max, double step) {
        Setting s = new Setting(name, Type.NUMBER);
        s.min = min; s.max = max; s.step = step; s.num = v; return s;
    }

    public static Setting mode(String name, int idx, String... modes) {
        Setting s = new Setting(name, Type.MODE); s.modes = modes; s.mode = idx; return s;
    }

    public void setNum(double v) {
        double r = Math.round((v - min) / step) * step + min;
        num = Math.max(min, Math.min(max, r));
    }

    public void cycle() { mode = (mode + 1) % modes.length; }

    public String display() {
        return switch (type) {
            case BOOL -> name + ": " + (bool ? "An" : "Aus");
            case NUMBER -> name + ": " + String.format(Locale.ROOT, "%.1f", num);
            case MODE -> name + ": " + modes[mode];
        };
    }
}
