package com.pwncraftpvp.prisonguard.core;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pwncraftpvp.prisonguard.utils.UTFUtils;
import com.pwncraftpvp.prisonguard.utils.Utils;

public class PPlayer {
	
	Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	Player player;
	public PPlayer(Player p){
		player = p;
	}
	
	String offlinePlayer;
	public PPlayer(String p){
		offlinePlayer = p;
	}
	
	/**
	 * Get the player's file
	 */
	@SuppressWarnings("deprecation")
	public File getFile(){
		if(player != null){
			return new File(main.getDataFolder() + File.separator + "players", player.getUniqueId() + ".yml");
		}else{
			return new File(main.getDataFolder() + File.separator + "players", Bukkit.getOfflinePlayer(offlinePlayer).getUniqueId() + ".yml");
		}
	}
	
	/**
	 * Get the player's config
	 */
	public FileConfiguration getConfig(){
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	/**
	 * Set a value in the player's config
	 * 
	 * @param key - The location of the value to set
	 * @param entry - The value to set
	 */
	public void setConfigValue(String key, Object entry){
		FileConfiguration fc = getConfig();
	    fc.set(key, entry);
	    try{
	      fc.save(getFile());
	    }catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	/**
	 * Get the player's strike count
	 * @return the player's strikes
	 */
	public int getStrikes(){
		return this.getConfig().getInt("strikes");
	}
	
	/**
	 * Set the player's strike count
	 * @param strikes - The strikes value to set
	 */
	public void setStrikes(int strikes){
		this.setConfigValue("strikes", strikes);
	}
	
	/**
	 * Get the player's jail count
	 * @return the player's jail count
	 */
	public int getJails(){
		return this.getConfig().getInt("jails");
	}
	
	/**
	 * Set the player's jail count
	 * @param strikes - The jail count value to set
	 */
	public void setJails(int jails){
		this.setConfigValue("jails", jails);
	}
	
	/**
	 * Send a message header to the player
	 * @param header - The header to be sent
	 */
	public void sendMessageHeader(String header){
		player.sendMessage(gray + "-=(" + yellow + "*" + gray + ")=-" + "  " + yellow + header + "  " + gray + "-=(" + yellow + "*" + gray + ")=-");
	}
	
	/**
	 * Send a message to the player
	 * @param message - The message to be sent
	 */
	public void sendMessage(String message){
		player.sendMessage(ChatColor.GOLD + UTFUtils.getArrow() + gray + " " + message);
	}
	
	/**
	 * Send an error message to the player
	 * @param error - The error message to be sent
	 */
	public void sendError(String error){
		player.sendMessage(ChatColor.GOLD + UTFUtils.getArrow() + ChatColor.DARK_RED + " " + error);
	}
	
	/**
	 * Send the command help page to the player
	 */
	public void sendCommandHelp(){
		this.sendMessageHeader("Command Help");
		this.sendMessage(yellow + "/guard  " + gray + "- Get the guard kit!");
		if(player.hasPermission("prisonguard.checkstrikes") == true){
			this.sendMessage(yellow + "/guard check <player> " + gray + "- Check a player's strikes!");
		}
		if(player.isOp() == true){
			this.sendMessage(yellow + "/guard setillegalitems " + gray + "- Set the illegal items!");
			this.sendMessage(yellow + "/guard setguardkit " + gray + "- Set the guard kit!");
			this.sendMessage(yellow + "/guard setjailspawn " + gray + "- Set the jail spawn!");
			this.sendMessage(yellow + "/guard setjailreturn " + gray + "- Set the jail return location!");
			this.sendMessage(yellow + "/guard setblocks <blocks> " + gray + "- Set the blocks a jailed player must mine!");
		}
	}
	
	/**
	 * Save the player's regular inventory to their player file
	 * @param type - The inventory save type
	 */
	public void saveInventory(InventorySaveType type){
		this.clearSavedInventory(type);
		for(int x = 0; x <= 35; x++){
			if(player.getInventory().getItem(x) != null && player.getInventory().getItem(x).getType() != Material.AIR){
				ItemStack item = player.getInventory().getItem(x);
				this.setConfigValue("cachedInventory." + type.toString().toLowerCase() + "." + x + ".item", item);
			}
		}
		if(player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() != Material.AIR){
			this.setConfigValue("cachedInventory." + type.toString().toLowerCase() + ".armor.helmet", player.getInventory().getHelmet());
		}
		if(player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() != Material.AIR){
			this.setConfigValue("cachedInventory." + type.toString().toLowerCase() + ".armor.chestplate", player.getInventory().getChestplate());
		}
		if(player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() != Material.AIR){
			this.setConfigValue("cachedInventory." + type.toString().toLowerCase() + ".armor.leggings", player.getInventory().getLeggings());
		}
		if(player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() != Material.AIR){
			this.setConfigValue("cachedInventory." + type.toString().toLowerCase() + ".armor.boots", player.getInventory().getBoots());
		}
	}
	
	/**
	 * Set the player's inventory to their regular inventory
	 * @param type - The inventory save type
	 */
	public void switchToSavedInventory(InventorySaveType type){
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		for(int x = 0; x <= 35; x++){
			if(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + "." + x + ".item") != null){
				player.getInventory().setItem(x, this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + "." + x + ".item"));
			}
		}
		if(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.helmet") != null){
			player.getInventory().setHelmet(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.helmet"));
		}
		if(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.chestplate") != null){
			player.getInventory().setChestplate(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.chestplate"));
		}
		if(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.leggings") != null){
			player.getInventory().setLeggings(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.leggings"));
		}
		if(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.boots") != null){
			player.getInventory().setBoots(this.getConfig().getItemStack("cachedInventory." + type.toString().toLowerCase() + ".armor.boots"));
		}
		player.updateInventory();
		this.clearSavedInventory(type);
	}
	
	/**
	 * Clear the player's cached regular inventory
	 * @param type - The inventory save type
	 */
	public void clearSavedInventory(InventorySaveType type){
		this.setConfigValue("cachedInventory." + type.toString().toLowerCase(), null);
	}
	
	/**
	 * Set the player's inventory to the guard inventory
	 */
	public void switchToGuardInventory(){
		this.saveInventory(InventorySaveType.REGULAR);
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		for(ItemStack i : Utils.getGuardKit()){
			String itemName = i.getType().toString().toLowerCase();
			if(!itemName.contains("helmet") && !itemName.contains("chestplate") && !itemName.contains("leggings") && !itemName.contains("boots")){
				player.getInventory().addItem(i);
			}else{
				if(itemName.contains("helmet")){
					player.getInventory().setHelmet(i);
				}else if(itemName.contains("chestplate")){
					player.getInventory().setChestplate(i);
				}else if(itemName.contains("leggings")){
					player.getInventory().setLeggings(i);
				}else if(itemName.contains("boots")){
					player.getInventory().setBoots(i);
				}
			}
		}
		player.updateInventory();
	}
	
	/**
	 * Send the player to jail
	 */
	public void sendToJail(){
		this.saveInventory(InventorySaveType.JAIL);
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.teleport(Utils.getJailSpawn());
		for(int x = 1; x <= 5; x++){
			player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
		}
		main.jailed.put(player.getName(), 0);
	}
	
	/**
	 * Remove a player from jail
	 */
	public void removeFromJail(){
		if(main.jailed.containsKey(player.getName())){
			main.jailed.remove(player.getName());
			player.teleport(Utils.getJailReturnLocation());
			this.switchToSavedInventory(InventorySaveType.JAIL);
		}
	}
	
	/**
	 * Remove any illegal items from the player's inventory
	 */
	public void removeIllegalItems(){
		for(int x = 0; x <= 35; x++){
			if(player.getInventory().getItem(x) != null && player.getInventory().getItem(x).getType() != Material.AIR){
				for(ItemStack i : Utils.getIllegalItems()){
					try{
						if(i.getType() == player.getInventory().getItem(x).getType()){
							player.getInventory().setItem(x, null);
						}
					}catch (NullPointerException ex){
						
					}
				}
			}
		}
	}
	
	/**
	 * Check if the player is a guard
	 * @return True or false depending on if the player is a guard or not
	 */
	public boolean isGuard(){
		if(main.guards.contains(player.getName())){
			return true;
		}else{
			return false;
		}
	}
}
