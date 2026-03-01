package ru.devprizrakk.voidbot.module.music.lavalink;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;


public class VoiceHelper {
    public static boolean connectVoiceOfMember(Guild guild, Member member) {

        if (member == null || guild == null)
            return false;

        GuildVoiceState memberVoiceState = member.getVoiceState();
        GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();

        if (memberVoiceState == null || !memberVoiceState.inAudioChannel())
            return false;

        // Уже подключены
        if (selfVoiceState != null && selfVoiceState.inAudioChannel())
            return true;

        AudioChannel channel = memberVoiceState.getChannel();

        // Проверка прав
        assert channel != null;
        if (!guild.getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT))
            return false;

        if (!guild.getSelfMember().hasPermission(channel, Permission.VOICE_SPEAK))
            return false;

        guild.getAudioManager().openAudioConnection(channel);

        return true;
    }
    public static boolean disconnectVoice(Guild guild) {

        if (guild == null)
            return false;

        GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();

        // Бот не подключён
        if (selfVoiceState == null || !selfVoiceState.inAudioChannel())
            return false;

        guild.getAudioManager().closeAudioConnection();

        return true;
    }
    public static boolean reconnectVoice(Guild guild, Member member) {
        if (disconnectVoice(guild)) {
            return connectVoiceOfMember(guild, member);
        } else {
            return false;
        }
    }
}
