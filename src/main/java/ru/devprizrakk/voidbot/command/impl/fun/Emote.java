package ru.devprizrakk.voidbot.command.impl.fun;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.exceptions.discord.DisabledFunctionErrorEmbedFactory;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class Emote extends BaseCommand {

    private static final Gson GSON = new Gson();
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    private static final List<String> EMOTE_TYPES = List.of(
            "hug", "kiss", "pat", "slap", "cuddle", "baka", "blush", "laugh",
            "smile", "love", "poke", "highfive", "happy", "wave", "sleepy",
            "facepalm", "dance", "shocked", "pout", "cry", "wink", "amazing",
            "nom", "tickle", "uwu"
    );

    @Override
    public String getName() {
        return "emote";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("emote.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        OptionData actionOption = new OptionData(OptionType.STRING, "action", getLangManager(event).getInfoLocale("emote.description.option.choice"
        ), true);

        for (String type : EMOTE_TYPES) {
            actionOption.addChoice(getEmoteTypeLocale(type), type);
        }

        OptionData userOption = new OptionData(OptionType.USER, "user", getLangManager(event).getInfoLocale("emote.description.option.user"
        ), false);

        return List.of(actionOption, userOption);
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void onExecute() {
        if (!getConfig().getBoolean("other.kawaii-api.enable")) {
            event.replyEmbeds(new DisabledFunctionErrorEmbedFactory(event).disabledFunction().build()).setEphemeral(true).queue();
            return;
        }

        String action = event.getOption("action").getAsString();
        User userAuthor = event.getUser();
        User userMentioned = event.getOption("user") != null ? event.getOption("user").getAsUser() : null;

        String gifUrl = fetchGifUrl(action);
        if (gifUrl == null) {
            new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getInfoLocale("emote.error.kawaii-api"));
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(buildTitle(action))
                .setDescription(buildDescription(userAuthor, userMentioned, action))
                .setFooter(getLangManager(event).getDescriptionLocale("emote.embed.footer"))
                .setImage(gifUrl);

        event.replyEmbeds(embed.build()).queue();
    }

    private String getEmoteTypeLocale(String type) {
        String path = "emote.type." + type;
        return getLangManager(event).getInfoLocale(path);
    }

    private String fetchGifUrl(String action) {
        String url = "https://kawaii.red/api/gif/" + action + "?token=" + getConfig().getString("other.kawaii-api.token");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
            return json.get("response").getAsString();
        } catch ( IOException e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Ошибка запроса к Kawaii API для action=" + action, e);
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            Logger.getLogger().log(LogType.ERROR, "command", "Запрос к Kawaii API прерван для action=" + action);
        }

        return null;
    }

    private String buildTitle(String action) {
        String authorName = Objects.requireNonNull(event.getMember()).getEffectiveName();
        String emoteName = getLangManager(event).getDescriptionLocale("emote.type." + action);

        return getLangManager(event).getDescriptionLocale("emote.embed.title")
                .replace("%user-author%", authorName)
                .replace("%emote%", emoteName);
    }

    private String buildDescription(User userAuthor, User userMentioned, String action) {
        String descriptionTemplate = getLangManager(event).getDescriptionLocale("emote.embed.description");

        if (userMentioned != null) {
            String emoteDescription = getLangManager(event).getDescriptionLocale("emote.type-mentioned." + action)
                    .replace("%user-author%", userAuthor.getAsMention())
                    .replace("%user-mentioned%", userMentioned.getAsMention());

            return descriptionTemplate.replace("%emote-description%", emoteDescription);
        }

        String emoteDescription = getLangManager(event).getDescriptionLocale("emote.type-not-mentioned." + action)
                .replace("%user-author%", userAuthor.getAsMention());

        return descriptionTemplate.replace("%emote-description%", emoteDescription);
    }
}
