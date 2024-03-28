package io.github.jamalam360.utility_belt.quilt;

import io.github.jamalam360.utility_belt.UtilityBelt;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class UtilityBeltQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        UtilityBelt.init();
    }
}
