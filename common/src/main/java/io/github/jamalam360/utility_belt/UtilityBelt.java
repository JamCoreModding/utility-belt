package io.github.jamalam360.utility_belt;

import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.JamLibPlatform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.utility_belt.content.CommonConfig;
import io.github.jamalam360.utility_belt.content.register.ModComponents;
import io.github.jamalam360.utility_belt.content.register.ModItems;
import io.github.jamalam360.utility_belt.content.register.ModMenus;
import io.github.jamalam360.utility_belt.network.ServerNetworking;
import io.github.jamalam360.utility_belt.state.ServerStateManager;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityBelt {
    public static final String MOD_ID = "utility_belt";
    public static final String MOD_NAME = "Utility Belt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final ConfigManager<CommonConfig> COMMON_CONFIG = new ConfigManager<>(UtilityBelt.MOD_ID, "common", CommonConfig.class);

    public static void init() {
        JamLib.checkForJarRenaming(UtilityBelt.class);
        ModComponents.init();
        ModItems.init();
        ModMenus.init();
        ServerNetworking.init();
        StateManager.setServerInstance(new ServerStateManager());
	    LOGGER.info("{} initialized on {}", MOD_NAME, JamLibPlatform.getPlatform());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
