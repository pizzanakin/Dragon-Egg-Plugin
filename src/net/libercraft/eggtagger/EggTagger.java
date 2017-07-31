package net.libercraft.eggtagger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class EggTagger extends JavaPlugin implements Tracer {

	File datafile;
	Database data;
	
	@Override
	public void onEnable() {
		createDatabase();
		this.getCommand("egg").setExecutor(new CommandEgg(this));
		this.getServer().getPluginManager().registerEvents(new EggListener(this), this);
		resetEggMetadata();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private void createDatabase() {
		
		// Create a reference to the datafile
		datafile = new File(getDataFolder(), "data.yml");
		
		// Make sure the data file exists
		if (!datafile.exists()) {
			datafile.getParentFile().mkdirs();
			saveResource("data.yml", false);
		}
		
		// Load the database in the data variable
		data = new Database(this);
		try  {
			data.load(datafile);
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public Database getData() {
		return data;
	}
	
	public void resetEggMetadata() {
		
		// Retrieve all the stored eggs
		List<EggTag> list = data.getEntries();
		for (EggTag entry:list) {
			
			// Store the index of this entry
			int i = list.indexOf(entry);
			Block block = entry.getLocation().getBlock();
			
			// Make sure the block is a dragon egg, and then restore the metadata
			if (block.getType().equals(Material.DRAGON_EGG)) {
				block.setMetadata(data.getString(i + ".name"), new FixedMetadataValue(this, data.getString(i + ".name")));
			}
		}
	}
}
