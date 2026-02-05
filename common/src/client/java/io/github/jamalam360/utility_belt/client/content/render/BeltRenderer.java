package io.github.jamalam360.utility_belt.client.content.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class BeltRenderer implements AccessoryRenderer {

	private static final ResourceLocation TEXTURE = UtilityBelt.id("textures/entity/belt.png");
	private static final Supplier<HumanoidModel<HumanoidRenderState>> MODEL = Suppliers.memoize(() ->
			new BeltModel(BeltModel.createLayerDefinition().bakeRoot())
	);

	@Override
	public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState, EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
		if (entityState.entityType != EntityType.PLAYER) {
			throw new IllegalStateException("Utility belt can only be rendered on players");
		}

		if (UtilityBeltClient.CLIENT_CONFIG.get().renderBelts) {
			this.render(accessoryState, (HumanoidRenderState) entityState, (HumanoidModel<HumanoidRenderState>) model, matrices, collector);
		}
	}

	private void render(AccessoryRenderState accessoryState, HumanoidRenderState entityState, HumanoidModel<HumanoidRenderState> model, PoseStack matrices, SubmitNodeCollector collector) {
		HumanoidModel<HumanoidRenderState> beltModel = MODEL.get();
		beltModel.setupAnim(entityState);
		matrices.mulPose(Axis.ZP.rotationDegrees(180));
		matrices.mulPose(Axis.YP.rotationDegrees(180));
		matrices.scale(1.75f, 1.75f, 1.75f);
		AccessoryRenderer.transformToModelPart(matrices, model.body, 0, 0.9, 0);
		collector.submitModel(beltModel, entityState, matrices, beltModel.renderType(TEXTURE), entityState.lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
	}
}
