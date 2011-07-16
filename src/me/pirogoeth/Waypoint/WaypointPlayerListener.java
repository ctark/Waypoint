package me.pirogoeth.Waypoint;

import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.ChatColor;
import java.util.logging.Logger;

public class WaypointPlayerListener extends PlayerListener {
    public static Waypoint plugin;
    Logger log = Logger.getLogger("Minecraft");
    public WaypointPlayerListener (Waypoint instance) {
    	plugin = instance;
    }
    public static String UserNodeChomp (Player p, String arg, String sub)
    {
        String a = "users." + p.getName().toString() + "." + arg + "." + sub;
        return a;
    }
    public static String HomeNodeChomp (Player p, World w, String sub)
    {
        String a = "home." + p.getName().toString() + "." + w.getName().toString() + "." + sub;
        return a;
    }
    public void onPlayerBedLeave(PlayerBedLeaveEvent event)
    {
        Player player = event.getPlayer();
        if (!plugin.permissionHandler.has(player, "waypoint.home.set_on_bed_leave")) { return; };
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        World w = player.getLocation().getWorld();
        plugin.config.setProperty(HomeNodeChomp(player, w, "coord.X"), x);
        plugin.config.setProperty(HomeNodeChomp(player, w, "coord.Y"), y);
        plugin.config.setProperty(HomeNodeChomp(player, w, "coord.Z"), z);
        plugin.config.save();
        player.sendMessage(ChatColor.AQUA + "[Waypoint] " + player.getName().toString() + ", your home for world " + player.getWorld().getName().toString() + " has been set to the bed you just got out of.");
    }
}
