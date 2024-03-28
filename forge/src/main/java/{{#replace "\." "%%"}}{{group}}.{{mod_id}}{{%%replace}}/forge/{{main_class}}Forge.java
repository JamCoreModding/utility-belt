package {{group}}.{{mod_id}}.forge;

import {{group}}.{{mod_id}}.{{main_class}};
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod({{main_class}}.MOD_ID)
public class {{main_class}}Forge {
    public {{main_class}}Forge() {
        EventBuses.registerModEventBus({{ main_class }}.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        {{main_class}}.init();
    }
}
