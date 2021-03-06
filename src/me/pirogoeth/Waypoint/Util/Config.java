package me.pirogoeth.Waypoint.Util;

// bukkit imports
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.World;
import org.bukkit.World.Environment;
// java imports
import java.io.File;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.FileInputStream;
// internals imports
import me.pirogoeth.Waypoint.Waypoint;

public class Config {
    // main vars
    public static Waypoint plugin;
    public static Logger log = Logger.getLogger("Minecraft");
    public static String maindir = "plugins/Waypoint";
    // quizzical vars
    public static String extension = ".yml";
    public static boolean loaded = false;
    // Configuration variables
    public static Configuration main;
    public static Configuration warps;
    public static Configuration users;
    public static Configuration spawn;
    public static Configuration home;
    public static Configuration world;
    public static Configuration links;
    public static Configuration strings;
    // File variables
    public static File mainf;
    public static File spawnf;
    public static File usersf;
    public static File homef;
    public static File warpsf;
    public static File worldf;
    public static File linksf;
    public static File stringf;
    // constructor
    public Config (Waypoint instance) {
        plugin = instance;
        new File(maindir).mkdir();
        new File(maindir + "/data").mkdir();
        if (new File(maindir + File.separator + ".usetxt").exists() == true) {
            log.info("[Waypoint] Found .usetxt, changing extensions to .txt");
            extension = ".txt";
        }
        // do our configuration initialisation here.
        initialise();
    }

    // File() instantiator
    private static File getFile(String fname)
    {
        File f = new File(maindir + File.separator + (String)fname + extension);
        return f;
    }

    // support method to move old data. 1.6 -> 1.6.1
    // I'll use IOStreams.
    public static boolean moveOldData () {
        // old files
        File old_warps = getFile("warps");
        File old_users = getFile("users");
        File old_home = getFile("home");
        File old_links = getFile("links");
        // file io stream and transfer variables
        FileInputStream fis;
        FileOutputStream fos;
        byte[] buf;
        int len;
        // this is a test. we'll recycle the io streams each time. this is gonna be a long method :/
        try {
            // warps
            fis = new FileInputStream(old_warps);
            fos = new FileOutputStream(warpsf);
            buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close(); fos.close();
            old_warps.delete();
            log.info("[Waypoint] Copied warps.");
            // users
            fis = new FileInputStream(old_users);
            fos = new FileOutputStream(usersf);
            buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close(); fos.close();
            old_users.delete();
            log.info("[Waypoint] Copied users.");
            // home
            fis = new FileInputStream(old_home);
            fos = new FileOutputStream(homef);
            buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close(); fos.close();
            old_home.delete();
            log.info("[Waypoint] Copied homes.");
            // links
            fis = new FileInputStream(old_links);
            fos = new FileOutputStream(linksf);
            buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close(); fos.close();
            old_links.delete();
            log.info("[Waypoint] Copied links.");
        } catch (Exception e) {
            log.info("[Waypoint] Failed moving old configuration data.");
            return false;
        }
        log.info("[Waypoint] Successfully moved old configuration data.");
        load();
        return true;
    }

    // initialise the Configuration setup
    public static boolean initialise () {
        log.info("[Waypoint] Initialising configurations.");
        // load all of the config files
        try {
            mainf = getFile("config");
            warpsf = getFile("data/warps");
            usersf = getFile("data/users");
            spawnf = getFile("spawn");
            homef = getFile("data/home");
            worldf = getFile("world");
            linksf = getFile("data/links");
            // stringf = getFile("strings.yml");
        }
        catch (Exception e) {
            return false;
        }
        // instantiate the Configuration objects
        try {
            main = new Configuration(mainf);
            warps = new Configuration(warpsf);
            users = new Configuration(usersf);
            spawn = new Configuration(spawnf);
            home = new Configuration(homef);
            world = new Configuration(worldf);
            links = new Configuration(linksf);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void load() {
        // load all of the configurations
        log.info("[Waypoint] Reading configurations.");
        main.load();
        warps.load();
        users.load();
        spawn.load();
        home.load();
        world.load();
        links.load();
        // world variables
        List<World> world_l;
        Iterator world_l_i;
        World world_o;
        String world_n;
        int world_m;
        boolean world_pvp;
        Environment world_e;
        // check if we need to write the defaults.
        if ((String)main.getString("version") == null) {
            log.info("[Waypoint] Writing default config values.");
            // main
            main.setProperty("version", "1.6.4");
            main.setProperty("autoupdate", "false");
            // cooldown timer
            main.setProperty("cooldown.duration", 0);
            // economy
            main.setProperty("economy", "false");
            // home settings
            main.setProperty("home.set_home_at_bed", "false");
            // warp permission groups
            List<String> warpgroups = new ArrayList<String>();
            warpgroups.add("Default");
            warpgroups.add("Mod");
            warpgroups.add("Admin");
            main.setProperty("warp.permissions", warpgroups);
            // write the spawn points to config file
            plugin.spawnManager.ConfigWriteSpawnLocations();
            // warp settings
            main.setProperty("warp.traverse_world_only", "false");
            main.setProperty("warp.list_world_only", "false");
            main.setProperty("warp.warpstring_enabled", "true");
            main.setProperty("warp.string", "welcome to %w, %p");
            // limits
            main.setProperty("limits.warp.enabled", true);
            main.setProperty("limits.warp.threshold", 10);
            main.setProperty("limits.waypoint.enabled", true);
            main.setProperty("limits.waypoint.threshold", 10);
            // placeholder
            links.setProperty("links", "");
            // world base and currently loaded world settings
            world_l = plugin.getServer().getWorlds();
            world_l_i = world_l.iterator();
            while (world_l_i.hasNext()) {
                world_o = (World) world_l_i.next();
                world_n = world_o.getName().toString();
                world_e = world_o.getEnvironment();
                world_m = 0;
                world_pvp = world_o.getPVP();
                log.info(String.format("[Waypoint] Processing world: { %s : [ENV:%s(MODE:%s)] } -- PVP: %s", world_n, (String) Integer.toString(world_e.getId()), world_m, world_pvp));
                world.setProperty("world." + world_n, "");
                world.setProperty("world." + world_n + ".mode", world_m);
                world.setProperty("world." + world_n + ".pvp", world_pvp);
                switch (world_e) {
                    case NORMAL:
                        world.setProperty("world." + world_n + ".env", "NORMAL");
                        break;
                    case NETHER:
                        world.setProperty("world." + world_n + ".env", "NETHER");
                        break;
                    case THE_END:
                        world.setProperty("world." + world_n + ".env", "THE_END");
                        break;
                    default:
                        // default shall be NORMAL
                        world.setProperty("world." + world_n + ".env", "NORMAL");
                        break;
                }
            }
            world.save();
            // TODO: actually implement case insensitive warps
            log.info("[Waypoint] Wrote defaults.");
            links.save();
            main.save();
        }
        else if (!((String)main.getString("version")).equals("1.6.4")) {
            // write values not entered in the 1.6.1 update, but were added during
            // the 1.6.2 alpha testing.
            log.info("[Waypoint] Finalising 1.6.4 configuration.");
            // set version
            main.setProperty("version", "1.6.4");
            // cooldown timers
            main.setProperty("cooldown.duration", 0);
            // economy
            main.setProperty("economy", false);
            // add config option added after 1.5-dev
            main.setProperty("warp.warpstring_enabled", true);
            main.setProperty("warp.string", "welcome to %w, %p");
            // limits
            main.setProperty("limits.warp.enabled", false);
            main.setProperty("limits.warp.threshold", 10);
            main.setProperty("limits.waypoint.enabled", false);
            main.setProperty("limits.waypoint.threshold", 10);
            main.save();
            moveOldData();
        }
        log.info("[Waypoint] Configuration successfully loaded.");
        loaded = true;
        return;
    }

    public static void save () {
        // use the save() method of all of the Configuration instances.
        main.save();
        users.save();
        warps.save();
        spawn.save();
        home.save();
        links.save();
        log.info("[Waypoint] Saved all configurations.");
    }

    // depreciated
    public static Configuration getMain () {
        // returns the Configuration object for the main config file
        return (Configuration) main;
    }

    // depreciated
    public static Configuration getWarp () {
        // returns the Configuration object for the warps config file
        return (Configuration) warps;
    }

    // depreciated
    public static Configuration getUsers () {
        // returns the Configuration object for the users config file
        return (Configuration) users;
    }

    // depreciated
    public static Configuration getSpawn () {
        // returns the Configuration object for the spawn config file
        return (Configuration) spawn;
    }

    // depreciated
    public static Configuration getHome () {
        // returns the Configuration object for the home config file
        return (Configuration) home;
    }

    // depreciated
    public static Configuration getWorld() {
        // returns the Configuration object for the world config file
        return (Configuration) world;
    }

    // depreciated
    public static Configuration getLinks() {
        // returns the Configuration object for the links config file
        return (Configuration) links;
    }
}