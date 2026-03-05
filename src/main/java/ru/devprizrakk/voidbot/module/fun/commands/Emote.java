package ru.devprizrakk.voidbot.module.fun.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.api.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.api.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.api.exceptions.discord.DisabledFunctionErrorEmbedFactory;
import ru.devprizrakk.voidbot.api.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.api.language.LangMessage;
import ru.devprizrakk.voidbot.api.logging.LogType;
import ru.devprizrakk.voidbot.api.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Emote extends BaseCommand {
    @Override
    public String getName() {
        return "emote";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Fun.Emote.FILE,
                LangMessage.Commands.Fun.Emote.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "action", getLangManager(event).getInfoLocale(
                LangMessage.Commands.Fun.Emote.FILE,
                LangMessage.Commands.Fun.Emote.Description.Option.CHOICE
                ), true)
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE,LangMessage.Commands.Fun.Emote.Type.HUG), "hug")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.KISS), "kiss")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.PAT), "pat")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.SLAP), "slap")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.CUDDLE), "cuddle")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.BAKA), "baka")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.BLUSH), "blush")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.LAUGH), "laugh")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.SMILE), "smile")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.LOVE), "love")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.POKE), "poke")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.HIGHFIVE), "highfive")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.HAPPY), "happy")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.WAVE), "wave")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.SLEEPY), "sleepy")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.FACEPALM), "facepalm")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.DANCE), "dance")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.SHOCKED), "shocked")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.POUT), "pout")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.CRY), "cry")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.WINK), "wink")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.AMAZING), "amazing")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.NOM), "nom")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.TICKLE), "tickle")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Type.UWU), "uwu")

        );
        options.add(new OptionData(OptionType.USER, "user", getLangManager(event).getInfoLocale(
                LangMessage.Commands.Fun.Emote.FILE,
                LangMessage.Commands.Fun.Emote.Description.Option.USER
        ), false));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    private static final Gson gson = new Gson();

    @Override
    public void onExecute() {
        if (!getConfigManager().getConfig().getBoolean("other.kawaii-api.enable")) {
            event.replyEmbeds(new DisabledFunctionErrorEmbedFactory(event).disabledFunction().build()).queue();
            return;
        }
        String action = Objects.requireNonNull(event.getOption("action")).getAsString();
        String url = "https://kawaii.red/api/gif/" + action + "?token=" + getConfigManager().getConfig().getString("other.kawaii-api.token");
        User userAuthor = event.getUser();
        User userMentioned = null;
        if (event.getOption("user") != null) {
            userMentioned = Objects.requireNonNull(event.getOption("user")).getAsUser();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Logger.getLogger().log(LogType.ERROR, "command", "", e);
            event.replyEmbeds(new WrongErrorEmbedFactory(event).wrongError("При опросе Kawaii API произошла!").build()).queue();
            return;
        }
        //Парсим JSON через Gson
        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        //Получаем ссылку
        String gifUrl = json.get("response").getAsString();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Fun.Emote.FILE,
                        LangMessage.Commands.Fun.Emote.Embed.TITLE
                )
                .replace("%user-author%", Objects.requireNonNull(event.getMember()).getEffectiveName())
                .replace("%emote%", getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Fun.Emote.FILE,
                        LangMessage.Commands.Fun.Emote.Type.BASE_PATH + action)
                ));
        if (event.getOption("user") != null) {
            assert userMentioned != null;
            embed.setDescription(getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Embed.DESCRIPTION)
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.TypeMentioned.BASE_PATH + action)
                            .replace("%user-author%", userAuthor.getEffectiveName())
                            .replace("%user-mentioned%", userMentioned.getEffectiveName())));
        } else {
            embed.setDescription(getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Embed.DESCRIPTION)
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.TypeNotMentioned.BASE_PATH + action)
                            .replace("%user-author%", userAuthor.getEffectiveName())));
        }
        embed.setDescription(getDescription(userAuthor, userMentioned, action));
        embed.setFooter(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Embed.FOOTER));
        embed.setImage(gifUrl);
        event.replyEmbeds(embed.build()).queue();
    }

    private String getDescription(User userAuthor, User userMentioned, String action) {
        String description;
        if (userMentioned != null) {
            description = getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.Embed.DESCRIPTION)
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.TypeMentioned.BASE_PATH + action)
                            .replace("%user-author%", userAuthor.getAsMention())
                            .replace("%user-mentioned%", userMentioned.getAsMention()));
        } else {
            description = getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Fun.Emote.FILE, "emote.embed.description")
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Emote.FILE, LangMessage.Commands.Fun.Emote.TypeNotMentioned.BASE_PATH + action)
                            .replace("%user-author%", userAuthor.getAsMention()));
        }
        return description;
    }
}
