package lach_01298.qmd.multiblock.particleChamber.tile;

import static nc.block.property.BlockProperties.AXIS_ALL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import lach_01298.qmd.QMD;
import lach_01298.qmd.io.IIOType;
import lach_01298.qmd.multiblock.particleChamber.ParticleChamber;
import lach_01298.qmd.particle.ITileParticleStorage;
import nc.Global;
import nc.ModCheck;
import nc.config.NCConfig;
import nc.multiblock.cuboidal.CuboidalPartPositionType;
import nc.multiblock.fission.FissionReactor;
import nc.tile.internal.inventory.InventoryConnection;
import nc.tile.internal.inventory.InventoryTileWrapper;
import nc.tile.internal.inventory.ItemOutputSetting;
import nc.tile.internal.inventory.ItemSorption;
import nc.tile.inventory.ITileInventory;
import nc.util.GasHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileParticleChamberPort extends TileParticleChamberPart implements ITileInventory
{
	
	private final @Nonnull NonNullList<ItemStack> inventoryStacks = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	private @Nonnull InventoryTileWrapper invWrapper;
	private IParticleChamberController controller;
	
	private final @Nonnull String inventoryName = QMD.MOD_ID + ".container.particle_chamber_port";
	private @Nonnull InventoryConnection[] inventoryConnections = ITileInventory.inventoryConnectionAll(Lists.newArrayList(ItemSorption.IN, ItemSorption.OUT));
	
	public TileParticleChamberPort()
	{
		super(CuboidalPartPositionType.WALL);
		invWrapper = new InventoryTileWrapper(this);
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onMachineAssembled(ParticleChamber chamber) 
	{
		controller =  chamber.controller;
		super.onMachineAssembled(chamber);
	}
	
	
	@Override
	public void onMachineBroken()
	{
		controller = null;
	}

	@Override
	public NonNullList<ItemStack> getInventoryStacks()
	{
		return controller == null ? inventoryStacks : controller.getInventoryStacks();
	}

	@Override
	public String getName()
	{
		return inventoryName;
	}

	@Override
	public @Nonnull InventoryConnection[] getInventoryConnections()
	{
		return inventoryConnections;
	}

	@Override
	public void setInventoryConnections(@Nonnull InventoryConnection[] connections)
	{
		inventoryConnections = connections;
	}

	@Override
	public @Nonnull  InventoryTileWrapper getInventory()
	{
		return controller == null ? invWrapper : controller.getInventory();
		
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
	
	
	// NBT
	
	@Override
	public NBTTagCompound writeAll(NBTTagCompound nbt) 
	{
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
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return !getInventoryStacks().isEmpty() && hasInventorySideCapability(side) && controller != null;
		}
		return super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (!getInventoryStacks().isEmpty() && hasInventorySideCapability(side) && controller != null)
			{
				return (T) getItemHandlerCapability(side);
			}
			return null;

		}
		return super.getCapability(capability, side);
	}

}
