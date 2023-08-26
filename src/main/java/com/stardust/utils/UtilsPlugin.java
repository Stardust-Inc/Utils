package com.stardust.utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.stardust.utils.Teleport;
import com.stardust.utils.UtilsListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import com.stardust.dbplugin.utils.Tabinator;

import com.stardust.dbplugin.utils.Cmd;
import com.stardust.dbplugin.saved.PlayerDocument;
import com.stardust.dbplugin.saved.PlayerDocument.Races;
import static com.stardust.utils.UtilsPlugin.PluginCommands.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utils Plugin
 */
public class UtilsPlugin extends JavaPlugin implements CommandExecutor {
    protected enum PluginCommands {
        tport_loc,
        tport_wild,
        chat_clear,
        tport_save,
        tport_spawn,
    }

    Tabinator tabinator;
    UtilsListener utilsListner;
    private Teleport teleport = new Teleport();
    private FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Utils enabled!");

        if (!config.contains("spawn")) {
            config.addDefault("spawn", "0 0 0");
        }
        config.options().copyDefaults(true);
        saveConfig();

        getCommand("tport").setExecutor(this);
        tabinator = new Tabinator(PluginCommands.class);
        utilsListner = new UtilsListener();
        getServer().getPluginManager().registerEvents(utilsListner, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Utils disabled!");
    }

    /** PlayerClass Command Handler */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        final Player player = (Player) sender;

        Cmd.build()
                .addCase(tport_save,                    () -> save(player, args))
                .addCase(tport_loc,                         teleport::onCommandGoLoc)
                .addCase(tport_wild,                    () -> teleport.toWild(player))
                .addCase(tport_spawn,                   () -> teleport.toSpawn(player, config.getString("spawn").split(" "), Bukkit.getServer().getWorld("spawn")))
                .addCase(chat_clear, () -> {
                    for (Integer i = 0; i < 100; i++) player.sendMessage("\n");
                })
                .addDefaultCase(                        () -> messageGeneralUsage(player, args))
                .execute(player, args);
        return true;
    }

    /** Called when tab completion is required. Given a list of current arguments, returns a list
     * of suggested strings for tab completion. Just let Tabinator auto handle the logic for this */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return tabinator.onTabComplete(sender, cmd, alias, args);
    }

    private void messageGeneralUsage(Player player, String[] args) {
        if (args.length == 1) {
            final String loc = args[0];
            if (!config.contains(loc)) {
                player.sendMessage("Location " + loc + " undefined"); 
                return;
            }

            final String[] coordinates = config.getString(loc).split(" ");

            teleport.toSpawn(player, coordinates, Bukkit.getServer().getWorld("world"));
            return;
        }
        player.sendMessage("Usage: /tport for tport info or /help tport for more info"); 
    }

    public void save(Player player, String[] args) {
        if (args.length == 2) {
            String locName = args[1];
            Location loc = player.getLocation();

            config.options().copyDefaults(true);
            config.addDefault(locName, "" + loc.getX() + " " + loc.getY() + " " + loc.getZ());
            saveConfig();
            player.sendMessage("Location saved!"); 
            return;
        }
        player.sendMessage("Please provide a name for your destination as follows: /tport save <name_of_destination>"); 
        return;
    }
}