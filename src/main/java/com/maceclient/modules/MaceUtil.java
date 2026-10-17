package com.maceclient.modules;

import com.maceclient.Compat;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.function.Predicate;

public final class MaceUtil {
    private MaceUtil() {}

    private static Inventory inv() { return Compat.mc().player.getInventory(); }

    /** Hotbar-Slot (0-8) des ersten passenden Items, sonst -1. */
    public static int findHotbar(Predicate<ItemStack> p) {
        for (int i = 0; i < 9; i++) if (p.test(inv().getItem(i))) return i;
        return -1;
    }

    public static int findItem(Item item) { return findHotbar(s -> s.is(item)); }

    public static boolean hasEnchant(ItemStack s, ResourceKey<Enchantment> key) {
        for (Holder<Enchantment> h : s.getEnchantments().keySet()) if (h.is(key)) return true;
        return false;
    }

    public static int findMace(ResourceKey<Enchantment> preferred) {
        if (preferred != null) {
            int slot = findHotbar(s -> s.is(net.minecraft.world.item.Items.MACE) && hasEnchant(s, preferred));
            if (slot != -1) return slot;
        }
        return findItem(net.minecraft.world.item.Items.MACE);
    }

    public static int selected() { return inv().getSelectedSlot(); }

    /** Sync zum Server passiert automatisch beim naechsten attack()/useItem(). */
    public static void select(int slot) { if (slot >= 0 && slot <= 8) inv().setSelectedSlot(slot); }

    public static ResourceKey<Enchantment> BREACH = Enchantments.BREACH;
    public static ResourceKey<Enchantment> DENSITY = Enchantments.DENSITY;
}
