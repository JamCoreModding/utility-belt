package io.github.jamalam360.utility_belt.mixin;

import com.mojang.authlib.GameProfile;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.network.ServerNetworking;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Unique
    private int utilitybelt$lastSyncedInventoryHash = 0;
    @Unique
    private boolean utilitybelt$hasBeltLastTick = false;

    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
          method = "tick",
          at = @At("HEAD")
    )
    private void utilitybelt$startPlayerTick(CallbackInfo ci) {
        StateManager stateManager = StateManager.getStateManager(this);
        ItemStack belt = UtilityBeltItem.getBelt(this);

        if (belt != null) {
            if (!this.utilitybelt$hasBeltLastTick) {
                this.utilitybelt$hasBeltLastTick = true;
                return;
            }

            UtilityBeltInventory inv = stateManager.getInventory(this);

            if (this.utilitybelt$lastSyncedInventoryHash != inv.hashCode()) {
                this.utilitybelt$lastSyncedInventoryHash = inv.hashCode();
                ServerNetworking.sendInventoryToClient((ServerPlayer) (Object) this, inv);
                UtilityBeltItem.setInventory(belt, inv);
            }
        } else {
            this.utilitybelt$hasBeltLastTick = false;
        }
    }
}
