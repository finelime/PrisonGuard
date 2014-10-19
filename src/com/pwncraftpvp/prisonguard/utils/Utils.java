package com.pwncraftpvp.prisonguard.utils;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.pwncraftpvp.prisonguard.core.Main;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

public class Utils {
	
	static Main main = Main.getInstance();
	private static String gray = ChatColor.GRAY + "";
	//private static String yellow = ChatColor.YELLOW + "";
	
	/**
	 * Get the WorldGuardPlugin
	 * @return - The WorldGuardPlugin
	 */
	public static WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = main.getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	
	/**
	 * Check if a player can build at the location	
	 * @param player - The player to perform checks on
	 * @return - True or false depending on if the player can build at the location or not
	 */
	public static boolean canBreakHere(Player player, Location loc){
		return getWorldGuard().canBuild(player, loc);
	}
	
	/**
	 * Check if a location is in a pvp area
	 * @param loc - The location to check
	 * @return True or false depending on if the location is in a pvp area or not
	 */
	public static boolean canPvP(Location loc){
		boolean can = true;
		ApplicableRegionSet set = getWorldGuard().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
        if(set.getFlag(DefaultFlag.PVP) != null){
        	if(set.getFlag(DefaultFlag.PVP) == State.DENY){
            	can = false;
        	}
        }
        return can;
	}
	
	/**
	 * Check if a string is also an integer
	 * @param isIt - The string to check
	 * @return True or false depending on if the string is an integer or not
	 */
	public static boolean isInteger(String isIt){
		try{
			Integer.parseInt(isIt);
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	/**
	 * Get the total illegal items
	 * @return The total illegal items
	 */
	public static int getTotalIllegalItems(){
		return main.getConfig().getInt("totalIllegalItems");
	}
	
	/**
	 * Broadcast a message to all players
	 * @param message - The message to broadcast
	 */
	public static void broadcastMessage(String message){
		for(Player p : Bukkit.getOnlinePlayers()){
			p.sendMessage(ChatColor.GOLD + UTFUtils.getArrow() + gray + " " + message);
		}
	}
	
	/**
	 * Set the total illegal items
	 * @param total - The total illegal items to set
	 */
	public static void setTotalIllegalItems(int total){
		main.getConfig().set("totalIllegalItems", total);
		main.saveConfig();
	}
	
	/**
	 * Set the list of illegal items by the items in a player's inventory
	 * @param player - The player to copy the inventory from
	 */
	public static void setIllegalItems(Player player){
		for(int x = 1; x <= player.getInventory().getSize(); x++){
			int slot = (x - 1);
			if(player.getInventory().getItem(slot) != null && player.getInventory().getItem(slot).getType() != Material.AIR){
				ItemStack item = player.getInventory().getItem(slot);
				main.getConfig().set("illegalItems." + slot + ".item", item);
			}
		}
		main.saveConfig();
	}
	
	/**
	 * Get the illegal items list
	 * @return The illegal items list
	 */
	public static List<ItemStack> getIllegalItems(){
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(int x = 0; x <= 35; x++){
			if(main.getConfig().getItemStack("illegalItems." + x + ".item") != null && main.getConfig().getItemStack("illegalItems." + x + ".item").getType() != Material.AIR){
				items.add(main.getConfig().getItemStack("illegalItems." + x + ".item"));
			}
		}
		return items;
	}
	
	/**
	 * Set the guard kit by the items in a player's inventory
	 * @param player - The player to copy the inventory from
	 */
	public static void setGuardKit(Player player){
		main.getConfig().set("guardKit", null);
		for(int x = 1; x <= player.getInventory().getSize(); x++){
			int slot = (x - 1);
			if(player.getInventory().getItem(slot) != null && player.getInventory().getItem(slot).getType() != Material.AIR){
				ItemStack item = player.getInventory().getItem(slot);
				main.getConfig().set("guardKit." + slot + ".item", item);
			}
		}
		if(player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() != Material.AIR){
			main.getConfig().set("guardKit.armor.helmet", player.getInventory().getHelmet());
		}
		if(player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() != Material.AIR){
			main.getConfig().set("guardKit.armor.chestplate", player.getInventory().getChestplate());
		}
		if(player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() != Material.AIR){
			main.getConfig().set("guardKit.armor.leggings", player.getInventory().getLeggings());
		}
		if(player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() != Material.AIR){
			main.getConfig().set("guardKit.armor.boots", player.getInventory().getBoots());
		}
		main.saveConfig();
	}
	
	/**
	 * Get the guard kit item list
	 * @return The guard kit item list
	 */
	public static List<ItemStack> getGuardKit(){
		List<ItemStack> kit = new ArrayList<ItemStack>();
		for(int x = 0; x <= 35; x++){
			if(main.getConfig().getItemStack("guardKit." + x + ".item") != null && main.getConfig().getItemStack("guardKit." + x + ".item").getType() != Material.AIR){
				kit.add(main.getConfig().getItemStack("guardKit." + x + ".item"));
			}
		}
		if(main.getConfig().getItemStack("guardKit.armor.helmet") != null){
			kit.add(main.getConfig().getItemStack("guardKit.armor.helmet"));
		}
		if(main.getConfig().getItemStack("guardKit.armor.chestplate") != null){
			kit.add(main.getConfig().getItemStack("guardKit.armor.chestplate"));
		}
		if(main.getConfig().getItemStack("guardKit.armor.leggings") != null){
			kit.add(main.getConfig().getItemStack("guardKit.armor.leggings"));
		}
		if(main.getConfig().getItemStack("guardKit.armor.boots") != null){
			kit.add(main.getConfig().getItemStack("guardKit.armor.boots"));
		}
		return kit;
	}
	
	/**
	 * Set the jail spawn to a location
	 * @param loc - The location to set the jail spawn to
	 */
	public static void setJailSpawn(Location loc){
		main.getConfig().set("jail.spawn.x", loc.getX());
		main.getConfig().set("jail.spawn.y", loc.getY());
		main.getConfig().set("jail.spawn.z", loc.getZ());
		main.getConfig().set("jail.spawn.yaw", loc.getYaw());
		main.getConfig().set("jail.spawn.pitch", loc.getPitch());
		main.getConfig().set("jail.spawn.world", loc.getWorld().getName());
		main.saveConfig();
	}
	
	/**
	 * Get the jail spawn
	 * @return - The location of the jail spawn
	 */
	public static Location getJailSpawn(){
		double x,y,z;
		x = main.getConfig().getDouble("jail.spawn.x");
		y = main.getConfig().getDouble("jail.spawn.y");
		z = main.getConfig().getDouble("jail.spawn.z");
		int yaw,pitch;
		yaw = main.getConfig().getInt("jail.spawn.yaw");
		pitch = main.getConfig().getInt("jail.spawn.pitch");
		World world = Bukkit.getWorld(main.getConfig().getString("jail.spawn.world"));
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	/**
	 * Set the jail return location
	 * @param loc - The location to set the jail return location to
	 */
	public static void setJailReturnLocation(Location loc){
		main.getConfig().set("jail.returnLoc.x", loc.getX());
		main.getConfig().set("jail.returnLoc.y", loc.getY());
		main.getConfig().set("jail.returnLoc.z", loc.getZ());
		main.getConfig().set("jail.returnLoc.yaw", loc.getYaw());
		main.getConfig().set("jail.returnLoc.pitch", loc.getPitch());
		main.getConfig().set("jail.returnLoc.world", loc.getWorld().getName());
		main.saveConfig();
	}
	
	/**
	 * Get the jail return location
	 * @return - The jail return location
	 */
	public static Location getJailReturnLocation(){
		double x,y,z;
		x = main.getConfig().getDouble("jail.returnLoc.x");
		y = main.getConfig().getDouble("jail.returnLoc.y");
		z = main.getConfig().getDouble("jail.returnLoc.z");
		int yaw,pitch;
		yaw = main.getConfig().getInt("jail.returnLoc.yaw");
		pitch = main.getConfig().getInt("jail.returnLoc.pitch");
		World world = Bukkit.getWorld(main.getConfig().getString("jail.returnLoc.world"));
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	/**
	 * Set the blocks a player must mine to exit jail!
	 * @param blocks
	 */
	public static void setJailBlocksToMine(int blocks){
		main.getConfig().set("jail.blocksToMine", blocks);
		main.saveConfig();
	}
	
	/**
	 * Get the blocks a player must mine to exit jail
	 * @return - The amount of blocks a player must mine to exit jail
	 */
	public static int getJailBlocksToMine(){
		return main.getConfig().getInt("jail.blocksToMine");
	}
	
	/**
	 * Get a list of worlds guards can jail players in
	 * @return - The list of worlds guards can jail players in
	 */
	public static List<World> getJailableWorlds(){
		List<World> worlds = new ArrayList<World>();
		for(String s : main.getConfig().getStringList("jail.worldsList")){
			worlds.add(Bukkit.getWorld(s));
		}
		return worlds;
	}
	
	public static boolean canJailInWorld(World world){
		boolean can = false;
		for(World w : getJailableWorlds()){
			if(world.getName().equalsIgnoreCase(w.getName())){
				can = true;
				break;
			}
		}
		return can;
	}
}
