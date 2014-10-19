package com.pwncraftpvp.prisonguard.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.prisonguard.core.Main;
import com.pwncraftpvp.prisonguard.core.PPlayer;

public class CombatTask extends BukkitRunnable{
	
	Main main = Main.getInstance();
	
	private Player player;
	private PPlayer pplayer;
	
	/**
	 * Create a new combat task
	 * @param taskPlayer - The player who is in combat
	 */
	public CombatTask(Player taskPlayer){
		player = taskPlayer;
		pplayer = new PPlayer(taskPlayer);
		pplayer.sendMessage("You are now in combat!");
	}

	private int currentTime = 0;
	
	public void run(){
		if(currentTime < this.getMaxTime()){
			currentTime++;
		}else{
			if(main.combat.containsKey(player.getName())){
				main.combat.remove(player.getName());
				pplayer.sendMessage("You are no longer in combat!");
			}
			this.cancel();
		}
	}
	
	/**
	 * Get the amount of time the player will remain in combat
	 * @return The amount of time the player will remain in combat
	 */
	public int getMaxTime(){
		return 15;
	}
	
	/**
	 * Get the amount of time the player has been in combat
	 * @return The amount of time the player has been in combat
	 */
	public int getCurrentTime(){
		return currentTime;
	}
	
	/**
	 * Set the amount of time the player has been in combat
	 * @param time - The amount of time the player has been in combat
	 */
	public void setCurrentTime(int time){
		currentTime = time;
	}
}
