package net.libercraft.eggtagger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandEgg implements CommandExecutor {

	EggTagger plugin;
	
	public CommandEgg(EggTagger _plugin) {
		plugin = _plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	
		// Make sure the command was issued by a player
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			// Check if the player has permission
			if (!player.hasPermission("eggtag.egg")) {
				player.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe this is in error.");
				return true;
			}
			
			String commandType = args[0];
			String tagName = args[1];
			Block block;
			
			switch (commandType) {
			case "teleport":
				if (args.length != 2) return false;
				player.sendMessage("Teleporting to the location of tag: " + tagName);
				for (EggTag tag:plugin.getEggtags()) {
					if (tag.getName().equals(tagName)) {
						player.teleport(tag.getLocation());
					}
				}
				return true;
			case "get":
				if (args.length != 2) return false;
				player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Requested info on tag: " + tagName);
				
				for (EggTag tag:plugin.getEggtags()) {
					if (tag.getName().equals(tagName)) {
						switch (tag.getState()) {
						case BLOCK:
							player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " State: " + tag.getState().name());
							player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Location: " + tag.getX() + ", " + tag.getY() + ", " + tag.getZ());
							return true;
						case INVENTORY:
							player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "State: " + tag.getState().name());
							player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "Location: " + tag.getX() + ", " + tag.getY() + ", " + tag.getZ());
							if (tag.getHolderType().equals("ENTITY")) player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "Holder: " + tag.getHolderEntity().getUniqueId().toString());
							player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "Holder Type: " + tag.getHolderType());
							return true;
						case ITEM:
							player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "State: " + tag.getState().name());
							player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "Location: " + tag.getX() + ", " + tag.getY() + ", " + tag.getZ());
							player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "Ticks Alive: " + tag.getTicksLived());
							player.sendMessage(ChatColor.GOLD + "[EggTagger] " + ChatColor.GRAY + "UUID: " + tag.getUUID().toString());
							return true;
						default:
							return true;
						}
					}
				}
				return true;
			case "place":
				
				// Make sure the proper arguments were given
				if (args.length != 2) return false;
				
				// Check for duplicate tag name
				for (EggTag tag:plugin.getEggtags()) {
					if (tag.getName().equals(tagName)) {
						player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Error: This tag already exists.");
						return true;
					}
				}
				
				// Place the egg
				block = player.getWorld().getBlockAt(player.getLocation());
				if (!block.getType().equals(Material.AIR)) {
					player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Error: Cannot place egg becase the block is occupied.");
					return true;
				}
				block.setType(Material.DRAGON_EGG);
				
				// Register the tag
				plugin.registerEggTag(new EggTag(tagName, block));
				
				// Send a confirm message to the player
				player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Placed egg with tag: " + args[1] + " at your current location");
				return true;
			case "give":
				
				// Make sure the proper arguments were given
				if (args.length != 2) return false;
				
				// Check for duplicate tag name
				for (EggTag tag:plugin.getEggtags()) {
					if (tag.getName().equals(tagName)) {
						player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Error: This tag already exists.");
						return true;
					}
				}
				
				// Create a new item with the tag as its name
				ItemStack item = new ItemStack(Material.DRAGON_EGG, 1);
				ItemMeta itemMeta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				lore.add(tagName);
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				
				// Give the egg to the player
				player.getInventory().addItem(item);
				
				// Register the tag
				plugin.registerEggTag(new EggTag(tagName, player));
				
				// Send a confirm message to the player
				player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Placed egg with tag: " + args[1] + " in your inventory");
				return true;
			case "tag":
				if (args.length != 5) return false;
				block = new Location(player.getWorld(), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])).getBlock();
				if (!block.getType().equals(Material.DRAGON_EGG)) {
					player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Error: That block is not a dragon egg");
					return true;
				}
				plugin.registerEggTag(new EggTag(tagName, block));
				return true;
			default:
				return false;
			}
		}
		else {
			System.out.println("EggTagger Error: Must be a player to execute this command");
			return true;
		}
	}
	
}