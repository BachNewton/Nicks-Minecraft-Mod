package minecraftbyexample.dream_food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;

import javax.annotation.Nullable;

public class DreamFoodStats extends FoodStats {

    private final static int FIXED_FOOD_LEVEL = 10;

    private final PlayerEntity playerEntity;

    public DreamFoodStats(final PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public int getFoodLevel() {
        return FIXED_FOOD_LEVEL;
    }

    @Override
    public boolean needFood() {
        final float currentHealth = playerEntity.getHealth();
        final float maxHealth = playerEntity.getMaxHealth();
        return currentHealth < maxHealth;
    }

    @Override
    public void tick(@Nullable final PlayerEntity player) {
        // Food should not decrease over time
        // Food should not regenerate player health
    }
}
