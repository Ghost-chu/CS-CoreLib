package me.mrCookieSlime.CSCoreLibPlugin.protection.modules;

import cat.nyaa.HamsterEcoHelper.HamsterEcoHelper;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import me.mrCookieSlime.CSCoreLibPlugin.protection.ProtectionModule;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class HamsterEcoHelperProtectionModule implements ProtectionModule {
	
	@Override
	public boolean canBuild(Player player, Block b) {
		return !isProtected(player, b);
	}

	@Override
	public boolean canAccessChest(Player player, Block b) {
		return !isProtected(player, b);
	}
	
	private boolean isProtected(Player player, Block b) {
		BlockBreakEvent blockBreakEvent = new BlockBreakEvent(b,player);
		HamsterEcoHelper heh = (HamsterEcoHelper) Bukkit.getPluginManager().getPlugin("HamsterEcoHelper");
		if(heh == null)
			return false;
		heh.signShopListener.onBreakBlock(blockBreakEvent);
		return blockBreakEvent.isCancelled();
	}

	@Override
	public String getName() {
		return "HamsterEcoHelper";
	}

}
