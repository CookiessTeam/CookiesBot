package ru.devprizrakk.voidbot.core.language;

public final class LangMessage {
    private LangMessage() {
    }

    public static final class Commands {
        private Commands() {
        }

        public static final class Server {
            private Server() {
            }

            public static final class Rank {
                private Rank() {
                }

                public static final String FILE = "command/server/rank.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "rank.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String USER = "rank.description.option.user";
                    }
                }
            }

            public static final class ServerStats {
                private ServerStats() {
                }

                public static final String FILE = "command/server/serverstats.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "serverstats.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String PERIOD = "serverstats.description.option.period";
                    }
                }
            }

            public static final class UserInfo {
                private UserInfo() {
                }

                public static final String FILE = "command/server/userinfo.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "userinfo.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String USER = "userinfo.description.option.user";
                    }
                }
            }

            public static final class ServerInfo {
                private ServerInfo() {
                }

                public static final String FILE = "command/server/serverinfo.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "serverinfo.description.command";
                }
            }
        }

        public static final class Fun {
            private Fun() {
            }

            public static final class Avatar {
                private Avatar() {
                }

                public static final String FILE = "command/fun/avatar.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "avatar.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String USER = "avatar.description.option.user";
                    }
                }

                public static final class EmbedNoMentioned {
                    private EmbedNoMentioned() {
                    }

                    public static final String TITLE = "avatar.embed-no-mentioned.title";
                    public static final String DESCRIPTION = "avatar.embed-no-mentioned.description";
                    public static final String FOOTER = "avatar.embed-no-mentioned.footer";
                }

                public static final class EmbedMentioned {
                    private EmbedMentioned() {
                    }

                    public static final String TITLE = "avatar.embed-mentioned.title";
                    public static final String DESCRIPTION = "avatar.embed-mentioned.description";
                    public static final String FOOTER = "avatar.embed-mentioned.footer";
                }
            }

            public static final class Calc {
                private Calc() {
                }

                public static final String FILE = "command/fun/calc.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "calc.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String CALC = "calc.description.option.calc";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "calc.embed.title";
                    public static final String DESCRIPTION = "calc.embed.description";
                    public static final String FOOTER = "calc.embed.footer";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String WRONG = "calc.error.wrong";
                }
            }

            public static final class CoinFlip {
                private CoinFlip() {
                }

                public static final String FILE = "command/fun/coinflip.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "coinflip.description.command";
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "coinflip.embed.title";
                    public static final String DESCRIPTION = "coinflip.embed.description";
                    public static final String FOOTER = "coinflip.embed.footer";
                }

                public static final class Flip {
                    private Flip() {
                    }

                    public static final String EAGLE = "coinflip.flip.eagle";
                    public static final String TAILS = "coinflip.flip.tails";
                }
            }

            public static final class Emote {
                private Emote() {
                }

                public static final String FILE = "command/fun/emote.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "emote.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String CHOICE = "emote.description.option.choice";
                        public static final String USER = "emote.description.option.user";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "emote.embed.title";
                    public static final String DESCRIPTION = "emote.embed.description";
                    public static final String FOOTER = "emote.embed.footer";
                }

                public static final class Type {
                    private Type() {
                    }

                    public static final String BASE_PATH = "emote.type.";
                    public static final String HUG = BASE_PATH + "hug";
                    public static final String KISS = BASE_PATH + "kiss";
                    public static final String PAT = BASE_PATH + "pat";
                    public static final String SLAP = BASE_PATH + "slap";
                    public static final String CUDDLE = BASE_PATH + "cuddle";
                    public static final String BAKA = BASE_PATH + "baka";
                    public static final String BLUSH = BASE_PATH + "blush";
                    public static final String LAUGH = BASE_PATH + "laugh";
                    public static final String SMILE = BASE_PATH + "smile";
                    public static final String LOVE = BASE_PATH + "love";
                    public static final String POKE = BASE_PATH + "poke";
                    public static final String HIGHFIVE = BASE_PATH + "highfive";
                    public static final String HAPPY = BASE_PATH + "happy";
                    public static final String WAVE = BASE_PATH + "wave";
                    public static final String SLEEPY = BASE_PATH + "sleepy";
                    public static final String FACEPALM = BASE_PATH + "facepalm";
                    public static final String DANCE = BASE_PATH + "dance";
                    public static final String SHOCKED = BASE_PATH + "shocked";
                    public static final String POUT = BASE_PATH + "pout";
                    public static final String CRY = BASE_PATH + "cry";
                    public static final String WINK = BASE_PATH + "wink";
                    public static final String AMAZING = BASE_PATH + "amazing";
                    public static final String NOM = BASE_PATH + "nom";
                    public static final String TICKLE = BASE_PATH + "tickle";
                    public static final String UWU = BASE_PATH + "uwu";
                }

                public static final class TypeMentioned {
                    private TypeMentioned() {
                    }

                    public static final String BASE_PATH = "emote.type-mentioned.";
                    public static final String HUG = BASE_PATH + "hug";
                    public static final String KISS = BASE_PATH + "kiss";
                    public static final String PAT = BASE_PATH + "pat";
                    public static final String slap = BASE_PATH + "slap";
                    public static final String cuddle = BASE_PATH + "cuddle";
                    public static final String BAKA = BASE_PATH + "baka";
                    public static final String BLUSH = BASE_PATH + "blush";
                    public static final String LAUGH = BASE_PATH + "laugh";
                    public static final String SMILE = BASE_PATH + "smile";
                    public static final String LOVE = BASE_PATH + "love";
                    public static final String POKE = BASE_PATH + "poke";
                    public static final String HIGHFIVE = BASE_PATH + "highfive";
                    public static final String HAPPY = BASE_PATH + "happy";
                    public static final String WAVE = BASE_PATH + "wave";
                    public static final String SLEEPY = BASE_PATH + "sleepy";
                    public static final String FACEPALM = BASE_PATH + "facepalm";
                    public static final String DANCE = BASE_PATH + "dance";
                    public static final String SHOCKED = BASE_PATH + "shocked";
                    public static final String POUT = BASE_PATH + "pout";
                    public static final String CRY = BASE_PATH + "cry";
                    public static final String WINK = BASE_PATH + "wink";
                    public static final String AMAZING = BASE_PATH + "amazing";
                    public static final String NOM = BASE_PATH + "nom";
                    public static final String TICKLE = BASE_PATH + "tickle";
                    public static final String UWU = BASE_PATH + "uwu";
                }

                public static final class TypeNotMentioned {
                    private TypeNotMentioned() {
                    }

                    public static final String BASE_PATH = "emote.type-not-mentioned.";
                    public static final String HUG = BASE_PATH + "hug";
                    public static final String KISS = BASE_PATH + "kiss";
                    public static final String PAT = BASE_PATH + "pat";
                    public static final String slap = BASE_PATH + "slap";
                    public static final String cuddle = BASE_PATH + "cuddle";
                    public static final String BAKA = BASE_PATH + "baka";
                    public static final String BLUSH = BASE_PATH + "blush";
                    public static final String LAUGH = BASE_PATH + "laugh";
                    public static final String SMILE = BASE_PATH + "smile";
                    public static final String LOVE = BASE_PATH + "love";
                    public static final String POKE = BASE_PATH + "poke";
                    public static final String HIGHFIVE = BASE_PATH + "highfive";
                    public static final String HAPPY = BASE_PATH + "happy";
                    public static final String WAVE = BASE_PATH + "wave";
                    public static final String SLEEPY = BASE_PATH + "sleepy";
                    public static final String FACEPALM = BASE_PATH + "facepalm";
                    public static final String DANCE = BASE_PATH + "dance";
                    public static final String SHOCKED = BASE_PATH + "shocked";
                    public static final String POUT = BASE_PATH + "pout";
                    public static final String CRY = BASE_PATH + "cry";
                    public static final String WINK = BASE_PATH + "wink";
                    public static final String AMAZING = BASE_PATH + ".amazing";
                    public static final String NOM = BASE_PATH + "nom";
                    public static final String TICKLE = BASE_PATH + "tickle";
                    public static final String UWU = BASE_PATH + "uwu";
                }
            }

            public static final class Joke {
                private Joke() {
                }

                public static final String FILE = "command/fun/joke.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "joke.description.command";
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "joke.embed.title";
                    public static final String DESCRIPTION = "joke.embed.description";
                    public static final String FOOTER = "joke.embed.footer";
                }

                public static final class Jokes {
                    private Jokes() {
                    }

                    public static final String LENGTH = "joke.jokes.length";
                    public static final String JOKE_STRING = "joke.jokes.%number%";
                }
            }

            public static final class Rps {
                private Rps() {
                }

                public static final String FILE = "command/fun/rps.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "rps.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String CHOICE = "rps.description.option.choice";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "rps.embed.title";
                    public static final String DESCRIPTION = "rps.embed.description";
                    public static final String FOOTER = "rps.embed.footer";
                }

                public static final class Type {
                    private Type() {
                    }

                    public static final String ROCK = "rps.type.rock";
                    public static final String PAPER = "rps.type.paper";
                    public static final String SCISSORS = "rps.type.scissors";
                }

                public static final class Status {
                    private Status() {
                    }

                    public static final String WIN = "rps.status.win";
                    public static final String DRAW = "rps.status.draw";
                    public static final String LOSE = "rps.status.lose";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String UNDEFIED_CHOICE = "rps.error.undefied-choice";
                }
            }
        }

        public static final class Moderation {
            private Moderation() {
            }

            public static final class Ban {
                private Ban() {
                }

                public static final String FILE = "command/moderation/ban.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "ban.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String TARGET_USER = "ban.description.option.target-user";
                        public static final String TIME = "ban.description.option.time";
                        public static final String REASON = "ban.description.option.reason";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "ban.embed.title";
                    public static final String DESCRIPTION = "ban.embed.description";
                    public static final String FOOTER = "ban.embed.footer";
                }

                public static final class Status {
                    private Status() {
                    }

                    public static final String FOREVER = "ban.status.forever";
                    public static final String TIME = "ban.status.time";
                }

                public static final class LocalTime {
                    private LocalTime() {
                    }

                    public static final String MOUNT = "ban.local-time.mount";
                    public static final String MOUNTS = "ban.local-time.mounts";
                    public static final String WEEK = "ban.local-time.week";
                    public static final String WEEKS = "ban.local-time.weeks";
                    public static final String DAY = "ban.local-time.day";
                    public static final String DAYS = "ban.local-time.days";
                    public static final String HOUR = "ban.local-time.hour";
                    public static final String HOURS = "ban.local-time.hours";
                    public static final String MINUTE = "ban.local-time.minute";
                    public static final String MINUTES = "ban.local-time.minutes";
                    public static final String SECOND = "ban.local-time.second";
                    public static final String SECONDS = "ban.local-time.seconds";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final class LowLevelPermission {
                        private LowLevelPermission() {
                        }

                        public static final String AUTHOR = "ban.error.low-level-permission.author";
                        public static final String BOT = "ban.error.low-level-permission.bot";
                    }

                    public static final String USER_NOT_FOUND = "ban.error.user-not-found";
                    public static final String NOT_CORRECTED = "ban.error.not-corrected";
                    public static final String OTHER = "ban.error.other";
                }
            }

            public static final class Kick {
                private Kick() {
                }

                public static final String FILE = "command/moderation/kick.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "kick.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String TARGET_USER = "kick.description.option.target-user";
                        public static final String REASON = "kick.description.option.reason";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "kick.embed.title";
                    public static final String DESCRIPTION = "kick.embed.description";
                    public static final String FOOTER = "kick.embed.footer";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final class LowLevelPermission {
                        private LowLevelPermission() {
                        }

                        public static final String AUTHOR = "kick.error.low-level-permission.author";
                        public static final String BOT = "kick.error.low-level-permission.bot";
                    }

                    public static final String USER_NOT_FOUND = "kick.error.user-not-found";
                    public static final String OTHER = "kick.error.other";
                }
            }

            public static final class Mute {
                private Mute() {
                }

                public static final String FILE = "command/moderation/mute.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "mute.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String TARGET_USER = "mute.description.option.target-user";
                        public static final String TIME = "mute.description.option.time";
                        public static final String REASON = "mute.description.option.reason";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "mute.embed.title";
                    public static final String DESCRIPTION = "mute.embed.description";
                    public static final String FOOTER = "mute.embed.footer";
                }

                public static final class Status {
                    private Status() {
                    }

                    public static final String FOREVER = "mute.status.forever";
                    public static final String TIME = "mute.status.time";
                }

                public static final class LocalTime {
                    private LocalTime() {
                    }

                    public static final String MOUNT = "mute.local-time.mount";
                    public static final String MOUNTS = "mute.local-time.mounts";
                    public static final String WEEK = "mute.local-time.week";
                    public static final String WEEKS = "mute.local-time.weeks";
                    public static final String DAY = "mute.local-time.day";
                    public static final String DAYS = "mute.local-time.days";
                    public static final String HOUR = "mute.local-time.hour";
                    public static final String HOURS = "mute.local-time.hours";
                    public static final String MINUTE = "mute.local-time.minute";
                    public static final String MINUTES = "mute.local-time.minutes";
                    public static final String SECOND = "mute.local-time.second";
                    public static final String SECONDS = "mute.local-time.seconds";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final class LowLevelPermission {
                        private LowLevelPermission() {
                        }

                        public static final String AUTHOR = "mute.error.low-level-permission.author";
                        public static final String BOT = "mute.error.low-level-permission.bot";
                    }

                    public static final String USER_NOT_FOUND = "mute.error.user-not-found";
                    public static final String NOT_CORRECTED = "mute.error.not-corrected";
                    public static final String OTHER = "mute.error.other";
                }
            }


            public static final class Unmute {
                private Unmute() {
                }

                public static final String FILE = "command/moderation/unmute.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "unmute.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String TARGET_USER = "unmute.description.option.target-user";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "unmute.embed.title";
                    public static final String DESCRIPTION = "unmute.embed.description";
                    public static final String FOOTER = "unmute.embed.footer";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String NO_MUTED = "unmute.error.no-muted";
                    public static final String USER_NOT_FOUND = "unmute.error.user-not-found";
                    public static final String OTHER = "unmute.error.other";
                }
            }

            public static final class Unban {
                private Unban() {
                }

                public static final String FILE = "command/moderation/unban.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "unban.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String TARGET_USER = "unban.description.option.target-user";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "unban.embed.title";
                    public static final String DESCRIPTION = "unban.embed.description";
                    public static final String FOOTER = "unban.embed.footer";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String USER_NOT_FOUND = "unban.error.user-not-found";
                    public static final String NO_MUTED = "unmute.error.no-muted";
                    public static final String OTHER = "unban.error.other";
                }
            }
        }

        public static final class Warn {
            private Warn() {
            }

            public static final String FILE = "command/moderation/warn.yml";

            public static final class Description {
                private Description() {
                }

                public static final String COMMAND = "warn.description.command";

                public static final class Option {
                    private Option() {
                    }

                    public static final String TARGET_USER = "warn.description.option.target-user";
                    public static final String REASON = "warn.description.option.reason";
                }
            }

            public static final class Embed {
                private Embed() {
                }

                public static final String TITLE = "warn.embed.title";
                public static final String DESCRIPTION = "warn.embed.description";
                public static final String FOOTER = "warn.embed.footer";
            }

            public static final class Error {
                private Error() {
                }

                public static final class LowLevelPermission {
                    private LowLevelPermission() {
                    }

                    public static final String AUTHOR = "warn.error.low-level-permission.author";
                    public static final String BOT = "warn.error.low-level-permission.bot";
                }

                public static final String USER_NOT_FOUND = "warn.error.user-not-found";
                public static final String OTHER = "warn.error.other";
            }
        }


        public static final class Music {
            private Music() {
            }

            public static final class NowPlaying {
                private NowPlaying() {
                }

                public static final String FILE = "command/music/nowplaying.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "now-playing.description.command";
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "now-playing.embed.title";
                    public static final String DESCRIPTION = "now-playing.embed.description";
                    public static final String FOOTER = "now-playing.embed.footer";

                    public static final class Fields {
                        private Fields() {
                        }

                        public static final class Time {
                            private Time() {
                            }

                            public static final class Video {
                                private Video() {
                                }

                                public static final String TITLE = "now-playing.embed.fields.time.video.title";
                                public static final String DESCRIPTION = "now-playing.embed.fields.time.video.description";
                            }

                            public static final class Stream {
                                private Stream() {
                                }

                                public static final String TITLE = "now-playing.embed.fields.time.stream.title";
                                public static final String DESCRIPTION = "now-playing.embed.fields.time.stream.description";
                            }
                        }

                        public static final class Author {
                            private Author() {
                            }

                            public static final String TITLE = "now-playing.embed.fields.author.title";
                            public static final String DESCRIPTION = "now-playing.embed.fields.author.description";
                        }

                        public static final class Views {
                            private Views() {
                            }

                            public static final String TITLE = "now-playing.embed.fields.views.title";
                            public static final String DESCRIPTION = "now-playing.embed.fields.views.description";
                        }

                        public static final class Like {
                            private Like() {
                            }

                            public static final String TITLE = "now-playing.embed.fields.like.title";
                            public static final String DESCRIPTION = "now-playing.embed.fields.like.title";
                        }

                        public static final class DateCreated {
                            private DateCreated() {
                            }

                            public static final String TITLE = "now-playing.embed.fields.date-created.title";
                            public static final String DESCRIPTION = "now-playing.embed.fields.date-created.description";
                        }

                        public static final class Link {
                            private Link() {
                            }

                            public static final String TITLE = "now-playing.embed.fields.link.title";
                            public static final String DESCRIPTION = "now-playing.embed.fields.link.description";
                        }

                        public static final class Volume {
                            private Volume() {
                            }

                            public static final String TITLE = "now-playing.embed.fields.volume.title";
                            public static final String DESCRIPTION = "now-playing.embed.fields.volume.description";
                        }
                    }
                }

                public static final class Message {
                    private Message() {
                    }

                    public static final String LOAD_PLAYLIST = "now-playing.message.load-playlist";
                }

                public static final class Status {
                    private Status() {
                    }

                    public static final class Sound {
                        private Sound() {
                        }

                        public static final String TRUE = "now-playing.status.sound.true";
                        public static final String FALSE = "now-playing.status.sound.false";
                    }
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String NO_FOUND_TRACK = "now-playing.error.no-found-track";
                    public static final String NO_FOUND_VOICE = "now-playing.error.no-found-voice";
                    public static final String NO_FOUND_PLAYER = "now-playing.error.no-found-player";
                    public static final String NO_DM = "now-playing.error.no-dm";
                }
            }

            public static final class Pause {
                private Pause() {
                }

                public static final String FILE = "command/music/pause.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "pause.description.command";
                }

                public static final class Message {
                    private Message() {
                    }

                    public static final String SUCCESSFUL = "pause.message.successful";
                }

                public static final class Status {
                    private Status() {
                    }

                    public static final String TRUE = "pause.status.true";
                    public static final String FALSE = "pause.status.false";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String OTHER = "pause.error.other";
                    public static final String NO_DM = "pause.error.no-dm";
                }
            }

            public static final class Play {
                private Play() {
                }

                public static final String FILE = "command/music/play.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "play.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String SONG = "play.description.option.song";
                    }
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "play.embed.title";
                    public static final String DESCRIPTION = "play.embed.description";
                    public static final String FOOTER = "play.embed.footer";

                    public static final class Fields {
                        private Fields() {
                        }

                        public static final class Time {
                            private Time() {
                            }

                            public static final class Video {
                                private Video() {
                                }

                                public static final String TITLE = "play.embed.fields.time.video.title";
                                public static final String DESCRIPTION = "play.embed.fields.time.video.description";
                            }

                            public static final class Stream {
                                private Stream() {
                                }

                                public static final String TITLE = "play.embed.fields.time.stream.title";
                                public static final String DESCRIPTION = "play.embed.fields.time.stream.description";
                            }
                        }

                        public static final class Author {
                            private Author() {
                            }

                            public static final String TITLE = "play.embed.fields.author.title";
                            public static final String DESCRIPTION = "play.embed.fields.author.description";
                        }

                        public static final class Views {
                            private Views() {
                            }

                            public static final String TITLE = "play.embed.fields.views.title";
                            public static final String DESCRIPTION = "play.embed.fields.views.description";
                        }

                        public static final class Like {
                            private Like() {
                            }

                            public static final String TITLE = "play.embed.fields.like.title";
                            public static final String DESCRIPTION = "play.embed.fields.like.description";
                        }

                        public static final class DateCreated {
                            private DateCreated() {
                            }

                            public static final String TITLE = "play.embed.fields.date-created.title";
                            public static final String DESCRIPTION = "play.embed.fields.date-created.description";
                        }

                        public static final class Link {
                            private Link() {
                            }

                            public static final String TITLE = "play.embed.fields.link.title";
                            public static final String DESCRIPTION = "play.embed.fields.link.description";
                        }

                        public static final class Volume {
                            private Volume() {
                            }

                            public static final String TITLE = "play.embed.fields.volume.title";
                            public static final String DESCRIPTION = "play.embed.fields.volume.description";
                        }
                    }
                }

                public static final class Message {
                    private Message() {
                    }

                    public static final String LOAD_PLAYLIST = "play.message.load-playlist";
                }

                public static final class Status {
                    private Status() {
                    }

                    public static final class Sound {
                        private Sound() {
                        }

                        public static final String TRUE = "play.status.sound.true";
                        public static final String FALSE = "play.status.sound.false";
                    }
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String NO_FOUND_VOICE = "play.error.no-found-voice";
                    public static final String NO_FOUND_ME = "play.error.no-found-me";
                    public static final String NO_FOUND_MUSIC = "play.error.no-found-music";
                    public static final String NO_FOUND_QUEUE = "play.error.no-found-queue";
                    public static final String NO_HAS_PERMISSION = "play.error.no-has-permission";
                    public static final String NO_DM = "play.error.no-dm";
                    public static final String NO_MATCHES = "play.error.no-matches";
                    public static final String NO_SUPPORT_PLATFORM = "play.error.no-support-platform";
                    public static final String OTHER = "play.error.other";
                }
            }

            public static final class Stop {
                private Stop() {
                }

                public static final String FILE = "command/music/stop.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "stop.description.command";
                }

                public static final class Message {
                    private Message() {
                    }

                    public static final String SUCCESSFUL = "stop.message.successful";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String OTHER = "stop.error.other";
                    public static final String NO_DM = "stop.error.no-dm";
                }
            }

            public static final class Volume {
                private Volume() {
                }

                public static final String FILE = "command/music/volume.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "volume.description.command";

                    public static final class Option {
                        private Option() {
                        }

                        public static final String VOLUME = "volume.description.option.volume";
                    }
                }

                public static final class Message {
                    private Message() {
                    }

                    public static final String SUCCESSFUL = "volume.message.successful";
                    public static final String INFO = "volume.message.info";
                }

                public static final class Error {
                    private Error() {
                    }

                    public static final String NO_FOUND_VOICE = "volume.error.no-found-voice";
                    public static final String NO_DM = "volume.error.no-dm";
                    public static final String OUT_OF_RANGE = "volume.error.out-of-range";
                }
            }
        }

        public static final class System {
            private System() {
            }

            public static final class Help {
                private Help() {
                }

                public static final String FILE = "command/system/help.yml";

                public static final class Description {
                    private Description() {
                    }

                    public static final String COMMAND = "help.description.command";
                }

                public static final class Command {
                    private Command() {
                    }

                    public static final class Embed {
                        private Embed() {
                        }

                        public static final String TITLE = "help.command.embed.title";
                        public static final String DESCRIPTION = "help.command.embed.description";
                        public static final String FOOTER = "help.command.embed.footer";

                        public static final class ActionRow {
                            private ActionRow() {
                            }

                            public static final String PLACEHOLDER = "help.command.embed.actionRow.placeholder";

                            public static final class Info {
                                private Info() {
                                }

                                public static final String TITLE = "help.command.embed.actionRow.info.title";
                                public static final String DESCRIPTION = "help.command.embed.actionRow.info.description";
                            }

                            public static final class CommandA {
                                private CommandA() {
                                }

                                public static final String TITLE = "help.command.embed.actionRow.command.title";
                                public static final String DESCRIPTION = "help.command.embed.actionRow.command.description";
                            }
                        }
                    }
                }

                public static final class Interacts {
                    private Interacts() {
                    }

                    public static final class Info {
                        private Info() {
                        }

                        public static final class Embed {
                            private Embed() {
                            }

                            public static final String TITLE = "help.interacts.info.embed.title";
                            public static final String DESCRIPTION = "help.interacts.info.embed.description";
                            public static final String FOOTER = "help.interacts.info.embed.footer";

                            public static final class Fields {
                                private Fields() {
                                }

                                public static final class Developers {
                                    private Developers() {
                                    }

                                    public static final String TITLE = "help.interacts.info.embed.fields.developers.title";
                                    public static final String DESCRIPTION = "help.interacts.info.embed.fields.developers.description";
                                }

                                public static final class ProgramLang {
                                    private ProgramLang() {
                                    }

                                    public static final String TITLE = "help.interacts.info.embed.fields.programLang.title";
                                    public static final String DESCRIPTION = "help.interacts.info.embed.fields.programLang.description";
                                }

                                public static final class DiscordLibs {
                                    private DiscordLibs() {
                                    }

                                    public static final String TITLE = "help.interacts.info.embed.fields.discordLibs.title";
                                    public static final String DESCRIPTION = "help.interacts.info.embed.fields.discordLibs.description";
                                }

                                public static final class Version {
                                    private Version() {
                                    }

                                    public static final String TITLE = "help.interacts.info.embed.fields.version.title";
                                    public static final String DESCRIPTION = "help.interacts.info.embed.fields.version.description";
                                }
                            }
                        }
                    }

                    public static final class Command {
                        private Command() {
                        }

                        public static final class Embed {
                            private Embed() {
                            }

                            public static final String TITLE = "help.interacts.command.embed.title";
                            public static final String DESCRIPTION = "help.interacts.command.embed.description";
                            public static final String FOOTER = "help.interacts.command.embed.footer";

                            public static final class ActionRow {
                                private ActionRow() {
                                }

                                public static final class Server {
                                    private Server() {
                                    }

                                    public static final String TITLE = "help.interacts.command.embed.actionRow.server.title";
                                    public static final String DESCRIPTION = "help.interacts.command.embed.actionRow.server.description";
                                }

                                public static final class Admin {
                                    private Admin() {
                                    }

                                    public static final String TITLE = "help.interacts.command.embed.actionRow.admin.title";
                                    public static final String DESCRIPTION = "help.interacts.command.embed.actionRow.admin.description";
                                }

                                public static final class Fun {
                                    private Fun() {
                                    }

                                    public static final String TITLE = "help.interacts.command.embed.actionRow.fun.title";
                                    public static final String DESCRIPTION = "help.interacts.command.embed.actionRow.fun.description";
                                }


                                public static final class Music {
                                    private Music() {
                                    }

                                    public static final String TITLE = "help.interacts.command.embed.actionRow.music.title";
                                    public static final String DESCRIPTION = "help.interacts.command.embed.actionRow.music.description";
                                }

                                public static final class User {
                                    private User() {
                                    }

                                    public static final String TITLE = "help.interacts.command.embed.actionRow.user.title";
                                    public static final String DESCRIPTION = "help.interacts.command.embed.actionRow.user.description";
                                }

                                public static final class Other {
                                    private Other() {
                                    }

                                    public static final String TITLE = "help.interacts.command.embed.actionRow.other.title";
                                    public static final String DESCRIPTION = "help.interacts.command.embed.actionRow.other.description";
                                }
                            }
                        }

                        public static final class Interact {
                            private Interact() {
                            }

                            public static final class Embed {
                                private Embed() {
                                }

                                public static final String TITLE = "help.interacts.command.interact.embed.title";
                                public static final String DESCRIPTION = "help.interacts.command.interact.embed.description";
                                public static final String FOOTER = "help.interacts.command.interact.embed.footer";

                                public static final class Category {
                                    private Category() {
                                    }

                                    public static final String BASE_PATH = "help.interacts.command.interact.embed.category.";
                                    public static final String SERVER = BASE_PATH + "server";
                                    public static final String ADMIN = BASE_PATH + "admin";
                                    public static final String FUN = BASE_PATH + "fun";
                                    public static final String MUSIC = BASE_PATH + "music";
                                    public static final String USER = BASE_PATH + "user";
                                    public static final String OTHER = BASE_PATH + "other";
                                    public static final String NOT_FOUND_CATEGORY = BASE_PATH + "notFoundCategory";
                                }

                                public static final class Fields {
                                    private Fields() {
                                    }

                                    public static final class Name {
                                        private Name() {
                                        }

                                        public static final String TITLE = "help.interacts.command.interact.embed.fields.name.title";
                                        public static final String DESCRIPTION = "help.interacts.command.interact.embed.fields.name.description";
                                    }

                                    public static final class Description {
                                        private Description() {
                                        }

                                        public static final String TITLE = "help.interacts.command.interact.embed.fields.description.title";
                                        public static final String DESCRIPTION = "help.interacts.command.interact.embed.fields.description.description";
                                    }

                                    public static final class Option {
                                        private Option() {
                                        }

                                        public static final String TITLE = "help.interacts.command.interact.embed.fields.options.title";
                                        public static final String DESCRIPTION = "help.interacts.command.interact.embed.fields.options.description";
                                        public static final String NOT_OPTION = "help.interacts.command.interact.embed.fields.options.not-option";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static final class Event {
        private Event() {
        }
        public static final class Threads {
            private Threads() {
            }
            public static final class Checklist {
                private Checklist() {
                }
                public static final String FILE = "event/threads/checklist.yml";
                public static final String TITLE = "checklist.title";
                public static final class Embed {
                    private Embed() {
                    }
                    public static final String TITLE = "checklist.embed.title";
                    public static final String DESCRIPTION = "checklist.embed.description";
                    public static final String FOOTER = "checklist.embed.footer";
                }
            }
            public static final class News {
                private News() {
                }
                public static final String FILE = "event/threads/news.yml";
                public static final String TITLE = "news.title";
                public static final class Embed {
                    private Embed() {
                    }
                    public static final String TITLE = "news.embed.title";
                    public static final String DESCRIPTION = "news.embed.description";
                    public static final String FOOTER = "news.embed.footer";
                }
            }
        }

        public static final class Level {
            private Level() {
            }

            public static final String FILE = "event/level.yml";

            public static final class Up {
                private Up() {
                }

                public static final class Embed {
                    private Embed() {
                    }

                    public static final String TITLE = "level.up.embed.title";
                    public static final String DESCRIPTION = "level.up.embed.description";
                    public static final String FOOTER = "level.up.embed.footer";
                }
            }
        }
    }

    public static final class System {
        private System() {
        }

        public static final String FILE = "system.yml";

        public static final class NoPermission {
            private NoPermission() {
            }

            public static final class Embed {
                private Embed() {
                }

                public static final String TITLE = "system.no-permission.embed.title";
                public static final String DESCRIPTION = "system.no-permission.embed.description";
                public static final String FOOTER = "system.no-permission.embed.footer";
            }
        }

        public static final class WrongError {
            private WrongError() {
            }

            public static final class Embed {
                private Embed() {
                }

                public static final String TITLE = "system.wrong-error.embed.title";
                public static final String DESCRIPTION = "system.wrong-error.embed.description";
                public static final String FOOTER = "system.wrong-error.embed.footer";
            }
        }

        public static final class DisableFunction {
            private DisableFunction() {
            }

            public static final class Embed {
                private Embed() {
                }

                public static final String TITLE = "system.disable-function.embed.title";
                public static final String DESCRIPTION = "system.disable-function.embed.description";
                public static final String FOOTER = "system.disable-function.embed.footer";
            }
        }
    }
}
