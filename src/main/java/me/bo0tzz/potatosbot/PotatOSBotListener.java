package me.bo0tzz.potatosbot;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableAudioMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by bo0tzz on 15-4-2016.
 */
public class PotatOSBotListener implements Listener {
    private final PotatOSBot main;
    private final Map<String, Consumer<CommandMessageReceivedEvent>> commands;

    public PotatOSBotListener(PotatOSBot main) {
        this.main = main;
        commands = new HashMap<String, Consumer<CommandMessageReceivedEvent>>(){{
            PotatOSBotListener that = PotatOSBotListener.this;
            put("get", that::getWAV);
            put("random", that::randomWAV);
        }};
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        commands.getOrDefault(event.getCommand(), (e) -> {}).accept(event);
    }

    private void getWAV(CommandMessageReceivedEvent event) {
        JSONArray results = ElasticSearchHook.getResults(event.getArgsString());
        JSONObject source = results.getJSONObject(0).getJSONObject("_source");
        String url = source.getString("url");
        String text = source.getString("text");
        InputFile inputFile = null;
        try {
            inputFile = new InputFile(new URL(url));
        } catch (MalformedURLException e) {
            event.getChat().sendMessage("Something went wrong while trying to get your result! If this happens again, please contact @bo0tzz", main.getBot());
            e.printStackTrace();
        }
        SendableAudioMessage message = SendableAudioMessage.builder().audio(inputFile).title(text).build();
        event.getChat().sendMessage(message, main.getBot());
    }

    private void randomWAV(CommandMessageReceivedEvent event) {
        JSONObject results = ElasticSearchHook.getRandom();
        JSONObject source = results.getJSONObject("_source");
        String url = source.getString("url");
        String text = source.getString("text");
        InputFile inputFile = null;
        try {
            inputFile = new InputFile(new URL(url));
        } catch (MalformedURLException e) {
            event.getChat().sendMessage("Something went wrong while trying to get your result! If this happens again, please contact @bo0tzz", main.getBot());
            e.printStackTrace();
        }
        SendableAudioMessage message = SendableAudioMessage.builder().audio(inputFile).title(text).build();
        event.getChat().sendMessage(message, main.getBot());
    }
}
