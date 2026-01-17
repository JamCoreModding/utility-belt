package io.github.jamalam360.utility_belt.mixin;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.DynamicTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
	@Shadow @Final private String directory;

	@Inject(
			method = "load",
			at = @At("TAIL")
	)
	private void utilitybelt$injectDynamicTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
		if (!this.directory.equals("tags/item")) {
			return;
		}
		
		Map<ResourceLocation, List<TagLoader.EntryWithSource>> map = cir.getReturnValue();
		List<TagLoader.EntryWithSource> list = map.computeIfAbsent(UtilityBelt.ALLOWED_IN_UTILITY_BELT.location(), id -> new ArrayList<>());
		
		for (ResourceLocation item : DynamicTags.getExtraItemsAllowedInUtilityBelt()) {
			list.add(new TagLoader.EntryWithSource(TagEntry.element(item), "utilitybelt"));
		}
	}
}
