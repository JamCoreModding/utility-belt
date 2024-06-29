package io.github.jamalam360.utility_belt.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jamalam360.utility_belt.UtilityBelt;
import java.util.function.Supplier;

import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BeltRenderer implements AccessoryRenderer {

    private static final ResourceLocation TEXTURE = UtilityBelt.id("textures/entity/belt.png");
    private static final Supplier<HumanoidModel<LivingEntity>> MODEL = Suppliers.memoize(() ->
          new BeltModel(BeltModel.createLayerDefinition().bakeRoot())
    );

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HumanoidModel<LivingEntity> beltModel = MODEL.get();
        beltModel.setupAnim(reference.entity(), limbSwing, limbSwingAmount, partialTicks, ageInTicks, headPitch);
        beltModel.prepareMobModel(reference.entity(), limbSwing, limbSwingAmount, partialTicks);
        AccessoryRenderer.followBodyRotations(reference.entity(), beltModel);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(beltModel.renderType(TEXTURE));
        beltModel.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
    }
}
