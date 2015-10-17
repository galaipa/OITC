
package me.artish1.OITC.Listeners;

import java.util.ArrayList;
import me.artish1.OITC.Arena.Arena;
import me.artish1.OITC.Arena.Arenas;
import me.artish1.OITC.Arena.LeaveReason;
import me.artish1.OITC.OITC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Gui implements Listener {
	OITC plugin;
	
	public Gui(OITC plugin) {
	
		this.plugin = plugin;
	}
    public static ItemStack item(Material material, int id, int amount,String name, String lore){
            ItemStack b = new ItemStack(material, amount, (short) id);
            ItemMeta metaB = b.getItemMeta();                          
            metaB.setDisplayName(name);
            ArrayList <String> gui = new ArrayList<>();
            gui.add(lore);
            metaB.setLore(gui);
            b.setItemMeta(metaB);
            return b;
    }
    
    public static void maingui(Player p){
        Inventory inv = p.getInventory();
        inv.clear();
        inv.setItem(0,item(Material.STAINED_GLASS_PANE,13,1,ChatColor.GREEN + "Game Erauntsia OITC",""));
        inv.setItem(1,item(Material.STAINED_GLASS_PANE,13,1,ChatColor.GREEN + "Game Erauntsia OITC",""));
        inv.setItem(2,item(Material.STAINED_GLASS_PANE,13,1,ChatColor.GREEN + "Game Erauntsia OITC",""));
        inv.setItem(3,item(Material.STAINED_CLAY,14,1,ChatColor.YELLOW + "Jokoa hasteko bozkatu",""));
        inv.setItem(2,item(Material.STAINED_GLASS_PANE,13,1,ChatColor.GREEN + "Game Erauntsia OITC",""));
        inv.setItem(5,item(Material.BARRIER,0,1,ChatColor.RED  + "Jokotik irten",""));
        inv.setItem(6,item(Material.STAINED_GLASS_PANE,13,1,ChatColor.GREEN + "Game Erauntsia OITC",""));
        inv.setItem(7,item(Material.STAINED_GLASS_PANE,13,1,ChatColor.GREEN + "Game Erauntsia OITC",""));
        inv.setItem(8,item(Material.STAINED_GLASS_PANE,13,1,ChatColor.GREEN + "Game Erauntsia OITC",""));
    }
      @EventHandler
      public void onInventoryClick2(PlayerInteractEvent event){
          if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ){
              Player p = event.getPlayer();
              if(Arenas.isInArena(p)){
                Arena a = Arenas.getArena(p);
              if(p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()){
                   if(p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW  +"Jokoa hasteko bozkatu")){
                      event.setCancelled(true);
                      p.getInventory().setItem(3,item(Material.STAINED_CLAY,5,1,ChatColor.GREEN + "Jokoa hasteko bozkatu duzu",""));
                      bozkatu(p);
                  }else if(p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED  +"Jokotik irten")){
                      event.setCancelled(true);
                      a.removePlayer(p, LeaveReason.QUIT);
                      p.sendMessage(ChatColor.GREEN +"[Oitc] " + ChatColor.RED + "Jokotik irten zara");
                  }else if(p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN  +"Game Erauntsia OITC")){
                      event.setCancelled(true);
                  }else if(p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Jokoa hasteko bozkatu duzu")){
                      event.setCancelled(true);
                  }
          }
              }
          }
      }
    public void bozkatu(Player p){
        Arena a = Arenas.getArena(p);
        a.bozkak++;
        p.sendMessage(ChatColor.GREEN +"[Oitc] " + ChatColor.YELLOW + "Jokoa hasteko bozkatu duzu. Jokalarien %60ak bozkatzean hasiko da");
        
        if(a.getPlayers().size() == 1){
            
        }else if(a.bozketa){
            
        }
        else if(a.bozkak *100 / a.getPlayers().size() > 60){
            a.bozketa = true;
            a.start();
        }
    }
}
