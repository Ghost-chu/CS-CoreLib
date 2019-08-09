package me.mrCookieSlime.CSCoreLibPlugin.protection.modules;

import me.crafter.mc.lockettepro.LocketteProAPI;
import me.mrCookieSlime.CSCoreLibPlugin.protection.ProtectionModule;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.yi.acru.bukkit.Lockette.Lockette;

public class LocketteProProtectionModule implements ProtectionModule{

	@Override
	public boolean canBuild(Player player, Block b) {
		return !isProtected(player, b);
	}

	@Override
	public boolean canAccessChest(Player player, Block b) {
		return !isProtected(player, b);
	}
	
	public boolean isProtected(Player player, Block b){
		if(LocketteProAPI.isProtected(b)) {
			// Signs need to be treated individually
			if (b.getState() instanceof Sign) {
				return !LocketteProAPI.isOwnerOfSign(b,player);
			}
			else {
				return !LocketteProAPI.isOwner(b, player);
			}
		}
		else return false;
	}

	@Override
	public String getName() {
		return "LockettePro";
	}

}
