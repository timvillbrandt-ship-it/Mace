package com.maceclient.modules;

import com.maceclient.core.Category;
import com.maceclient.core.Module;
import com.maceclient.core.Setting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

/**
 * Ein-Schuss-Modul: beim Aktivieren (z.B. per Keybind) wird eine Windladung nach unten gezuendet,
 * dann direkt auf die Mace gewechselt. Schaltet sich danach selbst wieder aus.
 */
public class WindChargeBoost extends Module {
    private final Setting swapToMace = add(Setting.bool("Danach Mace waehlen", true));
    private final Setting pitch = add(Setting.number("Winkel", 90.0, 60.0, 90.0, 1.0));

    public WindChargeBoost() { super("WindBoost", "Windladung nach unten -> Hochschleudern fuer Mace-Schlag.", Category.MACE); }

    @Override public void onEnable() {
        var p = mc.player;
        enabled = false; // Ein-Schuss
        if (p == null || mc.gameMode == null) return;

        int wc = MaceUtil.findItem(Items.WIND_CHARGE);
        if (wc == -1) return;

        int prevSlot = MaceUtil.selected();
        float prevPitch = p.getXRot();

        MaceUtil.select(wc);
        p.setXRot((float) pitch.num);                       // Rotation steckt im UseItem-Paket
        mc.gameMode.useItem(p, InteractionHand.MAIN_HAND);
        p.setXRot(prevPitch);

        int mace = MaceUtil.findMace(null);
        MaceUtil.select(swapToMace.bool && mace != -1 ? mace : prevSlot);
    }
}
