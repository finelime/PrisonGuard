package com.pwncraftpvp.prisonguard.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import com.pwncraftpvp.classes.core.CPlayer;
import com.pwncraftpvp.classes.utils.ClassType;

public class Events implements Listener{
	
	Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			Player damager = (Player) event.getDamager();
			PrisonPlayer pdamager = new PrisonPlayer(damager);
			Player player = (Player) event.getEntity();
			PrisonPlayer pplayer = new PrisonPlayer(player);
			if(main.guards.contains(damager.getName())){
				event.setCancelled(true);
				if(!main.guards.contains(player.getName())){
					if(!main.jailed.containsKey(player.getName())){
						boolean hasIllegalItems = false;
						for(int x = 0; x <= 8; x++){
							if(hasIllegalItems == true){
								break;
							}
							if(player.getInventory().getItem(x) != null && player.getInventory().getItem(x).getType() != Material.AIR){
								for(ItemStack i : Utils.getIllegalItems()){
									if(i.getType() == player.getInventory().getItem(x).getType()){
										hasIllegalItems = true;
										break;
									}
								}
							}
						}
						if(hasIllegalItems == true){
							if(main.getServer().getPluginManager().getPlugin("Classes") != null){
								CPlayer cplayer = new CPlayer(player);
								if(cplayer.getClassType() != ClassType.NINJA){
									for(int x = 0; x <= 8; x++){
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
							}else{
								for(int x = 0; x <= 8; x++){
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
							pplayer.sendToJail();
							pplayer.sendMessage("You have been sent to jail by " + yellow + damager.getName() + gray + "!");
							pdamager.sendMessage("You have sent " + yellow + player.getName() + gray + " to jail!");
							pdamager.setJails(pdamager.getJails() + 1);
						}else{
							pdamager.setStrikes(pdamager.getStrikes() + 1);
							pdamager.sendError("This player does not have any illegal items!");
							pdamager.sendError("You have received a strike!");
						}
					}else{
						pdamager.sendError("This player is already in jail!");
					}
				}else{
					pdamager.sendError("This player is also a guard!");
				}
			}
		}
	}
	
	/*
	 * Cancel teleporting instead of commands to prevent complications
	 * 
	@EventHandler
	public void playerCommandPreprocess(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		PrisonPlayer pplayer = new PrisonPlayer(player);
		if(main.jailed.contains(player.getName())){
			event.setCancelled(true);
			pplayer.sendMessage("Finish minin' prisoner. It's the only way to get out.");
		}
	}
	*/
	
	@EventHandler
	public void playerTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		PrisonPlayer pplayer = new PrisonPlayer(player);
		if(main.jailed.containsKey(player.getName())){
			event.setCancelled(true);
			pplayer.sendMessage("Finish minin' prisoner. It's the only way to get out.");
		}
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		PrisonPlayer pplayer = new PrisonPlayer(player);
		if(main.jailed.containsKey(player.getName())){
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
			int blocks = main.jailed.get(player.getName());
			main.jailed.remove(player.getName());
			main.jailed.put(player.getName(), blocks + 1);
			
			if(main.jailed.get(player.getName()) >= Utils.getJailBlocksToMine()){
				pplayer.removeFromJail();
				pplayer.sendMessage("You've done your work prisoner. You're free... for now.");
			}
		}
	}
	
}
