package com.maceclient.gui;

import com.maceclient.Compat;
import com.maceclient.core.Module;
import com.maceclient.core.ModuleManager;
import com.maceclient.modules.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HudRenderer {
    private HudRenderer() {}

    public static void render(GuiGraphicsExtractor g) {
        Minecraft mc = Compat.mc();
        if (mc.player == null) return;
        HudModule hud = ModuleManager.get(HudModule.class);
        if (hud == null || !hud.enabled) return;

        Font f = mc.font;
        if (hud.watermark.bool) g.text(f, "MaceClient", 4, 4, ClickGuiScreen.ACCENT, true);

        if (hud.arrayList.bool) {
            List<Module> active = new ArrayList<>();
            for (Module m : ModuleManager.MODULES) if (m.enabled && m != hud) active.add(m);
            active.sort(Comparator.comparingInt((Module m) -> f.width(m.name)).reversed());
            int w = mc.getWindow().getGuiScaledWidth();
            int y = 4;
            for (Module m : active) {
                int tw = f.width(m.name);
                g.fill(w - tw - 8, y - 1, w, y + 10, 0x90101010);
                g.fill(w - 2, y - 1, w, y + 10, ClickGuiScreen.ACCENT);
                g.text(f, m.name, w - tw - 5, y + 1, 0xFFFFFFFF, true);
                y += 11;
            }
        }
    }
}
