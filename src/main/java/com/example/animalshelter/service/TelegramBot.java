package com.example.animalshelter.service;

import com.example.animalshelter.configuration.ConfigurationBot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final ConfigurationBot configurationBot;

    @Override
    public String getBotUsername() {
        return configurationBot.getBotName();
    }

    @Override
    public String getBotToken() {
        return configurationBot.getBotToken();
    }
    /** Что должен делать бот, когда ему кто-то пишет
     */
    @Override
    public void onUpdateReceived(Update update) {

    }
}
