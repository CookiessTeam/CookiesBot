package ru.dev.prizrakk.cookiesbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;
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
import ru.dev.prizrakk.cookiesbot.command.slash.user.Avatar;
import ru.dev.prizrakk.cookiesbot.command.slash.user.UserInfo;
import ru.dev.prizrakk.cookiesbot.database.Database;
import ru.dev.prizrakk.cookiesbot.events.OnJoin;
import ru.dev.prizrakk.cookiesbot.events.OnLeft;
import ru.dev.prizrakk.cookiesbot.command.CommandManager;
import ru.dev.prizrakk.cookiesbot.manager.ConfigManager;
import ru.dev.prizrakk.cookiesbot.manager.LangManager;
import ru.dev.prizrakk.cookiesbot.manager.MessageManager;
import ru.dev.prizrakk.cookiesbot.util.Utils;
import ru.dev.prizrakk.cookiesbot.web.WebMain;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends Utils {
    static JDA jda;
    static Database database;
    public static void main(String[] args) {

        Main main = new Main();

        //ConsoleManager consoleManager = new ConsoleManager();
        //consoleManager.console();

        /* =============== */
        /*   Properties Loader   */
        /* =============== */
        LangManager.loadLanguages();
        File configFile = new File("config.properties");
        ConfigManager configManager = new ConfigManager();
        if(!configFile.exists()) {
            configManager.setProperty("bot.token", "please insert your token");
            configManager.setProperty("bot.activity.type", "playing");
            configManager.setProperty("bot.activity.status", "Minecraft!");
            configManager.setProperty("bot.activity.status.streaming.url", "https://www.twitch.tv/ender_games__");
            configManager.setProperty("type.database", "mysql/sqlite");
            configManager.setProperty("mysql.url", "jdbc:mysql://127.0.0.1:3306/BotDatabase?autoReconnect=true");
            configManager.setProperty("mysql.login", "please insert your password");
            configManager.setProperty("mysql.password", "please insert your password");
            configManager.saveConfig();
            getLogger().error("config.properties is not empty please insert");
            getLogger().info("Stopping bot!");
            System.exit(404);
        }
        /* =============== */
        /* Check updates   */
        /* =============== */
        try {
            String currentVersion = "1.0.0";
            String latestVersion = getLatestReleaseVersion("CookiessTeam", "CookiesBot");
            if (latestVersion == null) {
                getLogger().error("Failed to get the latest version from GitHub");
                latestVersion = currentVersion;
            }
            if (!currentVersion.equals(latestVersion)) {
                getLogger().warn("Version "+currentVersion+" is installed. New version "+latestVersion+" is available.");
                getLogger().warn("Please install the latest version.");
            } else {
                getLogger().info("Latest version installed: " + currentVersion + ".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ConfigManager configManager = new ConfigManager();
        Activity activity;
        switch (configManager.getProperty("bot.activity.type")) {
            case "streaming" -> activity = Activity.streaming(configManager.getProperty("bot.activity.status"), configManager.getProperty("bot.activity.status.streaming.url"));
            case "playing" -> activity = Activity.playing(configManager.getProperty("bot.activity.status"));
            case "competing" -> activity = Activity.competing(configManager.getProperty("bot.activity.status"));
            case "watching" -> activity = Activity.watching(configManager.getProperty("bot.activity.status"));
            case "listening" -> activity = Activity.listening(configManager.getProperty("bot.activity.status"));
            default -> activity = Activity.customStatus(configManager.getProperty("bot.activity.status"));
        }
        try {
            jda = JDABuilder.createDefault(configManager.getProperty("bot.token"))
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(activity)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableCache(CacheFlag.ONLINE_STATUS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
                    .build();
        } catch (InvalidTokenException e) {
            getLogger().error("The provided token is invalid!", e);
            System.exit(1);
        }
        catch (Exception e) {
            getLogger().error("An unexpected error occurred!", e);
            System.exit(1);
        }

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
            getLogger().error("Error Database!", ex);
        }
    }

    // Метод для получения последнего релиза с GitHub
    private static String getLatestReleaseVersion(String owner, String repository) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRelease latestRelease = github.getRepository(owner + "/" + repository).getLatestRelease();
        return null;
    }
}