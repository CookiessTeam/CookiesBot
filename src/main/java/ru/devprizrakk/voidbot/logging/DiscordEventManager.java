package ru.devprizrakk.voidbot.logging;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class DiscordEventManager extends InterfacedEventManager {

    @Override
    public void handle(GenericEvent event) {
        for (Object listener : getRegisteredListeners()) {
            try {
                dispatch(listener, event);
            } catch (Throwable throwable) {
                if (throwable instanceof InvocationTargetException ite && ite.getCause() != null) {
                    throwable = ite.getCause();
                }
                Logger.getLogger().log(LogType.ERROR, "DISCORD",
                        "Exception in listener " + listener.getClass().getSimpleName()
                                + " while handling " + event.getClass().getSimpleName(), throwable);
            }
        }
    }

    private void dispatch(Object listener, GenericEvent event) throws InvocationTargetException {
        Class<?> listenerClass = listener.getClass();
        for (Method method : listenerClass.getMethods()) {
            SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
            if (annotation == null) continue;

            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1) continue;
            if (!paramTypes[0].isInstance(event)) continue;

            try {
                method.setAccessible(true);
                method.invoke(listener, event);
            } catch (InvocationTargetException e) {
                throw e;
            } catch (IllegalAccessException e) {
                Logger.getLogger().log(LogType.ERROR, "DISCORD",
                        "Cannot access listener method " + method.getName() + " in " + listenerClass.getSimpleName(), e);
            } catch (Throwable t) {
                throw new InvocationTargetException(t);
            }
        }
    }
}