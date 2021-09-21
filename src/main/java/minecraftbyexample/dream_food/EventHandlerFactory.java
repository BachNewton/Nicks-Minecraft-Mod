package minecraftbyexample.dream_food;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

public class EventHandlerFactory {

    private static final String UNCHECKED_CAST = "unchecked";

    @SuppressWarnings(UNCHECKED_CAST)
    public static <T extends Event> EventHandler<T> getHandler(Class<T> eventClass) {
        if (eventClass == EntityJoinWorldEvent.class) {
            return (EventHandler<T>) new EntityJoinWorldEventHandler();
        } else if (eventClass == PlayerInteractEvent.class) {
            return (EventHandler<T>) new PlayerInteractEventHandler();
        } else {
            return event -> {};
        }
    }
}
