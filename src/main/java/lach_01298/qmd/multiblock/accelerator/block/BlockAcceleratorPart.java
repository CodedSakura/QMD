package lach_01298.qmd.multiblock.accelerator.block;

import lach_01298.qmd.tab.QMDTabs;
import nc.multiblock.block.BlockMultiblockPart;
import nc.tab.NCTabs;
import net.minecraft.block.material.Material;

public abstract class BlockAcceleratorPart extends BlockMultiblockPart
{

	public BlockAcceleratorPart()
	{
		super(Material.IRON, QMDTabs.MULTIBLOCKS);
	}

	public static abstract class Transparent extends BlockMultiblockPart.Transparent
	{

		public Transparent(boolean smartRender)
		{
			super(Material.IRON, QMDTabs.MULTIBLOCKS, smartRender);
		}
	}
}
