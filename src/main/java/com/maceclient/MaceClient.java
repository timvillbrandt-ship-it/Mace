package com.maceclient;

import com.maceclient.core.ModuleManager;
import com.maceclient.gui.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class MaceClient implements ClientModInitializer {
    public static final String ID = "maceclient";

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ClientTickEvents.END_CLIENT_TICK.register(ModuleManager::tick);
        // Lambda statt Interface-Methodenname -> weniger Angriffsflaeche fuer API-Umbenennungen
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ID, "hud"),
                (graphics, tickCounter) -> HudRenderer.render(graphics));
    }
}
