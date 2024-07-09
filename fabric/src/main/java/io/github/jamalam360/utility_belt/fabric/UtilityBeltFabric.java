package io.github.jamalam360.utility_belt.fabric;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.server.ServerNetworking;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class UtilityBeltFabric implements ModInitializer, DedicatedServerModInitializer {

    @Override
    public void onInitialize() {
        UtilityBelt.init();
    }

    @Override
    public void onInitializeServer() {
        ServerNetworking.initDedicatedServer();
    }
}
