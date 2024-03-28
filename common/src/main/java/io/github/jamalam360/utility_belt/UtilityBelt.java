package io.github.jamalam360.utility_belt;

import io.github.jamalam360.jamlib.JamLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityBelt {
	public static final String MOD_ID = "utility_belt";
	public static final String MOD_NAME = "Utility Belt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static void init() {
		JamLib.checkForJarRenaming(UtilityBelt.class);
	}
}
