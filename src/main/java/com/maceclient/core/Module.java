package com.maceclient.core;

import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name, description;
    public final Category category;
    public boolean enabled;
    public int key = -1;          // GLFW-Keycode, -1 = ungebunden
    public boolean keyWasDown;
    public boolean expanded;      // Einstellungen in der GUI aufgeklappt
    public final List<Setting> settings = new ArrayList<>();
    protected final Minecraft mc = Minecraft.getInstance();

    protected Module(String name, String description, Category category) {
        this.name = name; this.description = description; this.category = category;
    }

    protected Setting add(Setting s) { settings.add(s); return s; }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
}
