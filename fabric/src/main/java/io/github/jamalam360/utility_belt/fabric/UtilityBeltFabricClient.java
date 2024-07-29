package io.github.jamalam360.utility_belt.fabric;

import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import net.fabricmc.api.ClientModInitializer;

public class UtilityBeltFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		UtilityBeltClient.init();
	}
}
