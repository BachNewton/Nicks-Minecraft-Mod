package minecraftbyexample.dream_food;

import net.minecraftforge.eventbus.api.Event;

public interface EventHandler<T extends Event> {

    void handleEvent(T event);
}
