package com.pwncraftpvp.prisonguard.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.prisonguard.core.Main;

public class ProvokedTask extends BukkitRunnable{
	
	Main main = Main.getInstance();
	
	Player player;
	Player damager;
	public ProvokedTask(Player p, Player d){
		player = p;
		damager = d;
		main.provoked.put(player.getName(), damager.getName());
	}
	
	int count = 11;
	public void run(){
		if(count > 0){
			count--;
		}else{
			main.provoked.remove(player.getName());
			main.provokedTasks.remove(player.getName());
			this.cancel();
		}
	}

}
