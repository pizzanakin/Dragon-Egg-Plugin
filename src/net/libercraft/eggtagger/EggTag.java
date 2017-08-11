package net.libercraft.eggtagger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EggTag {

	private String name;
	private EggState state;
	private World world;
	private double x;
	private double y;
	private double z;
	private InventoryHolder holder;
	private String holderType;
	private Item item;
	private int ticksLived;
	private UUID uuid;
	
	public boolean updateTag() {
		boolean returnValue = false;
		switch (state) {
		case BLOCK:
			return false;
		case INVENTORY:
			if (holder instanceof DoubleChest) return false;
			if (holder instanceof BlockState) return false;
			if (holder instanceof Player) {
				if (((Player) holder).isOnline() && holderType.equals("OFFLINEPLAYER")) {
					holderType = "PLAYER";
					returnValue = true;
				}
				else if (!((Player) holder).isOnline() && holderType.equals("PLAYER")) {
					holderType = "OFFLINEPLAYER";
					returnValue = true;
				}
			}
			if (holder instanceof Entity) {
				if (world != ((Entity) holder).getWorld()) {
					world = ((Entity) holder).getWorld();
					returnValue = true;
				}
				if (x != ((Entity) holder).getLocation().getX()) {
					x = ((Entity) holder).getLocation().getX();
					returnValue = true;
				}
				if (y != ((Entity) holder).getLocation().getY()) {
					y = ((Entity) holder).getLocation().getY();
					returnValue = true;
				}
				if (z != ((Entity) holder).getLocation().getZ()) {
					z = ((Entity) holder).getLocation().getZ();
					returnValue = true;
				}
			}
			return returnValue;
		case ITEM:
			if (world != item.getWorld()) {
				world = item.getWorld();
				returnValue = true;
			}
			if (x != item.getLocation().getX()) {
				x = item.getLocation().getX();
				returnValue = true;
			}
			if (y != item.getLocation().getY()) {
				y = item.getLocation().getY();
				returnValue = true;
			}
			if (z != item.getLocation().getZ()) {
				z = item.getLocation().getZ();
				returnValue = true;
			}
			if (ticksLived != item.getTicksLived()) {
				ticksLived = item.getTicksLived();
				returnValue = true;
			}
			return returnValue;
		default:
			return false;
		}
	}
	
	
	// State block
	public EggTag(String _name, Block _block) {
		state = EggState.BLOCK;
		name = _name;
		world = _block.getWorld();
		x = _block.getX();
		y = _block.getY();
		z = _block.getZ();
	}
	
	// State item
	public EggTag (String _name, Item _item) {
		state = EggState.ITEM;
		name = _name;
		item = _item;
		world = item.getWorld();
		x = item.getLocation().getX();
		y = item.getLocation().getY();
		z = item.getLocation().getZ();
		ticksLived = item.getTicksLived();
		uuid = item.getUniqueId();
	}

	// State inventory
	public EggTag(String _name, InventoryHolder _holder) {
		state = EggState.INVENTORY;
		name = _name;
		holder = _holder;
		if (holder instanceof DoubleChest) {
			holderType = "DOUBLECHEST";
			world = getHolderDoubleChest().getWorld();
			x = getHolderDoubleChest().getX();
			y = getHolderDoubleChest().getY();
			z = getHolderDoubleChest().getZ();
		}
		if (holder instanceof BlockState) {
			holderType = "BLOCKSTATE";
			world = getHolderBlock().getWorld();
			x = getHolderBlock().getX();
			y = getHolderBlock().getY();
			z = getHolderBlock().getZ();
		}
		if (holder instanceof Player) {
			if (((Player) holder).isOnline()) holderType = "PLAYER"; else holderType = "OFFLINEPLAYER";
			world = getHolderPlayer().getWorld();
			x = getHolderPlayer().getLocation().getX();
			y = getHolderPlayer().getLocation().getY();
			z = getHolderPlayer().getLocation().getZ();
		}
		else if (holder instanceof Entity) {
			holderType = "ENTITY";
			world = getHolderEntity().getWorld();
			x = getHolderEntity().getLocation().getX();
			y = getHolderEntity().getLocation().getY();
			z = getHolderEntity().getLocation().getZ();
		}
	}
	
	public void changeToBlock(Block _block) {
		state = EggState.BLOCK;
		world = _block.getWorld();
		x = _block.getX();
		y = _block.getY();
		z = _block.getZ();
		item = null;
		ticksLived = 0;
		uuid = null;
		holder = null;
		holderType = null;
	}
	
	public void changeToItem(Item _item) {
		state = EggState.ITEM;
		item = _item;
		world = item.getWorld();
		x = item.getLocation().getX();
		y = item.getLocation().getY();
		z = item.getLocation().getZ();
		ticksLived = item.getTicksLived();
		uuid = item.getUniqueId();
		holder = null;
		holderType = null;
		
		ItemMeta meta = _item.getItemStack().getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(name);
		meta.setLore(lore);
		ItemStack stack = _item.getItemStack();
		stack.setItemMeta(meta);
		_item.setItemStack(stack);
	}
	
	public void changeToInventory(InventoryHolder _holder) {
		state = EggState.INVENTORY;
		holder = _holder;
		if (holder instanceof DoubleChest) {
			holderType = "DOUBLECHEST";
			world = getHolderDoubleChest().getWorld();
			x = getHolderDoubleChest().getX();
			y = getHolderDoubleChest().getY();
			z = getHolderDoubleChest().getZ();
		}
		if (holder instanceof BlockState) {
			holderType = "BLOCKSTATE";
			world = getHolderBlock().getWorld();
			x = getHolderBlock().getX();
			y = getHolderBlock().getY();
			z = getHolderBlock().getZ();
		}
		if (holder instanceof Player) {
			if (((Player) holder).isOnline()) holderType = "PLAYER"; else holderType = "OFFLINEPLAYER";
			world = getHolderPlayer().getWorld();
			x = getHolderPlayer().getLocation().getX();
			y = getHolderPlayer().getLocation().getY();
			z = getHolderPlayer().getLocation().getZ();
		}
		else if (holder instanceof Entity) {
			holderType = "ENTITY";
			world = getHolderEntity().getWorld();
			x = getHolderEntity().getLocation().getX();
			y = getHolderEntity().getLocation().getY();
			z = getHolderEntity().getLocation().getZ();
		}
		item = null;
		ticksLived = 0;
		uuid = null;
	}
	
	public EggState getState() {
		return state;
	}
	
	public Block getBlock() {
		return new Location(world, x + 0.5, y + 0.5, z + 0.5).getBlock();
	}
	
	public String getHolderType() {
		return holderType;
	}
	
	public DoubleChest getHolderDoubleChest() {
		return ((DoubleChest) holder);
	}
	
	public BlockState getHolderBlock() {
		return ((BlockState) holder);
	}
	
	public Player getHolderPlayer() {
		return ((Player) holder);
	}
	
	public Entity getHolderEntity() {
		return ((Entity) holder);
	}
	
	public InventoryHolder getHolder() {
		return holder;
	}
	
	public String getName() {
		return name;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public World getWorld() {
		return world;
	}
	
	public int getTicksLived() {
		return ticksLived;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Location getLocation() {
		return new Location(world, x, y, z);
	}
}
