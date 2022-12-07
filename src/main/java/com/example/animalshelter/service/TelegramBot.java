package com.example.animalshelter.service;

import com.example.animalshelter.configuration.ConfigurationBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final ConfigurationBot configurationBot;

    public TelegramBot(ConfigurationBot configurationBot) {
        this.configurationBot = configurationBot;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "запустить бота"));
        listOfCommands.add(new BotCommand("/info", "узнать о приюте"));
        listOfCommands.add(new BotCommand("/take_pet", "как взять собаку из приюта"));
        listOfCommands.add(new BotCommand("/report", "прислать отчет о питомце"));
        listOfCommands.add(new BotCommand("/call_volunteer", "позвать волонтера"));
        listOfCommands.add(new BotCommand("/more_info", "узнать больше о приюте"));
        listOfCommands.add(new BotCommand("/working_hours", "время работы приюта"));
        listOfCommands.add(new BotCommand("/regulations", "правила нахождения"));
        listOfCommands.add(new BotCommand("/add_contacts", "оставить контакты для связи,\n" +
                " в формате +7ХХХХХХХХХХ, мы свяжемся с Вами в ближайшее время"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return configurationBot.getBotName();
    }

    @Override
    public String getBotToken() {
        return configurationBot.getBotToken();
    }

    /**
     * Что должен делать бот, когда ему кто-то пишет
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();
            switch (messageText) {
                case "/start":
                    startCommand(chatId, userName);
                    break;
                case "/info":
                    infoCommand(chatId);
                    break;
                case "/more_info":
                    sendMessage(chatId, "Тут нужно добавить расширенный текст для описания " +
                            "общей информации о приюте");
                    break;
                case "/working_hours":
                    sendMessage(chatId, "Тут добавить текст с режимом работы приюта");
                    break;
                case "/regulations":
                    sendMessage(chatId, "Тут добавить текст с правилами нахождения на территории");
                case "/add_contacts":
                    // доделать этот кейс -> после подключения БД создать метод для добавления мобильного
                    // телефона и привязки его к пользователю.
                case "/call_volunteer":
                    sendMessage(chatId, "Волонтер свяжется с Вами в ближайшее время.");
                case "/take_pet":
                    takePetCommand(chatId);
                case "/transport":
                    sendMessage(chatId, "Тут добавить текст с рекомендациями по транспортировке.");
                case "/puppy":
                    sendMessage(chatId, "Сюда добавить текст для обустройства дома если щенок");
                case "/dog":
                    sendMessage(chatId, "Сюда добавить текст для обусьройства дома елси взрослая собака");
                case "/invalid_dog":
                    sendMessage(chatId,"Сюда добавить текст по обустройству дома дл ясобак-инфалидов");
                case "/cynologist":
                    sendMessage(chatId, "Сюда добавить текст с рекомендациями от кинологов");
                case "/our_/cynologist":
                    sendMessage(chatId,"Тут подумать над тем что будет отправляться : список кинологов, " +
                    "или же файл со списком, или может выгрузка из БД таблицы с кинологами, но для этого нужно будет " +
                            "еще создать его сущность");
                case "/refusal":
                    sendMessage(chatId, "Сюда текст с перечнем отказов в том, чтобы забрать питомца");
                case "/report":
                    // тут позже добавить логику по 3 этапу (работа после того как взяли питомца)
                default:
                    sendMessage(chatId, "Пока что данная функция не поддреживается");
            }
        }
    }

    /**
     * Приветственное сообщение от бота
     *
     * @param chatId
     * @param userName
     */
    private void startCommand(Long chatId, String userName) {

        String answer = "Приветствую " + userName + " . Я бот-помощник приюта для собак.\n\n" +
                "Как я могу Вам помочь?\n\n" +
                "Ниже приведены команды, либо можете выбрать команду из Menu\n" +
                "Команда /info поможет узнать больше о нашем приюте: где он находится, как и когда работает,\n" +
                "какие правила пропуска на территорию приюта, правила нахождения внутри и общения с собаками\n\n" +
                "Команда /take_pet поможет узнать как взять к себе питомца из приюта\n\n" +
                "Команда /report поможет отправить ежедневный отчет о питомце.";

        log.info("Ответили пользователю");
        sendMessage(chatId, answer);
    }

    /**
     * Если пользователь выбрал /info то выполняется данный метод
     * @param chatId
     */
    private void infoCommand(Long chatId) {
        String answerOnCommandInfo = "Если Вы хотите узнать больше о приюте, введите команду /more_info\n\n" +
                "Если Вас интересует время работы приюта, введите команду /working_hours\n\n" +
                "Если же Вас интересуют правила нахождения и обращения с собаками на территории приюта\n" +
                "введите команду /regulations";
        log.info("Ответили пользователю на команду /info");
        sendMessage(chatId, answerOnCommandInfo);
    }

    /**
     * Если пользователь выбрал /take_pet то выполняется данный метод
     * @param chatId
     */
    private void takePetCommand(Long chatId) {
        String answerOnTakePetCommand = "Если Вы узнать правила знакомства с собакой до того," +
                " как можно забрать ее из приюта, введите команду /acquaintance\n\n" +
                "Если Вам нужен список необходимых документов, для того, чтобы взять собаку из приюта\n" +
                "введите команду /documents\n\n" +
                "Если Вам нужны рекомендации по транспортировке животного, введите команду /transport\n\n" +
                "Если Вам нужны рекомендации по обустройству дома для щенка, введите команду /puppy\n\n" +
                "Если Вам нужны рекомендации по обустройству дома для взрослой собаки, введите команду /dog\n\n" +
                "Если Вам нужны рекомендации по обустройству дома для собак с ограниченными возможностями," +
                "введите команду /invalid_dog\n\n" +
                "Если Вам нужны советы кинолога по первичному общению с собакой, введите команду /cynologist\n\n" +
                "Если Вам нужна помощь проверенного кинолога, сотруднящего с нашим приютом, введите команду " +
                "/our_cynologist\n\n" +
                "Список причин, почему могут отказать и не дать забрать собаку из приюта /refusal\n\n" +
                "Для того, чтобы оставить свои контактые данные, введите команду /add_contacts";
        log.info("Ответили пользователю на команду /take_pet");
        sendMessage(chatId, answerOnTakePetCommand);
    }

    /**
     * Общий метод для отправки сообщений пользователю
     *
     * @param chatId
     * @param textToSend
     */
    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка " + e.getMessage());
        }
    }
}
