package io.github.jamalam360.utility_belt.neoforge;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(UtilityBelt.MOD_ID)
public class UtilityBeltNeoForge {
    public UtilityBeltNeoForge() {
        UtilityBelt.init();
        UtilityBelt.UTILITY_BELT_ITEM.listen((utilityBelt) -> CuriosApi.registerCurio(utilityBelt, new UtilityBeltCurio()));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            UtilityBeltNeoforgeClient.init();
        }
    }
}
