package com.maceclient;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;

/**
 * Alles, was sich zwischen MC-Versionen gern aendert, steht NUR hier.
 * Bei Compile-Fehlern zuerst diese Datei per javap/IDE gegen das 26.2-Jar pruefen.
 */
public final class Compat {
    private Compat() {}

    public static Minecraft mc() { return Minecraft.getInstance(); }

    public static Font font() { return mc().font; }

    /** 26.2: Minecraft.screen-Feld entfernt -> mc.gui.screen() (laut Porting-Notizen). */
    public static boolean noScreen() { return mc().gui.screen() == null; }

    /** Falls fehlerhaft: mc().setScreen(s) bzw. mc().gui.setScreen(s) probieren. */
    public static void openScreen(Screen s) { mc().gui.setScreen(s); }

    /** Ab 1.21.9 nimmt InputConstants das Window-Objekt, davor das long-Handle. */
    public static boolean isKeyDown(int glfwKey) {
        return InputConstants.isKeyDown(mc().getWindow(), glfwKey);
    }

    /** Entity unter dem Fadenkreuz (null wenn keins). */
    public static Entity crosshairTarget() { return mc().crosshairPickEntity; }
}
