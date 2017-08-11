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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EggListener implements Listener {

	EggTagger plugin;
	int index;
	
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
		for (int i = 0; i < plugin.getEggtags().size(); i++) {
			
			if (!plugin.getEggtags().get(i).getState().equals(EggState.BLOCK)) continue;
			if (!e.getBlock().equals(plugin.getEggtags().get(i).getBlock())) continue;
			plugin.getData().removeEntry(i);
			plugin.eggtags.remove(i);
		}
	}
	
	@EventHandler
	public final void onEggStore(InventoryClickEvent e) {
		
		if ((e.getAction().equals(InventoryAction.PLACE_ALL)|e.getAction().equals(InventoryAction.PLACE_ONE))&&e.getCursor().getType().equals(Material.DRAGON_EGG)) {
			ItemMeta itemMeta = e.getCursor().getItemMeta();// Check if the held item has a custom name
			List<EggTag> list = plugin.getEggtags();
			for (EggTag tag:list) {
				if (!tag.getState().equals(EggState.INVENTORY)) continue;
				
				for (String lore:itemMeta.getLore()) {
					if (!lore.equals(tag.getName())) continue;
					tag.changeToInventory(e.getClickedInventory().getHolder());
					plugin.getData().changeToInventory(list.indexOf(tag), e.getClickedInventory().getHolder());
				}
			}
		}
		else if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && e.getCurrentItem().getType().equals(Material.DRAGON_EGG)) {
			ItemMeta itemMeta = e.getCurrentItem().getItemMeta();// Check if the held item has a custom name
			List<EggTag> list = plugin.getEggtags();
			for (EggTag tag:list) {
				if (!tag.getState().equals(EggState.INVENTORY)) continue;
				
				for (String lore:itemMeta.getLore()) {
					if (!lore.equals(tag.getName())) continue;
					if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
						tag.changeToInventory(e.getInventory().getHolder());
						plugin.getData().changeToInventory(list.indexOf(tag), e.getInventory().getHolder());
					}
					else {
						tag.changeToInventory(e.getWhoClicked());
						plugin.getData().changeToInventory(list.indexOf(tag), e.getWhoClicked());
					}
				}
			}
		}
	}
	
	/**
	 * Called when the egg as item is picked up by a player
	 * @param e
	 */
	@EventHandler
	public void onEggPickup(PlayerPickupItemEvent e) {
		
		// Make sure the item picked up is a dragon egg
		if (!e.getItem().getItemStack().getType().equals(Material.DRAGON_EGG)) return;
		
		// Load the list of entries
		List<EggTag> list = plugin.getEggtags();
		for (EggTag tag:list) {
			if (!tag.getState().equals(EggState.ITEM)) continue;
			
			// Check if the egg has a tag as name
			for (String lore:e.getItem().getItemStack().getItemMeta().getLore()) {
				if (!lore.equals(tag.getName())) continue;
				tag.changeToInventory(e.getPlayer());
				plugin.getData().changeToInventory(list.indexOf(tag), e.getPlayer());
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
		List<EggTag> list = plugin.getEggtags();
		for (EggTag tag:list) {
			if (tag.getState().equals(EggState.ITEM)) continue;
			
			if (item.getLocation().distance(tag.getLocation()) <= 3) {
				tag.changeToItem(item);
				plugin.getData().changeToItem(list.indexOf(tag), item);
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
		if (!itemMeta.hasLore()) return;
		List<EggTag> list = plugin.getEggtags();
		for (EggTag tag:list) {
			if (!tag.getState().equals(EggState.INVENTORY)) continue;
			
			for (String lore:itemMeta.getLore()) {
				if (!lore.equals(tag.getName())) continue;
				tag.changeToBlock(e.getBlock());
				plugin.getData().changeToBlock(list.indexOf(tag), e.getBlock());
				ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
				item.setAmount(item.getAmount() - 1);
				e.getPlayer().getInventory().setItemInMainHand(item);
			}
		}
	}
	
	@EventHandler
	public void onEggLand(EntityChangeBlockEvent e) {
		
		if (!e.getEntity().getType().equals(EntityType.FALLING_BLOCK)) return;
		if (!e.getTo().equals(Material.DRAGON_EGG)) return;
		
		List<EggTag> list = plugin.getEggtags(); 
		for (EggTag tag:list) {
			if (!tag.getState().equals(EggState.BLOCK)) continue;
			
			if (e.getBlock().getX() == tag.getX() && e.getBlock().getZ() == tag.getZ()) {
				tag.changeToBlock(e.getBlock());
				plugin.getData().changeToBlock(list.indexOf(tag), e.getBlock());
				
			}
		}
	}
}
