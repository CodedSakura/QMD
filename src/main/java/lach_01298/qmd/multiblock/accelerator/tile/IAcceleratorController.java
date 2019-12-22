package lach_01298.qmd.multiblock.accelerator.tile;

import lach_01298.qmd.multiblock.accelerator.Accelerator;
import lach_01298.qmd.multiblock.accelerator.AcceleratorLogic;
import nc.multiblock.ILogicMultiblockController;

public interface IAcceleratorController extends IAcceleratorPart, ILogicMultiblockController<Accelerator, AcceleratorLogic> 
{

	public void updateBlockState(boolean isActive);
	
	public void doMeltdown();
}
