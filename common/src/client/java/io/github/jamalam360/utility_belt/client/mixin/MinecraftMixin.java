package io.github.jamalam360.utility_belt.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.client.network.ClientNetworking;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	public LocalPlayer player;

	@Inject(
			method = "pickBlock",
			at = @At("HEAD")
	)
	private void utilitybelt$preventPickBlockInBelt(CallbackInfo ci) {
		StateManager stateManager = StateManager.getStateManager(true);
		if (stateManager.isInBelt(this.player)) {
			stateManager.setInBelt(this.player, false);
			ClientNetworking.sendNewStateToServer(false, stateManager.getSelectedBeltSlot(this.player), false);
		}
	}
	
	@ModifyExpressionValue(
			method = "handleKeybinds",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z",
					ordinal = 2
			)
	)
	private boolean utilitybelt$useHotbarKeysInBelt(boolean pressed, @Local int i) {
		StateManager stateManager = StateManager.getStateManager(true);
		if (stateManager.isInBelt(this.player) && pressed) {
			switch (UtilityBeltClient.CONFIG.get().hotbarKeyBehaviour) {
				case SWITCH_BACK_TO_HOTBAR:
					stateManager.setInBelt(this.player, false);
					ClientNetworking.sendNewStateToServer(false, stateManager.getSelectedBeltSlot(this.player), this.player.isCrouching());
					return true;
				case SWITCH_BELT_SLOT:
					ItemStack belt = UtilityBeltItem.getBelt(this.player);

					if (belt == null) {
						return false;
					}

					int beltSize = stateManager.getInventory(this.player).getContainerSize();

					if (i >= 0 && i < beltSize) {
						stateManager.setSelectedBeltSlot(this.player, i);
						ClientNetworking.sendNewStateToServer(true, i, false);
						return false;
					}
			}

		}

		return pressed;
	}
}
