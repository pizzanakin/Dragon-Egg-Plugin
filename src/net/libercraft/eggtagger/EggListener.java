package net.libercraft.eggtagger;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class EggListener implements Listener, Tracer {

	EggTagger plugin;
	
	public EggListener(EggTagger _plugin) {
		plugin = _plugin;
	}
	
	/**
	 * Called when an egg is broken, only removes it from the database
	 * @param e
	 */
	@EventHandler
	public void onEggBreak(BlockBreakEvent e) {
		
		// Check that the block is a dragon egg
		if (!e.getBlock().getType().equals(Material.DRAGON_EGG)) return;
		
		// Check if the broken block has a metadata tag
		List<EggTag> list = plugin.getData().getEntries();
		for (EggTag tag:list) {
			if (e.getBlock().hasMetadata(tag.getName())) {
				e.getBlock().removeMetadata(tag.getName(), plugin);
				
				// Get the index number of where the egg is stored
				int index = list.indexOf(tag);
				plugin.getData().removeEntry(index);
			}
		}
	}
	
	/**
	 * Called when an item is spawned, makes sure that the egg will have it's tag after breaking.
	 * @param e
	 */
	@EventHandler
	public void onEggDrop(ItemSpawnEvent e) {

		// Check if the dropped item is a dragon egg
		if (!e.getEntityType().equals(EntityType.DROPPED_ITEM)) return;
		Item item = (Item) e.getEntity();
		if (!item.getItemStack().getType().equals(Material.DRAGON_EGG)) return;
		
		// Compare the location of the dropped item with the locations in the database
		List<EggTag> list = plugin.getData().getEntries();
		for (EggTag entry:list) {
			for (int xOffset = -1; xOffset < 2; xOffset++) {
				for (int zOffset = -1; zOffset < 2; zOffset++) {
					int x = entry.getX() + xOffset;
					int z = entry.getZ() + zOffset;
					
					if (e.getEntity().getLocation().getBlockX() == x && e.getEntity().getLocation().getBlockZ() == z) {
						int i = list.indexOf(entry);
						plugin.getData().removeEntry(i);
						
						// Give the item metadata if the location matches
						ItemMeta itemMeta = item.getItemStack().getItemMeta();
						itemMeta.setDisplayName(entry.getName());
						ItemStack itemStack = item.getItemStack();
						itemStack.setItemMeta(itemMeta);
						item.setItemStack(itemStack);
					}
				}
			}
		}
	}
	
	/**
	 * Called when a block is placed, makes sure that the placed egg contains a tag and that it is stored in the database.
	 * @param e
	 */
	@EventHandler
	public void onEggPlace(BlockPlaceEvent e) {
		
		// Make sure the placed block is a dragon egg, and held by the player
		if (!e.getBlock().getType().equals(Material.DRAGON_EGG)) return;
		if (!e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DRAGON_EGG)) return;
		if (!e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) return;
		
		// Check if the held item has a custom name
		ItemMeta itemMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
		if (!itemMeta.hasDisplayName()) return;
		
		// Store the tag in the egg block
		String tag = itemMeta.getDisplayName();
		e.getBlock().setMetadata(tag, new FixedMetadataValue(plugin, tag));
		plugin.getData().addEntry(tag, e.getBlock().getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ());
	}
	
	@EventHandler
	public void onEggLand(EntityChangeBlockEvent e) {
		
		if (!e.getEntity().getType().equals(EntityType.FALLING_BLOCK)) return;
		if (!e.getTo().equals(Material.DRAGON_EGG)) return;
		
		List<EggTag> list = plugin.getData().getEntries();
		for (EggTag entry:list) {
			if (e.getBlock().getX() == entry.getX() && e.getBlock().getZ() == entry.getZ()) {
				plugin.getData().editEntryY(list.indexOf(entry), e.getBlock().getY());
			}
		}
	}
}
