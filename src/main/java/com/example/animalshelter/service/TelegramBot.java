package com.example.animalshelter.service;

import com.example.animalshelter.configuration.ConfigurationBot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        if (update.hasMessage()&&update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();
            switch (messageText){
                case "/start":
                        startCommand(chatId,userName);
                        break;
                default:
                        sendMessage(chatId,"Пока что данная функция не поддреживается");
            }
        }
    }

    /**
     * Приветственное сообщение от бота
     * @param chatId
     * @param userName
     */
    private void startCommand (Long chatId,String userName) {

        String answer = "Приветствую "+userName+" . Я бот-помощник приюта для собак." +
                " Чтобы Вы хотели узнать?";
        sendMessage(chatId, answer);
    }
    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e){

        }
    }
}
