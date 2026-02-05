package io.github.jamalam360.utility_belt.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UtilityBelt.MOD_ID)
public class UtilityBeltForge {
	@SuppressWarnings("removal")
	public UtilityBeltForge() {
		EventBuses.registerModEventBus(UtilityBelt.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		UtilityBelt.init();
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> UtilityBeltClient::init);
	}
}
