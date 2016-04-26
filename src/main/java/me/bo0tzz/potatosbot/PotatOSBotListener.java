package me.bo0tzz.potatosbot;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.inline.send.content.InputTextMessageContent;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultAudio;
import pro.zackpollard.telegrambot.api.chat.message.send.ChatAction;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableAudioMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableChatAction;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        if (event.getQuery().getQuery().equals(""))
            return;
        System.out.println("Inline query received: " + event.getQuery().getQuery());
        JSONArray results = ElasticSearchHook.getResults(event.getQuery().getQuery());
        if (results == null) {
            System.out.println("Results was null");
            return;
        }

        List<InlineQueryResult> resultList = new ArrayList<>();
        System.out.println("Iterating over JSON");
        for (int i = 0; i < results.length(); i++) {
            JSONObject o = results.getJSONObject(i).getJSONObject("_source");
            String text = o.getString("text");
            URL url = null;
            try {
                url = new URL(o.getString("url"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                break;
            }
            InlineQueryResult r = InlineQueryResultAudio.builder()
                    .audioUrl(url)
                    .performer("GLaDOS") //temporary, until multichar is added
                    .title(text)
                    .build();
            resultList.add(r);
            System.out.println(i + " : " + r.toString());
        }

        InlineQueryResponse response = InlineQueryResponse.builder()
                .results(resultList)
                .build();

        System.out.println(response.toString());

        event.getQuery().answer(main.getBot(), response);
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        commands.getOrDefault(event.getCommand(), (e) -> {}).accept(event);
    }

    private void getWAV(CommandMessageReceivedEvent event) {
        JSONArray results = ElasticSearchHook.getResults(event.getArgsString());
        if (results == null) {
            event.getChat().sendMessage("I couldn't find anything! Maybe try a different query?");
            return;
        }

        event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOAD_AUDIO).build());

        JSONObject source = results.getJSONObject(0).getJSONObject("_source");
        String url = source.getString("url");
        String text = source.getString("text");
        InputFile inputFile = null;
        try {
            inputFile = new InputFile(new URL(url));
        } catch (MalformedURLException e) {
            event.getChat().sendMessage("Something went wrong while trying to get your result! If this happens again, please contact @bo0tzz");
            e.printStackTrace();
        }
        SendableAudioMessage message = SendableAudioMessage.builder().audio(inputFile).title(text).performer("GLaDOS").build();
        event.getChat().sendMessage(message);
    }

    private void randomWAV(CommandMessageReceivedEvent event) {
        event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOAD_AUDIO).build());

        JSONObject results = ElasticSearchHook.getRandom();
        JSONObject source = results.getJSONObject("_source");
        String url = source.getString("url");
        String text = source.getString("text");
        InputFile inputFile = null;
        try {
            inputFile = new InputFile(new URL(url));
        } catch (MalformedURLException e) {
            event.getChat().sendMessage("Something went wrong while trying to get your result! If this happens again, please contact @bo0tzz");
            e.printStackTrace();
        }
        SendableAudioMessage message = SendableAudioMessage.builder().audio(inputFile).title(text).performer("GLaDOS").build();
        event.getChat().sendMessage(message);
    }
}
