package com.pwncraftpvp.prisonguard.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	private static Main instance;
	public List<String> guards = new ArrayList<String>();
	public HashMap<String, Integer> jailed = new HashMap<String, Integer>();
	//private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	/**
	 * Get the instance of the main class
	 * @return the instance
	 */
	public static Main getInstance(){
		return instance;
	}
	
	public void onEnable(){
		instance = this;
		this.getServer().getPluginManager().registerEvents(new Events(), this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			PrisonPlayer pplayer = new PrisonPlayer(player);
			if(cmd.getName().equalsIgnoreCase("guard")){
				if(args.length > 0){
					if(args[0].equalsIgnoreCase("check")){
						if(args.length == 2){
							PrisonPlayer checkPlayer = new PrisonPlayer(args[1]);
							pplayer.sendMessageHeader("Guard Check");
							pplayer.sendMessage("Strikes: " + yellow + checkPlayer.getStrikes());
							pplayer.sendMessage("Jails: " + yellow + checkPlayer.getJails());
						}else{
							pplayer.sendError("Usage: /guard check <player>");
						}
					}else if(args[0].equalsIgnoreCase("setillegalitems")){
						Utils.setIllegalItems(player);
						pplayer.sendMessage("The illegal items list has been set!");
					}else if(args[0].equalsIgnoreCase("setguardkit")){
						Utils.setGuardKit(player);
						pplayer.sendMessage("The guard kit has been set!");
					}else if(args[0].equalsIgnoreCase("setjailspawn")){
						Utils.setJailSpawn(player.getLocation());
						pplayer.sendMessage("The jail spawn has been set!");
					}else if(args[0].equalsIgnoreCase("setjailreturn")){
						Utils.setJailReturnLocation(player.getLocation());
						pplayer.sendMessage("The jail return location has been set!");
					}else if(args[0].equalsIgnoreCase("setblocks")){
						if(args.length == 2){
							if(Utils.isInteger(args[1]) == true){
								int blocks = Integer.parseInt(args[1]);
								Utils.setJailBlocksToMine(blocks);
								pplayer.sendMessage("The amount of blocks to mine has been set!");
							}else{
								pplayer.sendError("The blocks must be an integer!");
							}
						}else{
							pplayer.sendError("Usage: /guard setblocks <blocks>");
						}
					}else if(args[0].equalsIgnoreCase("help")){
						pplayer.sendCommandHelp();
					}else{
						pplayer.sendCommandHelp();
					}
				}else{
					pplayer.setGuardInventory();
					guards.add(player.getName());
					pplayer.sendMessage("You have been given the guard kit!");
				}
			}
		}
		return false;
	}

}