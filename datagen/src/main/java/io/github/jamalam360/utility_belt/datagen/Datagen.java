package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class Datagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		UtilityBelt.LOGGER.info("Generating data and assets...");
		FabricDataGenerator.Pack pack = gen.createPack();
		pack.addProvider(Advancements::new);
		pack.addProvider(Models::new);
		pack.addProvider(Recipes::new);
		pack.addProvider(Tags::new);
	}
}
