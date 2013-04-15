package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

/**
 * Control for bukkit recipes to avoid confusion with RecipeManager's recipes
 */
public class Vanilla
{
    protected static Map<BaseRecipe, RecipeInfo> initialRecipes = new HashMap<BaseRecipe, RecipeInfo>();
    
    /**
     * Leather dyeing's special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_LEATHERDYE = new ItemStack(Material.LEATHER_HELMET, 0, (short)0);
    
    /**
     * Map cloning's special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_MAPCLONE = new ItemStack(Material.MAP, 0, (short)-1);
    
    /**
     * Map extending's special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_MAPEXTEND = new ItemStack(Material.EMPTY_MAP, 0, (short)0);
    
    /**
     * Fireworks' special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_FIREWORKS = new ItemStack(Material.FIREWORK, 0, (short)0);
    
    /**
     * Default time a furnace recipe burns for.<br>
     * This is a game constant.
     */
    public static final float FURNACE_RECIPE_TIME = 9.25f;
    
    /**
     * The data value wildcard for recipe ingredients.<br>
     * If an ingredient has this data value its data value will be ignored.
     */
    public static final short DATA_WILDCARD = Short.MAX_VALUE;
    
    protected static void init()
    {
        clean();
        
        RecipeInfo info = new RecipeInfo(RecipeOwner.MINECRAFT, null); // shared info
        
        // Add vanilla Minecraft fuels just for warning if user adds one that already exists or tries to overwrite an unexistent one
        initialRecipes.put(new FuelRecipe(Material.COAL, 80), info);
        initialRecipes.put(new FuelRecipe(Material.LOG, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_STEP, 5), info);
        initialRecipes.put(new FuelRecipe(Material.SAPLING, 5), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_AXE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_HOE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_PICKAXE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_SPADE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_SWORD, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_PLATE, 15), info);
        initialRecipes.put(new FuelRecipe(Material.STICK, 5), info);
        initialRecipes.put(new FuelRecipe(Material.FENCE, 15), info);
        initialRecipes.put(new FuelRecipe(Material.FENCE_GATE, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.BIRCH_WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.SPRUCE_WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.JUNGLE_WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.TRAP_DOOR, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WORKBENCH, 15), info);
        initialRecipes.put(new FuelRecipe(Material.BOOKSHELF, 15), info);
        initialRecipes.put(new FuelRecipe(Material.CHEST, 15), info);
        initialRecipes.put(new FuelRecipe(Material.JUKEBOX, 15), info);
        initialRecipes.put(new FuelRecipe(Material.NOTE_BLOCK, 15), info);
        initialRecipes.put(new FuelRecipe(Material.HUGE_MUSHROOM_1, 15), info);
        initialRecipes.put(new FuelRecipe(Material.HUGE_MUSHROOM_2, 15), info);
        initialRecipes.put(new FuelRecipe(Material.BLAZE_ROD, 120), info);
        initialRecipes.put(new FuelRecipe(Material.LAVA_BUCKET, 1000), info);
        initialRecipes.put(new FuelRecipe(Material.TRAPPED_CHEST, 15), info);
        initialRecipes.put(new FuelRecipe(Material.DAYLIGHT_DETECTOR, 15), info);
        
        // Index fuel recipes
        for(BaseRecipe recipe : initialRecipes.keySet())
        {
            if(recipe instanceof FuelRecipe)
            {
                RecipeManager.getRecipes().indexFuels.put(((FuelRecipe)recipe).getIndexString(), (FuelRecipe)recipe);
            }
        }
        
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe r;
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r == null || (RecipeManager.getRecipes() != null && RecipeManager.getRecipes().isCustomRecipe(r)))
            {
                continue;
            }
            
            if(r instanceof ShapedRecipe)
            {
                initialRecipes.put(new CraftRecipe((ShapedRecipe)r), info);
            }
            else if(r instanceof ShapelessRecipe)
            {
                initialRecipes.put(new CombineRecipe((ShapelessRecipe)r), info);
            }
            else if(r instanceof FurnaceRecipe)
            {
                initialRecipes.put(new SmeltRecipe((FurnaceRecipe)r), info);
            }
        }
        
        // Add them to recipe storage
        RecipeManager.getRecipes().index.putAll(Vanilla.initialRecipes);
    }
    
    protected static void clean()
    {
        initialRecipes.clear();
    }
    
    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     * 
     * @param recipe
     *            RecipeManager recipe
     * @return
     *         true if recipe was found and removed
     */
    public static boolean removeCustomRecipe(BaseRecipe recipe)
    {
        if(recipe instanceof CraftRecipe)
        {
            return removeCraftRecipe((CraftRecipe)recipe);
        }
        
        if(recipe instanceof CombineRecipe)
        {
            return removeCombineRecipe((CombineRecipe)recipe);
        }
        
        if(recipe instanceof SmeltRecipe)
        {
            return removeSmeltRecipe((SmeltRecipe)recipe);
        }
        
        return false;
    }
    
    /**
     * Removes a Bukkit recipe from the <b>server</b>
     * <b>Note: This method converts the Bukkit recipe to RecipeManager recipe. If you have the BaseRecipe object you should use {@link #removeCustomRecipe(BaseRecipe)}</b>
     * 
     * @param recipe
     *            Bukkit recipe
     * @return
     *         true if recipe was found and removed
     */
    public static boolean removeBukkitRecipe(Recipe recipe)
    {
        if(recipe instanceof ShapedRecipe)
        {
            return removeShapedRecipe((ShapedRecipe)recipe);
        }
        
        if(recipe instanceof ShapelessRecipe)
        {
            return removeShapelessRecipe((ShapelessRecipe)recipe);
        }
        
        if(recipe instanceof FurnaceRecipe)
        {
            return removeFurnaceRecipe((FurnaceRecipe)recipe);
        }
        
        return false;
    }
    
    /**
     * Removes a Bukkit recipe from the <b>server</b><br>
     * <b>Note: This method converts the Bukkit recipe to RecipeManager recipe. If you have the CraftRecipe object you should use {@link #removeCraftRecipe(CraftRecipe)}</b>
     * 
     * @param recipe
     *            Bukkit recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeShapedRecipe(ShapedRecipe recipe)
    {
        return removeCraftRecipe(new CraftRecipe(recipe));
    }
    
    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     * 
     * @param recipe
     *            RecipeManager recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeCraftRecipe(CraftRecipe recipe)
    {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        ShapedRecipe sr;
        Recipe r;
        String[] sh;
        
        ItemStack[] matrix = recipe.getIngredients().clone();
        Tools.trimItemMatrix(matrix);
        ItemStack[] matrixMirror = Tools.mirrorItemMatrix(matrix);
        int height = recipe.getHeight();
        int width = recipe.getWidth();
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof ShapedRecipe)
            {
                sr = (ShapedRecipe)r;
                sh = sr.getShape();
                
                if(sh.length == height && sh[0].length() == width && Tools.compareShapedRecipeToMatrix(sr, matrix, matrixMirror))
                {
                    iterator.remove();
                    return true;
                }
            }
        }
        
        iterator = null;
        return false;
    }
    
    /**
     * Removes a Bukkit recipe from the <b>server</b><br>
     * <b>Note: This method converts the Bukkit recipe to RecipeManager recipe. If you have the CombineRecipe object you should use {@link #removeCombineRecipe(CombineRecipe)}</b>
     * 
     * @param recipe
     *            Bukkit recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeShapelessRecipe(ShapelessRecipe recipe)
    {
        return removeCombineRecipe(new CombineRecipe(recipe));
    }
    
    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     * 
     * @param recipe
     *            RecipeManager recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeCombineRecipe(CombineRecipe recipe)
    {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        ShapelessRecipe sr;
        Recipe r;
        
        List<ItemStack> items = recipe.getIngredients();
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof ShapelessRecipe)
            {
                sr = (ShapelessRecipe)r;
                
                if(Tools.compareIngredientList(items, sr.getIngredientList()))
                {
                    iterator.remove();
                    return true;
                }
            }
        }
        
        iterator = null;
        return false;
    }
    
    /**
     * Removes a Bukkit furnace recipe from the <b>server</b><br>
     * Unlike {@link #removeShapedRecipe(ShapedRecipe)} and {@link #removeShapelessRecipe(ShapelessRecipe)} this method does not convert recipes since it only needs the ingredient.
     * 
     * @param recipe
     *            Bukkit recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeFurnaceRecipe(FurnaceRecipe recipe)
    {
        return removeFurnaceRecipe(recipe.getInput());
    }
    
    /**
     * Removes a RecipeManager smelt recipe from the <b>server</b>
     * 
     * @param recipe
     *            RecipeManager recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeSmeltRecipe(SmeltRecipe recipe)
    {
        return removeFurnaceRecipe(recipe.getIngredient());
    }
    
    private static boolean removeFurnaceRecipe(ItemStack ingredient)
    {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        FurnaceRecipe fr;
        Recipe r;
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof FurnaceRecipe)
            {
                fr = (FurnaceRecipe)r;
                
                if(ingredient.getTypeId() == fr.getInput().getTypeId())
                {
                    iterator.remove();
                    return true;
                }
            }
        }
        
        iterator = null;
        return false;
    }
    
    /**
     * Remove all RecipeManager recipes from the server.
     */
    public static void removeCustomRecipes()
    {
        if(RecipeManager.getRecipes() == null)
            return;
        
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe recipe;
        
        while(iterator.hasNext())
        {
            recipe = iterator.next();
            
            if(recipe != null && RecipeManager.getRecipes().isCustomRecipe(recipe))
            {
                iterator.remove();
            }
        }
    }
    
    /**
     * Adds all recipes that already existed when the plugin was enabled.
     */
    public static void restoreInitialRecipes()
    {
        for(Entry<BaseRecipe, RecipeInfo> entry : initialRecipes.entrySet())
        {
            // TODO maybe check if recipe is already in server ?
            Bukkit.addRecipe(entry.getKey().getBukkitRecipe());
        }
    }
}