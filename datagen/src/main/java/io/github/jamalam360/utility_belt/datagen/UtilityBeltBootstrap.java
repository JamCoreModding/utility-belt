package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.api.ModInitializer;

public class UtilityBeltBootstrap implements ModInitializer {
	@Override
	public void onInitialize() {
		UtilityBelt.init();
	}
}
