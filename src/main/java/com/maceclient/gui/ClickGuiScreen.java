package com.maceclient.gui;

import com.maceclient.Compat;
import com.maceclient.core.Category;
import com.maceclient.core.Module;
import com.maceclient.core.ModuleManager;
import com.maceclient.core.Setting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel-ClickGUI: pro Kategorie ein verschiebbares Panel.
 *  Linksklick Modul = an/aus | Rechtsklick = Einstellungen auf/zu | Mittelklick = Keybind setzen
 *  Header: ziehen = verschieben, Rechtsklick = ein-/ausklappen. Slider: ziehen.
 * Retained-Mode (26.2): extractRenderState statt render, MouseButtonEvent/KeyEvent statt int-Parameter.
 */
public class ClickGuiScreen extends Screen {
    public static final int ACCENT = 0xFF8A5CF5;
    private static final int W = 124, HEADER = 18, ROW = 16, SROW = 14;

    private static final class Panel {
        final Category cat; int x, y; boolean open = true;
        Panel(Category c, int x, int y) { cat = c; this.x = x; this.y = y; }
    }
    private record Hit(int x, int y, int w, int h, Panel panel, Module module, Setting setting) {
        boolean in(double mx, double my) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    }

    private static final List<Panel> PANELS = new ArrayList<>(); // static: Positionen bleiben beim erneuten Oeffnen
    private final List<Hit> hits = new ArrayList<>();
    private Panel dragPanel; private double dragOX, dragOY;
    private Setting dragSetting; private Hit dragHit;
    private Module binding;

    public ClickGuiScreen() {
        super(Component.literal("MaceClient"));
        if (PANELS.isEmpty()) {
            int x = 24;
            for (Category c : Category.values()) { PANELS.add(new Panel(c, x, 24)); x += W + 10; }
        }
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        hits.clear();
        Font f = Compat.font();
        g.fill(0, 0, width, height, 0x70000000);
        String tip = null;

        for (Panel p : PANELS) {
            int y = p.y;
            g.fill(p.x, y, p.x + W, y + HEADER, ACCENT);
            g.text(f, p.cat.label, p.x + 6, y + 5, 0xFFFFFFFF, true);
            hits.add(new Hit(p.x, y, W, HEADER, p, null, null));
            y += HEADER;
            if (!p.open) continue;

            for (Module m : ModuleManager.inCategory(p.cat)) {
                boolean hover = mx >= p.x && mx < p.x + W && my >= y && my < y + ROW;
                g.fill(p.x, y, p.x + W, y + ROW, hover ? 0xF0282828 : 0xF01B1B1B);
                if (m.enabled) g.fill(p.x, y, p.x + 2, y + ROW, ACCENT);
                String label = binding == m ? "Taste druecken..." : m.name
                        + (m.key > 0 ? " [" + keyName(m.key) + "]" : "");
                g.text(f, label, p.x + 7, y + 4, m.enabled ? 0xFFFFFFFF : 0xFFA0A0A0, true);
                if (!m.settings.isEmpty()) g.text(f, m.expanded ? "-" : "+", p.x + W - 10, y + 4, 0xFF808080, true);
                hits.add(new Hit(p.x, y, W, ROW, p, m, null));
                if (hover) tip = m.description;
                y += ROW;

                if (m.expanded) {
                    for (Setting s : m.settings) {
                        g.fill(p.x, y, p.x + W, y + SROW, 0xF0111111);
                        if (s.type == Setting.Type.NUMBER) {
                            int fw = (int) ((s.num - s.min) / (s.max - s.min) * (W - 6));
                            g.fill(p.x + 3, y + SROW - 3, p.x + 3 + fw, y + SROW - 1, ACCENT);
                        } else if (s.type == Setting.Type.BOOL && s.bool) {
                            g.fill(p.x + 4, y + 3, p.x + 6, y + SROW - 3, ACCENT);
                        }
                        g.text(f, s.display(), p.x + 9, y + 3, 0xFFD0D0D0, false);
                        hits.add(new Hit(p.x, y, W, SROW, p, m, s));
                        y += SROW;
                    }
                }
            }
            g.fill(p.x, y, p.x + W, y + 2, ACCENT);
        }
        if (tip != null) g.text(f, tip, 8, height - 14, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean doubleClick) {
        double mx = e.x(), my = e.y(); int b = e.button();
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit h = hits.get(i);
            if (!h.in(mx, my)) continue;
            if (h.setting() != null) {
                Setting s = h.setting();
                switch (s.type) {
                    case BOOL -> s.bool = !s.bool;
                    case MODE -> s.cycle();
                    case NUMBER -> { dragSetting = s; dragHit = h; applySlider(mx); }
                }
            } else if (h.module() != null) {
                Module m = h.module();
                if (b == 0) m.toggle();
                else if (b == 1) m.expanded = !m.expanded;
                else if (b == 2) binding = m;
            } else {
                Panel p = h.panel();
                if (b == 0) { dragPanel = p; dragOX = mx - p.x; dragOY = my - p.y; PANELS.remove(p); PANELS.add(p); }
                else if (b == 1) p.open = !p.open;
            }
            return true;
        }
        return super.mouseClicked(e, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (dragPanel != null) { dragPanel.x = (int) (e.x() - dragOX); dragPanel.y = (int) (e.y() - dragOY); return true; }
        if (dragSetting != null) { applySlider(e.x()); return true; }
        return super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        dragPanel = null; dragSetting = null; dragHit = null;
        return super.mouseReleased(e);
    }

    private void applySlider(double mx) {
        if (dragSetting == null || dragHit == null) return;
        double t = Math.max(0, Math.min(1, (mx - dragHit.x() - 3) / (W - 6)));
        dragSetting.setNum(dragSetting.min + t * (dragSetting.max - dragSetting.min));
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        int key = e.key();
        if (binding != null) {
            binding.key = (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE) ? -1 : key;
            binding = null;
            return true;
        }
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT || key == GLFW.GLFW_KEY_ESCAPE) { onClose(); return true; }
        return super.keyPressed(e);
    }

    private static String keyName(int key) {
        String n = GLFW.glfwGetKeyName(key, 0);
        return n != null ? n.toUpperCase() : "K" + key;
    }
}
