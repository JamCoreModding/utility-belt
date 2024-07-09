package io.github.jamalam360.utility_belt.neoforge;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.server.ServerNetworking;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(UtilityBelt.MOD_ID)
public class UtilityBeltNeoForge {
	public UtilityBeltNeoForge(Dist dist) {
		UtilityBelt.init();

		if(dist.isDedicatedServer()) {
			ServerNetworking.initDedicatedServer();
		}
	}
}
