package net.libercraft.eggtagger;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class Database extends YamlConfiguration {
	
	EggTagger plugin;
	
	public Database(EggTagger _plugin) {
		plugin = _plugin;
	}
	
	/**
	 * Get the total number of entries 
	 * @return
	 */
	public int getLength() {
		
		int length = 0;
		while (get(""+length) != null) {
			length++;
		}
		return length;
	}
	
	public void removeEntry(int index) {
		
		for (int i = index; i < getLength(); i++) {
			set(i + ".name", get((i+1) + ".name"));
			set(i + ".state", get((i+1) + ".state"));
			set(i + ".world", get((i+1) + ".world"));
			set(i + ".x", get((i+1) + ".x"));
			set(i + ".y", get((i+1) + ".y"));
			set(i + ".z", get((i+1) + ".z"));
			set(i + ".holder", get((i+1) + ".holder"));
			set(i + ".holdertype", get((i+1) + ".holdertype"));
			set(i + ".uuid", get((i+1) + ".uuid"));
			set(i + ".tickslived", get((i+1) + ".tickslived"));
		}
		
		int lastIndex = getLength() - 1;
		// Remove the entry from the database
		set(lastIndex + ".name", null);
		set(lastIndex + ".state", null);
		set(lastIndex + ".world", null);
		set(lastIndex + ".x", null);
		set(lastIndex + ".y", null);
		set(lastIndex + ".z", null);
		set(lastIndex + ".holder", null);
		set(lastIndex + ".holdertype", null);
		set(lastIndex + ".uuid", null);
		set(lastIndex + ".tickslived", null);
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public EggTag getEntry(int index) {
		String name = (String) get(index + ".name");
		EggState state = null;
		for (EggState _state:EggState.values()) {
			if (_state.name().equals(get(index + ".state"))) state = _state;
		}
		if (state == null) return null;
		switch (state) {
		case BLOCK:
			for (World world:plugin.getServer().getWorlds()) {
				if (!world.getName().equals(get(index + ".world"))) continue;
				Block block =  world.getBlockAt(getInt(index + ".x"), getInt(index + ".y"), getInt(index + ".z"));
				if (block.getType().equals(Material.DRAGON_EGG)) return new EggTag(name, block);
			}
			return null;
		case INVENTORY:
			InventoryHolder holder = null;
			String holderType = getString(index + ".holdertype");
			if (holderType.equals("DOUBLECHEST")) {
				for (World world:plugin.getServer().getWorlds()) {
					if (!world.getName().equals(get(index + ".world"))) continue;
					Block block = world.getBlockAt(getInt(index + ".x"), getInt(index + ".y"), getInt(index + ".z"));
					if (block instanceof InventoryHolder) holder = (InventoryHolder) block;
				}
			}
			if (holderType.equals("BLOCKSTATE")) {
				for (World world:plugin.getServer().getWorlds()) {
					if (!world.getName().equals(get(index + ".world"))) continue;
					Block block = world.getBlockAt(getInt(index + ".x"), getInt(index + ".y"), getInt(index + ".z"));
					if (block instanceof InventoryHolder) holder = (InventoryHolder) block;
				}
			}
			if (holderType.equals("PLAYER")) {
				holder = plugin.getServer().getPlayer(UUID.fromString(getString(index + ".holder")));
			}
			if (holderType.equals("OFFLINEPLAYER")) {
				holder = (Player) plugin.getServer().getOfflinePlayer(UUID.fromString(getString(index + ".holder")));
			}
			if (holderType.equals("ENTITY")) {
				holder = (InventoryHolder) plugin.getServer().getEntity(UUID.fromString(getString(index  + ".holder")));
			}
			if (holder == null) return null;
			return new EggTag(name, holder);
		case ITEM:
			Entity entity = plugin.getServer().getEntity(UUID.fromString(getString(index + ".uuid")));
			if (entity instanceof Item) return new EggTag(name, (Item) entity);
			else return null;
		default:
			return null;
		}
	}
	
	public void updateEntry(int index, EggTag entry) {
		// Calculate the index number for the new entry
		set(index + ".name", entry.getName());
		set(index + ".state", entry.getState().name());
		switch (entry.getState()) {
		case BLOCK:
			set(index + ".world", entry.getWorld().getName());
			set(index + ".x", entry.getX());
			set(index + ".y", entry.getY());
			set(index + ".z", entry.getZ());
			break;
		case INVENTORY:
			set(index + ".world", entry.getWorld().getName());
			set(index + ".x", entry.getX());
			set(index + ".y", entry.getY());
			set(index + ".z", entry.getZ());
			if (entry.getHolder() instanceof DoubleChest) set(index + ".holder", "BLOCK");
			if (entry.getHolder() instanceof BlockState) set(index + ".holder", "BLOCK");
			if (entry.getHolder() instanceof Player) set(index + ".holder", entry.getHolderPlayer().getUniqueId().toString());
			else if (entry.getHolder() instanceof Entity) set(index + ".holder", entry.getHolderEntity().getUniqueId().toString());
			set(index + ".holdertype", entry.getHolderType());
			break;
		case ITEM:
			set(index + ".world", entry.getWorld().getName());
			set(index + ".x", entry.getX());
			set(index + ".y", entry.getY());
			set(index + ".z", entry.getZ());
			set(index + ".uuid", entry.getUUID().toString());
			set(index + ".tickslived", entry.getTicksLived());
			break;
		default:
			break;
		
		}
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void changeToBlock(int index, Block block) {
		set(index + ".state", EggState.BLOCK.name());
		set(index + ".world", block.getWorld().getName());
		set(index + ".x", block.getX());
		set(index + ".y", block.getY());
		set(index + ".z", block.getZ());
		set(index + ".tickslived", null);
		set(index + ".uuid", null);
		set(index + ".holder", null);
		set(index + ".holdertype", null);
		
		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void changeToItem(int index, Item item) {
		set(index + ".state", EggState.ITEM.name());
		set(index + ".world", item.getWorld().getName());
		set(index + ".x", item.getLocation().getX());
		set(index + ".y", item.getLocation().getY());
		set(index + ".z", item.getLocation().getZ());
		set(index + ".tickslived", item.getTicksLived());
		set(index + ".uuid", item.getUniqueId());
		set(index + ".holder", null);
		set(index + ".holdertype", null);

		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void changeToInventory(int index, InventoryHolder holder) {
		set(index + ".state", EggState.INVENTORY.name());
		if (holder instanceof DoubleChest) {
			set(index + ".holder", "BLOCK");
			set(index + ".holdertype", "DOUBLECHEST");
			set(index + ".world", ((DoubleChest) holder).getWorld().getName());
			set(index + ".x", ((DoubleChest) holder).getX());
			set(index + ".y", ((DoubleChest) holder).getY());
			set(index + ".z", ((DoubleChest) holder).getZ());
		}
		if (holder instanceof BlockState) {
			set(index + ".holder", "BLOCK");
			set(index + ".holdertype", "BLOCKSTATE");
			set(index + ".world", ((BlockState) holder).getWorld().getName());
			set(index + ".x", ((BlockState) holder).getX());
			set(index + ".y", ((BlockState) holder).getY());
			set(index + ".z", ((BlockState) holder).getZ());
		}
		if (holder instanceof Player) {
			set(index + ".holder", ((Player) holder).getUniqueId().toString());
			set(index + ".holdertype", "PLAYER");
			set(index + ".world", ((Player) holder).getWorld().getName());
			set(index + ".x", ((Player) holder).getLocation().getX());
			set(index + ".y", ((Player) holder).getLocation().getY());
			set(index + ".z", ((Player) holder).getLocation().getZ());
		}
		else if (holder instanceof Entity) {
			set(index + ".holder", ((Entity) holder).getUniqueId().toString());
			set(index + ".holdertype", "ENTITY");
			set(index + ".world", ((Entity) holder).getWorld().getName());
			set(index + ".x", ((Entity) holder).getLocation().getX());
			set(index + ".y", ((Entity) holder).getLocation().getY());
			set(index + ".z", ((Entity) holder).getLocation().getZ());
		}
		set(index + ".tickslived", null);
		set(index + ".uuid", null);

		// Save the database
		try {
			save(new File(plugin.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
