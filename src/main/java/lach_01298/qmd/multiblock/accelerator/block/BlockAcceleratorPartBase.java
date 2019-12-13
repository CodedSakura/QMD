package lach_01298.qmd.multiblock.accelerator.block;

import nc.multiblock.MultiblockBlockPartBase;
import nc.tab.NCTabs;
import net.minecraft.block.material.Material;

public abstract class BlockAcceleratorPartBase extends MultiblockBlockPartBase
{

	public BlockAcceleratorPartBase()
	{
		super(Material.IRON, NCTabs.MULTIBLOCK);
	}

	public static abstract class Transparent extends MultiblockBlockPartBase.Transparent
	{

		public Transparent(boolean smartRender)
		{
			super(Material.IRON, NCTabs.MULTIBLOCK, smartRender);
		}
	}
}
