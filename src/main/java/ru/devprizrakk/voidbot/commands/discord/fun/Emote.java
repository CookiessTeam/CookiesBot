package ru.devprizrakk.voidbot.commands.discord.fun;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.commands.discord.loader.BaseCommand;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandCategory;
import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Emote extends BaseCommand {
    @Override
    public String getName() {
        return "emote";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "action", getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.description.option.choice"),true)
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.hug"), "hug")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.kiss"), "kiss")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.pat"), "pat")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.slap"), "slap")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.cuddle"), "cuddle")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.baka"), "baka")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.blush"), "blush")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.laugh"), "laugh")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.smile"), "smile")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.love"), "love")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.poke"), "poke")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.highfive"), "highfive")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.happy"), "happy")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.wave"), "wave")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.sleepy"), "sleepy")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.facepalm"), "facepalm")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.dance"), "dance")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.shocked"), "shocked")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.pout"), "pout")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.cry"), "cry")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.wink"), "wink")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.amazing"), "amazing")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.nom"), "nom")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.tickle"), "tickle")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.type.uwu"), "uwu")

        );
        options.add(new OptionData(OptionType.USER, "user", getLangManager(event).getInfoLocale("command/fun/emote.yml", "emote.description.option.user") , false));
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
            event.replyEmbeds(getErrorMessage(event).disableFunction().build()).queue();
            return;
        }
        String action = event.getOption("action").getAsString();
        String url = "https://kawaii.red/api/gif/" + action + "?token=" + getConfigManager().getConfig().getString("other.kawaii-api.token");
        User userAuthor = event.getUser();
        User userMentioned = null;
        if (event.getOption("user") != null) {
            userMentioned = event.getOption("user").getAsUser();
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
            Logger.getLogger().log(LogType.ERROR,"command","emote","", e);
            event.replyEmbeds(getErrorMessage(event).wrongError("При опросе Kawaii API произошла!").build()).queue();
            return;
        }
        //Парсим JSON через Gson
        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        //Получаем ссылку
        String gifUrl = json.get("response").getAsString();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.embed.title")
                .replace("%user-author%", event.getMember().getEffectiveName())
                .replace("%emote%", getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.type." + action)));
        if (event.getOption("user") != null) {
            embed.setDescription(getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.type-mentioned." + action)
                            .replace("%user-author%", userAuthor.getEffectiveName())
                            .replace("%user-mentioned%", userMentioned.getEffectiveName())));
        } else {
            embed.setDescription(getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.type-not-mentioned." + action)
                            .replace("%user-author%", userAuthor.getEffectiveName())));
        }
        embed.setDescription(getDescription(userAuthor, userMentioned, action));
        embed.setFooter(getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.embed.footer"));
        embed.setImage(gifUrl);
        event.replyEmbeds(embed.build()).queue();
    }
    private String getDescription(User userAuthor, User userMentioned, String action) {
        String description;
        if (userMentioned != null) {
            description = getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.type-mentioned." + action)
                            .replace("%user-author%", userAuthor.getAsMention())
                            .replace("%user-mentioned%", userMentioned.getAsMention()));
        } else {
            description = getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangManager(event).getDescriptionLocale("command/fun/emote.yml", "emote.type-not-mentioned." + action)
                            .replace("%user-author%", userAuthor.getAsMention()));
        }
        return description;
    }
}
