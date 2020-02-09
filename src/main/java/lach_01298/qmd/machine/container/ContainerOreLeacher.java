package lach_01298.qmd.machine.container;

import lach_01298.qmd.recipes.QMDRecipes;
import nc.container.processor.ContainerItemFluidProcessor;
import nc.container.slot.SlotFurnace;
import nc.container.slot.SlotProcessorInput;
import nc.container.slot.SlotSpecificInput;
import nc.recipe.ProcessorRecipeHandler;
import nc.tile.processor.TileItemFluidProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerOreLeacher extends ContainerItemFluidProcessor
{

	public ContainerOreLeacher(EntityPlayer player, TileItemFluidProcessor tileEntity)
	{
		super(player, tileEntity, QMDRecipes.ore_leacher);
		
		addSlotToContainer(new SlotProcessorInput(tileEntity, recipeHandler, 0, 46, 35));
		
		addSlotToContainer(new SlotFurnace(player, tileEntity, 1, 126, 35));
		addSlotToContainer(new SlotFurnace(player, tileEntity, 2, 146, 35));
		addSlotToContainer(new SlotFurnace(player, tileEntity, 3, 166, 35));
		
		addSlotToContainer(new SlotSpecificInput(tileEntity, 4, 132, 64, speedUpgrade));
		addSlotToContainer(new SlotSpecificInput(tileEntity, 5, 152, 64, energyUpgrade));
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + 9*i + 9, 8 + 18*j, 84 + 18*i));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + 18*i, 142));
		}
		
		
		
		
		
	}

}
