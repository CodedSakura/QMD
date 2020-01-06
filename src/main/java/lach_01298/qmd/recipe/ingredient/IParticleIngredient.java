package lach_01298.qmd.recipe.ingredient;

import java.util.List;

import lach_01298.qmd.particle.ParticleStack;
import nc.recipe.ingredient.IIngredient;

public interface IParticleIngredient extends IIngredient<ParticleStack>
{
	@Override
	public default ParticleStack getNextStack(int ingredientNumber) {
		ParticleStack nextStack = getStack();
		nextStack.setAmount( getNextStackSize(ingredientNumber));
		return nextStack;
	}
	
	@Override
	public default List<ParticleStack> getInputStackHashingList() {
		return getInputStackList();
	}
}
