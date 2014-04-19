package com.mydeblob;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mydeblob.Updater.ReleaseType;

public class ServerIcon extends JavaPlugin implements Listener{
		public Map<String, String> playerData = new HashMap<String, String>();
		public static boolean update = false;
		public static String name = "";
		public static ReleaseType type = null;
		public static String version = "";
		public static String link = "";
		public void onEnable(){
			File config = new File(getDataFolder(), "config.yml");
			File newFolder = new File(getDataFolder() + File.separator + "serverIcons");
			if(!config.exists()){
				saveDefaultConfig();
			}
			if(!newFolder.exists()){
				newFolder.mkdir();
			}
			getCommand("diupdate").setExecutor(this);
			getServer().getPluginManager().registerEvents(this, this);
			if(getConfig().getBoolean("auto-update")){
				Updater updater = new Updater(this, 66080, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false); // Start Updater but just do a version check
				update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE; // Determine if there is an update ready for us
				name = updater.getLatestName(); // Get the latest name
				version = updater.getLatestGameVersion(); // Get the latest game version
				type = updater.getLatestType(); // Get the latest file's type
				link = updater.getLatestFileLink(); // Get the latest link
			}
		}
		public void onDisable() {
			
		}
		public boolean onCommand(CommandSender sender, Command cmd,String commandLabel, String[] args) {
			if (cmd.getName().equalsIgnoreCase("diupdate")) {
				if(sender.hasPermission("dynamicicon.update")){
					if(getConfig().getBoolean("auto-update")){
						@SuppressWarnings("unused")
						Updater updater = new Updater(this, 66080, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true); // Go straight to downloading, and announce progress to console.
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "Please enable auto updating in the GuardOverseer config.yml to use this feature");
						return true;
					}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
					return true;
				}
			}
			return false;
		}
		@EventHandler
		public void onJoin(PlayerJoinEvent e){
			Player p = (Player) e.getPlayer();
			if(getConfig().getString("mode").equalsIgnoreCase("player_head")){
				String playerIP = e.getPlayer().getAddress().toString();
				playerIP = playerIP.replaceAll("/", "");
				playerIP = playerIP.replaceAll("\\.", "-");
				String[] split = playerIP.split(":");
				if (!(playerData.containsKey(split[0]))) {
					playerData.put(split[0], e.getPlayer().getName());
				}
			}
			if(p.hasPermission("guardoverseer.update") && update && getConfig().getBoolean("auto-update")){
			    p.sendMessage(ChatColor.BLUE + "An update is available: " + name + ", a " + type + " for " + version + " available at " + link);
			    // Will look like - An update is available: AntiCheat v1.5.9, a release for CB 1.6.2-R0.1 available at http://media.curseforge.com/XYZ
			    p.sendMessage(ChatColor.BLUE + "Type /diupdate if you would like to automatically update.");
			  }
		}
		@EventHandler
		public void change(ServerListPingEvent e){
			if(getConfig().getString("mode").equalsIgnoreCase("player_head")){
				Bukkit.getServer().getLogger().info("player_head is enabled");
				String playerIP = e.getAddress().toString();
				playerIP = playerIP.replaceAll("/", "");
				playerIP = playerIP.replaceAll("\\.", "-");
				Bukkit.getServer().getLogger().info("player ip: " + playerIP);
				if(playerData.containsKey(playerIP)){
					Bukkit.getServer().getLogger().info("playerData contains ip");
					try {
						BufferedImage img = ImageIO.read(new URL("http://cravatar.eu/avatar/" + playerData.get(playerIP) + "/64.png"));
						try{
							e.setServerIcon(Bukkit.loadServerIcon(img));
							Bukkit.getServer().getLogger().info("setting the icon");
						}catch (Exception e1){
							Bukkit.getServer().getLogger().log(Level.SEVERE, "Something bad occured! Please report this to the " + this.getName() + " dev!");
							Bukkit.getServer().getLogger().log(Level.SEVERE, "Include this, and the error below with the report **Setting server icon with cravatar");
							e1.printStackTrace();
						}
					}catch(IOException e1){
						Bukkit.getServer().getLogger().log(Level.SEVERE, "Something bad occured! Please report this to the " + this.getName() + " dev!");
						Bukkit.getServer().getLogger().log(Level.SEVERE, "Include this, and the error below with the report **Invalid URL with cravatar");
						e1.printStackTrace();
					}
				}else{
					try{
						if(chooseIcon() != null) e.setServerIcon(Bukkit.loadServerIcon(chooseIcon()));
					}catch (Exception e1){
						Bukkit.getServer().getLogger().log(Level.SEVERE, "Something bad occured! Please report this to the " + this.getName() + " dev!");
						Bukkit.getServer().getLogger().log(Level.SEVERE, "Include this, and the error below with the report **randomIcon 1");
						e1.printStackTrace();
					}
				}
			}
			try{
				if(chooseIcon() != null) e.setServerIcon(Bukkit.loadServerIcon(chooseIcon()));
			}catch (Exception e1){
				Bukkit.getServer().getLogger().log(Level.SEVERE, "Something bad occured! Please report this to the " + this.getName() + " dev!");
				Bukkit.getServer().getLogger().log(Level.SEVERE, "Include this, and the error below with the report **randomIcon 2");
				e1.printStackTrace();
			}
		}
		
		public File chooseIcon(){
			 File dir = new File(getDataFolder() + File.separator + "serverIcons");
			  File[] directoryListing = dir.listFiles();
			  ArrayList<File> randomFiles = new ArrayList<File>();
			  if (directoryListing != null) {
			    for (File f : directoryListing) {
			    	if(f.getName().contains(".png")){
			    		try {
							BufferedImage i = ImageIO.read(new File(dir + File.separator + f.getName()));
							int width = i.getWidth();
							int height = i.getHeight();
							if(width == 64 && height == 64){
								randomFiles.add(f);
							}else{
								if(!f.isHidden()){
									Bukkit.getServer().getLogger().log(Level.WARNING, "One of your server icons is not 64x64! It will not be used!");
								}
							}
						}catch(IOException e){
							Bukkit.getServer().getLogger().log(Level.SEVERE, "Something bad occured! Please report this to the " + this.getName() + " dev!");
							Bukkit.getServer().getLogger().log(Level.SEVERE, "Include this, and the error below with the report **Checking info");
							e.printStackTrace();
						}
			    	}else{
			    		if(!f.isHidden()){
			    			Bukkit.getServer().getLogger().log(Level.WARNING, "One of your server icons does not end in .png! It will not be used!");
			    		}
			    	}
			    }
			  }else{
			    Bukkit.getServer().getLogger().log(Level.SEVERE, "The serverIcons directory is missing! Trying to create one now..");
			    dir.mkdir();
			  }
			  if(randomFiles.isEmpty()) return null;
			  Random r = new Random();
			  int fileNumber = r.nextInt(randomFiles.size());
			  return randomFiles.get(fileNumber);
		}
}
