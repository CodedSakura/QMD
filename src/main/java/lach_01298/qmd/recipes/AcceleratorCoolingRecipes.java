package lach_01298.qmd.recipes;

import java.util.ArrayList;
import java.util.List;

import nc.recipe.ProcessorRecipeHandler;

public class AcceleratorCoolingRecipes extends ProcessorRecipeHandler
{

	public AcceleratorCoolingRecipes()
	{
		super("accelerator_cooling", 0, 1, 0, 1);
	}

	@Override
	public void addRecipes()
	{
		addRecipe(fluidStack("liquid_helium", 1), fluidStack("helium", 320), 1000);
		
	}

	@Override
	public List fixExtras(List extras)
	{
		return extras;
	}

}
