package com.maceclient.modules;

import com.maceclient.core.Category;
import com.maceclient.core.Module;
import com.maceclient.core.Setting;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Faellt der Spieler tief genug und ist ein Gegner in Reichweite -> Mace ziehen und zuschlagen. */
public class AutoMace extends Module {
    private final Setting minFall = add(Setting.number("Min. Fallhoehe", 3.0, 1.5, 30.0, 0.5));
    private final Setting range   = add(Setting.number("Reichweite", 3.0, 2.0, 4.5, 0.1));
    private final Setting autoSwap = add(Setting.bool("Auto-Swap", true));
    private final Setting prefer  = add(Setting.mode("Bevorzugt", 0, "Auto", "Breach", "Density"));
    private int cooldown;

    public AutoMace() { super("AutoMace", "Schlaegt im Fall automatisch mit der Mace zu.", Category.MACE); }

    @Override public void onTick() {
        var p = mc.player;
        if (p == null || mc.gameMode == null) return;
        if (cooldown > 0) { cooldown--; return; }
        if (p.onGround() || p.isInWater() || p.fallDistance < minFall.num) return;

        Player target = nearest(p);
        if (target == null) return;

        int mace = switch (prefer.mode) {
            case 1 -> MaceUtil.findMace(MaceUtil.BREACH);
            case 2 -> MaceUtil.findMace(MaceUtil.DENSITY);
            default -> target.getArmorValue() > 0 ? MaceUtil.findMace(MaceUtil.BREACH)
                                                  : MaceUtil.findMace(MaceUtil.DENSITY);
        };
        if (mace == -1) return;
        if (MaceUtil.selected() != mace) {
            if (!autoSwap.bool) return;
            MaceUtil.select(mace);
        }
        if (!p.getInventory().getItem(MaceUtil.selected()).is(Items.MACE)) return;

        mc.gameMode.attack(p, target);
        p.swing(InteractionHand.MAIN_HAND);
        cooldown = 6;
    }

    private Player nearest(Player self) {
        Player best = null; double bd = range.num;
        for (AbstractClientPlayer o : mc.level.players()) {
            if (o == self || !o.isAlive() || o.isSpectator()) continue;
            double d = self.distanceTo(o);
            if (d <= bd) { bd = d; best = o; }
        }
        return best;
    }
}
