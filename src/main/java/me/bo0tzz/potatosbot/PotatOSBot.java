package me.bo0tzz.potatosbot;

import pro.zackpollard.telegrambot.api.TelegramBot;

/**
 * Created by bo0tzz on 15-4-2016.
 */
public class PotatOSBot {
    private final TelegramBot bot;

    public static void main(String[] args) {
        new PotatOSBot(args);
    }

    public PotatOSBot(String[] args) {
        String botKey = System.getenv("BOT_KEY");
        if (botKey == null || botKey.equals("")) {
            botKey = args[0];
        }
        this.bot = TelegramBot.login(botKey);
        bot.getEventsManager().register(new PotatOSBotListener(this));
        bot.startUpdates(false);
    }

    public TelegramBot getBot() {
        return bot;
    }
}
