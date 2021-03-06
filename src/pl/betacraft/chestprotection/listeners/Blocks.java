package pl.betacraft.chestprotection.listeners;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import pl.betacraft.chestprotection.cp;
import pl.betacraft.chestprotection.managers.ConfigManager;
import pl.betacraft.chestprotection.managers.DatabaseManager;
import pl.betacraft.chestprotection.util.Permission;
import pl.betacraft.chestprotection.util.Permission.Perm;
/**
 * Block listener.
 */
public class Blocks extends BlockListener {

	public static HashMap<String, Location> lastClicked = new HashMap<String, Location>();

	@Override
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!(e.getBlockPlaced().getType() == Material.CHEST || e.getBlockPlaced().getType() == Material.FURNACE || e.getBlockPlaced().getType() == Material.DISPENSER || e.getBlockPlaced().getType() == Material.JUKEBOX || e.getBlockPlaced().getType() == Material.BURNING_FURNACE)) {
			if (DatabaseManager.isContainerProtected(e.getBlockPlaced().getLocation())) {
				DatabaseManager.removeContainerFromDB(e.getBlockPlaced().getLocation());
				e.getPlayer().sendMessage(ChatColor.RED + cp.lang.protectionRemoved);
			}
			return;
		}
		if (!Permission.hasPlayer(e.getPlayer(), Perm.CREATE)) {
			return;
		}
		if (!ConfigManager.autoProtection()) {
			return;
		}
		World world = cp.instance.getServer().getWorld(e.getBlockPlaced().getLocation().getWorld().getName());
		if (e.getBlockPlaced().getType() == Material.CHEST) {
			
			int x      = e.getBlockPlaced().getLocation().getBlockX();
			int xminus = e.getBlockPlaced().getLocation().getBlockX()-1;
			int x1     = e.getBlockPlaced().getLocation().getBlockX()+1;
			
			int y      = e.getBlockPlaced().getLocation().getBlockY();
			
			int z      = e.getBlockPlaced().getLocation().getBlockZ();
			int zminus = e.getBlockPlaced().getLocation().getBlockZ()-1;
			int z1     = e.getBlockPlaced().getLocation().getBlockZ()+1;
			List<String> players = new LinkedList<String>();
			if (world.getBlockAt(xminus, y, z).getType() == Material.CHEST && DatabaseManager.isContainerProtected(world.getBlockAt(xminus, y, z).getLocation())) {
				if (!DatabaseManager.doesPlayerOwnContainer(e.getPlayer(), world.getBlockAt(xminus, y, z).getLocation())) {
					e.getPlayer().sendMessage(ChatColor.RED + cp.lang.noAccess);
					e.setCancelled(true);
					return;
				}
			}
			if (world.getBlockAt(x, y, zminus).getType() == Material.CHEST && DatabaseManager.isContainerProtected(world.getBlockAt(x, y, zminus).getLocation())) {
				if (!DatabaseManager.doesPlayerOwnContainer(e.getPlayer(), world.getBlockAt(x, y, zminus).getLocation())) {
					e.getPlayer().sendMessage(ChatColor.RED + cp.lang.noAccess);
					e.setCancelled(true);
					return;
				}
			}
			if (world.getBlockAt(x1, y, z).getType() == Material.CHEST && DatabaseManager.isContainerProtected(world.getBlockAt(x1, y, z).getLocation())) {
				if (!DatabaseManager.doesPlayerOwnContainer(e.getPlayer(), world.getBlockAt(x1, y, z).getLocation())) {
					e.getPlayer().sendMessage(ChatColor.RED + cp.lang.noAccess);
					e.setCancelled(true);
					return;
				}
			}
			if (world.getBlockAt(x, y, z1).getType() == Material.CHEST && DatabaseManager.isContainerProtected(world.getBlockAt(x, y, z1).getLocation())) {
				if (!DatabaseManager.doesPlayerOwnContainer(e.getPlayer(), world.getBlockAt(x, y, z1).getLocation())) {
					e.getPlayer().sendMessage(ChatColor.RED + cp.lang.noAccess);
					e.setCancelled(true);
					return;
				}
			}
			DatabaseManager.addContainerToDB(e.getPlayer(), e.getBlockPlaced().getLocation());
			players.addAll(DatabaseManager.getPlayersOwning(e.getBlockPlaced().getLocation()));
			players.remove(e.getPlayer().getName());
			for (String p : players) {
				DatabaseManager.addPlayerToContainer(p, e.getBlockPlaced().getLocation());
			}
			e.getPlayer().sendMessage(ChatColor.YELLOW + cp.lang.protectionAdded);
			return;
		}
		DatabaseManager.addContainerToDB(e.getPlayer(), e.getBlockPlaced().getLocation());
		e.getPlayer().sendMessage(ChatColor.YELLOW + cp.lang.protectionAdded);
	}

	@Override
	public void onBlockBreak(BlockBreakEvent e) {
		if (!(e.getBlock().getType() == Material.CHEST || e.getBlock().getType() == Material.FURNACE || e.getBlock().getType() == Material.DISPENSER || e.getBlock().getType() == Material.JUKEBOX || e.getBlock().getType() == Material.BURNING_FURNACE)) {
			return;
		}
		if (DatabaseManager.isContainerProtected(e.getBlock().getLocation())) {
			if ((Permission.hasPlayer(e.getPlayer(), Perm.REMOVE_OTHER))) {
				DatabaseManager.removeContainerFromDB(e.getBlock().getLocation());
				e.getPlayer().sendMessage(ChatColor.YELLOW + cp.lang.removedProtContAdmin);
				return;
			}
			if (DatabaseManager.doesPlayerOwnContainer(e.getPlayer(), e.getBlock().getLocation()) == false) {
				e.getPlayer().sendMessage(ChatColor.RED + cp.lang.noAccess);
				e.setCancelled(true);
				return;
			}
			DatabaseManager.removeContainerFromDB(e.getBlock().getLocation());
			e.getPlayer().sendMessage(ChatColor.YELLOW + cp.lang.protectionRemoved);
		}
	}

	@Override
	public void onBlockIgnite(BlockIgniteEvent e) {
		if (!(e.getBlock().getType() == Material.CHEST || e.getBlock().getType() == Material.FURNACE || e.getBlock().getType() == Material.DISPENSER || e.getBlock().getType() == Material.JUKEBOX || e.getBlock().getType() == Material.BURNING_FURNACE)) {
			return;
		}
		if (DatabaseManager.isContainerProtected(e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}

	@Override
	public void onBlockDamage(BlockDamageEvent e) {
		if (e.getBlock().getType() == Material.CHEST || e.getBlock().getType() == Material.FURNACE || e.getBlock().getType() == Material.DISPENSER || e.getBlock().getType() == Material.JUKEBOX || e.getBlock().getType() == Material.BURNING_FURNACE) {
			if (DatabaseManager.isContainerProtected(e.getBlock().getLocation())) {
				if ((Permission.hasPlayer(e.getPlayer(), Perm.ACCESS_OTHER))) {
					if (lastClicked.get(e.getPlayer().getName()) != null) {
						lastClicked.remove(e.getPlayer().getName());
					}
					lastClicked.put(e.getPlayer().getName(), e.getBlock().getLocation());
					return;
				}
				if (DatabaseManager.doesPlayerOwnContainer(e.getPlayer(), e.getBlock().getLocation())) {
					if (lastClicked.get(e.getPlayer().getName()) != null) {
						lastClicked.remove(e.getPlayer().getName());
					}
					lastClicked.put(e.getPlayer().getName(), e.getBlock().getLocation());
				}
				else {
					e.getPlayer().sendMessage(ChatColor.RED + cp.lang.noAccess);
					e.setCancelled(true);
				}
			}
			else {
				if (lastClicked.get(e.getPlayer().getName()) != null) {
					lastClicked.remove(e.getPlayer().getName());
				}
				lastClicked.put(e.getPlayer().getName(), e.getBlock().getLocation());
			}
		}
		else {
			return;
		}
	}
}