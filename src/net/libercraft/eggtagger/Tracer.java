package net.libercraft.eggtagger;

import org.bukkit.Bukkit;

public interface Tracer {

	public default void trace(Object input) {
		System.out.println(input);
	}
	
	public default void traceChat(Object input) {
		Bukkit.broadcastMessage((String) input);
	}
}
