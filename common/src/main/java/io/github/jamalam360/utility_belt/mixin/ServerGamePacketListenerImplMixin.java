package io.github.jamalam360.utility_belt.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;
	
	@ModifyExpressionValue(
			method = "handlePlayerAction",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z",
					ordinal = 0
			)
	)
	private boolean utilitybelt$disallowOffhandWhenInBelt(boolean original) {
		return original || StateManager.getStateManager(this.player).isInBelt(this.player);
	}
}
