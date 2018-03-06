package Bot;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
    String resultArray = "";

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
        if (txt.equals("/start")) {
            sendMsg(msg, "Привет! Введите аббревиатуру, без кавычек");
        } else if (txt.equals("Добавить")){
            Insert.addAbbreviation(savedMsg);
            sendMsg(msg, "Запрос успешно отправлен");
        } else if (txt.equals("Отмена")) {
            Wait.waitMessage();
        } else {
            txt = txt.toUpperCase();
            resultArray = FindDef.findDefinition(txt);
            if (resultArray.length()==0)
            {sendButtons(msg, "Аббревиатура не найдена. Добавить запрос на расшифровку?");}
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
       // Wait.waitMessage();
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

        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Добавить");
        keyboardFirstRow.add("Отмена");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);

        // Устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendReplyMessage.setChatId(msg.getChatId().toString());
        sendReplyMessage.setReplyToMessageId(msg.getMessageId());
        sendReplyMessage.setText(text);
        savedMsg = msg.getText();
        try { //Чтобы не крашнулась программа при вылете Exception
            sendMessage(sendReplyMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
