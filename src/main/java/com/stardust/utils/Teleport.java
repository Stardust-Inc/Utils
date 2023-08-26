package com.stardust.utils;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Random;

/**
 * Teleportation Logic
 */
public class Teleport {
    public static class Position {
        public int x;
        public int y;
        public int z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean near(Object obj, int i) {
            if (!(obj instanceof Position)) return false;

            final Position other = (Position) obj;
            if (Math.abs(other.x - this.x) <= i && Math.abs(other.y - this.y) <= i && Math.abs(other.z - this.z) <= i) return true;
            return false;
        }

        @Override
        public String toString() {
            return this.x + " " + this.y + " " + this.z;
        }
    }

    public static Location toWild(Player player) {
        Location playerLoc = player.getLocation();
        World world = Bukkit.getServer().getWorld("world");

        Position playerPos = new Position((int) playerLoc.getX(), (int) playerLoc.getY(), (int) playerLoc.getZ());
        System.out.println(player.getName() + " selected world portal at position " + playerPos.toString());

        // & from.getEnvironment() == Environment.THE_END
        System.out.println(player.getName() + " Teleported to wilderness");

        int x = new Random().nextInt(5001) + 3;
        int z = new Random().nextInt(7001) + 3;
        int y = world.getHighestBlockYAt(x, z) + 1;
        Location randomLoc = new Location(world, x, y, z);

        // ensure location is not in water
        System.out.println(player.getName() + " teleported to a block is liquid: " + randomLoc.getBlock().isLiquid());
        while (randomLoc.getBlock().isLiquid()) {
            x = new Random().nextInt(5001) + 3;
            z = new Random().nextInt(7001) + 3;
            y = world.getHighestBlockYAt(x, z) + 1;
            randomLoc = new Location(world, x, y, z);
        }   

        // ensure teleport to highest non-air block
        while (randomLoc.getBlock().getType() == Material.AIR) {
            y -= 1;
            randomLoc = new Location(world, x, y, z);
        }
        randomLoc.add(0, 1, 0);
        player.setVelocity(new Vector(0.0, 0.0, 0.0));
        player.setFallDistance(0.0F);
        player.teleport(randomLoc);
        return randomLoc;
    }

    public static Location toSpawn(Player player, String[] spawn, World world) {
        final int x = Math.round(Float.parseFloat(spawn[0])); 
        final int y = Math.round(Float.parseFloat(spawn[1])); 
        final int z = Math.round(Float.parseFloat(spawn[2]));

        final Location spawnLoc = new Location(world, x, y, x);
        player.teleport(spawnLoc);
        return spawnLoc;
    }

    public boolean onCommandGoLoc(Player player, String[] args) {
        return true;
    }
}