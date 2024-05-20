package io.github.jamalam360.utility_belt.neoforge;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.BeltRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class UtilityBeltNeoforgeClient {

    protected static void init() {
        UtilityBelt.UTILITY_BELT_ITEM.listen((utilityBelt) -> CuriosRendererRegistry.register(utilityBelt, () -> new ICurioRenderer() {
            @Override
            public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                BeltRenderer.render(slotContext.entity(), matrixStack, renderTypeBuffer, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headPitch);
            }
        }));
    }
}
