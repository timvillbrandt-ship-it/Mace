package com.maceclient.modules;

import com.maceclient.core.Category;
import com.maceclient.core.Module;
import com.maceclient.core.Setting;

public class HudModule extends Module {
    public final Setting watermark = add(Setting.bool("Watermark", true));
    public final Setting arrayList = add(Setting.bool("Modulliste", true));

    public HudModule() { super("HUD", "Watermark und Liste aktiver Module.", Category.VISUAL); }
}
