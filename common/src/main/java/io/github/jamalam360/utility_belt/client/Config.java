package io.github.jamalam360.utility_belt.client;

import io.github.jamalam360.jamlib.config.ConfigExtensions;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class Config implements ConfigExtensions<Config> {
	public boolean displayUtilityBeltWhenNotSelected = true;
	public boolean invertScrolling = false;
	public boolean useSneakSwapping = true;
	public HotbarKeyBehaviour hotbarKeyBehaviour = HotbarKeyBehaviour.SWITCH_BELT_SLOT;
	public Position hotbarPosition = Position.MIDDLE_LEFT;

	@Override
	public List<Link> getLinks() {
		return List.of(
			new Link(Link.DISCORD, "https://jamalam.tech/discord", Component.translatable("config.utility_belt.discord")),
			new Link(Link.GITHUB, "https://github.com/JamCoreModding/utility-belt", Component.translatable("config.utility_belt.github")),
			new Link(Link.GENERIC_LINK, "https://modrinth.com/mod/utility-belt", Component.translatable("config.utility_belt.modrinth"))
		);
	}

	public enum HotbarKeyBehaviour {
		SWITCH_BACK_TO_HOTBAR,
		SWITCH_BELT_SLOT
	}

	public enum Position {
		TOP_LEFT,
		MIDDLE_LEFT,
		BOTTOM_LEFT,
		TOP_RIGHT,
		MIDDLE_RIGHT,
		BOTTOM_RIGHT
	}
}
