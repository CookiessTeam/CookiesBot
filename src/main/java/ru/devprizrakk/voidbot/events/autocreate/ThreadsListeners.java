package ru.devprizrakk.voidbot.events.autocreate;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.language.LangMessage;
import ru.devprizrakk.voidbot.utils.Utils;

public class ThreadsListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);

        if (event.getMessage().getAuthor().isBot()) return;

        // Checklist
        final String checklist = Utils.getConfig().getString("channel.checklist");
        final String news = Utils.getConfig().getString("channel.news");
        if (checklist != null && !checklist.isEmpty() && event.getChannel().getId().equals(checklist)) {
            sendThreadsEmbed(event,
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.Checklist.FILE,
                            LangMessage.Event.Threads.Checklist.TITLE
                    ),
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.Checklist.FILE,
                            LangMessage.Event.Threads.Checklist.Embed.TITLE
                    ),
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.Checklist.FILE,
                            LangMessage.Event.Threads.Checklist.Embed.DESCRIPTION
                    ),
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.Checklist.FILE,
                            LangMessage.Event.Threads.Checklist.Embed.FOOTER
                    ));
        }

        if (news != null && !news.isEmpty() && event.getChannel().getId().equals(news)) {
            sendThreadsEmbed(event,
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.News.FILE,
                            LangMessage.Event.Threads.News.TITLE
                    ),
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.News.FILE,
                            LangMessage.Event.Threads.News.Embed.TITLE
                    ),
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.News.FILE,
                            LangMessage.Event.Threads.News.Embed.DESCRIPTION
                    ),
                    Utils.getLangManager().getInfoLocale(
                            LangMessage.Event.Threads.News.FILE,
                            LangMessage.Event.Threads.News.Embed.FOOTER
                    ));
        }
    }

    public void sendThreadsEmbed(MessageReceivedEvent event, String threadTitle, String embedTitle, String embedDesc, String embedFooter) {
        event.getMessage().createThreadChannel(threadTitle)
                .queue(threadsChannel -> {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle(embedTitle);
                            embed.setDescription(embedDesc);
                            embed.setFooter(embedFooter);
                            threadsChannel.sendMessageEmbeds(embed.build()).queue();
                        }
                );
    }
}
