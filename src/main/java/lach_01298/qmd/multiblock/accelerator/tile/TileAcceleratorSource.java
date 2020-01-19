package lach_01298.qmd.multiblock.accelerator.tile;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lach_01298.qmd.QMD;
import lach_01298.qmd.gui.GUI_ID;
import lach_01298.qmd.multiblock.accelerator.Accelerator;
import lach_01298.qmd.network.QMDTileUpdatePacket;
import nc.Global;
import nc.ModCheck;
import nc.block.property.BlockProperties;
import nc.config.NCConfig;
import nc.multiblock.cuboidal.CuboidalPartPositionType;
import nc.network.tile.ProcessorUpdatePacket;
import nc.network.tile.TileUpdatePacket;
import nc.tile.ITileGui;
import nc.tile.internal.inventory.InventoryConnection;
import nc.tile.internal.inventory.InventoryTileWrapper;
import nc.tile.internal.inventory.ItemOutputSetting;
import nc.tile.internal.inventory.ItemSorption;
import nc.tile.inventory.ITileFilteredInventory;
import nc.tile.inventory.ITileInventory;
import nc.util.BlockPosHelper;
import nc.util.GasHelper;
import nc.util.Lang;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileAcceleratorSource extends TileAcceleratorPart implements ITileInventory,ITileGui<TileUpdatePacket>
{
	private final @Nonnull String inventoryName = QMD.MOD_ID + ".container.accelerator_source";
	private @Nonnull InventoryConnection[] inventoryConnections = ITileInventory.inventoryConnectionAll(Arrays.asList(ItemSorption.IN, ItemSorption.OUT));
	private @Nonnull InventoryTileWrapper invWrapper;
	private final @Nonnull NonNullList<ItemStack> inventoryStacks = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	
	protected Set<EntityPlayer> playersToUpdate;
	
	public TileAcceleratorSource()
	{
		super(CuboidalPartPositionType.WALL);
		invWrapper = new InventoryTileWrapper(this);
		
		playersToUpdate = new ObjectOpenHashSet<EntityPlayer>();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onMachineAssembled(Accelerator controller)
	{
		super.onMachineAssembled(controller);
		
	}

	@Override
	public void onMachineBroken()
	{
		super.onMachineBroken();
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NonNullList<ItemStack> getInventoryStacks()
	{
		return inventoryStacks;
	}

	@Override
	public String getName()
	{
		return Lang.localise("gui."+inventoryName);
	}

	@Override
	public InventoryConnection[] getInventoryConnections()
	{
		return inventoryConnections;
	}

	@Override
	public void setInventoryConnections(InventoryConnection[] connections)
	{
		inventoryConnections = connections;
	}

	@Override
	public InventoryTileWrapper getInventory()
	{
		return invWrapper;
	}

	@Override
	public ItemOutputSetting getItemOutputSetting(int slot)
	{
		return ItemOutputSetting.DEFAULT;
	}

	@Override
	public void setItemOutputSetting(int slot, ItemOutputSetting setting)
	{
	}
	
	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
	}
	
	@Override
	public NBTTagCompound writeAll(NBTTagCompound nbt) {
		super.writeAll(nbt);
		writeInventory(nbt);
		writeInventoryConnections(nbt);

		return nbt;
	}
	
	@Override
	public void readAll(NBTTagCompound nbt) 
	{
		super.readAll(nbt);
		readInventory(nbt);
		readInventoryConnections(nbt);
		
		
	}
	

	@Override
	public NBTTagCompound writeInventory(NBTTagCompound nbt)
	{
		ItemStackHelper.saveAllItems(nbt, inventoryStacks);
		return nbt;
	}

	@Override
	public void readInventory(NBTTagCompound nbt)
	{
		ItemStackHelper.loadAllItems(nbt, inventoryStacks);
	}

	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return !getInventoryStacks().isEmpty() && hasInventorySideCapability(side);
		}

		return super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) 
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) 
		{
			if (!getInventoryStacks().isEmpty() && hasInventorySideCapability(side)) 
			{
				return (T) getItemHandlerCapability(side);
			}
			return null;
		}
		return super.getCapability(capability, side);
	}

	@Override
	public int getGuiID()
	{
		
		return GUI_ID.ACCELERATOR_SOURCE;
	}

	@Override
	public Set<EntityPlayer> getPlayersToUpdate()
	{
		
		return playersToUpdate;
	}

	@Override
	public TileUpdatePacket getGuiUpdatePacket()
	{
		return new QMDTileUpdatePacket();
	}

	@Override
	public void onGuiPacket(TileUpdatePacket message)
	{	
		
	}

	
	
	
	
	
}