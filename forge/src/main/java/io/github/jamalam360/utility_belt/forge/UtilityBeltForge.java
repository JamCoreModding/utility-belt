package io.github.jamalam360.utility_belt.forge;

import io.github.jamalam360.utility_belt.UtilityBelt;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UtilityBelt.MOD_ID)
public class UtilityBeltForge {
    public UtilityBeltForge() {
        EventBuses.registerModEventBus(UtilityBelt.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        UtilityBelt.init();
    }
}
