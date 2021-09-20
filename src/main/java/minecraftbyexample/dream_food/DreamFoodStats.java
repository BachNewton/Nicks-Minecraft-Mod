package minecraftbyexample.dream_food;

import net.minecraft.util.FoodStats;

public class DreamFoodStats extends FoodStats {

    private final static int FIXED_FOOD_LEVEL = 10;

    @Override
    public int getFoodLevel() {
        return FIXED_FOOD_LEVEL;
    }
}
