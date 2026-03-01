package ru.devprizrakk.voidbot.module.music.command;

import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.api.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.api.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.api.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.module.music.MusicMain;
import ru.devprizrakk.voidbot.module.music.lavalink.AudioLoader;
import ru.devprizrakk.voidbot.module.music.lavalink.VoiceHelper;
import ru.devprizrakk.voidbot.module.music.lavalink.media.MediaService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Play extends BaseCommand {


    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("command/music/play.yml", "play.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "song", getLangManager(event).getInfoLocale("command/music/play.yml", "play.description.option.song"), true));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            event.reply(getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.no-dm"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        Guild guild = event.getGuild();

        Member member = event.getMember();
        assert member != null;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            event.reply(getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.error.no-found-voice")
                    .replace("%voiceChannel%", Objects.requireNonNull(memberVoiceState.getChannel()).getAsMention())).queue();
            return;
        }

        String name = Objects.requireNonNull(event.getOption("song")).getAsString();
        if (!name.startsWith("http")) {
            name = "ytsearch:" + name; // Поиск на YouTube, если это не ссылка
        } else {
            try {
                MediaService mediaService = new MediaService();
                mediaService.get(name);
            } catch (IllegalArgumentException e) {
                MediaService mediaService = new MediaService();
                String platforms = mediaService.getAvailablePlatforms().stream().map(item -> "`" + item + "`").collect(Collectors.joining(", "));
                event.reply(
                        getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.error.no-support-platform")
                                .replace("%music-platform-support%", platforms)
                ).queue();
                return;
            }
        }

        event.deferReply().queue();

        if (!VoiceHelper.connectVoiceOfMember(guild, member)) {
            event.reply(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.error.no-found-me")
            ).queue();
        }

        Link link = JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(Objects.requireNonNull(event.getGuild()).getIdLong());
        var mngr = MusicMain.getOrCreateMusicManager(event.getGuild().getIdLong());

        link.loadItem(name).subscribe(new AudioLoader(event, mngr, JDALoader.getLavalinkManager().getLavalinkClient()));

    }

}