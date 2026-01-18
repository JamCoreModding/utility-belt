package io.github.jamalam360.utility_belt;

import io.github.jamalam360.jamlib.config.ConfigExtensions;
import io.github.jamalam360.jamlib.config.WithinRange;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CommonConfig implements ConfigExtensions<CommonConfig> {
	@WithinRange(min = 1, max = 128)
	public int initialBeltSize = 4;
	@WithinRange(min = 1, max = 128)
	public int maxBeltSize = 6;

	@Override
	public List<Link> getLinks() {
		return List.of(
			new Link(Link.DISCORD, "https://jamalam.tech/discord", Component.translatable("config.utility_belt.discord")),
			new Link(Link.GITHUB, "https://github.com/JamCoreModding/utility-belt", Component.translatable("config.utility_belt.github")),
			new Link(Link.GENERIC_LINK, "https://modrinth.com/mod/utility-belt", Component.translatable("config.utility_belt.modrinth"))
		);
	}
}
