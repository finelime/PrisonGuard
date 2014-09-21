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

public class PrisonPlayer {
	
	Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	Player player;
	public PrisonPlayer(Player p){
		player = p;
	}
	
	String offlinePlayer;
	public PrisonPlayer(String p){
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
	 * Set the player's inventory to the guard inventory
	 */
	public void setGuardInventory(){
		player.getInventory().clear();
		for(ItemStack i : Utils.getGuardKit()){
			player.getInventory().addItem(i);
		}
	}
	
	/**
	 * Send the player to jail
	 */
	public void sendToJail(){
		player.teleport(Utils.getJailSpawn());
		player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
		main.jailed.put(player.getName(), 0);
	}
	
	/**
	 * Remove a player from jail
	 */
	public void removeFromJail(){
		if(main.jailed.containsKey(player.getName())){
			main.jailed.remove(player.getName());
			player.teleport(Utils.getJailReturnLocation());
		}
	}

}
