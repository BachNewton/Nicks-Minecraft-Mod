package minecraftbyexample.dream_food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class PlayerInteractEventHandler implements EventHandler<PlayerInteractEvent> {

    private static final boolean ALWAYS_IGNORE_HUNGER = false;

    @Override
    public void handleEvent(final PlayerInteractEvent event) {
        if (event instanceof RightClickItem) {
            handleRightClickItemEvent((RightClickItem) event);
        }
    }

    private void handleRightClickItemEvent(final RightClickItem event) {
        PlayerEntity playerEntity = event.getPlayer();
        ItemStack mainHandItemStack = playerEntity.getHeldItemMainhand();
        if (mainHandItemStack.isFood()) {
            handleRightClickFoodEvent(playerEntity, mainHandItemStack);
            event.setCanceled(true);
        }
    }

    private void handleRightClickFoodEvent(final PlayerEntity playerEntity, final ItemStack itemStack) {
        if (playerEntity.canEat(ALWAYS_IGNORE_HUNGER)) {
            final Food food = itemStack.getItem().getFood();
            eatFood(playerEntity, food);
            decrementItemStack(itemStack);
        }
    }

    private void eatFood(final PlayerEntity playerEntity, final Food food) {
        final int healing = food == null ? 0 : food.getHealing();
        playerEntity.heal(healing);
    }

    private void decrementItemStack(final ItemStack itemStack) {
        itemStack.setCount(itemStack.getCount() - 1);
    }
}
