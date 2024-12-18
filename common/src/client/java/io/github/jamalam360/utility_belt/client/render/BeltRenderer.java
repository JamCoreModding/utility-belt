package io.github.jamalam360.utility_belt.client.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jamalam360.utility_belt.UtilityBelt;

import java.util.function.Supplier;

import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BeltRenderer implements AccessoryRenderer {

	private static final ResourceLocation TEXTURE = UtilityBelt.id("textures/entity/belt.png");
	private static final Supplier<HumanoidModel<HumanoidRenderState>> MODEL = Suppliers.memoize(() ->
			new BeltModel(BeltModel.createLayerDefinition().bakeRoot())
	);

	@Override
	public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<S> entityModel, S state, MultiBufferSource multiBufferSource, int light, float v) {
		HumanoidModel<HumanoidRenderState> beltModel = MODEL.get();
		beltModel.setupAnim((HumanoidRenderState) state);
		followBodyRotations(reference.entity(), beltModel);
		VertexConsumer vertexConsumer = multiBufferSource.getBuffer(beltModel.renderType(TEXTURE));
		beltModel.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
	}

	@SuppressWarnings("unchecked")
	private static void followBodyRotations(LivingEntity entity, HumanoidModel<HumanoidRenderState> model) {
		EntityRenderer<? super LivingEntity, ?> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

		if (render instanceof LivingEntityRenderer<?, ?, ?> renderer && renderer.getModel() instanceof HumanoidModel<?> entityModel) {
			((HumanoidModel<HumanoidRenderState>) entityModel).copyPropertiesTo(model);
		}
	}
}
