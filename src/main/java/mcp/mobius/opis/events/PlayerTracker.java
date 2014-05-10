package mcp.mobius.opis.events;

import java.util.HashMap;
import java.util.HashSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.gui.overlay.OverlayStatus;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.swing.SelectedTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

//public class PlayerTracker implements IPlayerTracker{
public enum PlayerTracker{
	INSTANCE;
	
	private PlayerTracker(){}
	
	public HashSet<EntityPlayerMP> playersSwing = new HashSet<EntityPlayerMP>();		 //This is the list of players who have opened the UI
	//public HashSet<Player> playersOpis  = new HashSet<Player>();		 //This is the list of players who have opened the UI or used the command line
	public HashMap<String, Boolean>       filteredAmount      = new HashMap<String, Boolean>(); //Should the entity amount be filtered or not
	public HashMap<EntityPlayerMP, OverlayStatus> playerOverlayStatus = new HashMap<EntityPlayerMP, OverlayStatus>();
	public HashMap<EntityPlayerMP, Integer>       playerDimension     = new HashMap<EntityPlayerMP, Integer>();
	public HashMap<EntityPlayerMP, SelectedTab>   playerTab           = new HashMap<EntityPlayerMP, SelectedTab>();
	private HashSet<String> playerPrivileged = new HashSet<String>();
	
	public SelectedTab getPlayerSelectedTab(EntityPlayerMP player){
		return this.playerTab.get(player);
	}
	
	public AccessLevel getPlayerAccessLevel(EntityPlayerMP player){
		return this.getPlayerAccessLevel(player.getDisplayName());
	}
	
	public AccessLevel getPlayerAccessLevel(String name){
		if (MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(name) || MinecraftServer.getServer().isSinglePlayer())
			return AccessLevel.ADMIN;
		else if (playerPrivileged.contains(name))
			return AccessLevel.PRIVILEGED;
		else
			return AccessLevel.NONE;
	}	
	
	public void addPrivilegedPlayer(String name, boolean save){
		this.playerPrivileged.add(name);
		if (save){
			modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, modOpis.commentPrivileged).set(playerPrivileged.toArray(new String[]{}));
			modOpis.instance.config.save();
		}
	}
	
	public void addPrivilegedPlayer(String name){
		this.addPrivilegedPlayer(name, true);
	}
	
	public void rmPrivilegedPlayer(String name){
		this.playerPrivileged.remove(name);
		modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, modOpis.commentPrivileged).set(playerPrivileged.toArray(new String[]{}));
		modOpis.instance.config.save();		
	}
	
	public void reloeadPriviligedPlayers(){
		String[] users   = modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, modOpis.commentPrivileged).getStringList();
		for (String s : users)
			PlayerTracker.INSTANCE.addPrivilegedPlayer(s,false);		
	}
	
	public boolean isAdmin(EntityPlayerMP player){
		return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.ADMIN.ordinal();
	}
	
	public boolean isAdmin(String name){
		return this.getPlayerAccessLevel(name).ordinal() >= AccessLevel.ADMIN.ordinal();		
	}		
	
	public boolean isPrivileged(EntityPlayerMP player){
		return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
	}	
	
	public boolean isPrivileged(String name){
		return this.getPlayerAccessLevel(name).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
	}	
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
		this.playerOverlayStatus.remove(event.player);
		this.playerDimension.remove(event.player);
		//this.playersOpis.remove(player);
		this.playersSwing.remove(event.player);		
	}
	
	/*
	@Override
	public void onPlayerLogin(EntityPlayer player) {
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		this.playerOverlayStatus.remove(player);
		this.playerDimension.remove(player);
		//this.playersOpis.remove(player);
		this.playersSwing.remove(player);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}
	*/	
}
