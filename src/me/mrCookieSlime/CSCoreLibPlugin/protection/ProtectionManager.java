package me.mrCookieSlime.CSCoreLibPlugin.protection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.earth2me.essentials.antibuild.EssentialsAntiBuild;
import com.earth2me.essentials.antibuild.EssentialsAntiBuildListener;
import me.crafter.mc.lockettepro.LockettePro;
import me.crafter.mc.lockettepro.LocketteProAPI;
import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.protection.modules.*;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.yi.acru.bukkit.Lockette.Lockette;

public class ProtectionManager {

	private Config local = new Config("plugins/CS-CoreLib/protection.yml");
	private Set<ProtectionModule> modules = new HashSet<ProtectionModule>();
	private CoreProtectAPI coreProtectAPI;

	public void registerNewModule(String name, ProtectionModule module) {
		this.modules.add(module);
		this.loadModuleMSG(name);
		local.setDefaultValue("plugins." + name + ".no-block-access", "&4&l! &cSorry, but the Plugin &4" + name + " &cdoes not allow you to access this Block");
		local.setDefaultValue("plugins." + name + ".no-block-breaking", "&4&l! &cSorry, but the Plugin &4" + name + " &cdoes not allow you to break this Block");
		local.save();

	}

	private void loadModuleMSG(String module) {
		System.out.println("[CS-CoreLib - Protection] Loaded Protection Module \"" + module + "\"");
	}
	
	public ProtectionManager() {}

	public ProtectionManager(CSCoreLib cscorelib) {
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("WorldGuard") && cscorelib.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
			registerNewModule("WorldGuard", new WorldGuardProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("Factions")) {
			if (cscorelib.getServer().getPluginManager().getPlugin("Factions") instanceof com.massivecraft.factions.P)
				registerNewModule("Factions", new FactionsUUIDProtectionModule());
			else
				registerNewModule("Factions", new FactionsProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("Towny")) {
			registerNewModule("Towny", new TownyProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
			registerNewModule("GriefPrevention", new GriefPreventionProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("ASkyBlock")) {
			registerNewModule("ASkyBlock", new ASkyBlockProtectionModule());
		}
		if(cscorelib.getServer().getPluginManager().isPluginEnabled("LWC")){
			registerNewModule("LWC", new LWCProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("PreciousStones")) {
			registerNewModule("PreciousStones", new PreciousStonesProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("Lockette")) {
			registerNewModule("Lockette", new LocketteProtectionModule());
		}
		if(cscorelib.getServer().getPluginManager().isPluginEnabled("ProtectionStones")) {
            this.loadModuleMSG("ProtectionStones");
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("uSkyblock")) {
			this.loadModuleMSG("uSkyblock");
		}
		if(cscorelib.getServer().getPluginManager().isPluginEnabled("PlotSquared")) {
			registerNewModule("PlotSquared", new PlotSquaredProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("RedProtect")) {
			registerNewModule("RedProtect", new RedProtectProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("BentoBox")) {
			registerNewModule("BentoBox", new BentoBoxProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("HamsterEcoHelper")) {
			registerNewModule("HamsterEcoHelper", new HamsterEcoHelperProtectionModule());
		}
		if (cscorelib.getServer().getPluginManager().isPluginEnabled("CoreProtect")) {
			coreProtectAPI = ((CoreProtect)cscorelib.getServer().getPluginManager().getPlugin("CoreProtect")).getAPI();
		}
	}

	public boolean canBuild(UUID uuid, Block b) {
		return this.canBuild(uuid, b, false);
	}

	public boolean canAccessChest(UUID uuid, Block b) {
		return this.canAccessChest(uuid, b, false);
	}

	public boolean canBuild(UUID uuid, Block b, boolean message) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) return false;

		for (ProtectionModule module: modules) {
			if (!module.canBuild(player, b)) {
				if (message) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', local.getString("plugins." + module.getName() + ".no-block-breaking")));
				}
				return false;
			}
		}

		return true;
	}

	public boolean canAccessChest(UUID uuid, Block b, boolean message) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) return false;

		for (ProtectionModule module: modules) {
			if (!module.canAccessChest(player, b)) {
				if (message) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', local.getString("plugins." + module.getName() + ".no-block-access")));
				}
				return false;
			}
		}
		logInteraction(player.getName(), b.getLocation());
		logContainerTransaction(player.getName(),b.getLocation());
		return true;
	}
	public boolean logRemoveal(String user, Location location, Material type, BlockData blockData){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logRemoval(user,location,type,blockData);
	}
	public boolean logPlacement(String user, Location location, Material type, BlockData blockData){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logPlacement(user,location,type,blockData);
	}
	public boolean logInteraction(String user, Location location){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logInteraction(user,location);
	}
	public boolean logContainerTransaction(String user, Location location){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logContainerTransaction(user,location);
	}
}
