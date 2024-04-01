package io.github.jamalam360.utility_belt.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import earth.terrarium.baubly.client.BaubleRenderer;
import earth.terrarium.baubly.common.SlotInfo;
import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class BeltRenderer implements BaubleRenderer {
	private static final ResourceLocation TEXTURE = UtilityBelt.id("textures/entity/belt.png");
	private static final Supplier<HumanoidModel<LivingEntity>> MODEL = Suppliers.memoize(() ->
			new BeltModel(BeltModel.createLayerDefinition().bakeRoot())
	);

	@Override
	public void render(ItemStack stack, SlotInfo slotContext, PoseStack matrixStack, EntityModel<? extends LivingEntity> entityModel, MultiBufferSource renderTypeBuffer, int light, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float netHeadYaw, float headPitch) {
		HumanoidModel<LivingEntity> model = MODEL.get();
		LivingEntity entity = slotContext.wearer();
		model.setupAnim(entity, limbAngle, limbDistance, animationProgress, animationProgress, headPitch);
		model.prepareMobModel(entity, limbAngle, limbDistance, tickDelta);
		followBodyRotations(entity, model);
		VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(model.renderType(TEXTURE));
		model.renderToBuffer(matrixStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
	}

	// shamelessly copied from https://github.com/emilyploszaj/trinkets/blob/main/src/main/java/dev/emi/trinkets/api/client/TrinketRenderer.java
	// MIT licensed; thanks Trinkets contributors!
	private static void followBodyRotations(final LivingEntity entity, final HumanoidModel<LivingEntity> model) {
		EntityRenderer<? super LivingEntity> render = Minecraft.getInstance()
				.getEntityRenderDispatcher().getRenderer(entity);

		if (render instanceof LivingEntityRenderer) {
			//noinspection unchecked
			LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer =
					(LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>) render;
			EntityModel<LivingEntity> entityModel = livingRenderer.getModel();

			if (entityModel instanceof HumanoidModel<LivingEntity> bipedModel) {
				bipedModel.copyPropertiesTo(model);
			}
		}
	}
}
