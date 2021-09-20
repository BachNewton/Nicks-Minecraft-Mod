package minecraftbyexample.dream_food;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class StartupCommon {

    private static final String FOOD_STATS_FIELD_NAME = "foodStats";

    @SubscribeEvent
    public static void onEntityJoinWorldEvent(final EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof PlayerEntity) {
            handlePlayerEntityJoinWorld((PlayerEntity) entity);
        }
    }

    private static void handlePlayerEntityJoinWorld(final PlayerEntity playerEntity) {
        final FoodStats existingFoodStats = playerEntity.getFoodStats();

        if (!(existingFoodStats instanceof DreamFoodStats)) {
            replacePlayerEntityFoodStats(playerEntity);
        }
    }

    private static void replacePlayerEntityFoodStats(final PlayerEntity playerEntity) {
        final FoodStats dreamFoodStats = new DreamFoodStats();

        ObfuscationReflectionHelper.setPrivateValue(
            PlayerEntity.class,
            playerEntity,
            dreamFoodStats,
            FOOD_STATS_FIELD_NAME
        );
    }
}
