package me.artish1.OITC.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.artish1.OITC.OITC;
import me.artish1.OITC.Listeners.Gui;
import me.artish1.OITC.Utils.Methods;

public class Arena {
	private int id = 0;
	private String name;
	List<Location> spawnpoints;
	private int killsToWin, maxPlayers, autoStartPlayers, counter, maxPlayTime;
	private int schedulerCountdown,schedulerTime;
	boolean endtimeOn = false;
	
	private GameState state = GameState.LOBBY;
	private List<Player> players = new ArrayList<Player>();
	
	private HashMap<Player,ItemStack[]> armor = new HashMap<>();
	private HashMap<Player,ItemStack[]> inventory = new HashMap<>();
	  
	OITC plugin;
	private Scoreboard scoreboard;
	
	public Arena(String name) {
		this.name = name;
		this.plugin = Methods.getPlugin();
		loadArenaConfig();
	}
	
	public void sendAll(String msg){
	    for(Player p : players) 
	    	p.sendMessage(msg);
	}
	
	private void saveInventory(Player player) {
	  armor.put(player, player.getInventory().getArmorContents());
	  inventory.put(player, player.getInventory().getContents());
	  
	  player.getInventory().setArmorContents(null);
	  player.getInventory().clear();
	  player.updateInventory();
	}
	
	private  void loadInventory(Player player) {
		player.getInventory().setArmorContents(armor.get(player));
		armor.remove(player);
		player.getInventory().setContents(inventory.get(player));
		inventory.remove(player);
		player.updateInventory();
	}
	
	public GameState getState() {
		return state;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public void setGameState(GameState state) {
		this.state = state;
	}
	
	public boolean isOn() {
		return (getState() == GameState.INGAME) || getState() == GameState.STOPPING;
	}
	
	public String getName() {
		return name;
	}
	
	public int getKillsToWin() {
		return killsToWin;
	}
	
	public boolean canStart() {
		return (getState() != GameState.INGAME &&
				getState() != GameState.STARTING && 
				getState() != GameState.STOPPING &&
				(this.players.size() >= autoStartPlayers));
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public void healAll() {
		for(Player p : players) {
			p.setHealth(20);
			p.setFoodLevel(20);
		}
	}
	
	public void setInventories() {
		for(Player p : players) {
			setInventory(p);
		}
	}
	
	public static void setInventory(Player pl) {
		pl.setGameMode(GameMode.SURVIVAL);
		pl.getInventory().clear();
		ItemStack bow = new ItemStack(Material.BOW, 1);
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		ItemStack sword = new ItemStack(Material.WOOD_SWORD,1);
		pl.getInventory().addItem(sword);
		pl.getInventory().addItem(bow);
		pl.getInventory().addItem(arrow);
	}
	
	private void setScoreboard() {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective main = board.registerNewObjective(ChatColor.RED + "OITC", "kills");
		main.setDisplaySlot(DisplaySlot.SIDEBAR);
		for(Player p : players) {
			main.getScore(p.getName()).setScore(0);
			p.setScoreboard(board);
		}
		scoreboard = board;
	}
	
	public Scoreboard getScoreBoard(){
          return scoreboard;
    }
	
	private void spawnPlayers() {
		for(Player p : players) {
			p.teleport(getRandomSpawn());
		}
	}
	
	public Location getRandomSpawn() {
		Random rand = new Random();
		return spawnpoints.get(rand.nextInt(spawnpoints.size()));
	}
	
	public void loadSpawnPoints() {
		int count = this.plugin.arenas.getInt("Arenas." + getName() + ".Spawns.Counter");
		spawnpoints = new ArrayList<>();
		for(int i = 1; i < count;i++) {
		    Location loc = new Location(Bukkit.getWorld(
		    		this.plugin.arenas.getString("Arenas." + getName() + ".Spawns." + i + ".World")), 
		  	        this.plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + i + ".X"), 
		  	        this.plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + i + ".Y"), 
		  	        this.plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + i + ".Z"));
		  	loc.setPitch((float)this.plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + id + ".Pitch"));
		  	loc.setYaw((float)this.plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + id + ".Yaw"));
		  	spawnpoints.add(loc);
		}
	}
	
	public void loadArenaConfig() {
		killsToWin = this.plugin.getConfig().getInt(getName() + ".KillsToWin");
		maxPlayers = this.plugin.getConfig().getInt(getName() + ".MaxPlayers");
		autoStartPlayers = this.plugin.getConfig().getInt(getName() + ".AutoStartPlayers");
		counter = this.plugin.getConfig().getInt(getName() + ".Countdown");
		maxPlayTime = (this.plugin.getConfig().getInt(getName() + ".EndTime") * 20);
		loadSpawnPoints();
	}
	
	public boolean hasPlayer(Player player) {
		return players.contains(player);
	}
	
	public void addPlayer(Player player) {
		if(!players.contains(player)) {
			players.add(player);
			Arenas.addArena(player,this);
			sendAll(ChatColor.AQUA + player.getName() + ChatColor.GRAY + " sartu da.");
			saveInventory(player);
			if(state == GameState.INGAME) {
    		    player.teleport(getRandomSpawn());
    		    setInventory(player);
    		    player.setScoreboard(scoreboard);
    		    player.setHealth(20.0);
    		    player.setFoodLevel(20);
			}else {
				player.teleport(getLobbySpawn());
				waitAndGiveInv(player);
				if(canStart()) 
					start();
				
					
			}
			updateSigns();
		}
	}
	
	public void waitAndGiveInv(Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Gui.maingui(p);
		        this.cancel();
				
			}
		}.runTaskLater(plugin,10);
	}
	
	public void removePlayer(Player player, LeaveReason reason) {
		if(players.contains(player)) {
			players.remove(player);
			player.teleport(Methods.getLobby());
			loadInventory(player);
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			OITC.sendMessage(player, "Lobby-ra telegarraiatzen");
			Arenas.removeArena(player);
			
		    switch(reason) {
		    case QUIT:
		    	sendAll(ChatColor.RED + player.getName() + ChatColor.GRAY + " atera da.");
		    	break;
		    case KICK:
		    	sendAll(ChatColor.RED + player.getName() + ChatColor.GRAY + " kanporatua izan da.");
			default:
				break;
		    }
		    
		    if ((state == GameState.INGAME || state == GameState.STARTING) && (players.size() <= 1))
		    	stop();
		    
		    updateSigns();
		}
			
	}
	
	public void start() {
		if(getState() ==GameState.INGAME || getState() == GameState.STARTING || getState() == GameState.STOPPING) return;
		loadArenaConfig();
		schedulerCountdown = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {

			@Override
			public void run() {
				if(counter > 0) {
					if(counter == 45 || counter == 30 || counter == 20 || counter <= 10)
						sendAll(ChatColor.AQUA +"" + counter + ChatColor.GRAY + " segundu barru hasiko da.");
				}else {
		            state = GameState.INGAME;
		            startGameTimer();
		            healAll();
		            setScoreboard();
		            spawnPlayers();
		            setInventories();
		            Arena.this.sendAll(ChatColor.AQUA + "Jokoa hasi da!");
		            Bukkit.getScheduler().cancelTask(Arena.this.schedulerCountdown);
		            updateSigns();
				}
				counter--;
			}
			
		}, 0L, 20L);
	}
	
	public void startGameTimer() {
		endtimeOn = true;
		schedulerTime = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {

			@Override
			public void run() {
		        Arena.this.sendAll(ChatColor.GRAY + "Denbora bukatu da!");
		        Arena.this.stop();
		        Bukkit.getScheduler().cancelTask(Arena.this.schedulerTime);
			}
			
		},maxPlayTime);
	}
	
	public void stop() {
		if(getState() == GameState.STARTING)
			Bukkit.getScheduler().cancelTask(schedulerCountdown);
		if(endtimeOn)
			Bukkit.getScheduler().cancelTask(schedulerTime);
		state = GameState.STOPPING;
		updateSigns();
		
		for(Player player : players) {
			player.teleport(Methods.getLobby());
	        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	        loadInventory(player);
	 	    player.teleport(Methods.getLobby());
	 	    OITC.sendMessage(player, "Lobby nagusira telegarraiatu zaitugu");
	        Arenas.removeArena(player);	
		}
		players.clear();
		bozketa = false;
		endtimeOn = false;
		state = GameState.LOBBY;
		updateSigns();
	}
	  
      public int bozkak;
      public boolean bozketa = false;
	 
	  
	  public void updateSigns()
	  {
	    for (Location loc : getSigns()) {
	      if ((loc.getBlock().getState() instanceof Sign))
	      {
	        Sign sign = (Sign)loc.getBlock().getState();
	        int total = getPlayers().size();
	        if (this.getState() == GameState.INGAME) {
	          sign.setLine(3, ChatColor.BOLD +""+ total + "/" + getMaxPlayers());
	        } else {
	          sign.setLine(3, ChatColor.BOLD + ""+getPlayers().size() + "/" + getMaxPlayers());
	        }
	        
	       
	        if (getState() == GameState.INGAME) {
	          sign.setLine(2, ChatColor.DARK_RED + "Jokoan");
	        } else {
	        	if(getState() == GameState.LOBBY){
	          sign.setLine(2, ChatColor.GREEN + "Zain");
	        	}else{
	        		if(getState() == GameState.STOPPING){
	      	          sign.setLine(2, ChatColor.RED + "Gelditzen");
	        		}else{
	        			if(getState() == GameState.STARTING){
	        		          sign.setLine(2, ChatColor.AQUA + "Hasten");
	        			}
	        		}
	        	}
	        }
	        
	        
	        
	        sign.update();
	      }
	    }
	  }
	  
	  public List<Location> getSigns()
	  {
	    String ArenaName = getName();
	    List<Location> locs = new ArrayList<Location>();
	    for (int count = 1; this.plugin.arenas.contains("Arenas." + ArenaName + ".Signs." + count + ".X"); count++)
	    {
	      Location loc = new Location(Bukkit.getWorld(this.plugin.arenas.getString("Arenas." + ArenaName + ".Signs." + count + ".World")), 
	        this.plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".X"), 
	        this.plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Y"), 
	        this.plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Z"));
	      locs.add(loc);
	    }
	    return locs;
	  }
	  
	  public void addSign(Location loc)
	  {
	    String Arena = getName();
	    int counter = this.plugin.arenas.getInt("Arenas." + Arena + ".Signs.Counter");
	    counter++;
	    this.plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".X", Double.valueOf(loc.getX()));
	    this.plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".Y", Double.valueOf(loc.getY()));
	    this.plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".Z", Double.valueOf(loc.getZ()));
	    this.plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".World", loc.getWorld().getName());
	    
	    this.plugin.arenas.set("Arenas." + Arena + ".Signs.Counter", Integer.valueOf(counter));
	    
	    Methods.saveYamls();
	  }
	  
	  public void removeSign(Location loc)
	  {
		  String ArenaName = getName();
		  for (int count = 1; this.plugin.arenas.contains("Arenas." + ArenaName + ".Signs." + count + ".X"); count++)
		    {
		      Location loc2 = new Location(Bukkit.getWorld(this.plugin.arenas.getString("Arenas." + ArenaName + ".Signs." + count + ".World")), 
		        this.plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".X"), 
		        this.plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Y"), 
		        this.plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Z"));
		      
		      if(loc.getX() == loc2.getX() && loc.getY() == loc2.getY() && loc.getZ() == loc2.getZ()){
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".X", null);
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".Y", null);
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".Z", null);
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".World", null);
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count, null);

				  resetSigns();
				  
				  Methods.saveYamls();
		    	  break;
		     }
		  }
	  }
	  
	  private void resetSigns(){
		  	String ArenaName = getName();
		  	int newCount = 0;
		    int counter = this.plugin.arenas.getInt("Arenas." + ArenaName + ".Signs.Counter");

		  for(int i = 0; i <= counter; i++){
			  if(plugin.arenas.contains("Arenas." + ArenaName + ".Signs." + i + ".X")){
				  newCount++;
				  double x = plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + i + ".X");
				  double y = plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + i + ".Y");
				  double z = plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + i + ".Z");
				  String world = plugin.arenas.getString("Arenas." + ArenaName + ".Signs." + i + ".World");
				  
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".X", null);
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".Y", null);
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".Z", null);
		    	  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".World", null);	
				  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i, null);

				  
				  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".X",
						  x);
				  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".Y",
						  y);
				  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".Z",
						  z);
				  plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".World",
						 world);
				  
				  plugin.arenas.set("Arenas." + ArenaName + ".Signs.Counter", newCount);
			  }
		  }
	  
	  }
	  
	//FIXME
	  public void addSpawn(Location loc)
	  {
	    if (!this.plugin.arenas.contains("Arenas." + getName() + ".Spawns.1.X"))
	    {
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.Counter", Integer.valueOf(2));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".X", Double.valueOf(loc.getX()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Y", Double.valueOf(loc.getY()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Z", Double.valueOf(loc.getZ()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".World", loc.getWorld().getName());
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Pitch", Float.valueOf(loc.getPitch()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Yaw", Float.valueOf(loc.getYaw()));
	    }
	    else
	    {
	      int counter = this.plugin.arenas.getInt("Arenas." + getName() + ".Spawns.Counter");
	      this.plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".X", Double.valueOf(loc.getX()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Y", Double.valueOf(loc.getY()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Z", Double.valueOf(loc.getZ()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".World", loc.getWorld().getName());
	      this.plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Pitch", Float.valueOf(loc.getPitch()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Yaw", Float.valueOf(loc.getYaw()));
	      
	      counter++;
	      
	      this.plugin.arenas.set("Arenas." + getName() + ".Spawns.Counter", Integer.valueOf(counter));
	    }
	    Methods.saveYamls();
	  }
	  
	  
	  
	  //FIXME
	  public void setLobbySpawn(Location loc)
	  {
	    if (!this.plugin.arenas.contains("Arenas." + getName() + ".Lobby.Spawn"))
	    {
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".X", Double.valueOf(loc.getX()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Y", Double.valueOf(loc.getY()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Z", Double.valueOf(loc.getZ()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".World", loc.getWorld().getName());
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Pitch", Float.valueOf(loc.getPitch()));
	      this.plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Yaw", Float.valueOf(loc.getYaw()));
	    }
	    else
	    {
	      this.plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".X", Double.valueOf(loc.getX()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Y", Double.valueOf(loc.getY()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Z", Double.valueOf(loc.getZ()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".World", loc.getWorld().getName());
	      this.plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Pitch", Float.valueOf(loc.getPitch()));
	      this.plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Yaw", Float.valueOf(loc.getYaw()));
	    }
	    Methods.saveYamls();
	  }
	  
	  // FIXME
	  public Location getLobbySpawn()
	  {
	    if (this.plugin.arenas.contains("Arenas." + getName() + ".Lobby.Spawn" + ".World"))
	    {
	      Location loc = new Location(Bukkit.getWorld(this.plugin.arenas.getString("Arenas." + getName() + ".Lobby.Spawn" + ".World")), 
	        this.plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".X"), 
	        this.plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Y"), 
	        this.plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Z"));
	      loc.setPitch((float)this.plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Pitch"));
	      loc.setYaw((float)this.plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Yaw"));
	      return loc;
	    }
	    return null;
	  }

}
