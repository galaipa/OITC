package me.artish1.OITC;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.artish1.OITC.Arena.Arena;
import me.artish1.OITC.Arena.Arenas;
import me.artish1.OITC.Arena.LeaveReason;
import me.artish1.OITC.Listeners.GameListener;
import me.artish1.OITC.Listeners.Gui;
import me.artish1.OITC.Listeners.SignListener;
import me.artish1.OITC.Utils.Methods;

public class OITC extends JavaPlugin {
    public final Methods methods = new Methods(this);
    public final GameListener gameListener = new GameListener(this);
    public final SignListener signListener = new SignListener(this);
    public final Gui guiListener = new Gui(this);

    public File arenasFile;
    public FileConfiguration arenas;

    public void onEnable() {
        // CONFIGURATION FILES
        getConfig().options().copyDefaults(true);
        // LOAD ARENA FILES
        this.arenasFile = new File(getDataFolder(), "arenas.yml");
        this.arenas = new YamlConfiguration();
        methods.loadYamls();
        this.arenas.options().copyDefaults(true);
        // LOAD ARENAS
        for (String name : arenas.getStringList("Arenas.List")) {
            Arena arena = new Arena(name);
            Arenas.addArena(arena);
            arena.updateSigns();
        }
        // methods.firstRun();
        // LISTENERS
        getServer().getPluginManager().registerEvents(gameListener, this);
        getServer().getPluginManager().registerEvents(signListener, this);
        getServer().getPluginManager().registerEvents(guiListener, this);

        super.onEnable();
    }

    public void onDisable() {
        for (Arena arena : Arenas.getArenas()) {
            arena.stop();
        }
        super.onDisable();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (label.equalsIgnoreCase("oitc") && !(sender instanceof Player)) {
            sender.sendMessage("Must be a player to send OITC commands");
        }

        if (label.equalsIgnoreCase("oitc") && sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");

                player.sendMessage(ChatColor.GRAY + "--------" + ChatColor.AQUA + "OITC" + ChatColor.GRAY + "--------");
                player.sendMessage(ChatColor.GRAY + "Created By: " + ChatColor.RED + "Artish1");

                player.sendMessage(ChatColor.AQUA + "/oitc lobby" + ChatColor.DARK_GRAY + " || " + ChatColor.GRAY
                        + "Teleports you to the Main Lobby");
                player.sendMessage(ChatColor.AQUA + "/oitc leave" + ChatColor.DARK_GRAY + " || " + ChatColor.GRAY
                        + "Leaves the current arena you are in");
                if (player.hasPermission("oitc.admin")) {
                    player.sendMessage(ChatColor.AQUA + "/oitc create [Arena]" + ChatColor.DARK_GRAY + " || "
                            + ChatColor.GRAY + "Creates a new Arena");
                    player.sendMessage(ChatColor.AQUA + "/oitc delete [Arena]" + ChatColor.DARK_GRAY + " || "
                            + ChatColor.GRAY + "Deletes the Arena");
                    player.sendMessage(ChatColor.AQUA + "/oitc addspawn [Arena]" + ChatColor.DARK_GRAY + " || "
                            + ChatColor.GRAY + "Adds a spawn for the Arena");

                    player.sendMessage(ChatColor.AQUA + "/oitc setmainlobby" + ChatColor.DARK_GRAY + " || "
                            + ChatColor.GRAY + " Sets the Main Lobby");
                    player.sendMessage(ChatColor.AQUA + "/oitc setlobby [Arena]" + ChatColor.DARK_GRAY + " || "
                            + ChatColor.GRAY + " Sets the Lobby of the Arena.");
                    player.sendMessage(ChatColor.AQUA + "/oitc stop [Arena]" + ChatColor.DARK_GRAY + " || "
                            + ChatColor.GRAY + "Force stops the Arena");
                    player.sendMessage(ChatColor.AQUA + "/oitc start [Arena]" + ChatColor.DARK_GRAY + " || "
                            + ChatColor.GRAY + "Force starts the Arena");
                    player.sendMessage(ChatColor.AQUA + "/oitc reload" + ChatColor.DARK_GRAY + " || " + ChatColor.GRAY
                            + "Reloads the configs.");
                    player.sendMessage(ChatColor.AQUA + "/oitc list" + ChatColor.DARK_GRAY + " || " + ChatColor.GRAY
                            + "Lists all the Arenas.");
                    player.sendMessage(ChatColor.AQUA + "/oitc version" + ChatColor.DARK_GRAY + " || " + ChatColor.GRAY
                            + "Gives you the version of this plugin.");

                }
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {

                    String arenas = "List of Arenas: " + ChatColor.DARK_AQUA;

                    for (Arena arena : Arenas.getArenas()) {
                        arenas = arenas + arena.getName() + ", ";
                    }

                    sendMessage(player, arenas);
                }

                if (args[0].equalsIgnoreCase("version")) {
                    sendMessage(player, "You are using OITC Version " + ChatColor.RED + getDescription().getVersion());

                }

                if (player.hasPermission("oitc.admin")) {
                    if (args[0].equalsIgnoreCase("setmainlobby")) {
                        Methods.setLobby(player.getLocation());
                        sendMessage(player, "You have set the Main Lobby!");
                    }

                    if (args[0].equalsIgnoreCase("reload")) {
                        Methods.loadYamls();
                        reloadConfig();
                        for (Arena arena : Arenas.getArenas()) {
                            arena.updateSigns();
                        }

                        sendMessage(player, "Reloaded Configs Successfully!");
                    }

                }

                if (args[0].equalsIgnoreCase("lobby")) {
                    if (!Arenas.isInArena(player)) {
                        if (Methods.getLobby() != null) {
                            player.teleport(Methods.getLobby());
                            sendMessage(player, "Welcome to the " + ChatColor.DARK_AQUA + "Main Lobby!");

                        } else {
                            sendMessage(player,
                                    "Oops, it seems there is no Main Lobby setup yet! Please alert your server admins.");
                        }
                    } else {
                        Arena arena = Arenas.getArena(player);
                        sendMessage(player, "Jokotik atera zara.");
                        arena.removePlayer(player, LeaveReason.QUIT);
                    }
                }

                if (args[0].equalsIgnoreCase("leave")) {
                    if (Arenas.isInArena(player)) {
                        Arena arena = Arenas.getArena(player);
                        arena.removePlayer(player, LeaveReason.QUIT);
                    } else {
                        sendMessage(player, "Ez zaude jokoan!");
                    }
                }

            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (Arenas.arenaExists(args[1])) {
                        Arena arena = Arenas.getArena(args[1]);

                        if (arena.getPlayers().size() >= 2) {
                            arena.start();
                            sendMessage(player, ChatColor.DARK_AQUA + arena.getName() + "jokoa hasi duzu");

                        } else {
                            sendMessage(player, "Cannot start arena.");
                            sendMessage(player, "It is either ingame, stopping, or not enough players.");
                        }

                    } else {
                        sendMessage(player, "Sorry, there is no such arena named " + ChatColor.RED + args[1]);
                    }
                }
                if (player.hasPermission("oitc.admin")) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (!Arenas.arenaExists(args[1])) {
                            this.arenas.addDefault("Arenas." + args[1], args[1]);
                            this.arenas.addDefault("Arenas." + args[1] + ".Signs.Counter", Integer.valueOf(0));
                            getConfig().addDefault(args[1] + ".Countdown", Integer.valueOf(15));
                            getConfig().addDefault(args[1] + ".MaxPlayers", Integer.valueOf(20));
                            getConfig().addDefault(args[1] + ".KillsToWin", Integer.valueOf(25));
                            getConfig().addDefault(args[1] + ".AutoStartPlayers", Integer.valueOf(8));
                            getConfig().addDefault(args[1] + ".EndTime", Integer.valueOf(600));
                            Arena arena = new Arena(args[1]);
                            Arenas.addArena(arena);
                            Methods.addToList(arena);
                            sendMessage(player,
                                    ChatColor.GRAY + "You have created the Arena: " + ChatColor.GOLD + arena.getName());
                            Methods.saveYamls();
                            saveConfig();
                        } else {
                            sendMessage(player, ChatColor.RED + "That Arena already Exists!");
                        }
                    }

                    if (args[0].equalsIgnoreCase("delete")) {
                        if (getConfig().contains(args[1])) {
                            getConfig().set(args[1], null);
                            this.arenas.set("Arenas." + args[1], null);

                            Methods.removeFromList(args[1]);

                            Methods.saveYamls();
                            saveConfig();
                            sendMessage(player, "You have deleted " + ChatColor.DARK_RED + args[1]);
                        } else {
                            sendMessage(player, "Sorry, there is no such arena named " + ChatColor.RED + args[1]);
                        }
                    }

                    if (args[0].equalsIgnoreCase("stop")) {
                        if (Arenas.arenaExists(args[1])) {

                            Arena arena = Arenas.getArena(args[1]);
                            arena.sendAll(
                                    ChatColor.RED + player.getName() + ChatColor.GRAY + " Has stopped the Arena!");
                            arena.stop();

                        } else {
                            sendMessage(player, "Sorry, there is no such arena named " + ChatColor.RED + args[1]);
                        }
                    }

                    if (args[0].equalsIgnoreCase("addspawn")) {
                        if (Arenas.arenaExists(args[1])) {
                            Arena arena = Arenas.getArena(args[1]);
                            arena.addSpawn(player.getLocation());
                            sendMessage(player, "You have added a spawn for " + ChatColor.DARK_AQUA + arena.getName());

                        } else {
                            sendMessage(player, "Sorry, there is no such arena named " + ChatColor.RED + args[1]);
                        }
                    }

                    if (args[0].equalsIgnoreCase("setlobby")) {
                        if (Arenas.arenaExists(args[1])) {
                            Arena arena = Arenas.getArena(args[1]);
                            arena.setLobbySpawn(player.getLocation());
                            sendMessage(player,
                                    "You have set the lobby spawn for " + ChatColor.DARK_AQUA + arena.getName());
                        } else {
                            sendMessage(player, "Sorry, there is no such arena named " + ChatColor.RED + args[1]);
                        }
                    }

                }

            }

        }

        return super.onCommand(sender, command, label, args);
    }

    public static void sendMessage(Player player, String Message) {
        player.sendMessage(
                ChatColor.GRAY + "[" + ChatColor.AQUA + "OITC" + ChatColor.GRAY + "] " + ChatColor.GRAY + Message);
    }

}
