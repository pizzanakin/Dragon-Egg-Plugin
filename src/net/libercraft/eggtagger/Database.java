package net.libercraft.eggtagger;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class Database extends YamlConfiguration implements Tracer {
	
	EggTagger plugin;
	
	public Database(EggTagger _plugin) {
		plugin = _plugin;
	}
	
	public void addEntry(EggTag entry) {
		
		// Calculate the index number for the new entry
		Integer index = getEntryIndex();
		
		// Set the new entry in the database
		set(index + ".name", entry.getName());
		set(index + ".world", entry.getWorld().getName());
		set(index + ".x", entry.getX());
		set(index + ".y", entry.getY());
		set(index + ".z", entry.getZ());
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeEntry(int index) {

		// Remove the entry from the database
		set(index + ".name", null);
		set(index + ".world", null);
		set(index + ".x", null);
		set(index + ".y", null);
		set(index + ".z", null);
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addEntry(String name, World world, int x, int y, int z) {
		
		// Calculate the index number for the new entry
		Integer index = getEntryIndex();
		
		// Set the new entry in the database
		EggTag entry = new EggTag(name, world, x, y, z);
		set(index + ".name", entry.getName());
		set(index + ".world", entry.getWorld().getName());
		set(index + ".x", entry.getX());
		set(index + ".y", entry.getY());
		set(index + ".z", entry.getZ());
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void editEntryY(int index, int newY) {
		set(index + ".y", newY);
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the total number of entries 
	 * @return
	 */
	public int getLength() {
		
		// Prepare the return variable
		int lengthvalue;
		
		// Calculate the length and assign the number to the return variable
		if (this.get("0") == null) lengthvalue = -1; else lengthvalue = iterateOverDataLength(0);
		
		// Return the variable
		return lengthvalue;
	}
	
	/**
	 * Get the index number of the first empty entry to fill it with a new entry
	 * @return
	 */
	private int getEntryIndex() {
		
		// Prepare the return variable
		int indexvalue;
		
		// Calculate the first empty index
		if (this.getString("0.name") == null) indexvalue = 0; else indexvalue = iterateOverEntries(0);
		
		// Return the variable
		return indexvalue;
	}
	
	/**
	 * Return a list of all the tags in the database. Returns an empty list if no entries exist.
	 * @return
	 */
	public List<EggTag> getEntries() {
		
		// Prepare the return variable
		List<EggTag> list = new ArrayList<EggTag>();
		int length = getLength();
		
		// Return empty list if the database is empty
		if (length == -1) return list; 
		
		// Load each entry from the database
		for (int i = 0; i<length; i++) {
			list.add(new EggTag(this.getString(i + ".name"), plugin.getServer().getWorld(this.getString(i + ".world")), this.getInt(i + ".x"), this.getInt(i + ".y"), this.getInt(i + ".z")));
		}
		
		// Return the list
		return list;
	}
	
	/**
	 * Check through the entries until an empty one is found
	 * @param number
	 * @return
	 */
	private int iterateOverEntries(int number) {
		
		// If entry exists
		if (this.getString(number + ".name") != null) {
			
			// Check for next entry
			number = iterateOverEntries(number + 1);
		}
		
		// Return the number
		return number;
	}
	
	/**
	 * Check through the entries until the end is found
	 * @param number
	 * @return
	 */
	private int iterateOverDataLength(int number) {
		
		// If this entry exists
		if (this.get(""+number) != null && this.get(number + ".name") != null) {
			
			// Check for next entry
			number = iterateOverDataLength(number+1);
		}
		
		// Return the number
		return number;
	}
}
