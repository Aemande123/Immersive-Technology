package ferro2000.immersivetech.common.util.compat.jei.distiller;

import java.util.List;

import ferro2000.immersivetech.api.crafting.DistillerRecipe;
import ferro2000.immersivetech.common.ITContent;
import ferro2000.immersivetech.common.blocks.metal.types.BlockType_MetalMultiblock;
import ferro2000.immersivetech.common.util.compat.jei.ITRecipeCategory;
import ferro2000.immersivetech.common.util.compat.jei.JEIHelper;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

public class DistillerRecipeCategory extends ITRecipeCategory<DistillerRecipe, DistillerRecipeWrapper> {
	public static ResourceLocation background = new ResourceLocation("immersivetech:textures/gui/gui_distiller.png");
	private final IDrawable tankOverlay;

	@SuppressWarnings("deprecation")
	public DistillerRecipeCategory(IGuiHelper helper) {
		super("distiller", "tile.immersivetech.metal_multiblock.distiller.name", helper.createDrawable(background, 6, 12, 164, 59), DistillerRecipe.class, new ItemStack(ITContent.blockMetalMultiblock, 1, BlockType_MetalMultiblock.DISTILLER.getMeta()));
		tankOverlay = helper.createDrawable(background, 177, 31, 16, 47, -2, 2, -2, 2);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, DistillerRecipeWrapper recipeWrapper, IIngredients ingredients) {
		List<List<FluidStack>> inputs = ingredients.getInputs(FluidStack.class);
		List<List<FluidStack>> outputs = ingredients.getOutputs(FluidStack.class);
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		if(inputs.size() > 0) {
			guiFluidStacks.init(0, true, 52, 9, 16, 47, 6000, false, tankOverlay);
			guiFluidStacks.set(0, inputs.get(0));
		}
		guiFluidStacks.init(1, false, 106, 9, 16, 47, 6000, false, tankOverlay);
		guiFluidStacks.set(1, outputs.get(0));
		guiFluidStacks.addTooltipCallback(JEIHelper.fluidTooltipCallback);
	}
	
	@Override
	public IRecipeWrapper getRecipeWrapper(DistillerRecipe recipe) {
		return new DistillerRecipeWrapper(recipe);
	}

}