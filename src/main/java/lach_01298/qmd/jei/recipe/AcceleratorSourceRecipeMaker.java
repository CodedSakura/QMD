package lach_01298.qmd.jei.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.recipe.QMDRecipe;
import lach_01298.qmd.recipe.QMDRecipes;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.plugins.vanilla.furnace.SmeltingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class AcceleratorSourceRecipeMaker
{
	private AcceleratorSourceRecipeMaker()
	{
	}

	public static List<AcceleratorSourceRecipe> getRecipes(IJeiHelpers helpers)
	{
		List<QMDRecipe> recipes = QMDRecipes.accelerator_source.getRecipeList();
		IStackHelper stackHelper = helpers.getStackHelper();
		List<AcceleratorSourceRecipe> jeiRecipes = new ArrayList<>();

		for (QMDRecipe recipe : recipes)
		{
			ItemStack input = recipe.itemIngredients().get(0).getStack();
			ParticleStack output = recipe.particleProducts().get(0).getStack();
			AcceleratorSourceRecipe jeiRecipe = new AcceleratorSourceRecipe(input, output);
			jeiRecipes.add(jeiRecipe);
		}

		return jeiRecipes;
	}
}
