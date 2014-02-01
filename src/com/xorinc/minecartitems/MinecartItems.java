package com.xorinc.minecartitems;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.ContainerAnvil;
import net.minecraft.server.v1_7_R1.ContainerAnvilInventory;
import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityMinecartAbstract;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutOpenWindow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftMinecart;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_7_R1.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class MinecartItems extends JavaPlugin implements Listener {

	public static final String ENDER_CHEST = ChatColor.RESET + "Minecart with Ender Chest";
	public static final String WORK_BENCH = ChatColor.RESET + "Minecart with Crafting Table";
	public static final String ENCHANTING = ChatColor.RESET + "Minecart with Enchantment Table";
	public static final String ANVIL = ChatColor.RESET + "Minecart with Anvil";
	public static final String ANVIL_1 = ChatColor.RESET + "Minecart with Slightly Damaged Anvil";
	public static final String ANVIL_2 = ChatColor.RESET + "Minecart with Very Damaged Anvil";
	
	private static final Random random = new Random();
	
	public void onEnable(){
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
	}
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEntityEvent e){
		
		if(!(e.getRightClicked() instanceof Minecart))
			return;
		
		Minecart m = (Minecart) e.getRightClicked();
		
		switch(getBlock(m)){
		
		case ENDER_CHEST:
			e.getPlayer().openInventory(e.getPlayer().getEnderChest());
			break;
		
		case WORKBENCH:
			e.getPlayer().openWorkbench(null, true);
			break;
			
		case ENCHANTMENT_TABLE:
			e.getPlayer().openEnchanting(null, true);
			break;
		
		case ANVIL:
			openAnvil(e.getPlayer(), m);
			break;
			
		default:
			return;
		
		}
		
		e.setCancelled(true);
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockDispense(BlockDispenseEvent e){
		
		if(e.getBlock().getType() != Material.DISPENSER || !isCart(e.getItem().getType()) || !e.getItem().hasItemMeta() || !e.getItem().getItemMeta().hasDisplayName())
			return;
		
		String name = e.getItem().getItemMeta().getDisplayName();
		
		if(name.equals(ENDER_CHEST) || name.equals(WORK_BENCH) || name.equals(ENCHANTING) || name.equals(ANVIL) || name.equals(ANVIL_1) || name.equals(ANVIL_2))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK || !isRail(e.getClickedBlock().getType()) || !isCart(e.getItem().getType()) || !e.getItem().hasItemMeta() || !e.getItem().getItemMeta().hasDisplayName())
			return;
		
		Location loc = e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
		String name = e.getItem().getItemMeta().getDisplayName();
		
		Minecart m = null;
		
		if(name.equals(ENDER_CHEST)){
			
			m = (Minecart) loc.getWorld().spawn(loc, EntityType.MINECART_FURNACE.getEntityClass());
			setBlock(m, Material.ENDER_CHEST, 0);
		}
		else if(name.equals(WORK_BENCH)){
			
			m = (Minecart) loc.getWorld().spawn(loc, EntityType.MINECART_FURNACE.getEntityClass());
			setBlock(m, Material.WORKBENCH, 0);
		}
		else if(name.equals(ENCHANTING)){
			
			m = (Minecart) loc.getWorld().spawn(loc, EntityType.MINECART_FURNACE.getEntityClass());
			setBlock(m, Material.ENCHANTMENT_TABLE, 0);
		}
		else if(name.equals(ANVIL)){
			
			m = (Minecart) loc.getWorld().spawn(loc, EntityType.MINECART_FURNACE.getEntityClass());
			setBlock(m, Material.ANVIL, 0);
		}
		else if(name.equals(ANVIL_1)){
			
			m = (Minecart) loc.getWorld().spawn(loc, EntityType.MINECART_FURNACE.getEntityClass());
			setBlock(m, Material.ANVIL, 1);
		}
		else if(name.equals(ANVIL_2)){
			
			m = (Minecart) loc.getWorld().spawn(loc, EntityType.MINECART_FURNACE.getEntityClass());
			setBlock(m, Material.ANVIL, 2);
		}
		else{
			return;
		}
				
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE){
			int slot = e.getPlayer().getInventory().getHeldItemSlot();
			e.getPlayer().getInventory().setItem(slot, null);
		}
		
		e.setCancelled(true);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onVehicleBreak(VehicleDestroyEvent e){
		
		if(!(e.getVehicle() instanceof Minecart))
			return;
		
		Minecart m = (Minecart) e.getVehicle();
		
		switch(getBlock(m)){
		
		case ENDER_CHEST:
			m.getWorld().dropItemNaturally(m.getLocation(), new ItemStack(Material.ENDER_CHEST));			
			break;
			
		case WORKBENCH:
			m.getWorld().dropItemNaturally(m.getLocation(), new ItemStack(Material.WORKBENCH));			
			break;
		
		case ENCHANTMENT_TABLE:
			m.getWorld().dropItemNaturally(m.getLocation(), new ItemStack(Material.ENCHANTMENT_TABLE));			
			break;
			
		case ANVIL:
			m.getWorld().dropItemNaturally(m.getLocation(), new ItemStack(Material.ANVIL, 1, (short) getData(m)));			
			break;
			
		default:
			return;
		
		}
		
		m.getWorld().dropItemNaturally(m.getLocation(), new ItemStack(Material.MINECART));
		m.remove();
		e.setCancelled(true);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {

		if (!(event.getWhoClicked() instanceof Player))
			return;

		if (!(event.getInventory() instanceof AnvilInventory))
			return;

		if (event.getSlotType() != SlotType.RESULT)
			return;
		
		final AnvilInventory inventory = (AnvilInventory) event.getInventory();
		final Player player = (Player) event.getWhoClicked();
		
		
		
		if(!( ((CraftInventory) inventory).getInventory() instanceof ContainerAnvilInventory))
			return;
		
		ContainerAnvilInventory cai = (ContainerAnvilInventory) ((CraftInventory) inventory).getInventory();
		
		ContainerAnvil anvil = getAnvil(cai);
		
		if(!(anvil instanceof FakeAnvil))
			return;
		
		player.getWorld().playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
		
		if(random.nextDouble() > 0.12D){
			player.getWorld().playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
			return;
		}
		
		Minecart m = ((FakeAnvil) anvil).m;
		
		int i = getData(m);
		
		if(i >= 2){
			
			Location l = m.getLocation();
			
			if(l == null)
				return;
			
			m.remove();
			l.getWorld().spawn(l, RideableMinecart.class);
			player.getWorld().playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 1);
			
			new BukkitRunnable() {

				@Override
				public void run() {

					player.closeInventory();					
				}				
			}.runTask(this);	
			
			return;
		}
		
		else{
			
			setBlock(m, Material.ANVIL, ++i);			
		}
		
		player.getWorld().playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
		
	}
	
	public static boolean setBlock(Minecart minecart, Material type, int data){
		
		if(!type.isBlock())
			return false;
		
		EntityMinecartAbstract m = ((CraftMinecart) minecart).getHandle();
		
		Block b = CraftMagicNumbers.getBlock(type);
		
		int block = b.getMaterial() == net.minecraft.server.v1_7_R1.Material.AIR ? 0 : Block.b(b);
				
		m.k(block);
		m.l(data);
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static Material getBlock(Minecart minecart){
		
		EntityMinecartAbstract m = ((CraftMinecart) minecart).getHandle();
		int block = m.n().getMaterial() == net.minecraft.server.v1_7_R1.Material.AIR ? 0 : Block.b(m.n());
		
		return CraftMagicNumbers.getMaterial(CraftMagicNumbers.getBlock(block));
		
	}
	
	public static int getData(Minecart minecart){
		
		EntityMinecartAbstract m = ((CraftMinecart) minecart).getHandle();
		
		return m.p();
		
	}
	
	public static boolean isRail(Material type){
		
		switch(type){
		
		case RAILS:
		case POWERED_RAIL:
		case DETECTOR_RAIL:
		case ACTIVATOR_RAIL:
			return true;
		
		default:
			return false;
		
		}
		
	}
	
	public static boolean isCart(Material type){
		
		switch(type){
		
		case MINECART:
		case STORAGE_MINECART:
		case POWERED_MINECART:
		case EXPLOSIVE_MINECART:
		case HOPPER_MINECART:
		case COMMAND_MINECART:
			return true;
		
		default:
			return false;
		
		}
		
	}
	
	private ContainerAnvil getAnvil(ContainerAnvilInventory i){
		
		try{
			
			Field f = ContainerAnvilInventory.class.getDeclaredField("a");
			f.setAccessible(true);
			return (ContainerAnvil) f.get(i);		
		}
		catch (Exception e){
			return null;
		}
		
	}

	private static void openAnvil(Player p, Minecart m){
		
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		int id = player.nextContainerCounter();
		
		player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(id, 8, "Repairing", 9, true));
		player.activeContainer = new FakeAnvil(player, m);
		player.activeContainer.windowId = id;
		player.activeContainer.addSlotListener(player);
		
	}
	
	private static class FakeAnvil extends ContainerAnvil {

		public Minecart m;
		
		public FakeAnvil(EntityHuman entity, Minecart m) {
			super(entity.inventory, entity.world, 0, 0, 0, entity);
			this.m = m;
		}
		
		@Override
		public boolean a(EntityHuman entityhuman) {
			return true;
		}
	}
}
