package ru.devprizrakk.voidbot.commands.music.lavalink;

import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.core.config.Config;
import ru.devprizrakk.voidbot.core.config.ConfigManager;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.core.utils.Utils;


import java.util.Map;
import java.util.Objects;


public class LavalinkManager {
    private LavalinkClient lavalinkClient = null;
    private JDAVoiceUpdateListener voiceUpdateListener;
    private final int SESSION_INVALID = 4006;

    public LavalinkManager() {
        try {
            lavalinkClient = new LavalinkClient(Helpers.getUserIdFromToken(Utils.getConfigManager().getConfig().getString("bot.token")));
            voiceUpdateListener = new JDAVoiceUpdateListener(lavalinkClient);
//            lavalinkClient.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());
            Logger.getLogger().log(LogType.INFO, "Music","===================");
            Logger.getLogger().log(LogType.INFO, "Music","Load lavalink nodes");
            Logger.getLogger().log(LogType.INFO, "Music","===================");
            registerLavalinkListeners();
            registerLavalinkNodes();
        } catch (InvalidTokenException e) {
            Logger.getLogger().log(LogType.ERROR, "Music","The provided token is invalid!", e);
            //System.exit(1);
        }
        catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "Music","An unexpected error occurred!", e);
            //System.exit(1);
        }
    }


    public LavalinkClient getLavalinkClient() {
        return lavalinkClient;
    }



    public JDAVoiceUpdateListener getVoiceUpdateListener() {
        return voiceUpdateListener;
    }

    public void registerLavalinkNodes() {
        ConfigManager configManager = new ConfigManager();
        Config config = configManager.getConfig(); // основной config.yml

        // lavalink.node.* лежит внутри общего Map
        Object rawNodes = config.getProperties().get("lavalink");
        if (!(rawNodes instanceof Map<?, ?> lavalinkSection)) {
            Logger.getLogger().log(LogType.WARN, "Music",
                    "No 'lavalink' section in config!");
            return;
        }

        Object rawNodeSection = lavalinkSection.get("node");
        if (!(rawNodeSection instanceof Map<?, ?> nodesMap)) {
            Logger.getLogger().log(LogType.WARN, "Music",
                    "No 'lavalink.node' section in config!");
            return;
        }

        if (nodesMap.isEmpty()) {
            Logger.getLogger().log(LogType.WARN, "Music",
                    "No Lavalink nodes found in config!");
            return;
        }

        for (Map.Entry<?, ?> entry : nodesMap.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();

            if (!(value instanceof Map<?, ?> nodeMap)) {
                Logger.getLogger().log(LogType.WARN, "Music",
                        "Skip lavalink node '" + key + "': not a map");
                continue;
            }

            // Достаём поля name/url/password
            String name = asString(nodeMap.get("name"));
            String url = asString(nodeMap.get("url"));
            String password = asString(nodeMap.get("password"));

            if (name == null || url == null || password == null) {
                Logger.getLogger().log(LogType.WARN, "Music",
                        "Skip lavalink node '" + key + "': missing name/url/password");
                continue;
            }

            lavalinkClient.addNode(
                    new NodeOptions.Builder()
                            .setName(name)
                            .setServerUri(url)
                            .setPassword(password)
                            .build()
            );

            Logger.getLogger().log(LogType.INFO, "Music",
                    "Registered Lavalink node: " + name);
        }
    }

    private String asString(Object o) {
        return o != null ? o.toString() : null;
    }

    private void registerLavalinkListeners() {


        lavalinkClient.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();
            Logger.getLogger().log(LogType.DEBUG, "Music","Node '" + node.getName() + "' is ready, session id is '" + event.getSessionId() + "'");
        });

        lavalinkClient.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();
            Logger.getLogger().log(LogType.DEBUG, "Music","Node '" + node.getName()+ "' has stats, current players: " + event.getPlayingPlayers()+ "/" + event.getPlayers() + "(link count " + event.getPlayers() + ")");
        });

        lavalinkClient.on(TrackStartEvent.class).subscribe((event) -> Logger.getLogger().log(LogType.DEBUG, "Music",event.getNode().getName() + ": track started: {}" + event.getTrack().getInfo()));

        lavalinkClient.on(TrackEndEvent.class).subscribe((event) -> Logger.getLogger().log(LogType.DEBUG, "Music",event.getNode().getName() + ": track ended: " + event.getTrack().getInfo() + "due to: " + event.getEndReason()));

        lavalinkClient.on(EmittedEvent.class).subscribe((event) -> Logger.getLogger().log(LogType.DEBUG, "Music","Node '" + event.getNode().getName() + "' emitted event: " + event));
        // Переподключение при ошибке сессии
        lavalinkClient.on(WebSocketClosedEvent.class).subscribe((event) -> {
            if (event.getCode() == SESSION_INVALID) {
                JDA jda = JDALoader.getJDA();
                final var guildId = event.getGuildId();
                final var guild = jda.getGuildById(guildId);

                if (guild == null) {
                    return;
                }

                final var connectedChannel = Objects.requireNonNull(guild.getSelfMember().getVoiceState()).getChannel();

                if (connectedChannel == null) {
                    return;
                }

                //jda.getDirectAudioController().reconnect(connectedChannel);
            }
        });
    }
}
