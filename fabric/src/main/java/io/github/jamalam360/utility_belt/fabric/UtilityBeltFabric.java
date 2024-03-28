package io.github.jamalam360.utility_belt.fabric;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.api.ModInitializer;

public class UtilityBeltFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        UtilityBelt.init();
    }
}
