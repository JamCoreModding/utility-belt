package io.github.jamalam360.utility_belt.mixin;

import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.server.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@SuppressWarnings("UnreachableCode")
    @Inject(
			method = "tick",
			at = @At("HEAD")
	)
	private void utilitybelt$startPlayerTick(CallbackInfo ci) {
		StateManager stateManager = StateManager.getServerInstance();
		ItemStack belt = UtilityBeltItem.getBelt((Player) (Object) this);

		if (belt != null) {
			UtilityBeltInventory inv = stateManager.getInventory((Player) (Object) this);
			UtilityBeltInventory nbtInv = UtilityBeltItem.getInventoryFromTag(belt);

			if (!inv.equals(nbtInv)) {
				UtilityBeltItem.setInventory(belt, inv);
				ServerNetworking.sendNewInventoryToClient((ServerPlayer) (Object) this);
			}
		}

		stateManager.onStartTick((Player) (Object) this);
	}
}
