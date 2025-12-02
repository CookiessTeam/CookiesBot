package ru.devprizrakk.voidbot.events.minecraft;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import ru.devprizrakk.voidbot.core.Utils;
import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class ChatListener extends Utils {
    private static WebSocketClient client;
    private static TextChannel bridgeChannel;
    public void init() throws URISyntaxException {
        // Запуск WebSocket клиента
        client = new WebSocketClient(new URI(getConfigManager().getConfig().getString("other.websocket-api"))) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                Logger.getLogger().log(LogType.INFO,"event", "Подключение к чату через веб-сокет");
            }

            @Override
            public void onMessage(String message) {
                // MC -> Discord
                if (bridgeChannel != null) {
                    bridgeChannel.sendMessage(message).queue();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Logger.getLogger().log(LogType.INFO,"event", "Отключение от вебсокета");
            }

            @Override
            public void onError(Exception ex) {
                Logger.getLogger().log(LogType.ERROR,"event","minecraft", "ошибка!)!))!)!)!)", ex);
            }
        };

        client.connect();
    }
}
