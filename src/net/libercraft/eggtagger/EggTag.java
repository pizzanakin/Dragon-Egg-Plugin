package net.libercraft.eggtagger;

import org.bukkit.Location;
import org.bukkit.World;

public class EggTag {

	private String name;
	private World world;
	private int x;
	private int y;
	private int z;
	
	public EggTag(String _name, World _world, int _x, int _y, int _z) {
		name = _name;
		world = _world;
		x = _x;
		y = _y;
		z = _z;
	}
	
	public String getName() {
		return name;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Location getLocation() {
		return new Location(world, x, y, z);
	}
}
