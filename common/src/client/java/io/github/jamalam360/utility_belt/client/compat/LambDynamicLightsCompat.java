package io.github.jamalam360.utility_belt.client.compat;

//import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
//import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
//import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
//import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;

//public class LambDynamicLightsCompat implements DynamicLightsInitializer
public class LambDynamicLightsCompat {


//	@Override
//	public void onInitializeDynamicLights(DynamicLightsContext ctx) {
//		UtilityBelt.LOGGER.info("Initializing LambDynamicLights compat for Utility Belt.");
//		ctx.entityLightSourceManager().onRegisterEvent().register((registerContext) -> {
//			registerContext.register(EntityType.PLAYER, UtilityBeltEntityLuminance.INSTANCE);
//		});
//	}
//
//	private static class UtilityBeltEntityLuminance implements EntityLuminance {
//
//		public static final UtilityBeltEntityLuminance INSTANCE = new UtilityBeltEntityLuminance();
//
//		public static final EntityLuminance.Type TYPE = EntityLuminance.Type.registerSimple(
//				UtilityBelt.id("belt_luminance"),
//				INSTANCE
//		);
//
//		private UtilityBeltEntityLuminance() {}
//
//		@Override
//		public @NotNull Type type() {
//			return TYPE;
//		}
//
//		@Override
//		public @Range(from = 0L, to = 15L) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
//			if (!(entity instanceof Player player)) {
//				return 0;
//			}
//
//			int luminance = 0;
//			StateManager stateManager = StateManager.getStateManager(player);
//			if (stateManager.hasBelt(player)) {
//				UtilityBeltInventory inv = stateManager.getInventory(player);
//
//				for (ItemStack stack : inv.items()) {
//					luminance = Math.max(luminance, itemLightSourceManager.getLuminance(stack));
//				}
//			}
//
//			return luminance;
//		}
//	}
}
