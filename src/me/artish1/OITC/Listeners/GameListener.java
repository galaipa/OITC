package me.artish1.OITC.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.artish1.OITC.OITC;
import me.artish1.OITC.Arena.Arena;
import me.artish1.OITC.Arena.Arenas;
import me.artish1.OITC.Arena.LeaveReason;
import me.artish1.OITC.Utils.Methods;

public class GameListener implements Listener{
	
	OITC plugin;
	
	public GameListener(OITC plugin) {
	
		this.plugin = plugin;
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		final Player player = e.getPlayer();
		
		if(Arenas.isInArena(player)){
			e.setCancelled(true);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

				@SuppressWarnings("deprecation")
				public void run() {
					player.updateInventory();
					
				}
				
			}, 1L);
			
		}
	}
	
	
	 @SuppressWarnings("deprecation")
	 @EventHandler
	 public void onHit(EntityDamageByEntityEvent e)
	  {
	    if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow)))
	    {
	      Arrow arrow = (Arrow)e.getDamager();
	      if ((arrow.getShooter() instanceof Player))
	      {
	        Player attacker = (Player)arrow.getShooter();
	        Player player = (Player)e.getEntity();
	        if ((Arenas.isInArena(player)) && (Arenas.isInArena(attacker)))
	        {
	          Arena arena = Arenas.getArena(player);
	          if (arena.isOn()) {
	            if (!player.getName().equalsIgnoreCase(attacker.getName())) {
	              e.setDamage(100.0D);
	            } else {
	              e.setCancelled(true);
	            }
	          }
	        }
	      }
	    }
	  }
	
	 
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProjHit(ProjectileHitEvent e){

		if(e.getEntity() instanceof Arrow){

			Arrow arrow = (Arrow) e.getEntity();
			
			if(arrow.getShooter() instanceof Player){
				
				Player shooter = (Player) arrow.getShooter();
				
				if(Arenas.isInArena(shooter)){
					Arena arena = Arenas.getArena(shooter);
					if(arena.isOn()){
						arrow.remove();
					}
				}
			}
		}
	}
	
	@EventHandler			(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e){
		Player player = e.getPlayer();
		
		if(!Arenas.isInArena(player)){
			return;
		}
		
		Arena arena = Arenas.getArena(player);
		if(arena.isOn()){
			e.setRespawnLocation(arena.getRandomSpawn());
			Arena.setInventory(player);
			player.updateInventory();
                        player.setScoreboard(arena.getScoreBoard());
		}
		
		
		
	}
	
	@EventHandler		(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e){
		final Player player = e.getEntity();
		if(Arenas.isInArena(player)){
			Arena arena = Arenas.getArena(player);
			if(arena.isOn()){
				e.getDrops().clear();
				e.setDeathMessage("");
				e.setDroppedExp(0);
				/*Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
			      {
                               @Override
			        public void run()
			        {
			          PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
			          ((CraftPlayer)player).getHandle().playerConnection.a(packet);
			        }
			      }, 1);*/
				
				if(player.getKiller() != null){
					Player killer = player.getKiller();
					onPlayerKill(killer, player);
					
				}
			}
		}
		
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player player = e.getPlayer();
		if(!Arenas.isInArena(player)){
			return;
		}
		
		
		Arena arena = Arenas.getArena(player);
		player.teleport(Methods.getLobby());
		player.getInventory().clear();
		arena.removePlayer(player, LeaveReason.QUIT);
		
	}
	
	
	
	public void onPlayerKill(Player killer, Player player){
		Arena arena = Arenas.getArena(killer);
		killer.sendMessage(ChatColor.AQUA + player.getName()+ ChatColor.GRAY + " hil duzu" );
		player.sendMessage(ChatColor.DARK_RED + killer.getName() + ChatColor.GRAY + " -(e)k hil zaitu!");
		Methods.addArrow(killer);
		Scoreboard board = killer.getScoreboard();
		Score score = board.getObjective(DisplaySlot.SIDEBAR).getScore(killer.getName());
                int kills = score.getScore();
                kills++;
		score.setScore(kills);
		
		if(kills >= arena.getKillsToWin()){
			arena.sendAll("");
			arena.sendAll("");
			arena.sendAll("");
			arena.sendAll("");
			arena.sendAll("");
			arena.sendAll("");
			arena.sendAll("");
			arena.sendAll("");
			arena.sendAll("");

			arena.sendAll(ChatColor.GREEN +"================" + ChatColor.GRAY + "[" + ChatColor.AQUA + "OITC" + ChatColor.GRAY + "]" +ChatColor.GREEN +  "================");


			arena.sendAll(ChatColor.RED + killer.getName() + ChatColor.GRAY +
					" -(e)k irabazi du " + ChatColor.GOLD + arena.getKillsToWin() + ChatColor.GRAY + 
					" pertsona hilda" + ChatColor.AQUA + arena.getName());
			arena.sendAll(ChatColor.GREEN +"================" + ChatColor.GRAY + "[" + ChatColor.AQUA + "OITC" + ChatColor.GRAY + "]" +ChatColor.GREEN +  "================");
			
			arena.sendAll("");
			arena.sendAll("");
			arena.stop();
			
			
		}
		
	}
	
	
	@EventHandler	(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e){
		Player player = e.getPlayer();
		if(player.hasPermission("oitc.admin") || player.isOp()){
			return;
		}
		
		if(Arenas.isInArena(player)){
			Arena arena = Arenas.getArena(player);
			if(arena.isOn()){
				if(!e.getMessage().equalsIgnoreCase("/oitc") &&
						!e.getMessage().equalsIgnoreCase("/oitc lobby") &&
						!e.getMessage().equalsIgnoreCase("/oitc leave")){
					
					e.setCancelled(true);
					OITC.sendMessage(player, "Ezin duzu komandorik erabili jolasten zauden bitartean");
				}
				
				
			}
		}
		
		
		
	}
	
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		Player player = e.getPlayer();
		if(!Arenas.isInArena(player))
			return;
		
		Arena arena = Arenas.getArena(player);
		if(arena.isOn()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(!Arenas.isInArena(player))
			return;
		
		Arena arena = Arenas.getArena(player);
		if(arena.isOn()){
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
				if(e.getClickedBlock().getType() == Material.CHEST){
					e.setUseInteractedBlock(Result.DENY);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player player = e.getPlayer();
		if(!Arenas.isInArena(player))
			return;
	
		Arena arena = Arenas.getArena(player);
		if(arena.isOn()){
			e.setCancelled(true);
		}
	
	}

	
}
