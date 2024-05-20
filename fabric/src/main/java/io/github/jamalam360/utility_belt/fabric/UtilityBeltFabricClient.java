package io.github.jamalam360.utility_belt.fabric;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.BeltRenderer;
import net.fabricmc.api.ClientModInitializer;

public class UtilityBeltFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UtilityBelt.UTILITY_BELT_ITEM.listen((item) -> TrinketRendererRegistry.registerRenderer(item, (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> BeltRenderer.render(entity, matrices, vertexConsumers, light, limbAngle, limbDistance, tickDelta, animationProgress, headPitch)));
    }
}
