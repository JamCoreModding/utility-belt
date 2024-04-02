package io.github.jamalam360.utility_belt.mixin.client;

import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(method = "handleLogin", at = @At("TAIL"))
	private void utilitybelt$onClientPacketListenerLogin(CallbackInfo ci) {
		UtilityBeltClient.onClientConnect();
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void utilitybelt$onClientPacketListenerClose(CallbackInfo ci) {
		UtilityBeltClient.onClientDisconnect();
	}
}
