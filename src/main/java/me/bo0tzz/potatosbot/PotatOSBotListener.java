package me.bo0tzz.potatosbot;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
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
            put("search", that::search);
            put("random", that::random);
            put("announcer", that::announcer);
            put("caroline", that::caroline);
            put("cave", that::cave);
            put("core", that::core);
            put("defective", that::defective);
            put("glados", that::glados);
            put("turret", that::turret);
            put("wheatley", that::wheatley);
        }};
    }

    @Override
    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        if (event.getQuery().getQuery().equals(""))
            return;

        String arg = event.getQuery().getQuery().split(" ")[0];
        Character character = null;

        for (Character c : Character.values()) {
            String s = c.getName().split(" ")[0].toLowerCase();
            if (arg.equals(s)) {
                character = c;
                break;
            }
        }

        JSONArray results;

        if (!(character == null)) {
            results = ElasticSearchHook.getResults(character, event.getQuery().getQuery());
        } else {
            results = ElasticSearchHook.getResults(event.getQuery().getQuery());
        }

        if (results == null)
            return;

        List<InlineQueryResult> resultList = new ArrayList<>();
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
                    .performer(character.getName())
                    .title(text)
                    .build();
            resultList.add(r);
        }

        InlineQueryResponse response = InlineQueryResponse.builder()
                .results(resultList)
                .build();


        event.getQuery().answer(main.getBot(), response);
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        commands.getOrDefault(event.getCommand(), (e) -> {}).accept(event);
    }

    private void search(Character character, CommandMessageReceivedEvent event) {
        JSONArray results = ElasticSearchHook.getResults(character, event.getArgsString());
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
        SendableAudioMessage message = SendableAudioMessage.builder()
                .audio(inputFile)
                .title(text)
                .performer(character.getName())
                .build();
        event.getChat().sendMessage(message);
    }

    private void search(CommandMessageReceivedEvent event) {
        search(Character.ALL, event);
    }

    private void announcer(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.ANNOUNCER, event);
        } else {
            search(Character.ANNOUNCER, event);
        }
    }

    private void caroline(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.CAROLINE, event);
        } else {
            search(Character.CAROLINE, event);
        }
    }

    private void cave(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.CAVE_JOHNSON, event);
        } else {
            search(Character.CAVE_JOHNSON, event);
        }
    }

    private void core(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.CORE, event);
        } else {
            search(Character.CORE, event);
        }
    }

    private void defective(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.DEFECTIVE_TURRET, event);
        } else {
            search(Character.DEFECTIVE_TURRET, event);
        }
    }

    private void glados(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.GLADOS, event);
        } else {
            search(Character.GLADOS, event);
        }
    }

    private void turret(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.TURRET, event);
        } else {
            search(Character.TURRET, event);
        }
    }

    private void wheatley(CommandMessageReceivedEvent event) {
        if (event.getArgsString().equals("")) {
            random(Character.WHEATLEY, event);
        } else {
            search(Character.WHEATLEY, event);
        }
    }

    private void random(Character character, CommandMessageReceivedEvent event) {
        event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOAD_AUDIO).build());

        JSONObject results = ElasticSearchHook.getRandom(character);
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
        SendableAudioMessage message = SendableAudioMessage.builder()
                .audio(inputFile)
                .title(text)
                .performer(character.getName())
                .build();
        event.getChat().sendMessage(message);
    }

    private void random(CommandMessageReceivedEvent event) {
        random(Character.ALL, event);
    }
}
