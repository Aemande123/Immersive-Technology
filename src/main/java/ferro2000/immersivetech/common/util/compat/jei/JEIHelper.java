package ferro2000.immersivetech.common.util.compat.jei;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import ferro2000.immersivetech.api.crafting.BoilerRecipe;
import ferro2000.immersivetech.api.crafting.BoilerRecipe.BoilerFuelRecipe;
import ferro2000.immersivetech.api.crafting.DistillerRecipe;
import ferro2000.immersivetech.api.crafting.SolarTowerRecipe;
import ferro2000.immersivetech.api.crafting.SteamTurbineRecipe;
import ferro2000.immersivetech.common.util.compat.ITCompatModule;
import ferro2000.immersivetech.common.util.compat.jei.boiler.BoilerFuelRecipeCategory;
import ferro2000.immersivetech.common.util.compat.jei.boiler.BoilerRecipeCategory;
import ferro2000.immersivetech.common.util.compat.jei.distiller.DistillerRecipeCategory;
import ferro2000.immersivetech.common.util.compat.jei.solartower.SolarTowerRecipeCategory;
import ferro2000.immersivetech.common.util.compat.jei.steamturbine.SteamTurbineRecipeCategory;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;

import net.minecraftforge.fluids.FluidStack;

@JEIPlugin
public class JEIHelper implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	public static IModRegistry modRegistry;
	public static IDrawable slotDrawable;
	public static ITooltipCallback<FluidStack> fluidTooltipCallback = new ITFluidTooltipCallback();

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
	}

	@SuppressWarnings("rawtypes")
	Map<Class, ITRecipeCategory> categories = new LinkedHashMap<>();

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		jeiHelpers = registry.getJeiHelpers();

		//Recipes
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		slotDrawable = guiHelper.getSlotDrawable();
		
		categories.put(DistillerRecipe.class, new DistillerRecipeCategory(guiHelper));
		categories.put(BoilerRecipe.class, new BoilerRecipeCategory(guiHelper));
		categories.put(BoilerFuelRecipe.class, new BoilerFuelRecipeCategory(guiHelper));
		categories.put(SolarTowerRecipe.class, new SolarTowerRecipeCategory(guiHelper));
		categories.put(SteamTurbineRecipe.class, new SteamTurbineRecipeCategory(guiHelper));
			
		registry.addRecipeCategories(categories.values().toArray(new IRecipeCategory[categories.size()]));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void register(IModRegistry registryIn) {
		modRegistry = registryIn;

		for(ITRecipeCategory<Object, IRecipeWrapper> cat : categories.values()) {
			cat.addCatalysts(registryIn);
			modRegistry.handleRecipes(cat.getRecipeClass(), cat, cat.getRecipeCategoryUid());
		}

		modRegistry.addRecipes(new ArrayList<Object>((DistillerRecipe.recipeList)), "it.distiller");
		modRegistry.addRecipes(new ArrayList<Object>((BoilerRecipe.recipeList)), "it.boiler");
		modRegistry.addRecipes(new ArrayList<Object>((BoilerRecipe.fuelList)), "it.boilerFuel");
		modRegistry.addRecipes(new ArrayList<Object>((SolarTowerRecipe.recipeList)), "it.solarTower");
		modRegistry.addRecipes(new ArrayList<Object>((SteamTurbineRecipe.recipeList)), "it.steamTurbine");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		final IRecipeRegistry registry = jeiRuntime.getRecipeRegistry();
		ITCompatModule.jeiAddFunc = recipe -> {
			ITRecipeCategory<Object, ?> factory = getFactory(recipe.getClass());
			if(factory != null)	registry.addRecipe(factory.getRecipeWrapper(recipe), factory.getUid());
		};
		ITCompatModule.jeiRemoveFunc = recipe -> {
			ITRecipeCategory<Object, ?> factory = getFactory(recipe.getClass());
			if(factory != null)	registry.removeRecipe(factory.getRecipeWrapper(recipe), factory.getUid());
		};
	}

	@SuppressWarnings("unchecked")
	private ITRecipeCategory<Object, ?> getFactory(Class<?> recipeClass) {
		ITRecipeCategory<Object, ?> factory = this.categories.get(recipeClass);

		if(factory == null && recipeClass != Object.class)
			factory = getFactory(recipeClass.getSuperclass());

		return factory;
	}

}