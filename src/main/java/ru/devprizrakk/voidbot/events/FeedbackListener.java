package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.core.config.Config;
import ru.devprizrakk.voidbot.core.language.LangMessage;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.core.utils.Utils;

import java.awt.Color;

public class FeedbackListener extends ListenerAdapter {

    private static final String MODAL_IDEA = "fb_modal_idea";
    private static final String MODAL_MOD = "fb_modal_mod:";
    private static final String MODAL_USER = "fb_modal_user:";
    private static final String SELECT_MOD = "fb_select_mod";
    private static final String SELECT_USER = "fb_select_user";

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getComponentId();
        switch (id) {
            case "fb_idea" -> openIdeaModal(event);
            case "fb_mod" -> openUserSelect(event, SELECT_MOD, "fb.select.mod.placeholder");
            case "fb_user" -> openUserSelect(event, SELECT_USER, "fb.select.user.placeholder");
            default -> {
            }
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        String id = event.getComponentId();
        if (!SELECT_MOD.equals(id) && !SELECT_USER.equals(id)) {
            return;
        }
        if (event.getValues().isEmpty()) {
            return;
        }
        long targetId = event.getMentions().getUsers().stream()
                .findFirst()
                .map(User::getIdLong)
                .orElse(0L);
        if (targetId == 0L) {
            return;
        }

        boolean isMod = SELECT_MOD.equals(id);
        String modalId = (isMod ? MODAL_MOD : MODAL_USER) + targetId;
        Modal modal = Modal.create(modalId, getModalTitle(isMod ? "fb.modal.mod.title" : "fb.modal.user.title"))
                .addComponents(buildReasonInput())
                .build();
        event.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String id = event.getModalId();
        if (id.equals(MODAL_IDEA)) {
            handleIdea(event);
        } else if (id.startsWith(MODAL_MOD)) {
            long targetId = parseTargetId(id, MODAL_MOD);
            handleReport(event, false, targetId);
        } else if (id.startsWith(MODAL_USER)) {
            long targetId = parseTargetId(id, MODAL_USER);
            handleReport(event, true, targetId);
        }
    }

    private void openIdeaModal(ButtonInteractionEvent event) {
        Modal modal = Modal.create(MODAL_IDEA, getModalTitle("fb.modal.idea.title"))
                .addComponents(buildReasonInput())
                .build();
        event.replyModal(modal).queue();
    }

    private void openUserSelect(ButtonInteractionEvent event, String selectId, String placeholderKey) {
        EntitySelectMenu menu = EntitySelectMenu.create(selectId, EntitySelectMenu.SelectTarget.USER)
                .setPlaceholder(getInfo(LangMessage.Commands.System.Feedback.FILE, placeholderKey))
                .setRequiredRange(1, 1)
                .build();
        event.reply(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.select.prompt"))
                .addComponents(ActionRow.of(menu))
                .setEphemeral(true)
                .queue();
    }

    private void handleIdea(ModalInteractionEvent event) {
        var mapping = event.getValue("fb_reason");
        String text = mapping == null ? null : mapping.getAsString();
        if (text == null || text.isBlank()) {
            event.reply(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.error.empty")).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = baseEmbed("fb.embed.idea.title", "fb.embed.idea.description");
        embed.addField(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.field.author"),
                event.getUser().getAsMention(), true);
        embed.addField(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.field.content"), text, false);

        if (!sendToChannel(event.getGuild(), "channel.feedback.ideas", embed)) {
            event.reply(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.error.channel")).setEphemeral(true).queue();
            return;
        }
        event.reply(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.success")).setEphemeral(true).queue();
    }

    private void handleReport(ModalInteractionEvent event, boolean isUserReport, long targetId) {
        var mapping = event.getValue("fb_reason");
        String text = mapping == null ? null : mapping.getAsString();
        if (text == null || text.isBlank()) {
            event.reply(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.error.empty")).setEphemeral(true).queue();
            return;
        }

        String titleKey = isUserReport ? "fb.embed.user.title" : "fb.embed.mod.title";
        String descKey = isUserReport ? "fb.embed.user.description" : "fb.embed.mod.description";
        String channelKey = isUserReport ? "channel.feedback.user" : "channel.feedback.moderation";

        EmbedBuilder embed = baseEmbed(titleKey, descKey);
        embed.addField(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.field.author"),
                event.getUser().getAsMention(), true);
        embed.addField(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.field.target"),
                "<@" + targetId + ">", true);
        embed.addField(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.field.content"), text, false);

        if (!sendToChannel(event.getGuild(), channelKey, embed)) {
            event.reply(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.error.channel")).setEphemeral(true).queue();
            return;
        }
        event.reply(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.success")).setEphemeral(true).queue();
    }

    private EmbedBuilder baseEmbed(String titleKey, String descKey) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getInfo(LangMessage.Commands.System.Feedback.FILE, titleKey));
        embed.setDescription(getInfo(LangMessage.Commands.System.Feedback.FILE, descKey));
        return embed;
    }

    private boolean sendToChannel(Guild guild, String configKey, EmbedBuilder embed) {
        if (guild == null) {
            return false;
        }
        String channelId = config().getString(configKey, "");
        if (channelId == null || channelId.isEmpty()) {
            Logger.getLogger().log(LogType.WARN, "FEEDBACK", "Channel key not set: " + configKey);
            return false;
        }
        TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            Logger.getLogger().log(LogType.WARN, "FEEDBACK", "Channel not found by id: " + channelId);
            return false;
        }
        channel.sendMessageEmbeds(embed.build()).queue();
        return true;
    }

    private Label buildReasonInput() {
        TextInput input = TextInput.create("fb_reason", TextInputStyle.PARAGRAPH)
                .setPlaceholder(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.modal.reason.placeholder"))
                .setMinLength(10)
                .setMaxLength(2000)
                .setRequired(true)
                .build();
        return Label.of(getInfo(LangMessage.Commands.System.Feedback.FILE, "fb.modal.reason.label"), input);
    }

    private long parseTargetId(String modalId, String prefix) {
        try {
            return Long.parseLong(modalId.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String getModalTitle(String key) {
        return getInfo(LangMessage.Commands.System.Feedback.FILE, key);
    }

    private String getInfo(String file, String key) {
        return Utils.getLangManager().getInfoLocale(file, key);
    }

    private Config config() {
        return Utils.getConfigManager().getConfig();
    }
}