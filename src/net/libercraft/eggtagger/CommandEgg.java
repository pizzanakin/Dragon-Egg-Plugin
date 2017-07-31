package net.libercraft.eggtagger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

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
			
			// Make sure there was exactly one argument issued
			if (args.length != 2) return false;
			
			if (args[0].equals("set")) {
				
				// Store the egg as an entry in the database
				plugin.getData().addEntry(args[1], player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
				
				// Place the egg and give it metadata value
				player.getWorld().getBlockAt(player.getLocation()).setType(Material.DRAGON_EGG);
				player.getWorld().getBlockAt(player.getLocation()).setMetadata(args[0], new FixedMetadataValue(plugin, args[0]));
				
				// Send a confirm message to the player
				player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Placed egg with tag: " + args[1] + " at your current location");
				
			} else if (args[0].equals("get")) {
				
				// Create a new egg item with the tag as its name
				ItemStack item = new ItemStack(Material.DRAGON_EGG, 1);
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(args[1]);
				item.setItemMeta(itemMeta);
				
				// Give the egg to the player
				player.getInventory().addItem(item);
				
				// Send a confirm message to the player
				player.sendMessage(ChatColor.GOLD + "[EggTagger]" + ChatColor.GRAY + " Placed egg with tag: " + args[1] + " in your inventory");
			}

			return true;
			
		} else return false;
	}
	
}