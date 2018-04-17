package Bot;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Main extends TelegramLongPollingBot{
    String savedMsg = "";
    DAO Insert = new DAO();
    DAO Wait = new DAO();
    DAO FindDef = new DAO();
    DAO AddDef = new DAO();
    String resultArray = "";
    Boolean needDef = false;
    Boolean checkCommand;

    public static void main(String[] args) {
        ApiContextInitializer.init(); // Инициализируем апи
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new Main());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    //@Override
    public String getBotUsername() {
        return "SberBotan_bot";
        //возвращаем юзера
    }

    //@Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage(); // Это нам понадобится
        String txt = msg.getText();
        txt = checkFindCommand(txt);
        if (needDef == true) {
            AddDef.addDefinition(savedMsg,txt);
            needDef = false;
            savedMsg = "";
            sendMsg(msg, "Перевод успешно отправлен");
        } else if (txt.equals("/start")) {
            sendMsg(msg, "Привет! Введите аббревиатуру, без кавычек");
        } else if (txt.equals("Добавить")) {
            Insert.addAbbreviation(savedMsg);
            sendMsg(msg, "Запрос успешно отправлен");
        } else if (txt.equals("Предложить перевод")) {
            sendMsg(msg,"Введите свой вариант перевода");
            needDef = true;
        } else if (txt.equals("Отмена")) {
            Wait.waitMessage();
        } else {
            txt = txt.toUpperCase();
            resultArray = FindDef.findDefinition(txt);
            if (resultArray.length()==0)
            {
                sendButtons1(msg, "Аббревиатура не найдена. Добавить запрос на расшифровку?");
            }
            else {
                sendMsg(msg,resultArray);
                FindDef.definitionList.clear();
                FindDef.returnAbbr = "";
            }
        }
    }

    @Override
    public String getBotToken() {
        return "476600922:AAHrTQiXjZVW5JMWgDHndO-evqMhVOpocO4";
        //Токен бота
    }

    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        s.setText(text);
        try { //Чтобы не крашнулась программа при вылете Exception
            sendMessage(s);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    private void sendButtons(Message msg, String text) {
        SendMessage sendReplyMessage = new SendMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendReplyMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Добавить");
        keyboardFirstRow.add("Отмена");
        keyboardSecondRow.add("Предложить перевод");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        // Устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendReplyMessage.setChatId(msg.getChatId().toString());
        sendReplyMessage.setReplyToMessageId(msg.getMessageId());
        sendReplyMessage.setText(text);
        savedMsg = checkFindCommand(msg.getText());
        savedMsg = savedMsg.toUpperCase();
        try { //Чтобы не крашнулась программа при вылете Exception
            sendMessage(sendReplyMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public String checkFindCommand (String inputString){
        String processedString = "";
        checkCommand = inputString.contains("/find ");
        if (checkCommand) {
            processedString = inputString.substring(6,inputString.length());
        } else {
            processedString = inputString;
        }
        return processedString;
    }

    private void sendButtons1(Message msg, String text) {
        SendMessage sendReplyMessage = new SendMessage();
        InlineKeyboardMarkup replyKeyboardMarkup = new InlineKeyboardMarkup();
        sendReplyMessage.setReplyMarkup(replyKeyboardMarkup);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> button1 = new ArrayList<InlineKeyboardButton>();
        button1.add(new InlineKeyboardButton().setText("Кнопка").setCallbackData("123"));
        buttons.add(button1);


        // Устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(buttons);
        sendReplyMessage.setChatId(msg.getChatId().toString());
        sendReplyMessage.setReplyToMessageId(msg.getMessageId());
        sendReplyMessage.setText(text);
        savedMsg = checkFindCommand(msg.getText());
        savedMsg = savedMsg.toUpperCase();
        try { //Чтобы не крашнулась программа при вылете Exception
            sendMessage(sendReplyMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
