package digi.recipeManager.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import digi.recipeManager.Tools;

public class CombineRecipe extends MultiResultRecipe
{
    private List<ItemStack> ingredients;
    
    public CombineRecipe()
    {
    }
    
    public CombineRecipe(ShapelessRecipe recipe)
    {
        setIngredients(recipe.getIngredientList());
        setResult(recipe.getResult());
    }
    
    public CombineRecipe(RmRecipe recipe)
    {
        super(recipe);
    }
    
    public CombineRecipe(Flags flags)
    {
        super(flags);
    }
    
    public List<ItemStack> getIngredients()
    {
        return ingredients;
    }
    
    public void addIngredient(Material type)
    {
        addIngredient(1, type, (short)-1);
    }
    
    public void addIngredient(Material type, short data)
    {
        addIngredient(1, type, data);
    }
    
    public void addIngredient(ItemStack ingredient)
    {
        addIngredient(ingredient.getAmount(), ingredient.getType(), ingredient.getDurability());
    }
    
    public void addIngredient(int amount, Material type, short data)
    {
        if(ingredients == null)
            ingredients = new ArrayList<ItemStack>();
        
        if((ingredients.size() + amount) > 9) // check if they're more than they should...
            throw new IllegalArgumentException("Recipe can't have more than 9 ingredients!");
        
        while(amount-- > 0)
        {
            ingredients.add(new ItemStack(type, 1, data));
        }
        
        sort();
    }
    
    public void setIngredients(List<ItemStack> ingredients)
    {
        // unstack ingredients
        this.ingredients = new ArrayList<ItemStack>();
        int amount;
        
        for(ItemStack ingredient : ingredients)
        {
            amount = ingredient.getAmount();
            
            while(amount-- > 0)
            {
                this.ingredients.add(new ItemStack(ingredient.getType(), 1, ingredient.getDurability()));
            }
        }
        
        if(this.ingredients.size() > 9) // check if they're more than they should...
            throw new IllegalArgumentException("Recipe can't have more than 9 ingredients!");
        
        sort();
    }
    
    private void sort()
    {
        // sort by type and data
        Tools.sortIngredientList(ingredients);
        
        // build hashcode
        StringBuilder str = new StringBuilder("combine");
        
        for(ItemStack ingredient : ingredients)
        {
            str.append(ingredient.getTypeId()).append(':').append(ingredient.getDurability()).append(';');
        }
        
        hash = str.toString().hashCode();
    }
    
    public ShapelessRecipe toShapelessRecipe()
    {
        return toShapelessRecipe(0);
    }
    
    public ShapelessRecipe toShapelessRecipe(int markResultId)
    {
        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(markResultId == 0 ? getFirstResult() : Tools.generateRecipeIdResult(getFirstResult(), markResultId));
        
        for(ItemStack item : ingredients)
        {
            bukkitRecipe.addIngredient(item.getAmount(), item.getType(), item.getDurability());
        }
        
        return bukkitRecipe;
    }
    
    public boolean hasIngredients()
    {
        return ingredients != null && !ingredients.isEmpty();
    }
    
    @Override
    public boolean isValid()
    {
        return hasIngredients() && hasResults();
    }
}