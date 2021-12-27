package com.dotbait.main.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PurgeEvent extends Event {
	
	public static final HandlerList handlers = new HandlerList();
	private boolean purgeEnabled;
	
	
	public PurgeEvent(boolean setEvent) {
		this.purgeEnabled = setEvent;
	}
	
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public boolean getPurgeEvent() {
		return purgeEnabled;
	}
	
	public void setPurgeEvent(boolean setEvent) {
		this.purgeEnabled = setEvent;
	}
	
	
}
