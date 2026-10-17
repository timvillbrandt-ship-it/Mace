package com.maceclient.modules;

import com.maceclient.Compat;
import com.maceclient.core.Category;
import com.maceclient.core.Module;
import com.maceclient.core.Setting;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/**
 * Blockt der Gegner mit dem Schild: Axt-Schlag (Schild deaktivieren), dann Mace-Schlag.
 * Ausloeser: Angriffstaste gedrueckt halten, waehrend das Fadenkreuz auf dem Ziel liegt.
 * Axt UND Mace muessen in der Hotbar liegen.
 */
public class StunSlam extends Module {
    private final Setting testAll = add(Setting.bool("Test: alle Ziele", false)); // auch Mobs, auch ohne Schild
    private int phase;   // 0 = bereit, 1 = auf Mace-Schlag warten
    private int timeout, msgCd;

    public StunSlam() { super("StunSlam", "Axt bricht den Schild, danach sofort Mace.", Category.COMBAT); }

    @Override public void onEnable() { phase = 0; Compat.chat("StunSlam an - Angriffstaste halten, Fadenkreuz auf Ziel mit Schild."); }
    @Override public void onDisable() { phase = 0; }

    private void hint(String s) { if (msgCd == 0) { Compat.chat(s); msgCd = 100; } }

    @Override public void onTick() {
        var p = mc.player;
        if (p == null || mc.gameMode == null) return;
        if (msgCd > 0) msgCd--;
        Entity t = Compat.crosshairTarget();

        if (phase == 0) {
            if (!mc.options.keyAttack.isDown()) return;
            if (!(t instanceof LivingEntity target)) return;
            if (!testAll.bool && !(target instanceof Player pl && pl.isBlocking())) return;

            int axe = MaceUtil.findHotbar(s -> s.is(ItemTags.AXES));
            int mace = MaceUtil.findItem(Items.MACE);
            if (axe == -1 || mace == -1) { hint("StunSlam: Axt UND Mace muessen in der Hotbar liegen."); return; }

            MaceUtil.select(axe);
            mc.gameMode.attack(p, target);
            p.swing(InteractionHand.MAIN_HAND);
            Compat.chat("StunSlam: Axt-Schlag");
            phase = 1; timeout = 40;
        } else {
            if (--timeout <= 0 || !(t instanceof LivingEntity target)) { phase = 0; return; }
            if (p.getAttackStrengthScale(0f) < 0.9f) return;
            int mace = MaceUtil.findMace(null);
            if (mace == -1) { phase = 0; return; }
            MaceUtil.select(mace);
            mc.gameMode.attack(p, target);
            p.swing(InteractionHand.MAIN_HAND);
            Compat.chat("StunSlam: Mace-Schlag");
            phase = 0;
        }
    }
}
