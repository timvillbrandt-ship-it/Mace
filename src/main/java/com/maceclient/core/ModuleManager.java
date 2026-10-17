package com.maceclient.core;

import com.maceclient.Compat;
import com.maceclient.gui.ClickGuiScreen;
import com.maceclient.modules.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    public static final List<Module> MODULES = new ArrayList<>();
    private static boolean guiKeyWasDown;

    private ModuleManager() {}

    public static void init() {
        MODULES.add(new AutoMace());
        MODULES.add(new BreachSwap());
        MODULES.add(new WindChargeBoost());
        MODULES.add(new StunSlam());
        MODULES.add(new AutoTotem());
        MODULES.add(new HudModule());
        get(HudModule.class).enabled = true;
    }

    public static List<Module> inCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) if (m.category == c) out.add(m);
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> cls) {
        for (Module m : MODULES) if (cls.isInstance(m)) return (T) m;
        return null;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;
        boolean free = Compat.noScreen();

        // GUI oeffnen mit Rechts-Shift
        boolean guiDown = free && Compat.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
        if (guiDown && !guiKeyWasDown) Compat.openScreen(new ClickGuiScreen());
        guiKeyWasDown = guiDown;

        for (Module m : MODULES) {
            if (m.key > 0) {
                boolean down = free && Compat.isKeyDown(m.key);
                if (down && !m.keyWasDown) m.toggle();
                m.keyWasDown = down;
            }
            if (m.enabled) {
                try { m.onTick(); }
                catch (Throwable t) { m.enabled = false; t.printStackTrace(); } // Modul-Fehler crasht nie das Spiel
            }
        }
    }
}
