/**
 *
 * This plugin was made by VortexTM aka. HyChrod
 * All rights reserved, 2016
 *
 */
package de.HyChrod.Friends.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;

public class InventoryPage {
	
	public Player player;
	public int site;
	public PlayerUtilities pu;
	
	private Friends plugin;
	private FileManager mgr;
	private FileConfiguration cfg;
	
	public InventoryPage(Friends plugin, Player player, int site, PlayerUtilities pu) {
		this.player = player;
		this.site = site;
		this.pu = pu;
		this.mgr = new FileManager();
		this.cfg = this.mgr.getConfig("", "config.yml");
		this.plugin = plugin;
	}
	
	public void open() {
		Inventory inv = Bukkit.createInventory(null, cfg.getInt("Friends.GUI.InventorySize"), 
				ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.GUI.Title")));
		
		for(String placeholder : cfg.getStringList("Friends.GUI.PlaceholderItem.InventorySlots")) {
			inv.setItem(Integer.valueOf(placeholder)-1, ItemStacks.MAIN_PLACEHOLDER.getItem());
		}
		inv.setItem(cfg.getInt("Friends.GUI.RequestsItem.InventorySlot")-1, ItemStacks.MAIN_REQUESTS(pu.getRequests().size()));
		inv.setItem(cfg.getInt("Friends.GUI.BlockedItem.InventorySlot")-1, ItemStacks.MAIN_BLOCKED(pu.getBlocked().size()));
		inv.setItem(ItemStacks.MAIN_OPTIONSITEM.getInvSlot()-1, ItemStacks.MAIN_OPTIONSITEM.getItem());
		inv.setItem(ItemStacks.MAIN_NEXTPAGEITEM.getInvSlot()-1, ItemStacks.MAIN_NEXTPAGEITEM.getItem());
		inv.setItem(ItemStacks.MAIN_PREVIOUSPAGEITEM.getInvSlot()-1, ItemStacks.MAIN_PREVIOUSPAGEITEM.getItem());
		
		int freeSlots = 0;
		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(i) == null || (inv.getItem(i) != null && inv.getItem(i).getType().equals(Material.AIR))) {
				freeSlots++;
			}
		}
		freeSlots = freeSlots*site;
		List<ItemStack> items = new ArrayList<>();
		for(OfflinePlayer player : pu.getFriends()) {
			PlayerUtilities puT = new PlayerUtilities(player);
			items.add(this.getHead(player, puT));
		}
		
		if(items.size() > freeSlots) {
			for(int i = 0; i < freeSlots; i++) {
				items.remove(0);
			}
		} else {
			items.clear();
		}
		for(ItemStack item : items) {
			inv.addItem(item);
		}
		player.openInventory(inv);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				player.updateInventory();
			}
		}, 5);
	}
	
	private ItemStack getHead(OfflinePlayer player, PlayerUtilities pu) {
		ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta SM = (SkullMeta)IS.getItemMeta();
		SM.setDisplayName(player.getName() + " §7(§8Offline§7)");;
		SM.setOwner(player.getName());
		if(Friends.bungeeMode) {
			if(BungeeSQL_Manager.isOnline(player)) {
				SM.setDisplayName(player.getName() + " §7(§aOnline§7)");
				if(this.cfg.getBoolean("Friends.ShowServer.Enable")) {
					SM.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Friends.ShowServer.Lore").replace("%SERVER%",
							BungeeSQL_Manager.getServer(player)))));
				}
			} else {
				int[] time = PlayerUtilities.getLastOnline(BungeeSQL_Manager.getLastOnline(player));
				if(cfg.getBoolean("Friends.Options.LastOnline.Enable") && time != null && time.length >= 3) {
					SM.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.Options.LastOnline.Format")
							.replace("%days%", ""+time[3])
							.replace("%hours%", time[2]+"")
							.replace("%minutes%", ""+time[1])
							.replace("%seconds%", ""+time[0]))));
				}
			}
			IS.setItemMeta(SM);
			return IS;
		}
		int[] time = PlayerUtilities.getLastOnline(pu.getLastOnline());
		if(!player.isOnline()) {
			if(cfg.getBoolean("Friends.Options.LastOnline.Enable") && time != null && time.length >= 3) {
				SM.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.Options.LastOnline.Format")
						.replace("%days%", ""+time[3])
						.replace("%hours%", time[2]+"")
						.replace("%minutes%", ""+time[1])
						.replace("%seconds%", ""+time[0]))));
			}
		} else SM.setDisplayName(player.getName() + " §7(§aOnline§7)");
		IS.setItemMeta(SM);
		return IS;
	}

}
