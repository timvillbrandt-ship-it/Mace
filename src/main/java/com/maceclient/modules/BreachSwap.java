package com.maceclient.modules;

import com.maceclient.Compat;
import com.maceclient.core.Category;
import com.maceclient.core.Module;
import com.maceclient.core.Setting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;

/** Waehlt beim Angriff die passende Mace: Breach gegen Ruestung, Density gegen nackte Ziele. */
public class BreachSwap extends Module {
    private final Setting onlyHolding = add(Setting.bool("Nur mit Mace in der Hand", true));

    public BreachSwap() { super("BreachSwap", "Tauscht automatisch zwischen Breach- und Density-Mace.", Category.MACE); }

    @Override public void onTick() {
        var p = mc.player;
        if (p == null || !mc.options.keyAttack.isDown()) return;
        Entity t = Compat.crosshairTarget();
        if (!(t instanceof LivingEntity le)) return;
        if (onlyHolding.bool && !p.getMainHandItem().is(Items.MACE)) return;

        int slot = le.getArmorValue() > 0 ? MaceUtil.findMace(MaceUtil.BREACH)
                                          : MaceUtil.findMace(MaceUtil.DENSITY);
        if (slot != -1 && slot != MaceUtil.selected()) MaceUtil.select(slot);
    }
}
