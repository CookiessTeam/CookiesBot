package ru.devprizrakk.voidbot.lavalink;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public class VoiceHelper {

    public static boolean connectToMemberVoice(Guild guild, Member member) {
        if (guild == null || member == null)
            return false;

        AudioChannel targetChannel = getMemberChannel(member);
        if (targetChannel == null)
            return false;

        if (isSelfInChannel(guild))
            return true;

        return openConnection(guild, targetChannel);
    }

    public static boolean disconnectVoice(Guild guild) {
        if (guild == null || !isSelfInChannel(guild))
            return false;

        guild.getAudioManager().closeAudioConnection();
        return true;
    }

    public static boolean reconnectVoice(Guild guild, Member member) {
        if (guild == null || member == null)
            return false;

        AudioChannel targetChannel = getMemberChannel(member);
        if (targetChannel == null)
            return false;

        if (isSelfInSameChannel(guild, targetChannel))
            return true;

        if (isSelfInChannel(guild) && !disconnectVoice(guild))
            return false;

        return openConnection(guild, targetChannel);
    }

    private static AudioChannel getMemberChannel(Member member) {
        GuildVoiceState voiceState = member.getVoiceState();
        return (voiceState != null && voiceState.inAudioChannel()) ? voiceState.getChannel() : null;
    }

    private static boolean isSelfInChannel(Guild guild) {
        GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
        return selfVoiceState != null && selfVoiceState.inAudioChannel();
    }

    private static boolean isSelfInSameChannel(Guild guild, AudioChannel channel) {
        if (!isSelfInChannel(guild))
            return false;

        AudioChannel selfChannel = guild.getSelfMember().getVoiceState().getChannel();
        return selfChannel.getIdLong() == channel.getIdLong();
    }

    private static boolean hasConnectPermissions(Guild guild, AudioChannel channel) {
        Member selfMember = guild.getSelfMember();
        return selfMember.hasPermission(channel, Permission.VOICE_CONNECT)
                && selfMember.hasPermission(channel, Permission.VOICE_SPEAK);
    }

    private static boolean openConnection(Guild guild, AudioChannel channel) {
        if (!hasConnectPermissions(guild, channel))
            return false;

        guild.getAudioManager().openAudioConnection(channel);
        return true;
    }
}