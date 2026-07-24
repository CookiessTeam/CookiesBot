package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.util.Collections;

public class FeedbackListener extends ListenerAdapter {

    private static final String MODAL_IDEA = "fb_modal_idea";
    private static final String MODAL_MOD = "fb_modal_mod:";
    private static final String MODAL_USER = "fb_modal_user:";
    private static final String SELECT_MOD = "fb_select_mod";
    private static final String SELECT_USER = "fb_select_user";

    private static final String BTN_APPROVE = "fb_dec_a:";
    private static final String BTN_REJECT = "fb_dec_r:";
    private static final String MODAL_REJECT = "fb_rej_m:";

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getComponentId();
        switch (id) {
            case "fb_idea" -> openIdeaModal(event);
            case "fb_mod" -> openUserSelect(event, SELECT_MOD, "feedback.select.mod.placeholder");
            case "fb_user" -> openUserSelect(event, SELECT_USER, "feedback.select.user.placeholder");
            default -> {
                if (id.startsWith(BTN_APPROVE)) {
                    handleApprove(event, parseId(id, BTN_APPROVE));
                } else if (id.startsWith(BTN_REJECT)) {
                    openRejectModal(event, parseId(id, BTN_REJECT));
                }
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
        Modal modal = Modal.create(modalId, getModalTitle(isMod ? "feedback.modal.mod.title" : "feedback.modal.user.title"))
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
        } else if (id.startsWith(MODAL_REJECT)) {
            long authorId = parseId(id, MODAL_REJECT);
            handleReject(event, authorId);
        }
    }

    // ---------- submit (idea/report) ----------

    private void openIdeaModal(ButtonInteractionEvent event) {
        Modal modal = Modal.create(MODAL_IDEA, getModalTitle("feedback.modal.idea.title"))
                .addComponents(buildReasonInput())
                .build();
        event.replyModal(modal).queue();
    }

    private void openUserSelect(ButtonInteractionEvent event, String selectId, String placeholderKey) {
        EntitySelectMenu menu = EntitySelectMenu.create(selectId, EntitySelectMenu.SelectTarget.USER)
                .setPlaceholder(getInfo(placeholderKey))
                .setRequiredRange(1, 1)
                .build();
        event.reply(getInfo("feedback.select.prompt"))
                .addComponents(ActionRow.of(menu))
                .setEphemeral(true)
                .queue();
    }

    private void handleIdea(ModalInteractionEvent event) {
        String text = readReason(event);
        if (text == null) {
            event.reply(getInfo("feedback.error.empty")).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = baseEmbed("feedback.embed.idea.title", "feedback.embed.idea.description");
        embed.addField(getInfo("feedback.field.author"), event.getUser().getAsMention(), true);
        embed.addField(getInfo("feedback.field.content"), text, false);
        embed.setFooter(getInfo("feedback.footer-status.pending"));

        long authorId = event.getUser().getIdLong();
        if (!sendToChannelWithButtons(event.getGuild(), "channel.feedback.ideas", embed, authorId)) {
            event.reply(getInfo("feedback.error.channel")).setEphemeral(true).queue();
            return;
        }
        event.reply(getInfo("feedback.success")).setEphemeral(true).queue();
    }

    private void handleReport(ModalInteractionEvent event, boolean isUserReport, long targetId) {
        String text = readReason(event);
        if (text == null) {
            event.reply(getInfo("feedback.error.empty")).setEphemeral(true).queue();
            return;
        }

        String titleKey = isUserReport ? "feedback.embed.user.title" : "feedback.embed.mod.title";
        String descKey = isUserReport ? "feedback.embed.user.description" : "feedback.embed.mod.description";
        String channelKey = isUserReport ? "channel.feedback.user" : "channel.feedback.moderation";

        EmbedBuilder embed = baseEmbed(titleKey, descKey);
        embed.addField(getInfo("feedback.field.author"), event.getUser().getAsMention(), true);
        embed.addField(getInfo("feedback.field.target"), "<@" + targetId + ">", true);
        embed.addField(getInfo("feedback.field.content"), text, false);
        embed.setFooter(getInfo("feedback.footer-status.pending"));

        long authorId = event.getUser().getIdLong();
        if (!sendToChannelWithButtons(event.getGuild(), channelKey, embed, authorId)) {
            event.reply(getInfo("feedback.error.channel")).setEphemeral(true).queue();
            return;
        }
        event.reply(getInfo("feedback.success")).setEphemeral(true).queue();
    }

    // ---------- moderation: approve / reject ----------

    private void handleApprove(ButtonInteractionEvent event, long authorId) {
        if (!hasManagePermission(event)) {
            event.reply(getInfo("feedback.error.no-permission")).setEphemeral(true).queue();
            return;
        }

        var msg = event.getMessage();
        if (msg == null) {
            event.reply(getInfo("feedback.error.no-message")).setEphemeral(true).queue();
            return;
        }

        User moderator = event.getUser();
        EmbedBuilder embed = rebuildFrom(msg, new Color(0x57F287), "feedback.status.approved");
        embed.clearFields();
        appendModerationFields(msg, embed, moderator, null, true);

        msg.editMessageEmbeds(embed.build()).setComponents(Collections.emptyList()).queue();
        event.reply(getInfo("feedback.notice.approved")).setEphemeral(true).queue();

        notifyAuthor(event.getJDA(), event.getGuild(), authorId, moderator, true, null);
    }

    private void openRejectModal(ButtonInteractionEvent event, long authorId) {
        if (!hasManagePermission(event)) {
            event.reply(getInfo("feedback.error.no-permission")).setEphemeral(true).queue();
            return;
        }

        Modal modal = Modal.create(MODAL_REJECT + authorId, getModalTitle("feedback.modal.reject.title"))
                .addComponents(buildRejectReasonInput())
                .build();
        event.replyModal(modal).queue();
    }

    private void handleReject(ModalInteractionEvent event, long authorId) {
        String reason = readRejectReason(event);
        if (reason == null) {
            event.reply(getInfo("feedback.error.empty")).setEphemeral(true).queue();
            return;
        }

        var msg = event.getMessage();
        if (msg == null) {
            event.reply(getInfo("feedback.error.no-message")).setEphemeral(true).queue();
            return;
        }

        User moderator = event.getUser();
        EmbedBuilder embed = rebuildFrom(msg, new Color(0xED4245), "feedback.status.rejected");
        embed.clearFields();
        appendModerationFields(msg, embed, moderator, reason, false);

        msg.editMessageEmbeds(embed.build()).setComponents(Collections.emptyList()).queue();
        event.reply(getInfo("feedback.notice.rejected")).setEphemeral(true).queue();

        notifyAuthor(event.getJDA(), event.getGuild(), authorId, moderator, false, reason);
    }

    private boolean hasManagePermission(ButtonInteractionEvent event) {
        var member = event.getMember();
        return member != null && member.hasPermission(Permission.MANAGE_SERVER);
    }

    private EmbedBuilder rebuildFrom(Message source, Color color, String statusKey) {
        EmbedBuilder embed = new EmbedBuilder(source.getEmbeds().isEmpty() ? null : source.getEmbeds().getFirst());
        embed.setColor(color);

        String status = getInfo(statusKey);
        if (status != null && !status.isEmpty()) {
            embed.setTitle(status);
        }

        embed.setFooter(getInfo("feedback.footer-status.decided"));
        return embed;
    }

    private void appendModerationFields(Message source, EmbedBuilder embed, User moderator, String reason, boolean approved) {
        if (!source.getEmbeds().isEmpty()) {
            var original = source.getEmbeds().getFirst();
            for (var f : original.getFields()) {
                embed.addField(f);
            }
        }

        embed.addField(getInfo(approved ? "feedback.field.approved-by" : "feedback.field.rejected-by"), moderator.getAsMention(), true);
        if (reason != null && !reason.isBlank()) {
            embed.addField(getInfo("feedback.field.rejected-reason"), reason, false);
        }
    }

    private void notifyAuthor(JDA jda, Guild guild, long authorId, User moderator, boolean approved, String reason) {
        if (authorId <= 0) return;

        try {
            jda.retrieveUserById(authorId).queue(
                    u -> u.openPrivateChannel().queue(
                            pc -> pc.sendMessageEmbeds(buildDmEmbed(guild, moderator, approved, reason).build())
                                    .queue(_ -> {
                                    }, _ -> {
                                    }),
                            _ -> {
                            }
                    ),
                    _ -> {
                    }
            );
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.WARN, "FEEDBACK", "Failed to DM author " + authorId, e);
        }
    }

    private EmbedBuilder buildDmEmbed(Guild guild, User moderator, boolean approved, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(approved ? new Color(0x57F287) : new Color(0xED4245));
        embed.setTitle(getInfo(approved ? "feedback.dm.approved.title" : "feedback.dm.rejected.title"));
        String desc = getInfo(approved ? "feedback.dm.approved.description" : "feedback.dm.rejected.description")
                .replace("%server%", guild == null ? "?" : guild.getName())
                .replace("%moderator%", moderator.getAsMention());
        embed.setDescription(desc);
        embed.addField(getInfo("feedback.field.moderator"), moderator.getAsMention(), true);
        if (reason != null && !reason.isBlank()) {
            embed.addField(getInfo("feedback.field.rejected-reason"), reason, false);
        }
        embed.setFooter(getInfo("feedback.dm.footer"));
        return embed;
    }

    // ---------- embeds / send ----------

    private EmbedBuilder baseEmbed(String titleKey, String descKey) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getInfo(titleKey));
        embed.setDescription(getInfo(descKey));
        return embed;
    }

    private boolean sendToChannelWithButtons(Guild guild, String configKey, EmbedBuilder embed, long authorId) {
        if (guild == null) return false;

        String channelId = Utils.getConfig().getString(configKey, "");
        if (channelId == null || channelId.isEmpty()) {
            Logger.getLogger().log(LogType.WARN, "FEEDBACK", "Channel key not set: " + configKey);
            return false;
        }

        TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            Logger.getLogger().log(LogType.WARN, "FEEDBACK", "Channel not found by id: " + channelId);
            return false;
        }

        Button approve = Button.of(ButtonStyle.SUCCESS, BTN_APPROVE + authorId,
                getInfo("feedback.button.approve"), Emoji.fromUnicode("✅"));
        Button reject = Button.of(ButtonStyle.DANGER, BTN_REJECT + authorId,
                getInfo("feedback.button.reject"), Emoji.fromUnicode("❌"));

        channel.sendMessageEmbeds(embed.build())
                .addComponents(ActionRow.of(approve, reject))
                .queue();
        return true;
    }

    // ---------- inputs ----------

    private Label buildReasonInput() {
        TextInput input = TextInput.create("fb_reason", TextInputStyle.PARAGRAPH)
                .setPlaceholder(getInfo("feedback.modal.reason.placeholder"))
                .setMinLength(10)
                .setMaxLength(2000)
                .setRequired(true)
                .build();
        return Label.of(getInfo("feedback.modal.reason.label"), input);
    }

    private Label buildRejectReasonInput() {
        TextInput input = TextInput.create("fb_reject_reason", TextInputStyle.PARAGRAPH)
                .setPlaceholder(getInfo("feedback.modal.reason.reject.placeholder"))
                .setMinLength(5)
                .setMaxLength(1000)
                .setRequired(true)
                .build();
        return Label.of(getInfo("feedback.modal.reason.reject.label"), input);
    }

    private String readReason(ModalInteractionEvent event) {
        var mapping = event.getValue("fb_reason");
        String text = mapping == null ? null : mapping.getAsString();
        return (text == null || text.isBlank()) ? null : text;
    }

    private String readRejectReason(ModalInteractionEvent event) {
        var mapping = event.getValue("fb_reject_reason");
        String text = mapping == null ? null : mapping.getAsString();
        return (text == null || text.isBlank()) ? null : text;
    }

    // ---------- utils ----------

    private long parseTargetId(String modalId, String prefix) {
        return parseId(modalId, prefix);
    }

    private long parseId(String fullId, String prefix) {
        try {
            return Long.parseLong(fullId.substring(prefix.length()));
        } catch ( NumberFormatException e ) {
            return 0L;
        }
    }

    private String getModalTitle(String key) {
        return clip(getInfo(key), 45);
    }

    private String clip(String s, int max) {
        if (s == null || s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 1)) + "…";
    }

    private String getInfo(String key) {
        return Utils.getLangManager().getInfoLocale(key);
    }
}