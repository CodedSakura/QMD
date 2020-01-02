package lach_01298.qmd.multiblock.accelerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lach_01298.qmd.QMD;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.multiblock.accelerator.Accelerator;
import lach_01298.qmd.multiblock.accelerator.AcceleratorLogic;
import lach_01298.qmd.multiblock.accelerator.DipoleMagnet;
import lach_01298.qmd.multiblock.accelerator.QuadrupoleMagnet;
import lach_01298.qmd.multiblock.accelerator.RFCavity;
import lach_01298.qmd.multiblock.accelerator.tile.IAcceleratorComponent;
import lach_01298.qmd.multiblock.accelerator.tile.IAcceleratorController;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorBeam;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorBeamPort;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorCooler;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorEnergyPort;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorInlet;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorMagnet;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorOutlet;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorRFCavity;
import lach_01298.qmd.multiblock.accelerator.tile.TileAcceleratorSource;
import lach_01298.qmd.multiblock.container.ContainerLinearAcceleratorController;
import lach_01298.qmd.multiblock.network.AcceleratorUpdatePacket;
import lach_01298.qmd.multiblock.network.LinearAcceleratorUpdatePacket;
import lach_01298.qmd.multiblock.network.RingAcceleratorUpdatePacket;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.recipe.IParticleIngredient;
import lach_01298.qmd.recipe.QMDRecipe;
import lach_01298.qmd.recipe.QMDRecipeInfo;
import nc.Global;
import nc.block.property.BlockProperties;
import nc.config.NCConfig;
import nc.multiblock.Multiblock;
import nc.multiblock.TileBeefBase.SyncReason;
import nc.multiblock.container.ContainerMultiblockController;
import nc.multiblock.container.ContainerSaltFissionController;
import nc.multiblock.fission.FissionReactor;
import nc.multiblock.fission.FissionReactorLogic;
import nc.multiblock.fission.tile.IFissionController;
import nc.multiblock.network.FissionUpdatePacket;
import nc.multiblock.network.SolidFissionUpdatePacket;
import nc.recipe.ProcessorRecipe;
import nc.recipe.RecipeInfo;
import nc.recipe.ingredient.IFluidIngredient;
import nc.tile.internal.fluid.Tank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import static nc.block.property.BlockProperties.FACING_ALL;
import static lach_01298.qmd.recipe.QMDRecipes.accelerator_source;

public class LinearAcceleratorLogic extends AcceleratorLogic
{

	
	protected TileAcceleratorSource source;
	public QMDRecipeInfo<QMDRecipe> recipeInfo;
	private int updateCount = 0;
	private int updateRate = 20;
	
	public LinearAcceleratorLogic(AcceleratorLogic oldLogic) 
	{
		super(oldLogic);
	}

	
	// Multiblock Validation
	
	@Override
	public boolean isMachineWhole(Multiblock multiblock)
	{
		Axis axis;
		Accelerator acc = getAccelerator();

		if (acc.getExteriorLengthY() != thickness)
		{
			multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.wrong_height, ", null);
			return false;
		}
		
		
		if (acc.getExteriorLengthX() > acc.getExteriorLengthZ())
		{
			axis = Axis.X;
			if(acc.getExteriorLengthX() < QMDConfig.accelerator_linear_min_size)
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.to_short, ", null);
				return false;
			}
			if(acc.getExteriorLengthZ() != thickness)
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.must_be_5_wide, ", null);
				return false;
			}
			
		}
		else
		{
			axis = Axis.Z;
			if(acc.getExteriorLengthZ() < QMDConfig.accelerator_linear_min_size)
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.to_short, ", null);
				return false;
			}
			if(acc.getExteriorLengthX() != thickness)
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.must_be_5_wide, ", null);
				return false;
			}
		}

		// Beam
		for (BlockPos pos : getinteriorAxisPositions(axis))
		{
			if (!(acc.WORLD.getTileEntity(pos) instanceof TileAcceleratorBeam))
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.must_be_beam, ", pos);
				return false;
			}
		}

		// Source and beam port
		if(axis == Axis.X)
		{
			BlockPos end1 =acc.getExtremeCoord(false, false, false).add(0, thickness / 2,thickness / 2);
			BlockPos end2 =acc.getExtremeCoord(true, false, false).add(0, thickness / 2,thickness / 2);
			if (!(acc.WORLD.getTileEntity(end1) instanceof TileAcceleratorSource|| acc.WORLD.getTileEntity(end2) instanceof TileAcceleratorSource))
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.have_source_and_beam_port, ", null);
				return false;
			}
			if (!(acc.WORLD.getTileEntity(end1) instanceof TileAcceleratorBeamPort|| acc.WORLD.getTileEntity(end2) instanceof TileAcceleratorBeamPort))
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.have_source_and_beam_port, ", null);
				return false;
			}
			if (acc.WORLD.getTileEntity(end1) instanceof TileAcceleratorSource)
			{
				if(acc.WORLD.getBlockState(end1).getValue(FACING_ALL) !=  EnumFacing.EAST)
				{
					multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.source_must_face_in, ", null);
					return false;
				}
			}
			else
			{
				if(acc.WORLD.getBlockState(end2).getValue(FACING_ALL) !=  EnumFacing.WEST)
				{
					multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.source_must_face_in, ", null);
					return false;
				}
			}
			
			
			
		}
		if(axis == Axis.Z)
		{
			BlockPos end1 =acc.getExtremeCoord(false, false, false).add(thickness / 2, thickness / 2,0);
			BlockPos end2 =acc.getExtremeCoord(false, false, true).add(thickness / 2, thickness / 2,0);
			if (!(acc.WORLD.getTileEntity(end1) instanceof TileAcceleratorSource|| acc.WORLD.getTileEntity(end2) instanceof TileAcceleratorSource))
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.have_source_and_beam_port, ", null);
				return false;
			}
			if (!(acc.WORLD.getTileEntity(end1) instanceof TileAcceleratorBeamPort|| acc.WORLD.getTileEntity(end2) instanceof TileAcceleratorBeamPort))
			{
				multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.have_source_and_beam_port, ", null);
				return false;
			}
			if (acc.WORLD.getTileEntity(end1) instanceof TileAcceleratorSource)
			{
				if(acc.WORLD.getBlockState(end1).getValue(FACING_ALL) !=  EnumFacing.SOUTH)
				{
					multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.source_must_face_in, ", null);
					return false;
				}
			}
			else
			{
				if(acc.WORLD.getBlockState(end1).getValue(FACING_ALL) !=  EnumFacing.NORTH)
				{
					multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.linear.source_must_face_in, ", null);
					return false;
				}
			}
		}
		
		
		
		// inlet

		if (getPartMap(TileAcceleratorInlet.class).size() < 1)
		{
			multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.no_inlet", null);
			return false;
		}
		
		//outlet
		if (getPartMap(TileAcceleratorOutlet.class).size() < 1)
		{
			multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.no_outlet", null);
			return false;
		}


		// Energy Ports
		if (getPartMap(TileAcceleratorEnergyPort.class).size() < 1)
		{
			multiblock.setLastError(QMD.MOD_ID + ".multiblock_validation.accelerator.need_energy_ports", null);
			return false;
		}

		
		return true;

	}
	
	public Set<BlockPos> getinteriorAxisPositions(EnumFacing.Axis axis)
	{
		Set<BlockPos> postions = new HashSet<BlockPos>();
		Accelerator acc = getAccelerator();
		
		if (axis == Axis.X)
		{
			for (BlockPos pos : BlockPos.getAllInBoxMutable(
					acc.getExtremeInteriorCoord(false, false, false).add(0, acc.getInteriorLengthY() / 2, acc.getInteriorLengthZ() / 2),
					acc.getExtremeInteriorCoord(true, false, false).add(0, acc.getInteriorLengthY() / 2, acc.getInteriorLengthZ() / 2)))
			{
				postions.add(pos.toImmutable());
			}
		}

		if (axis == Axis.Z)
		{
			for (BlockPos pos : BlockPos.getAllInBoxMutable(
					acc.getExtremeInteriorCoord(false, false, false).add(acc.getInteriorLengthX() / 2, acc.getInteriorLengthY() / 2, 0),
					acc.getExtremeInteriorCoord(false, false, true).add(acc.getInteriorLengthX() / 2, acc.getInteriorLengthY() / 2, 0)))
			{
				postions.add(pos.toImmutable());
			}
		}

		return postions;
	}
	
	
	
	// Multiblock Methods
	
	@Override
	public void onAcceleratorFormed()
	{
		
			Axis axis;

			if (multiblock.getExteriorLengthX() > multiblock.getExteriorLengthZ())
			{
				axis = Axis.X;
			}
			else
			{

				axis = Axis.Z;
			}

			// beam
			 Accelerator acc = getAccelerator();
				
			 
			 
			 
			 
			 if (!getWorld().isRemote)
			{

				// beam
				for (BlockPos pos :getinteriorAxisPositions(axis))
				{
					
					if (acc.WORLD.getTileEntity(pos) instanceof TileAcceleratorBeam)
					{
						
						TileAcceleratorBeam beam = (TileAcceleratorBeam) getWorld().getTileEntity(pos);
						beam.setFunctional(true);
						
					}
				}

				// ports
				for (TileAcceleratorOutlet port : acc.getPartMap((TileAcceleratorOutlet.class)).values())
				{

				}

				// beam
				for (TileAcceleratorBeam beam :acc.getPartMap(TileAcceleratorBeam.class).values())
				{
					if(beam.isFunctional())
					{
						if (acc.isValidRFCavity(beam.getPos(), Axis.X))
						{
							
							acc.getRFCavityMap().put(beam.getPos().toLong(), new RFCavity(acc, beam.getPos(), Axis.X));
						}
						else if (acc.isValidRFCavity(beam.getPos(), Axis.Z))
						{
							acc.getRFCavityMap().put(beam.getPos().toLong(), new RFCavity(acc, beam.getPos(), Axis.Z));
						}
						else if (acc.isValidQuadrupole(beam.getPos(), Axis.X))
						{
							acc.getQuadrupoleMap().put(beam.getPos().toLong(), new QuadrupoleMagnet(acc, beam.getPos(), Axis.X));
						}
						else if (acc.isValidQuadrupole(beam.getPos(), Axis.Z))
						{
							acc.getQuadrupoleMap().put(beam.getPos().toLong(), new QuadrupoleMagnet(acc, beam.getPos(), Axis.Z));
						}
					}
				
					
				}

				acc.RFCavityNumber = acc.getRFCavityMap().size();
				acc.quadrupoleNumber = acc.getQuadrupoleMap().size();


			
				for (RFCavity cavity : acc.getRFCavityMap().values())
				{
					for (IAcceleratorComponent componet : cavity.getComponents().values())
					{
						componet.setFunctional(true);
					}

				}
				
				
				for (QuadrupoleMagnet quad : acc.getQuadrupoleMap().values())
				{
					for (IAcceleratorComponent componet : quad.getComponents().values())
					{
						componet.setFunctional(true);
					}

				}

				//source
				for (TileAcceleratorSource source :acc.getPartMap(TileAcceleratorSource.class).values())
				{
					this.source = source;
				}
			}

			 refreshStats();
			 super.onAcceleratorFormed();
	}
	
	
	public void onMachineDisassembled()
	{
		 Accelerator acc = getAccelerator();
		 source = null;
		 
		 for (RFCavity cavity : acc.getRFCavityMap().values())
		{
			for (IAcceleratorComponent componet : cavity.getComponents().values())
			{
				componet.setFunctional(false);
			}

		}
		
		
		for (QuadrupoleMagnet quad : acc.getQuadrupoleMap().values())
		{
			for (IAcceleratorComponent componet : quad.getComponents().values())
			{
				componet.setFunctional(false);
			}

		}
		
		
		acc.getRFCavityMap().clear();
		acc.getQuadrupoleMap().clear();
		
		for (TileAcceleratorBeam beam :acc.getPartMap(TileAcceleratorBeam.class).values())
		{
			beam.setFunctional(false);
		}
		
		super.onMachineDisassembled();
	}
	
	
	
	@Override
	public boolean onUpdateServer() 
	{
		updateCount--;
		if(updateCount <= 0)
		{
			
			if(getAccelerator().isAcceleratorOn)
			{
				refreshRecipe();
				if (recipeInfo != null)
				{
					produceBeam();
				}
				else
				{
					resetBeam();
				}
			}
			else
			{
				resetBeam();
			}
		
			updateCount = updateRate;
		}
		return super.onUpdateServer();
	}
	
	private void refreshStats()
	{
		int energy = 0;
		int heat = 0;
		double efficiency =1;
		double strength =0;
		double voltage = 0;
		for(TileAcceleratorMagnet magnet :getAccelerator().getPartMap(TileAcceleratorMagnet.class).values())
		{
			heat += magnet.heat;
			energy += magnet.basePower;
			efficiency *= Math.pow(magnet.efficiency,1/4d);
		}
		for(TileAcceleratorRFCavity cavity :getAccelerator().getPartMap(TileAcceleratorRFCavity.class).values())
		{
			heat += cavity.heat;
			energy += cavity.basePower;
			efficiency *= Math.pow(cavity.efficiency,1/8d);
			
		}
		
		for (QuadrupoleMagnet quad : getAccelerator().getQuadrupoleMap().values())
		{
			for (IAcceleratorComponent componet : quad.getComponents().values())
			{
				if(componet instanceof TileAcceleratorMagnet)
				{
					TileAcceleratorMagnet magnet = (TileAcceleratorMagnet) componet;
					strength += magnet.strength/4;
				}
			}
		}
		
		for (RFCavity cavity : getAccelerator().getRFCavityMap().values())
		{
			for (IAcceleratorComponent componet : cavity.getComponents().values())
			{
				if(componet instanceof TileAcceleratorRFCavity)
				{
					TileAcceleratorRFCavity cav = (TileAcceleratorRFCavity) componet;
					voltage += cav.voltage/8d;
				}
			}
		}
		
		
		getAccelerator().requiredEnergy =  (int) (energy*(2-efficiency));
		getAccelerator().rawHeating = heat;
		getAccelerator().quadrupoleStrength = strength;
		getAccelerator().efficiency = efficiency;
		getAccelerator().acceleratingVoltage=(int) voltage;
		
	}
	
	
	// Recipe Stuff
	
	private void resetBeam()
	{
		getAccelerator().beam.setParticle(Particles.none);
		getAccelerator().beam.setMeanEnergy(0);
		getAccelerator().beam.setLuminosity(0);
	}


	private void produceBeam()
	{
		IParticleIngredient particleIngredient = recipeInfo.getRecipe().particleProducts().get(0);
		ParticleBeam beam = particleIngredient.getStack();
		getAccelerator().beam.setParticle(beam.getParticle());
		
		getAccelerator().beam.setMeanEnergy((int) (getAccelerator().acceleratingVoltage*beam.getParticle().getCharge()));
		getAccelerator().beam.setLuminosity((int) (beam.getLuminosity()*getAccelerator().quadrupoleStrength));
		useItemDurability();
	}


	private void useItemDurability()
	{
		if(source.getInventory().getStackInSlot(0).attemptDamageItem(1, rand, null))
		{
			source.getInventory().setInventorySlotContents(0, ItemStack.EMPTY);
		}
	}


	protected void refreshRecipe() 
	{
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack item =source.getInventory().getStackInSlot(0).copy();
		item.setItemDamage(0);
		items.add(item);
		recipeInfo = accelerator_source.getRecipeInfoFromInputs(items, new ArrayList<Tank>(), new ArrayList<ParticleBeam>());
	}
	
	
	
	


	
	
	// Network
	
	@Override
	public AcceleratorUpdatePacket getUpdatePacket() 
	{

		return new LinearAcceleratorUpdatePacket(getAccelerator().controller.getTilePos(),
				getAccelerator().isAcceleratorOn, getAccelerator().cooling, getAccelerator().rawHeating,getAccelerator().maxCoolantIn,getAccelerator().maxCoolantOut,
				getAccelerator().requiredEnergy, getAccelerator().efficiency, getAccelerator().acceleratingVoltage,
				getAccelerator().RFCavityNumber, getAccelerator().quadrupoleNumber, getAccelerator().quadrupoleStrength,
				getAccelerator().heatBuffer, getAccelerator().energyStorage,getAccelerator().beam, getAccelerator().tanks);
	}
	
	@Override
	public void onPacket(AcceleratorUpdatePacket message)
	{
		super.onPacket(message);
		if (message instanceof LinearAcceleratorUpdatePacket)
		{
			LinearAcceleratorUpdatePacket packet = (LinearAcceleratorUpdatePacket) message;

		}
	}
	
	// NBT
	
	@Override
	public void writeToNBT(NBTTagCompound data, SyncReason syncReason)
	{
		super.writeToNBT(data, syncReason);
		NBTTagCompound logicTag = new NBTTagCompound();
		//stuff
		data.setTag("linear_accelerator", logicTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound data, SyncReason syncReason)
	{
		super.readFromNBT(data, syncReason);
		if (data.hasKey("linear_accelerator"))
		{
			NBTTagCompound logicTag = data.getCompoundTag("linear_accelerator");
			//stuff
		}
	}

	
	
	
	@Override
	public ContainerMultiblockController<Accelerator, IAcceleratorController> getContainer(EntityPlayer player)
	{
		return new ContainerLinearAcceleratorController(player, getAccelerator().controller);
	}
	
}
