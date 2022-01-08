package me.artish1.OITC.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.artish1.OITC.OITC;
import me.artish1.OITC.Arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Methods {

    static OITC plugin;

    public Methods(OITC plugin) {
        Methods.plugin = plugin;
    }

    public static OITC getPlugin() {
        return plugin;
    }

    public static void addArrow(Player player) {
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        player.getInventory().addItem(new ItemStack[] { arrow });
    }

    /*
     * @SuppressWarnings("deprecation") public static void
     * setDefaultGameInventory(final Player player){
     * 
     * ItemStack bow = new ItemStack(Material.BOW, 1); ItemStack arrow = new
     * ItemStack(Material.ARROW, 1); ItemStack sword = new
     * ItemStack(Material.WOOD_SWORD,1); ItemStack boots = new
     * ItemStack(Material.LEATHER_BOOTS,1);
     * boots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
     * player.getInventory().clear();
     * 
     * player.getInventory().addItem(sword); player.getInventory().addItem(bow);
     * player.getInventory().addItem(arrow);
     * 
     * if("4".equals(Arenas.getArena(player).getName())
     * ||"9".equals(Arenas.getArena(player).getName())){
     * player.getInventory().setBoots(boots); new BukkitRunnable(){
     * 
     * @Override public void run(){ player.addPotionEffect(new
     * PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 1)); cancel(); }
     * }.runTaskLater(plugin, 3L); } player.updateInventory();
     * 
     * }
     */

    public static ItemStack createColorArmor(ItemStack i, Color c) {
        LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
        meta.setColor(c);
        i.setItemMeta(meta);
        return i;
    }

    /*
     * public static List<Block> getNearbyCircleBlocks(Location loc, Integer r,
     * Integer h, Boolean hollow, Boolean sphere, int plus_y) { List<Block>
     * circleblocks = new ArrayList<>(); int cx = loc.getBlockX(); int cy =
     * loc.getBlockY(); int cz = loc.getBlockZ(); for (int x = cx - r.intValue(); x
     * <= cx + r.intValue(); x++) { for (int z = cz - r.intValue(); z <= cz +
     * r.intValue(); z++) { for (int y = sphere.booleanValue() ? cy - r.intValue() :
     * cy; y < (sphere.booleanValue() ? cy + r.intValue() : cy + h.intValue()); y++)
     * { double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) +
     * (sphere.booleanValue() ? (cy - y) * (cy - y) : 0); if ((dist < r.intValue() *
     * r.intValue()) && ((!hollow.booleanValue()) || (dist >= (r.intValue() - 1) *
     * (r.intValue() - 1)))) { Location l = new Location(loc.getWorld(), x, y +
     * plus_y, z); circleblocks.add(l.getBlock()); } } } } return circleblocks; }
     */

    public static List<Location> circle(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere,
            int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r.intValue(); x <= cx + r.intValue(); x++) {
            for (int z = cz - r.intValue(); z <= cz + r.intValue(); z++) {
                for (int y = sphere.booleanValue() ? cy - r.intValue()
                        : cy; y < (sphere.booleanValue() ? cy + r.intValue() : cy + h.intValue()); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
                            + (sphere.booleanValue() ? (cy - y) * (cy - y) : 0);
                    if ((dist < r.intValue() * r.intValue())
                            && ((!hollow.booleanValue()) || (dist >= (r.intValue() - 1) * (r.intValue() - 1)))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<Block> getNearbyBlocks(Location location, int Radius) {
        List<Block> Blocks = new ArrayList<Block>();
        for (int X = location.getBlockX() - Radius; X <= location.getBlockX() + Radius; X++) {
            for (int Y = location.getBlockY() - Radius; Y <= location.getBlockY() + Radius; Y++) {
                for (int Z = location.getBlockZ() - Radius; Z <= location.getBlockZ() + Radius; Z++) {
                    Block block = location.getWorld().getBlockAt(X, Y, Z);
                    if (!block.isEmpty()) {
                        Blocks.add(block);
                    }
                }
            }
        }
        return Blocks;
    }

    public static void setLobby(Location loc) {
        if (!plugin.arenas.contains("LobbySpawn")) {
            plugin.arenas.addDefault("LobbySpawn.X", loc.getX());
            plugin.arenas.addDefault("LobbySpawn.Y", loc.getY());
            plugin.arenas.addDefault("LobbySpawn.Z", loc.getZ());
            plugin.arenas.addDefault("LobbySpawn.World", loc.getWorld().getName());
            plugin.arenas.addDefault("LobbySpawn.Pitch", loc.getPitch());
            plugin.arenas.addDefault("LobbySpawn.Yaw", loc.getYaw());
        } else {
            plugin.arenas.set("LobbySpawn.X", loc.getX());
            plugin.arenas.set("LobbySpawn.Y", loc.getY());
            plugin.arenas.set("LobbySpawn.Z", loc.getZ());
            plugin.arenas.set("LobbySpawn.World", loc.getWorld().getName());
            plugin.arenas.set("LobbySpawn.Pitch", loc.getPitch());
            plugin.arenas.set("LobbySpawn.Yaw", loc.getYaw());
        }
        saveYamls();
    }

    public static Location getLobby() {
        if (plugin.arenas.contains("LobbySpawn.World")) {
            Location loc = new Location(Bukkit.getWorld(plugin.arenas.getString("LobbySpawn.World")),
                    plugin.arenas.getDouble("LobbySpawn.X"), plugin.arenas.getDouble("LobbySpawn.Y"),
                    plugin.arenas.getDouble("LobbySpawn.Z"));
            loc.setPitch((float) plugin.arenas.getDouble("LobbySpawn.Pitch"));
            loc.setYaw((float) plugin.arenas.getDouble("LobbySpawn.Yaw"));
            return loc;
        }
        return null;
    }

    public static void addToList(Arena arena) {
        if (plugin.arenas.contains("Arenas.List")) {
            List<String> list = plugin.arenas.getStringList("Arenas.List");
            list.add(arena.getName());
            plugin.arenas.set("Arenas.List", list);
        } else {
            List<String> list = new ArrayList<>();
            list.add(arena.getName());
            plugin.arenas.addDefault("Arenas.List", list);
        }
    }

    public static void removeFromList(String name) {
        if (plugin.arenas.contains("Arenas.List")) {
            List<String> list = plugin.arenas.getStringList("Arenas.List");
            list.remove(name);
            plugin.arenas.set("Arenas.List", list);
        }
    }

    public void firstRun() throws Exception {
        if (!plugin.arenasFile.exists()) {
            plugin.arenasFile.getParentFile().mkdirs();
            copy(plugin.getResource("arenasFile.yml"), plugin.arenasFile);
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception localException) {
        }
    }

    public static void saveYamls() {
        try {
            plugin.arenas.save(plugin.arenasFile);
        } catch (IOException localIOException) {
        }
    }

    public static void loadYamls() {
        try {
            plugin.arenas.load(plugin.arenasFile);

        } catch (Exception localException) {
        }
    }

}
