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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerIcon extends JavaPlugin implements Listener{
		public Map<String, String> playerData = new HashMap<String, String>();
		public void onEnable(){
			File config = new File(getDataFolder(), "config.yml");
			File newFolder = new File(getDataFolder() + File.separator + "serverIcons");
			if(!config.exists()){
				saveDefaultConfig();
			}
			if(!newFolder.exists()){
				newFolder.mkdir();
			}
			getServer().getPluginManager().registerEvents(this, this);
		}
		public void onDisable() {
			
		}
		@EventHandler
		public void onJoin(PlayerJoinEvent e){
			if(getConfig().getString("mode").equalsIgnoreCase("player_head")){
				String playerIP = e.getPlayer().getAddress().toString();
				playerIP = playerIP.replaceAll("/", "");
				playerIP = playerIP.replaceAll("\\.", "-");
				if (!(playerData.containsKey(playerIP))) {
					playerData.put(playerIP, e.getPlayer().getName());
				}
			}
		}
		@EventHandler
		public void change(ServerListPingEvent e){
			if(getConfig().getString("mode").equalsIgnoreCase("player_head")){
				String playerIP = e.getAddress().toString();
				playerIP = playerIP.replaceAll("/", "");
				playerIP = playerIP.replaceAll("\\.", "-");
				if(playerData.containsKey(playerIP)){
					try {
						BufferedImage img = ImageIO.read(new URL("http://cravatar.eu/avatar/" + playerData.get(playerIP) + "/64.png"));
						try{
							e.setServerIcon(Bukkit.loadServerIcon(img));
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
						e.setServerIcon(Bukkit.loadServerIcon(chooseIcon()));
					}catch (Exception e1){
						Bukkit.getServer().getLogger().log(Level.SEVERE, "Something bad occured! Please report this to the " + this.getName() + " dev!");
						Bukkit.getServer().getLogger().log(Level.SEVERE, "Include this, and the error below with the report **randomIcon 1");
						e1.printStackTrace();
					}
				}
			}
			try{
				e.setServerIcon(Bukkit.loadServerIcon(chooseIcon()));
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
			    		Bukkit.getServer().getLogger().log(Level.WARNING, "One of your server icons does not end in .png! It will not be used!");
			    	}
			    }
			  }else{
			    Bukkit.getServer().getLogger().log(Level.SEVERE, "The serverIcons directory is missing! Trying to create one now..");
			    dir.mkdir();
			  }
			  Random r = new Random();
			  int fileNumber = r.nextInt(randomFiles.size());
			  return randomFiles.get(fileNumber);
		}
}
