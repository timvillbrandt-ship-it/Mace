package com.maceclient.modules;

import com.maceclient.Compat;
import com.maceclient.core.Category;
import com.maceclient.core.Module;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Blockt der Gegner mit dem Schild: Axt-Schlag (Schild deaktivieren), dann Mace-Schlag. */
public class StunSlam extends Module {
    private int phase;   // 0 = bereit, 1 = auf Mace-Schlag warten
    private int timeout;

    public StunSlam() { super("StunSlam", "Axt bricht den Schild, danach sofort Mace.", Category.COMBAT); }

    @Override public void onDisable() { phase = 0; }

    @Override public void onTick() {
        var p = mc.player;
        if (p == null || mc.gameMode == null) return;
        Entity t = Compat.crosshairTarget();

        if (phase == 0) {
            if (!mc.options.keyAttack.isDown()) return;
            if (!(t instanceof Player target) || !target.isBlocking()) return;
            int axe = MaceUtil.findHotbar(s -> s.is(ItemTags.AXES));
            if (axe == -1 || MaceUtil.findItem(Items.MACE) == -1) return;
            MaceUtil.select(axe);
            mc.gameMode.attack(p, target);
            p.swing(InteractionHand.MAIN_HAND);
            phase = 1; timeout = 20;
        } else {
            if (--timeout <= 0 || !(t instanceof Player target)) { phase = 0; return; }
            if (p.getAttackStrengthScale(0f) < 0.9f) return;
            int mace = MaceUtil.findMace(null);
            if (mace == -1) { phase = 0; return; }
            MaceUtil.select(mace);
            mc.gameMode.attack(p, target);
            p.swing(InteractionHand.MAIN_HAND);
            phase = 0;
        }
    }
}
