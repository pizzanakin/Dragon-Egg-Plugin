package net.libercraft.eggtagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EggTagger extends JavaPlugin {

	File datafile;
	Database data;
	List<EggTag> eggtags;
	
	@Override
	public void onEnable() {
		createDatabase();
		this.getCommand("egg").setExecutor(new CommandEgg(this));
		this.getServer().getPluginManager().registerEvents(new EggListener(this), this);
		eggtags = loadEggtags();
		
		new BukkitRunnable() {

			@Override
			public void run() {
				for (EggTag eggtag:eggtags) {
					if (eggtag.updateTag()) {
						data.updateEntry(eggtags.indexOf(eggtag), eggtag);
					}
				}
			}
			
		}.runTaskTimer(this, 0, 1);
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
	
	private List<EggTag> loadEggtags() {
		
		List<EggTag> list = new ArrayList<EggTag>();
		for (int i = 0; i < data.getLength(); i++) {
			EggTag entry = data.getEntry(i);
			if (entry == null) continue; 
			list.add(entry);
		}
		return list;
	}
	
	public List<EggTag> getEggtags() {
		return eggtags;
	}
	
	public void registerEggTag(EggTag eggtag) {
		eggtags.add(eggtag);
		data.updateEntry(eggtags.indexOf(eggtag), eggtag);
	}
	
	public Database getData() {
		return data;
	}
}
