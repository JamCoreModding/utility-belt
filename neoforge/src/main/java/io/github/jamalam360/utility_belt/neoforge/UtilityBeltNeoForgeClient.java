package io.github.jamalam360.utility_belt.neoforge;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = UtilityBelt.MOD_ID, dist = Dist.CLIENT)
public class UtilityBeltNeoForgeClient {
	public UtilityBeltNeoForgeClient() {
		UtilityBeltClient.init();
	}
}
