package com.pwncraftpvp.prisonguard.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import com.pwncraftpvp.classes.core.CPlayer;
import com.pwncraftpvp.classes.utils.ClassType;
import com.pwncraftpvp.prisonguard.tasks.ProvokedTask;
import com.pwncraftpvp.prisonguard.utils.Utils;

public class Events implements Listener{
	
	Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			final Player damager = (Player) event.getDamager();
			PPlayer pdamager = new PPlayer(damager);
			final Player player = (Player) event.getEntity();
			PPlayer pplayer = new PPlayer(player);
			if(main.guards.contains(damager.getName())){
				if(damager.getItemInHand().getType() == Material.WOOD_HOE){
					event.setCancelled(true);
					if(Utils.canJailInWorld(damager.getWorld()) == true){
						if(!main.guards.contains(player.getName())){
							if(!main.jailed.containsKey(player.getName())){
								boolean hasIllegalItems = false;
								for(int x = 0; x <= 35; x++){
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
									CPlayer cplayer = new CPlayer(player);
									if(cplayer.getClassType() != ClassType.NINJA){
										pplayer.removeIllegalItems();
										pplayer.sendToJail();
										pplayer.sendMessage("You have been sent to jail by " + yellow + damager.getName() + gray + "!");
										pdamager.sendMessage("You have sent " + yellow + player.getName() + gray + " to jail!");
										pdamager.setJails(pdamager.getJails() + 1);
									}else{
										boolean hasJailPass = false;
										for(int x = 0; x <= 35; x++){
											if(player.getInventory().getItem(x) != null && player.getInventory().getItem(x).getType() != Material.AIR){
												ItemStack item = player.getInventory().getItem(x);
												if(item.getType() == Material.PAPER && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equalsIgnoreCase(yellow + "Jail Pass")){
													hasJailPass = true;
													if(item.getAmount() == 1){
														player.getInventory().setItem(x, null);
													}else if(item.getAmount() > 1){
														player.getInventory().getItem(x).setAmount(player.getInventory().getItem(x).getAmount() - 1);
													}
													break;
												}
											}
										}
										if(hasJailPass == false){
											pplayer.removeIllegalItems();
											pplayer.sendToJail();
											pplayer.sendMessage("You have been sent to jail by " + yellow + damager.getName() + gray + "!");
											pdamager.sendMessage("You have sent " + yellow + player.getName() + gray + " to jail!");
											pdamager.setJails(pdamager.getJails() + 1);
										}else{
											player.teleport(Utils.getJailReturnLocation());
											pplayer.sendMessage(yellow + damager.getName() + gray + " tried to send you to jail but you had a pass!");
										}
									}
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
					}else{
						pdamager.sendError("You may not jail people in this world!");
					}
				}else{
					if(!main.combat.contains(player.getName())){
						main.combat.add(player.getName());
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
							public void run(){
								main.combat.remove(player.getName());
							}
						}, 300);
					}
					if(!main.combat.contains(damager.getName())){
						main.combat.add(damager.getName());
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
							public void run(){
								main.combat.remove(damager.getName());
							}
						}, 300);
					}
				}
			}
			if(main.guards.contains(damager.getName()) && !main.guards.contains(player.getName())){
				if(!main.provoked.containsKey(damager.getName())){
					event.setCancelled(true);
					pdamager.sendError("You cannot attack players unless they attack you!");
				}else if(main.provoked.containsKey(damager.getName())){
					if(!main.provoked.get(damager.getName()).equalsIgnoreCase(player.getName())){
						pdamager.sendError("You cannot attack players unless they attack you!");
					}
				}
			}
			
			if(main.guards.contains(player.getName()) && !main.guards.contains(damager.getName())){
				player.getInventory().getHelmet().setDurability((short) 0);
				player.getInventory().getChestplate().setDurability((short) 0);
				player.getInventory().getLeggings().setDurability((short) 0);
				player.getInventory().getBoots().setDurability((short) 0);
				
				if(!main.provoked.containsKey(player.getName())){
					ProvokedTask task = new ProvokedTask(player, damager);
					task.runTaskTimer(main, 0, 20);
					main.provokedTasks.put(player.getName(), task);
				}else{
					main.provokedTasks.get(player.getName()).cancel();
					main.provokedTasks.remove(player.getName());
					
					ProvokedTask task = new ProvokedTask(player, damager);
					task.runTaskTimer(main, 0, 20);
					main.provokedTasks.put(player.getName(), task);
				}
			}
		}else if(event.getDamager() instanceof Projectile && event.getEntity() instanceof Player){
			final Player player = (Player) event.getEntity();
			Projectile proj = (Projectile) event.getDamager();
			if(proj.getShooter() instanceof Player){
				final Player damager = (Player) proj.getShooter();
				PPlayer pdamager = new PPlayer(damager);
				
				if(!main.combat.contains(player.getName())){
					main.combat.add(player.getName());
					main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
						public void run(){
							main.combat.remove(player.getName());
						}
					}, 300);
				}
				if(!main.combat.contains(damager.getName())){
					main.combat.add(damager.getName());
					main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
						public void run(){
							main.combat.remove(damager.getName());
						}
					}, 300);
				}
				
				if(main.guards.contains(damager.getName()) && !main.guards.contains(player.getName())){
					if(!main.provoked.containsKey(damager.getName())){
						event.setCancelled(true);
						pdamager.sendError("You cannot attack players unless they attack you!");
					}else if(main.provoked.containsKey(damager.getName())){
						if(!main.provoked.get(damager.getName()).equalsIgnoreCase(player.getName())){
							pdamager.sendError("You cannot attack players unless they attack you!");
						}
					}
				}
				
				if(main.guards.contains(player.getName()) && !main.guards.contains(damager.getName())){
					player.getInventory().getHelmet().setDurability((short) 0);
					player.getInventory().getChestplate().setDurability((short) 0);
					player.getInventory().getLeggings().setDurability((short) 0);
					player.getInventory().getBoots().setDurability((short) 0);
					
					if(!main.provoked.containsKey(player.getName())){
						ProvokedTask task = new ProvokedTask(player, damager);
						task.runTaskTimer(main, 0, 20);
						main.provokedTasks.put(player.getName(), task);
					}else{
						main.provokedTasks.get(player.getName()).cancel();
						main.provokedTasks.remove(player.getName());
						
						ProvokedTask task = new ProvokedTask(player, damager);
						task.runTaskTimer(main, 0, 20);
						main.provokedTasks.put(player.getName(), task);
					}
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
		PPlayer pplayer = new PPlayer(player);
		if(main.jailed.containsKey(player.getName())){
			event.setCancelled(true);
			pplayer.sendMessage("Finish minin' prisoner. It's the only way to get out.");
		}
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		PPlayer pplayer = new PPlayer(player);
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
	
	@EventHandler
	public void playerCommandPreprocess(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		PPlayer pplayer = new PPlayer(player);
		if(main.guards.contains(player.getName())){
			if(!event.getMessage().equalsIgnoreCase("/spawn") && !event.getMessage().equalsIgnoreCase("/guard") && !event.getMessage().contains("/warp") && !event.getMessage().contains("/tempmute")
					&& !event.getMessage().contains("/kick") && !event.getMessage().contains("/warn")){
				event.setCancelled(true);
				pplayer.sendError("You may not perform this command as a guard!");
			}
		}
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		PPlayer pplayer = new PPlayer(player);
		if(main.guards.contains(player.getName())){
			event.setCancelled(true);
			pplayer.sendError("You may not drop items as a guard!");
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		if(main.guards.contains(player.getName())){
			event.getDrops().clear();
		}
	}
	
	@EventHandler
	public void playerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(main.guards.contains(player.getName())){
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
		}
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		PPlayer pplayer = new PPlayer(player);
		if(main.guards.contains(player.getName())){
			pplayer.switchToSavedInventory(InventorySaveType.REGULAR);
			main.guards.remove(player.getName());
		}
	}
	
	@EventHandler
	public void playerKick(PlayerKickEvent event){
		Player player = event.getPlayer();
		PPlayer pplayer = new PPlayer(player);
		if(main.guards.contains(player.getName())){
			pplayer.switchToSavedInventory(InventorySaveType.REGULAR);
			main.guards.remove(player.getName());
		}
	}
}
