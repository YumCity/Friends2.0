/**
 *
 * This plugin was made by VortexTM aka. HyChrod
 * All rights reserved, 2016
 *
 */
package de.HyChrod.Friends.Util;

import java.util.ArrayList;
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

public class BlockedPage {
	
	public Player player;
	public int site;
	public PlayerUtilities pu;
	
	private Friends plugin;
	private FileManager mgr;
	private FileConfiguration cfg;
	
	public BlockedPage(Friends plugin, Player player, int site, PlayerUtilities pu) {
		this.player = player;
		this.site = site;
		this.pu = pu;
		this.mgr = new FileManager();
		this.cfg = this.mgr.getConfig("", "config.yml");
		this.plugin = plugin;
	}
	
	public void open() {
		Inventory inv = Bukkit.createInventory(null, cfg.getInt("Friends.GUI.BlockedInv.InventorySize"), 
				ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.GUI.BlockedInv.Title")));
		
		for(String placeholder : cfg.getStringList("Friends.GUI.BlockedInv.PlaceholderItem.InventorySlots")) {
			inv.setItem(Integer.valueOf(placeholder)-1, ItemStacks.BLOCKED_PLACEHOLDER.getItem());
		}
		inv.setItem(ItemStacks.BLOCKED_NEXTPAGE.getInvSlot()-1, ItemStacks.BLOCKED_NEXTPAGE.getItem());
		inv.setItem(ItemStacks.BLOCKED_PREVIOUSPAGE.getInvSlot()-1, ItemStacks.BLOCKED_PREVIOUSPAGE.getItem());
		inv.setItem(ItemStacks.BLOCKED_BACK.getInvSlot()-1, ItemStacks.BLOCKED_BACK.getItem());
		
		int freeSlots = 0;
		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(i) == null || (inv.getItem(i) != null && inv.getItem(i).getType().equals(Material.AIR))) {
				freeSlots++;
			}
		}
		freeSlots = freeSlots*site;
		List<ItemStack> items = new ArrayList<>();
		for(OfflinePlayer player : pu.getBlocked()) {
			ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
			SkullMeta SM = (SkullMeta)IS.getItemMeta();
			SM.setDisplayName("�c" + player.getName());
			SM.setOwner(player.getName());
			IS.setItemMeta(SM);
			items.add(IS);
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

}
