package io.github.jamalam360.utility_belt;

public class Config {
	public boolean displayUtilityBeltWhenNotSelected = true;
	public boolean invertScrolling = false;
	public HotbarKeyBehaviour hotbarKeyBehaviour = HotbarKeyBehaviour.SWITCH_BELT_SLOT;
	public Position hotbarPosition = Position.MIDDLE_LEFT;

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
