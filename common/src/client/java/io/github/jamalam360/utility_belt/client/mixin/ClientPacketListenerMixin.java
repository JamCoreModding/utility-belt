package io.github.jamalam360.utility_belt.client.mixin;

import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.client.network.ClientNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(method = "handleLogin", at = @At("TAIL"))
	private void utilitybelt$onClientPacketListenerLogin(CallbackInfo ci) {
		UtilityBeltClient.resetClientState();
		ClientNetworking.sendNewStateToServer(false, 0, false);
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void utilitybelt$onClientPacketListenerClose(CallbackInfo ci) {
		UtilityBeltClient.resetClientState();
	}
}
