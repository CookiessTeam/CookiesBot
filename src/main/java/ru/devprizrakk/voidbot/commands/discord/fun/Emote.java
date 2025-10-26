package ru.devprizrakk.voidbot.commands.discord.fun;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandCategory;
import ru.devprizrakk.voidbot.commands.discord.loader.ICommand;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Emote extends UtilsManager implements ICommand {
    @Override
    public String getName() {
        return "emote";
    }

    @Override
    public String getDescription() {
        return getLangMessage("command/fun/emote.yml", "emote.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "action", getLangMessage("command/fun/emote.yml", "emote.description.choice"),true)
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.hug"), "hug")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.kiss"), "kiss")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.pat"), "pat")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.slap"), "slap")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.cuddle"), "cuddle")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.baka"), "baka")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.blush"), "blush")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.laugh"), "laugh")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.smile"), "smile")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.love"), "love")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.poke"), "poke")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.highfive"), "highfive")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.happy"), "happy")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.wave"), "wave")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.sleepy"), "sleepy")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.facepalm"), "facepalm")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.dance"), "dance")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.shocked"), "shocked")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.pout"), "pout")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.cry"), "cry")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.wink"), "wink")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.amazing"), "amazing")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.nom"), "nom")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.tickle"), "tickle")
                .addChoice(getLangMessage("command/fun/emote.yml", "emote.type.uwu"), "uwu")

        );
        options.add(new OptionData(OptionType.USER, "user", getLangMessage("command/system/help.yml","emote.description.option.user") , false));
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
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        String action = event.getOption("action").getAsString();
        String url = "https://kawaii.red/api/gif/" + action + "?token=" + getConfig().getProperty("other.kawaii-api.token");
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
            getLogger().error("command | emote","", e);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(getLangMessage("system.yml", "system.wrong-error.embed.title"));
            embed.setDescription(getLangMessage("system.yml", "system.wrong-error.embed.description")
                    .replace("%error-code%", "IOException | InterruptedException successful triggered"));
            embed.setFooter(getLangMessage("system.yml", "system.wrong-error.embed.footer"));
            event.replyEmbeds(embed.build()).queue();
            return;
        }
        //Парсим JSON через Gson
        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        //Получаем ссылку
        String gifUrl = json.get("response").getAsString();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getLangMessage("command/fun/emote.yml","emote.embed.title")
                .replace("%user-author%", event.getMember().getEffectiveName())
                .replace("%emote%", getLangMessage("command/fun/emote.yml", "emote.type." + action)));
        if (event.getOption("user") != null) {
            embed.setDescription(getLangMessage("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangMessage("command/fun/emote.yml", "emote.type-mentioned." + action)
                            .replace("%user-author%", userAuthor.getEffectiveName())
                            .replace("%user-mentioned%", userMentioned.getEffectiveName())));
        } else {
            embed.setDescription(getLangMessage("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangMessage("command/fun/emote.yml", "emote.type-not-mentioned." + action)
                            .replace("%user-author%", userAuthor.getEffectiveName())));
        }
        embed.setDescription(getDescription(userAuthor, userMentioned, action));
        embed.setFooter(getLangMessage("command/fun/emote.yml","emote.embed.footer"));
        embed.setImage(gifUrl);
        event.replyEmbeds(embed.build()).queue();
    }
    private String getDescription(User userAuthor, User userMentioned, String action) {
        String description;
        if (userMentioned != null) {
            description = getLangMessage("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangMessage("command/fun/emote.yml", "emote.type-mentioned." + action)
                            .replace("%user-author%", userAuthor.getAsMention())
                            .replace("%user-mentioned%", userMentioned.getAsMention()));
        } else {
            description = getLangMessage("command/fun/emote.yml", "emote.embed.description")
                    .replace("%emote-description%", getLangMessage("command/fun/emote.yml", "emote.type-not-mentioned." + action)
                            .replace("%user-author%", userAuthor.getAsMention()));
        }
        return description;
    }
}
