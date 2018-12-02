package com.therandomlabs.randompatches.api;

import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TeleporterSetEvent extends Event {
	private final WorldServer world;
	private Teleporter teleporter;

	public TeleporterSetEvent(WorldServer world, Teleporter teleporter) {
		this.world = world;
		this.teleporter = teleporter;
	}

	public WorldServer getWorld() {
		return world;
	}

	public Teleporter getTeleporter() {
		return teleporter;
	}

	public void setTeleporter(Teleporter teleporter) {
		this.teleporter = teleporter;
	}
}
