package com.therandomlabs.randompatches.client;

/**
 * An interface used to access the switch F3 state of {@link net.minecraft.client.Keyboard}.
 */
public interface SwitchF3StateAccessor {
	/**
	 * Returns the switch F3 state of this {@link net.minecraft.client.Keyboard}.
	 *
	 * @return the switch F3 state of this {@link net.minecraft.client.Keyboard}.
	 */
	boolean getSwitchF3State();

	/**
	 * Sets the switch F3 state of this {@link net.minecraft.client.Keyboard}.
	 *
	 * @param state the new switch F3 state.
	 */
	void setSwitchF3State(boolean state);
}
