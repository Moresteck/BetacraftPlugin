package pl.betacraft.moressentials;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betacraft.moresteck.Betacraft;

public class Moressentials {
	//public static Map<String, CraftInventoryPlayer> inventories = new HashMap<String, CraftInventoryPlayer>();

	public static void onEnable(JavaPlugin instance) {
		Bukkit.getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, new PL(), Priority.Normal, instance);
		Bukkit.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, new PL(), Priority.Normal, instance);
		Bukkit.getServer().getPluginManager().registerEvent(Type.PLAYER_TELEPORT, new PL(), Priority.Normal, instance);
		Bukkit.getServer().getPluginManager().registerEvent(Type.PLAYER_COMMAND_PREPROCESS, new PL(), Priority.Normal, instance);
		Bukkit.getServer().getPluginManager().registerEvent(Type.ENTITY_DAMAGE, new EL(), Priority.Normal, instance);
		Bukkit.getServer().getLogger().info(" [BetaCraft] Mssentials: wlaczone");
	}

	public static void onDisable() {
/*		for (Player p: Bukkit.getServer().getOnlinePlayers()) {
			if (Moressentials.inventories.get(p.getName()) != null) {
				PlayerInventory pi = Moressentials.inventories.get(p.getName());
				p.getInventory().clear();
				p.getInventory().setContents(pi.getContents());
				p.getInventory().setArmorContents(pi.getArmorContents());
				Moressentials.inventories.remove(p.getName());
			}
		}*/
	}

	// betacraft.mssentials.nick.self
	// betacraft.mssentials.nick.others
	// betacraft.mssentials.commandspy
	// betacraft.mssentials.invsee
	// betacraft.mssentials.ci

	public static boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("seen")) {
			if (args.length == 0) {
				sender.sendMessage("/seen <nick>");
				return true;
			}
			String player = args[0];
			sender.sendMessage(ChatColor.GRAY + "Gracz " + player + " byl ostatnio online " + PDB.seen(player));
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("cinv")) {
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					return true;
				}
				else {
					Player target = Bukkit.getServer().getPlayer(args[0]);
					PDB.ci(target);
					sender.sendMessage(ChatColor.GRAY + "Wyczysciles ekwipunek gracza " + args[0] + ".");
					target.sendMessage(ChatColor.GRAY + "Ekwipunek wyczyszczony przez <CONSOLE>.");
				}
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("nick")) {
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					sender.sendMessage("/nick <gracz> <nick>");
					return true;
				}
				if (args.length == 1) {
					sender.sendMessage("/nick <gracz> <nick>");
					return true;
				}
				String name = args[0];
				String display = args[1];
				PDB.nick(name, display);
				Player tar = Bukkit.getServer().getPlayer(name);
				if (tar != null) {
					tar.setDisplayName(display);
					tar.sendMessage(ChatColor.GRAY + "Ustawiono tw�j nick na: " + display + ".");
				}
				sender.sendMessage(ChatColor.GRAY + "Ustawiono nick gracza " + name + " na: " + display + ".");
				return true;
			}
		}
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("back")) {
			Location loc = PDB.back(p);
			p.teleport(loc);
			p.sendMessage(ChatColor.GRAY + "Wr�ciles do poprzedniego miejsca");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("ignore")) {
			if (args.length == 0) {
				p.sendMessage("/ignore <nick>");
				p.sendMessage("/ignorelist");
				return true;
			}
			String toIgnore = args[0];
			List<String> ignorednow = PDB.ignored(p.getName());
			if (ignorednow.contains(toIgnore)) {
				PDB.ignore(p, toIgnore, false);
				p.sendMessage(ChatColor.GRAY + "Od teraz widzisz publiczne wiadomosci gracza " + toIgnore);
			}
			else if (!ignorednow.contains(toIgnore)) {
				PDB.ignore(p, toIgnore, true);
				p.sendMessage(ChatColor.GRAY + "Od teraz nie widzisz publicznych wiadomosci gracza " + toIgnore);
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("ignorelist")) {
			List<String> ignored = PDB.ignored(p.getName());
			p.sendMessage(ChatColor.GRAY + "Nie widzisz wiadomosci od graczy:");
			for (String s: ignored) {
				p.sendMessage(" - " + ChatColor.GRAY + s);
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("cinv")) {
			if (args.length == 0) {
				PDB.ci(p);
				p.sendMessage(ChatColor.GRAY + "Wyczyszczono ekwipunek.");
				return true;
			}
			else {
				if (!Betacraft.permissions.getHandler().has(p, "betacraft.mssentials.ci")) {
					return true;
				}
				Player target = Bukkit.getServer().getPlayer(args[0]);
				PDB.ci(target);
				p.sendMessage(ChatColor.GRAY + "Wyczysciles ekwipunek gracza " + args[0] + ".");
				target.sendMessage(ChatColor.GRAY + "Ekwipunek wyczyszczony przez " + p.getName() + ".");
			}
		}
		if (cmd.getName().equalsIgnoreCase("invsee")) {
			if (!Betacraft.permissions.getHandler().has(p, "betacraft.mssentials.invsee")) {
				return true;
			}
			if (args.length == 0) {
				if (PDB.readInventory(p)) {
					p.sendMessage(ChatColor.GRAY + "Przywr�cono tw�j ekwipunek.");
				}
				else {
					p.sendMessage("/invsee <gracz>");
				}
/*				if (Moressentials.inventories.get(p.getName()) != null) {
					final CraftInventoryPlayer pi = Moressentials.inventories.get(p.getName());
					p.getInventory().clear();
					p.getInventory().setContents(pi.getContents());
					p.getInventory().setArmorContents(pi.getArmorContents());
					Moressentials.inventories.remove(p.getName());
					p.sendMessage(ChatColor.GRAY + "Przywr�cono tw�j ekwipunek.");
				}
				else {
					p.sendMessage("/invsee <gracz>");
				}*/
				return true;
			}
			Player target = Bukkit.getServer().getPlayer(args[0]);
			if (target == null) {
				return true;
			}
			//final CraftInventoryPlayer c = (CraftInventoryPlayer) p.getInventory();
			//inventories.put(p.getName(), c);
			PDB.writeInventory(p);
			p.getInventory().setContents(target.getInventory().getContents());
			p.getInventory().setArmorContents(target.getInventory().getArmorContents());
			p.sendMessage(ChatColor.GRAY + "Widzisz ekwipunek gracza " + args[0] + ".");
		}
		if (cmd.getName().equalsIgnoreCase("commandspy")) {
			if (!Betacraft.permissions.getHandler().has(p, "betacraft.mssentials.commandspy")) {
				return true;
			}
			if (PDB.commandspy(p.getName())) {
				PDB.commandspy(p.getName(), false);
				p.sendMessage(ChatColor.GRAY + "Od teraz nie bedziesz juz widziec komend graczy.");
				return true;
			}
			PDB.commandspy(p.getName(), true);
			p.sendMessage(ChatColor.GRAY + "Od teraz bedziesz widziec wszystkie komendy graczy.");
		}
		if (cmd.getName().equalsIgnoreCase("nick")) {
			if (args.length == 0) {
				p.sendMessage("/nick <nick>");
				p.sendMessage("/nick <gracz> <nick>");
				return true;
			}
			String name = args[0];
			String display = "";
			if (args.length == 1) {
				if (!Betacraft.permissions.getHandler().has(p, "betacraft.mssentials.nick.self")) {
					return true;
				}
				name = p.getName();
				display = args[0];
				PDB.nick(p.getName(), display);
				p.setDisplayName(display);
				p.sendMessage(ChatColor.GRAY + "Ustawiono tw�j nick na: " + display + ".");
				return true;
			}
			if (!Betacraft.permissions.getHandler().has(p, "betacraft.mssentials.nick.others")) {
				return true;
			}
			display = args[1];
			PDB.nick(name, display);
			Player tar = Bukkit.getServer().getPlayer(name);
			if (tar != null) {
				tar.setDisplayName(display);
				tar.sendMessage(ChatColor.GRAY + "Ustawiono tw�j nick na: " + display + ".");
			}
			p.sendMessage(ChatColor.GRAY + "Ustawiono nick gracza " + name + " na: " + display + ".");
		}
		return true;
	}
}