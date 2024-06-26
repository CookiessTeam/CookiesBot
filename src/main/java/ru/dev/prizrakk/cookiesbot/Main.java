package ru.dev.prizrakk.cookiesbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.dev.prizrakk.cookiesbot.command.prefix.fun.Dogs;
import ru.dev.prizrakk.cookiesbot.command.prefix.fun.Kawaii;
import ru.dev.prizrakk.cookiesbot.command.prefix.system.Api;
import ru.dev.prizrakk.cookiesbot.command.prefix.system.Diagnostics;
import ru.dev.prizrakk.cookiesbot.command.slash.fun.Calc;
import ru.dev.prizrakk.cookiesbot.command.slash.music.*;
import ru.dev.prizrakk.cookiesbot.command.slash.server.*;
import ru.dev.prizrakk.cookiesbot.command.slash.server.moderation.Mute;
import ru.dev.prizrakk.cookiesbot.command.slash.system.Status;
import ru.dev.prizrakk.cookiesbot.command.slash.system.help.Help;
import ru.dev.prizrakk.cookiesbot.command.slash.system.help.HelpSelectMenu;
import ru.dev.prizrakk.cookiesbot.command.slash.system.settings.SettingsSelectMenu;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.events.OnJoin;
import ru.dev.prizrakk.cookiesbot.events.OnLeft;
import ru.dev.prizrakk.cookiesbot.command.CommandManager;
import ru.dev.prizrakk.cookiesbot.manager.ConsoleManager;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.manager.MessageManager;
import ru.dev.prizrakk.cookiesbot.util.Config;
import ru.dev.prizrakk.cookiesbot.util.Utils;
import ru.dev.prizrakk.cookiesbot.web.WebMain;

import java.sql.SQLException;

public class Main extends Utils {
    static JDA jda;
    static Database database;
    public static void main(String[] args) {

        Main main = new Main();

        ConsoleManager consoleManager = new ConsoleManager();
        consoleManager.console();

        /* =============== */
        /*   Lang Loader   */
        /* =============== */

        LangManager.loadLanguages();
        /* =============== */
        /* DataBase Loader */
        /* =============== */
        main.JDBCConnect();
        main.jda(database);
        Utils utils = new Utils(database);
        /* =============== */
        /*    Web Loader   */
        /* =============== */
        WebMain.initialize();
    }
    public void jda(Database database) {
        jda = JDABuilder.createDefault(Config.token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("PornHub | Eve Elfi в лесу с парнем 4k"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
                .build();
        /* ======================= */
        /* Slash Command           */
        /* ======================= */
        CommandManager commandManager = new CommandManager();
        /* fun command */
        commandManager.add(new Calc());
        /* music command */
        commandManager.add(new Play());
        commandManager.add(new Skip());
        commandManager.add(new Stop());
        commandManager.add(new NowPlaying());
        commandManager.add(new Queue());
        commandManager.add(new Repeat());
        /* server */
        commandManager.add(new ServerInfo());
        commandManager.add(new UserInfo());
        commandManager.add(new Avatar());

        commandManager.add(new RankCard());
        commandManager.add(new LeaderBoard(database));

        /*System command*/
        commandManager.add(new Status());
        commandManager.add(new Help());
        jda.addEventListener(new HelpSelectMenu());
        jda.addEventListener(new SettingsSelectMenu(database));
        /* moderation */
        commandManager.add(new Mute());

        jda.addEventListener(commandManager);

        /* ======================= */
        /* Prefix Command          */
        /* ======================= */
        /* fun */
        jda.addEventListener(new Dogs(database));
        jda.addEventListener(new Kawaii(database));
        /* system */
        jda.addEventListener(new Api());
        jda.addEventListener(new Diagnostics());

        /* ======================= */
        /* Event                   */
        /* ======================= */
        /* logger */
        //jda.addEventListener(new MessageAudit());
        jda.addEventListener(new MessageManager(database));
        jda.addEventListener(new OnLeft());
        jda.addEventListener(new OnJoin());
    }
    public void JDBCConnect() {
        try {
            database = new Database();
            database.initializeDatabase();
        } catch (SQLException ex) {
            getLogger().error("Error Database!");
            ex.printStackTrace();
        }
    }
}