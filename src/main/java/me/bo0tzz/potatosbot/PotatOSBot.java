package me.bo0tzz.potatosbot;

import pro.zackpollard.telegrambot.api.TelegramBot;

/**
 * Created by bo0tzz on 15-4-2016.
 */
public class PotatOSBot {
    private final TelegramBot bot;

    public static void main(String[] args) {
        new PotatOSBot(args[0]);
    }

    public PotatOSBot(String key) {
        this.bot = TelegramBot.login(key);
        bot.getEventsManager().register(new PotatOSBotListener(this));
        bot.startUpdates(false);
    }

    public TelegramBot getBot() {
        return bot;
    }

}
