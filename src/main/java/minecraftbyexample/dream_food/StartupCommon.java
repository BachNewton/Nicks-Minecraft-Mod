package minecraftbyexample.dream_food;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StartupCommon {

    private static final EventHandler<EntityJoinWorldEvent> ENTITY_JOIN_WORLD_EVENT_HANDLER = EventHandlerFactory.getHandler(EntityJoinWorldEvent.class);
    private static final EventHandler<PlayerInteractEvent> PLAYER_INTERACT_EVENT_EVENT_HANDLER = EventHandlerFactory.getHandler(PlayerInteractEvent.class);

    @SubscribeEvent
    public static void onEntityJoinWorldEvent(final EntityJoinWorldEvent event) {
        ENTITY_JOIN_WORLD_EVENT_HANDLER.handleEvent(event);
    }

    @SubscribeEvent
    public static void onPlayerInteractEvent(final PlayerInteractEvent event) {
        PLAYER_INTERACT_EVENT_EVENT_HANDLER.handleEvent(event);
    }
}
