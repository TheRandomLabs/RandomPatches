package com.therandomlabs.randompatches.api;

import net.minecraft.world.Teleporter;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TeleporterSetEvent extends Event {
	private Teleporter teleporter;

	public TeleporterSetEvent(Teleporter teleporter) {
		this.teleporter = teleporter;
	}

	public Teleporter getTeleporter() {
		return teleporter;
	}

	public void setTeleporter(Teleporter teleporter) {
		this.teleporter = teleporter;
	}
}
