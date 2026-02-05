package io.github.jamalam360.utility_belt.client.content.render;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
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

public class BeltRenderer implements AccessoryRenderer {

	private static final ResourceLocation TEXTURE = UtilityBelt.id("textures/entity/belt.png");
	private static final Supplier<BeltModel> MODEL = Suppliers.memoize(() ->
			new BeltModel(BeltModel.createLayerDefinition().bakeRoot())
	);

	@Override
	public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		HumanoidModel<LivingEntity> beltModel = MODEL.get();
		beltModel.setupAnim(reference.entity(), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headPitch);
		beltModel.prepareMobModel(reference.entity(), limbSwing, limbSwingAmount, partialTicks);
		followBodyRotations(reference.entity(), beltModel);
		VertexConsumer vertexConsumer = multiBufferSource.getBuffer(beltModel.renderType(TEXTURE));
		beltModel.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
	}

	@SuppressWarnings("unchecked")
	private static void followBodyRotations(LivingEntity entity, HumanoidModel<LivingEntity> model) {
		EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

		if (render instanceof LivingEntityRenderer<?, ?> renderer && renderer.getModel() instanceof HumanoidModel<?> entityModel) {
			((HumanoidModel<LivingEntity>) entityModel).copyPropertiesTo(model);
		}
	}
}
