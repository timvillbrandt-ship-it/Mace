package com.maceclient.modules;

import com.maceclient.core.Category;
import com.maceclient.core.Module;
import com.maceclient.core.Setting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.item.Items;

/** Haelt einen Totem in der Offhand (Totem muss in der Hotbar liegen). */
public class AutoTotem extends Module {
    private final Setting hp = add(Setting.number("Ab HP (20 = immer)", 20.0, 1.0, 20.0, 1.0));
    private final Setting delay = add(Setting.number("Verzoegerung (Ticks)", 0.0, 0.0, 10.0, 1.0));
    private int wait;
    private int cooldown;

    public AutoTotem() { super("AutoTotem", "Legt automatisch einen Totem in die Offhand.", Category.COMBAT); }

    @Override public void onTick() {
        var p = mc.player;
        var conn = mc.getConnection();
        if (p == null || conn == null) return;
        if (cooldown > 0) { cooldown--; return; }
        if (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) { wait = 0; return; }
        if (p.getHealth() + p.getAbsorptionAmount() > hp.num) return;
        if (wait < (int) delay.num) { wait++; return; }

        int slot = MaceUtil.findItem(Items.TOTEM_OF_UNDYING);
        if (slot == -1) return;
        int prev = MaceUtil.selected();
        conn.send(new ServerboundSetCarriedItemPacket(slot));
        conn.send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
        conn.send(new ServerboundSetCarriedItemPacket(prev));
        wait = 0;
        cooldown = 10;
    }
}
