package ru.devprizrakk.voidbot.events.voiceroom;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.language.LangHelper;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class VoiceRoomListener extends ListenerAdapter {

    private static final String SUFFIX = " - voice";

    // ---- button ids ----
    static final String BTN_RENAME = "vr_rename";
    static final String BTN_LIMIT = "vr_limit";
    static final String BTN_LOCK = "vr_lock";
    static final String BTN_UNLOCK = "vr_unlock";
    static final String BTN_DENY = "vr_deny";
    static final String BTN_GRANT = "vr_grant";
    static final String BTN_KICK = "vr_kick";
    static final String BTN_MUTE = "vr_mute";
    static final String BTN_UNMUTE = "vr_unmute";
    static final String BTN_TRANSFER = "vr_transfer";

    // ---- select ids ----
    static final String SEL_GRANT = "vr_sel_grant";
    static final String SEL_DENY = "vr_sel_deny";
    static final String SEL_KICK = "vr_sel_kick";
    static final String SEL_MUTE = "vr_sel_mute";
    static final String SEL_UNMUTE = "vr_sel_unmute";
    static final String SEL_TRANSFER = "vr_sel_transfer";

    // ---- modal ids ----
    private static final String MOD_RENAME = "vr_mod_rename";
    private static final String MOD_LIMIT = "vr_mod_limit";
    private static final String IN_RENAME = "vr_in_rename";
    private static final String IN_LIMIT = "vr_in_limit";

    // ---- ownership state ----
    private static final ConcurrentHashMap<Long, String> channelOwner = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> ownerChannel = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, ScheduledFuture<?>> pendingDeletions = new ConcurrentHashMap<>();
    // Запоминаем, кого владелец замьютил в своей комнате, чтобы автоматически снять
    // сервер-мьют при выходе из комнаты. Ключ — "guildId:channelId:userId".
    private static final ConcurrentHashMap<String, Boolean> mutedMembers = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1, r -> {
                Thread t = new Thread(r, "voiceroom-scheduler");
                t.setDaemon(true);
                return t;
            });

    // =====================================================
    //                      CONFIG
    // =====================================================

    public static boolean isEnabled() {
        return Utils.getConfig().getBoolean("voice-room.enabled", true);
    }

    private static long getCreateChannelId(Guild guild) {
        String raw = Utils.getConfig().getString("voice-room.create-channel", "");
        if (raw == null || raw.isEmpty() || raw.startsWith("fill_id_")) return -1L;
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    private static int getCountdownSeconds() {
        int s = Utils.getConfig().getInt("voice-room.countdown-seconds", 30);
        return s <= 0 ? 30 : s;
    }

    private static int getDefaultLimit() {
        int l = Utils.getConfig().getInt("voice-room.default-user-limit", 0);
        if (l < 0) l = 0;
        if (l > 99) l = 99;
        return l;
    }

    // =====================================================
    //                  BUTTON ROWS BUILDER
    // =====================================================

    public static List<ActionRow> buildControlRows(LangHelper lang) {
        Button rename = btn(ButtonStyle.PRIMARY, BTN_RENAME, lang, "voiceroom.button.rename", "✏️");
        Button limit = btn(ButtonStyle.SECONDARY, BTN_LIMIT, lang, "voiceroom.button.limit", "👥");
        Button lock = btn(ButtonStyle.SECONDARY, BTN_LOCK, lang, "voiceroom.button.lock", "🔒");
        Button unlock = btn(ButtonStyle.SUCCESS, BTN_UNLOCK, lang, "voiceroom.button.unlock", "🔓");
        Button deny = btn(ButtonStyle.DANGER, BTN_DENY, lang, "voiceroom.button.deny", "🚫");

        Button grant = btn(ButtonStyle.SUCCESS, BTN_GRANT, lang, "voiceroom.button.grant", "🗝");
        Button kick = btn(ButtonStyle.DANGER, BTN_KICK, lang, "voiceroom.button.kick", "⚡");
        Button mute = btn(ButtonStyle.SECONDARY, BTN_MUTE, lang, "voiceroom.button.mute", "🔇");
        Button unmute = btn(ButtonStyle.SUCCESS, BTN_UNMUTE, lang, "voiceroom.button.unmute", "🎤");
        Button transfer = btn(ButtonStyle.PRIMARY, BTN_TRANSFER, lang, "voiceroom.button.transfer", "🔁");

        return List.of(
                ActionRow.of(rename, limit, lock, unlock, deny),
                ActionRow.of(grant, kick, mute, unmute, transfer)
        );
    }

    private static Button btn(ButtonStyle style, String id, LangHelper lang, String key, String emoji) {
        return Button.of(style, id, lang.getInfoLocale(key), Emoji.fromUnicode(emoji));
    }

    // =====================================================
    //                  VOICE STATE EVENTS
    // =====================================================

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (!isEnabled()) return;

        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) return;

        AudioChannelUnion joined = event.getChannelJoined();
        AudioChannelUnion left = event.getChannelLeft();

        long createId = getCreateChannelId(event.getGuild());

        if (joined != null && createId > 0 && joined.getIdLong() == createId) {
            createPrivateRoom(member, event.getGuild(), joined);
            return;
        }

        if (left != null && channelOwner.containsKey(left.getIdLong())) {
            cancelDeletion(left.getIdLong());
            // Если замьюченный в этой комнате участник уходит — снимаем сервер-мьют,
            // чтобы мьют оставался привязан только к комнате.
            String mkey = event.getGuild().getIdLong() + ":" + left.getIdLong() + ":" + member.getIdLong();
            if (mutedMembers.remove(mkey) != null) {
                event.getGuild().mute(member, false).queue(_ -> { }, _ -> { });
            }
            if (left.getMembers().isEmpty() || onlyBotsOrLeft(left, member)) {
                scheduleDeletion(event.getGuild().getIdLong(), left.getIdLong());
            }
        } else if (left != null) {
            // Пользователь вышел из произвольного канала — снимем мьют, если он был поставлен
            // нашей системой (на всякий случай, чтобы не зависал после удаления комнаты).
            String mkey2 = event.getGuild().getIdLong() + ":" + left.getIdLong() + ":" + member.getIdLong();
            if (mutedMembers.remove(mkey2) != null) {
                event.getGuild().mute(member, false).queue(_ -> { }, _ -> { });
            }
        }

        if (joined != null && channelOwner.containsKey(joined.getIdLong())) {
            cancelDeletion(joined.getIdLong());
        }
    }

    private boolean onlyBotsOrLeft(AudioChannelUnion channel, Member justLeft) {
        return channel.getMembers().stream()
                .allMatch(m -> m.getUser().isBot() || m.getIdLong() == justLeft.getIdLong());
    }

    private void createPrivateRoom(Member member, Guild guild, AudioChannelUnion creationChannel) {
        // Не создавать вторую комнату, если у пользователя уже есть активная
        String key = key(guild.getIdLong(), member.getIdLong());
        Long existing = ownerChannel.get(key);
        if (existing != null) {
            VoiceChannel existingChannel = guild.getVoiceChannelById(existing);
            if (existingChannel != null) {
                guild.moveVoiceMember(member, existingChannel).queue();
                return;
            }
            // Атомарно: снимаем только устаревший маппинг, не трогая более новый.
            ownerChannel.remove(key, existing);
            channelOwner.remove(existing);
        }

        VoiceChannel creationVoice = guild.getVoiceChannelById(creationChannel.getIdLong());
        var parent = creationVoice != null ? creationVoice.getParentCategory() : null;

        String name = clipName(member.getEffectiveName()) + SUFFIX;
        int limit = getDefaultLimit();

        var action = parent != null
                ? guild.createVoiceChannel(name, parent)
                : guild.createVoiceChannel(name);

        action.setUserlimit(limit);

        // Бот: явный override, чтобы всегда мог управлять perm-ами комнаты
        // (иначе в категориях с ограничениями @everyone бот теряет MANAGE_PERMISSIONS)
        Member selfMember = guild.getSelfMember();
        if (selfMember != null) {
            EnumSet<Permission> botAllow = EnumSet.of(
                    Permission.VIEW_CHANNEL,
                    Permission.VOICE_CONNECT,
                    Permission.VOICE_SPEAK,
                    Permission.VOICE_MOVE_OTHERS,
                    Permission.VOICE_MUTE_OTHERS,
                    Permission.VOICE_DEAF_OTHERS,
                    Permission.MANAGE_CHANNEL,
                    Permission.MANAGE_PERMISSIONS
            );
            action.addPermissionOverride(selfMember, botAllow, EnumSet.noneOf(Permission.class));
        }

        // Владелец: полный контроль над комнатой
        EnumSet<Permission> ownerAllow = EnumSet.of(
                Permission.VIEW_CHANNEL,
                Permission.VOICE_CONNECT,
                Permission.VOICE_SPEAK,
                Permission.VOICE_MOVE_OTHERS,
                Permission.MANAGE_CHANNEL,
                Permission.MANAGE_PERMISSIONS
        );
        action.addPermissionOverride(member, ownerAllow, EnumSet.noneOf(Permission.class));

        action.queue(
                channel -> {
                    channelOwner.put(channel.getIdLong(), key);
                    ownerChannel.put(key, channel.getIdLong());
                    guild.moveVoiceMember(member, channel).queue();
                },
                failure -> Logger.getLogger().log(LogType.ERROR, "VOICEROOM",
                        "Failed to create private room for " + member.getIdLong(), failure)
        );
    }

    private void scheduleDeletion(long guildId, long channelId) {
        int delay = getCountdownSeconds();
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            pendingDeletions.remove(channelId);
            var jda = ru.devprizrakk.voidbot.bootstrap.discord.JDALoader.getJDA();
            if (jda == null) return;
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return;
            VoiceChannel channel = guild.getVoiceChannelById(channelId);
            if (channel == null) {
                unregister(channelId);
                return;
            }
            boolean hasHumans = channel.getMembers().stream().anyMatch(m -> !m.getUser().isBot());
            if (!hasHumans) {
                channel.delete().queue(_ -> unregister(channelId), _ -> unregister(channelId));
            }
        }, delay, TimeUnit.SECONDS);
        pendingDeletions.put(channelId, future);
    }

    private void cancelDeletion(long channelId) {
        ScheduledFuture<?> f = pendingDeletions.remove(channelId);
        if (f != null) f.cancel(false);
    }

    private static void unregister(long channelId) {
        String k = channelOwner.remove(channelId);
        if (k != null) {
            // Удаляем маппинг владелец→комната только если он всё ещё указывает на ЭТУ комнату
            ownerChannel.remove(k, channelId);
        }
        // Снимаем сервер-мьюты, навешанные в этой комнате
        unmuteAllInChannel(channelId);
    }

    private static void unmuteAllInChannel(long channelId) {
        var jda = ru.devprizrakk.voidbot.bootstrap.discord.JDALoader.getJDA();
        for (String mk : new ArrayList<>(mutedMembers.keySet())) {
            String[] parts = mk.split(":", 3);
            if (parts.length != 3) continue;
            if (!String.valueOf(channelId).equals(parts[1])) continue;
            long guildId = Long.parseLong(parts[0]);
            long userId = Long.parseLong(parts[2]);
            mutedMembers.remove(mk);
            if (jda == null) continue;
            Guild g = jda.getGuildById(guildId);
            if (g == null) continue;
            Member m = g.getMemberById(userId);
            if (m != null) {
                g.mute(m, false).queue(_ -> { }, _ -> { });
            }
        }
    }

    // =====================================================
    //                  BUTTON INTERACTIONS
    // =====================================================

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!isEnabled()) return;
        String id = event.getComponentId();
        if (!id.startsWith("vr_")) return;

        switch (id) {
            case BTN_RENAME -> openRenameModal(event);
            case BTN_LIMIT -> openLimitModal(event);
            case BTN_LOCK -> toggleLock(event, true);
            case BTN_UNLOCK -> toggleLock(event, false);
            case BTN_GRANT -> askSelect(event, SEL_GRANT, "voiceroom.select.grant.placeholder");
            case BTN_DENY -> askSelect(event, SEL_DENY, "voiceroom.select.deny.placeholder");
            case BTN_KICK -> askSelect(event, SEL_KICK, "voiceroom.select.kick.placeholder");
            case BTN_MUTE -> askSelect(event, SEL_MUTE, "voiceroom.select.mute.placeholder");
            case BTN_UNMUTE -> askSelect(event, SEL_UNMUTE, "voiceroom.select.unmute.placeholder");
            case BTN_TRANSFER -> askSelect(event, SEL_TRANSFER, "voiceroom.select.transfer.placeholder");
            default -> { /* not ours */ }
        }
    }

    private void openRenameModal(ButtonInteractionEvent event) {
        VoiceChannel room = requireOwnerRoom(event);
        if (room == null) return;
        TextInput input = TextInput.create(IN_RENAME, TextInputStyle.PARAGRAPH)
                .setPlaceholder(lang().getInfoLocale("voiceroom.modal.rename.placeholder"))
                .setMinLength(1)
                .setMaxLength(100)
                .setRequired(true)
                .build();
        Label label = Label.of(lang().getInfoLocale("voiceroom.modal.rename.label"), input);
        Modal modal = Modal.create(MOD_RENAME, lang().getInfoLocale("voiceroom.modal.rename.title"))
                .addComponents(label)
                .build();
        event.replyModal(modal).queue();
    }

    private void openLimitModal(ButtonInteractionEvent event) {
        VoiceChannel room = requireOwnerRoom(event);
        if (room == null) return;
        TextInput input = TextInput.create(IN_LIMIT, TextInputStyle.SHORT)
                .setPlaceholder(lang().getInfoLocale("voiceroom.modal.limit.placeholder"))
                .setMinLength(1)
                .setMaxLength(2)
                .setRequired(true)
                .build();
        Label label = Label.of(lang().getInfoLocale("voiceroom.modal.limit.label"), input);
        Modal modal = Modal.create(MOD_LIMIT, lang().getInfoLocale("voiceroom.modal.limit.title"))
                .addComponents(label)
                .build();
        event.replyModal(modal).queue();
    }

    private void toggleLock(ButtonInteractionEvent event, boolean closed) {
        VoiceChannel room = requireOwnerRoom(event);
        if (room == null) return;
        var publicRole = event.getGuild().getPublicRole();
        if (closed) {
            room.upsertPermissionOverride(publicRole)
                    .deny(Permission.VOICE_CONNECT)
                    .queue(_ -> event.reply(lang().getInfoLocale("voiceroom.notice.locked")).setEphemeral(true).queue(),
                            f -> replyError(event, f));
        } else {
            room.upsertPermissionOverride(publicRole)
                    .clear(Permission.VOICE_CONNECT)
                    .queue(_ -> event.reply(lang().getInfoLocale("voiceroom.notice.unlocked")).setEphemeral(true).queue(),
                            f -> replyError(event, f));
        }
    }

    private void askSelect(ButtonInteractionEvent event, String selectId, String placeholderKey) {
        VoiceChannel room = requireOwnerRoom(event);
        if (room == null) return;

        EntitySelectMenu menu = EntitySelectMenu.create(selectId, EntitySelectMenu.SelectTarget.USER)
                .setPlaceholder(lang().getInfoLocale(placeholderKey))
                .setRequiredRange(1, 1)
                .build();
        event.reply(lang().getInfoLocale("voiceroom.select.prompt"))
                .addComponents(ActionRow.of(menu))
                .setEphemeral(true)
                .queue();
    }

    // =====================================================
    //                  SELECT INTERACTIONS
    // =====================================================

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (!isEnabled()) return;
        String id = event.getComponentId();
        if (!id.startsWith("vr_sel_")) return;

        long targetId = event.getMentions().getUsers().stream()
                .findFirst()
                .map(u -> u.getIdLong())
                .orElse(0L);
        if (targetId == 0L) {
            event.reply(lang().getInfoLocale("voiceroom.notice.no-target")).setEphemeral(true).queue();
            return;
        }

        VoiceChannel room = requireOwnerRoom(event);
        if (room == null) return;

        long ownerId = event.getUser().getIdLong();
        if (targetId == ownerId) {
            event.reply(lang().getInfoLocale("voiceroom.notice.cannot-self")).setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        Member target = guild == null ? null : guild.getMemberById(targetId);
        if (target == null) {
            event.reply(lang().getInfoLocale("voiceroom.notice.no-target")).setEphemeral(true).queue();
            return;
        }

        // Для действий с «живым» голосом (kick/mute/unmute/transfer) целевой участник
        // должен находиться в приватной комнате владельца. Для grant/deny прав
        // это не требуется — права можно выдавать/закрывать и вне комнаты.
        boolean requireInRoom = !SEL_GRANT.equals(id) && !SEL_DENY.equals(id);
        if (requireInRoom && !isInRoom(target, room)) {
            event.reply(lang().getInfoLocale("voiceroom.notice.not-in-room")).setEphemeral(true).queue();
            return;
        }

        switch (id) {
            case SEL_GRANT -> grantAccess(room, target, event);
            case SEL_DENY -> denyAccess(room, target, event);
            case SEL_KICK -> kickMember(room, guild, target, event);
            case SEL_MUTE -> muteMember(room, target, true, event);
            case SEL_UNMUTE -> muteMember(room, target, false, event);
            case SEL_TRANSFER -> transferOwnership(room, guild, target, event);
            default -> { }
        }
    }

    private static boolean isInRoom(Member target, VoiceChannel room) {
        var vs = target.getVoiceState();
        return vs != null && vs.getChannel() != null
                && vs.getChannel().getIdLong() == room.getIdLong();
    }

    private void grantAccess(VoiceChannel room, Member target, EntitySelectInteractionEvent event) {
        // Сначала убираем прежние deny (от denyAccess) — clear() сбрасывает биты в обеих масках,
        // делая permission нейтральным, после чего grant добавляет в allow.
        room.upsertPermissionOverride(target)
                .clear(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT)
                .grant(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)
                .queue(_ -> respondTarget(event, "voiceroom.notice.granted", target),
                        f -> replySelectError(event, f));
    }

    private void denyAccess(VoiceChannel room, Member target, EntitySelectInteractionEvent event) {
        room.upsertPermissionOverride(target)
                .deny(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT)
                .queue(_ -> {
                    if (isInRoom(target, room)) {
                        event.getGuild().kickVoiceMember(target).queue();
                    }
                    respondTarget(event, "voiceroom.notice.denied", target);
                }, f -> replySelectError(event, f));
    }

    private void kickMember(VoiceChannel room, Guild guild, Member target, EntitySelectInteractionEvent event) {
        // isInRoom уже проверен в onEntitySelectInteraction до dispatch
        guild.kickVoiceMember(target)
                .queue(_ -> respondTarget(event, "voiceroom.notice.kicked", target),
                        f -> replySelectError(event, f));
    }

    private void muteMember(VoiceChannel room, Member target, boolean mute, EntitySelectInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;
        String mkey = guild.getIdLong() + ":" + room.getIdLong() + ":" + target.getIdLong();
        if (mute) {
            // Сервер-мьют даёт видимый значок и надёжно блокирует голос.
            guild.mute(target, true).queue(
                    _ -> {
                        mutedMembers.put(mkey, true);
                        respondTarget(event, "voiceroom.notice.muted", target);
                    },
                    f -> replySelectError(event, f)
            );
        } else {
            guild.mute(target, false).queue(
                    _ -> {
                        mutedMembers.remove(mkey);
                        respondTarget(event, "voiceroom.notice.unmuted", target);
                    },
                    f -> replySelectError(event, f)
            );
        }
    }

    private void transferOwnership(VoiceChannel room, Guild guild, Member target, EntitySelectInteractionEvent event) {
        long guildId = guild.getIdLong();
        long oldOwnerId = event.getUser().getIdLong();

        // Новый владелец: сбрасываем прежние deny (например, от denyAccess) и выставляем чистый allow
        EnumSet<Permission> ownerAllow = EnumSet.of(
                Permission.VIEW_CHANNEL,
                Permission.VOICE_CONNECT,
                Permission.VOICE_SPEAK,
                Permission.VOICE_MOVE_OTHERS,
                Permission.MANAGE_CHANNEL,
                Permission.MANAGE_PERMISSIONS
        );
        room.upsertPermissionOverride(target)
                .resetDeny()
                .setAllowed(ownerAllow)
                .queue(_ -> {
                    // Старый владелец теряет управляющие права, но может остаться в комнате как обычный участник
                    Member oldOwner = guild.getMemberById(oldOwnerId);
                    if (oldOwner != null && oldOwner.getIdLong() != target.getIdLong()) {
                        room.upsertPermissionOverride(oldOwner)
                                .clear(Permission.VOICE_MOVE_OTHERS, Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS)
                                .queue();
                    }
                    String oldKey = key(guildId, oldOwnerId);
                    String newKey = key(guildId, target.getIdLong());
                    long channelId = room.getIdLong();
                    channelOwner.put(channelId, newKey);
                    // Атомарно: снимаем старый маппинг, только если он принадлежал этой комнате
                    ownerChannel.remove(oldKey, channelId);
                    ownerChannel.put(newKey, channelId);

                    // Снимаем сервер-мьюты, которые повесил прежний владелец
                    unmuteAllInChannel(channelId);

                    respondTarget(event, "voiceroom.notice.transferred", target);
                }, f -> replySelectError(event, f));
    }

    // =====================================================
    //                  MODAL INTERACTIONS
    // =====================================================

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (!isEnabled()) return;
        String id = event.getModalId();

        if (MOD_RENAME.equals(id)) {
            handleRenameModal(event);
        } else if (MOD_LIMIT.equals(id)) {
            handleLimitModal(event);
        }
    }

    private void handleRenameModal(ModalInteractionEvent event) {
        VoiceChannel room = requireOwnerRoom(event);
        if (room == null) return;

        var mapping = event.getValue(IN_RENAME);
        String name = mapping == null ? null : mapping.getAsString();
        if (name == null || name.isBlank()) {
            event.reply(lang().getInfoLocale("voiceroom.notice.invalid-name")).setEphemeral(true).queue();
            return;
        }
        String finalName = clipName(name) + SUFFIX;
        room.getManager().setName(finalName).queue(
                _ -> event.reply(lang().getInfoLocale("voiceroom.notice.renamed")).setEphemeral(true).queue(),
                f -> replyModalError(event, f)
        );
    }

    private void handleLimitModal(ModalInteractionEvent event) {
        VoiceChannel room = requireOwnerRoom(event);
        if (room == null) return;

        var mapping = event.getValue(IN_LIMIT);
        String raw = mapping == null ? null : mapping.getAsString();
        int limit;
        try {
            limit = Integer.parseInt(raw == null ? "" : raw.trim());
            if (limit < 0 || limit > 99) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            event.reply(lang().getInfoLocale("voiceroom.notice.invalid-limit")).setEphemeral(true).queue();
            return;
        }
        room.getManager().setUserLimit(limit).queue(
                _ -> event.reply(lang().getInfoLocale("voiceroom.notice.limit-set")).setEphemeral(true).queue(),
                f -> replyModalError(event, f)
        );
    }

    // =====================================================
    //                  HELPERS
    // =====================================================

    private static String key(long guildId, long userId) {
        return guildId + ":" + userId;
    }

    private static String clipName(String s) {
        if (s == null) return "User";
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return "User";
        int max = 100 - SUFFIX.length();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }

    private static VoiceChannel requireOwnerRoom(ButtonInteractionEvent event) {
        VoiceChannel room = findOwnerRoom(event.getGuild(), event.getUser().getIdLong());
        if (room == null) {
            event.reply(lang().getInfoLocale("voiceroom.notice.no-room")).setEphemeral(true).queue();
        }
        return room;
    }

    private static VoiceChannel requireOwnerRoom(EntitySelectInteractionEvent event) {
        VoiceChannel room = findOwnerRoom(event.getGuild(), event.getUser().getIdLong());
        if (room == null) {
            event.reply(lang().getInfoLocale("voiceroom.notice.no-room")).setEphemeral(true).queue();
        }
        return room;
    }

    private static VoiceChannel requireOwnerRoom(ModalInteractionEvent event) {
        VoiceChannel room = findOwnerRoom(event.getGuild(), event.getUser().getIdLong());
        if (room == null) {
            event.reply(lang().getInfoLocale("voiceroom.notice.no-room")).setEphemeral(true).queue();
        }
        return room;
    }

    /**
     * Находит приватную комнату, которой владеет пользователь, и проверяет, что он всё ещё
     * актуальный владелец (channelOwner[room] == key(user)). Это защищает от рассинхронизации
     * после нескольких передач владения и от кликов по старой панели.
     */
    private static VoiceChannel findOwnerRoom(Guild guild, long userId) {
        if (guild == null) return null;
        String key = key(guild.getIdLong(), userId);
        Long channelId = ownerChannel.get(key);
        if (channelId == null) return null;
        VoiceChannel room = guild.getVoiceChannelById(channelId);
        if (room == null) {
            unregister(channelId);
            return null;
        }
        // Cross-check: если room уже принадлежит другому владельцу, clicker больше не владелец.
        String actualOwner = channelOwner.get(channelId);
        if (actualOwner == null || !actualOwner.equals(key)) {
            // рассинхронизация — clicker больше не владелец этой комнаты
            ownerChannel.remove(key, channelId);
            return null;
        }
        return room;
    }

    private void respondTarget(EntitySelectInteractionEvent event, String key, Member target) {
        event.reply(lang().getInfoLocale(key, Map.of("target", target.getAsMention())))
                .setEphemeral(true).queue();
    }

    private void replyError(ButtonInteractionEvent event, Throwable f) {
        Logger.getLogger().log(LogType.ERROR, "VOICEROOM", "Action failed", f);
        event.reply(lang().getInfoLocale("voiceroom.error.generic")).setEphemeral(true).queue();
    }

    private void replySelectError(EntitySelectInteractionEvent event, Throwable f) {
        Logger.getLogger().log(LogType.ERROR, "VOICEROOM", "Select action failed", f);
        event.reply(lang().getInfoLocale("voiceroom.error.generic")).setEphemeral(true).queue();
    }

    private void replyModalError(ModalInteractionEvent event, Throwable f) {
        Logger.getLogger().log(LogType.ERROR, "VOICEROOM", "Modal action failed", f);
        event.reply(lang().getInfoLocale("voiceroom.error.generic")).setEphemeral(true).queue();
    }

    private static LangHelper lang() {
        return Utils.getLangManager();
    }

    // =====================================================
    //                  STARTUP RECONCILE
    // =====================================================

    public static void reconcile(Guild guild) {
        if (!isEnabled()) return;
        long createId = getCreateChannelId(guild);
        if (createId <= 0) return;
        VoiceChannel creation = guild.getVoiceChannelById(createId);
        if (creation == null) return;
        var parent = creation.getParentCategory();

        for (VoiceChannel vc : parent != null ? parent.getVoiceChannels() : guild.getVoiceChannels()) {
            if (vc.getIdLong() == createId) continue;
            if (!vc.getName().endsWith(SUFFIX)) continue;
            Optional<Member> firstHuman = vc.getMembers().stream()
                    .filter(m -> !m.getUser().isBot())
                    .findFirst();
            if (firstHuman.isEmpty()) {
                vc.delete().queue(_ -> { }, _ -> { });
                continue;
            }
            Member owner = firstHuman.get();
            String key = key(guild.getIdLong(), owner.getIdLong());
            // Не затираем маппинг, если этот пользователь уже владеет другой комнатой —
            // иначе мы потеряем id ранее созданной комнаты. Просто помечаем текущую как
            // принадлежащую ему, только если у него ещё нет своей.
            channelOwner.put(vc.getIdLong(), key);
            ownerChannel.putIfAbsent(key, vc.getIdLong());
        }
    }

    /**
     * Автоматически публикуet панель управления в настроенный текстовый канал,
     * если её там ещё нет (один раз и навсегда). Вызывается из OnReady.
     */
    public static void postPanelIfMissing(Guild guild) {
        if (!isEnabled()) return;
        if (guild == null) return;

        String channelId = Utils.getConfig().getString("voice-room.text-channel", "");
        if (channelId == null || channelId.isEmpty() || channelId.startsWith("fill_id_")) {
            Logger.getLogger().log(LogType.WARN, "VOICEROOM",
                    "voice-room.text-channel не настроен, панель не опубликована.");
            return;
        }

        TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            Logger.getLogger().log(LogType.WARN, "VOICEROOM",
                    "Текстовый канал панели не найден по id: " + channelId);
            return;
        }

        EmbedBuilder embed = buildPanelEmbed();
        List<ActionRow> rows = buildControlRows(Utils.getLangManager());

        MessageHistory history = channel.getHistory();
        history.retrievePast(30).queue(messages -> {
            long selfId = guild.getJDA().getSelfUser().getIdLong();
            boolean already = messages.stream()
                    .filter(m -> m.getAuthor().isBot() && m.getAuthor().getIdLong() == selfId)
                    .flatMap(m -> m.getComponents().stream())
                    .filter(ActionRow.class::isInstance)
                    .map(ActionRow.class::cast)
                    .flatMap(ar -> ar.getButtons().stream())
                    .anyMatch(b -> b.getCustomId() != null && b.getCustomId().startsWith("vr_"));
            if (already) {
                Logger.getLogger().log(LogType.DEBUG, "VOICEROOM",
                        "Панель приватных комнат уже присутствует в канале — повторная публикация пропущена.");
                return;
            }
            channel.sendMessageEmbeds(embed.build()).addComponents(rows).queue(
                    _ -> Logger.getLogger().log(LogType.INFO, "VOICEROOM",
                            "Панель приватных комнат опубликована в канал " + channel.getName() + "."),
                    f -> Logger.getLogger().log(LogType.ERROR, "VOICEROOM",
                            "Не удалось опубликовать панель приватных комнат", f)
            );
        }, f -> Logger.getLogger().log(LogType.ERROR, "VOICEROOM",
                "Не удалось получить историю канала для проверки панели", f));
    }

    public static EmbedBuilder buildPanelEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(88, 101, 242));
        LangHelper lang = Utils.getLangManager();
        embed.setTitle(lang.getInfoLocale("voiceroom.embed.title"));
        embed.setDescription(lang.getInfoLocale("voiceroom.embed.description"));
        embed.setFooter(lang.getInfoLocale("voiceroom.embed.footer"));
        return embed;
    }
}