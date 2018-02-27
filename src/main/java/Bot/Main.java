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
import java.sql.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Main extends TelegramLongPollingBot{
    static final String DB_URL = "jdbc:postgresql://185.5.249.120:5432/sber_botan";
    static final String USER = "sb";
    static final String PASS = "qwe123";
    String savedMsg = "";

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
        savedMsg = txt;// Как сделать так, чтобы переменная хранила значение введенного аттрибута, а не значение кнопки?
        if (txt.equals("/start")) {
            sendMsg(msg, "Привет! Введите аббревиатуру, без кавычек");
        } else if (txt.equals("Добавить")){
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ep) {
                System.out.println("Where is your PostgreSQL JDBC Driver? "
                        + "Include in your library path!");
                ep.printStackTrace();
                return;
            }
            System.out.println("PostgreSQL JDBC Driver Registered!");
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement preparedStatement = null;
                preparedStatement = connection.prepareStatement("insert into needdecrypt (abbreviation) values (?)");
                preparedStatement.setString(1,savedMsg);
                preparedStatement.executeQuery();
                /*int count = 0;
                while (response.next()) {
                    String str = response.getString(1);
                    sendMsg(msg, str);
                    count++;
                }
                if (count==0){
                    sendButtons(msg, "Аббревиатура не найдена.");
                }
                response.close();*/
                preparedStatement.close();
            }
            catch (SQLException ex) {
                System.out.println("Connection Failed! Check output console");
                ex.printStackTrace();
                return;
            }
        } else
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ep) {
                System.out.println("Where is your PostgreSQL JDBC Driver? "
                        + "Include in your library path!");
                ep.printStackTrace();
                return;
            }
            System.out.println("PostgreSQL JDBC Driver Registered!");
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement preparedStatement = null;
                preparedStatement = connection.prepareStatement("select definition from dictionary where abbreviation = ?");
                preparedStatement.setString(1,txt);
                ResultSet response = preparedStatement.executeQuery();
                int count = 0;
                while (response.next()) {
                        String str = response.getString(1);
                        sendMsg(msg, str);
                        count++;
                        }
                if (count==0){
                    sendButtons(msg, "Аббревиатура не найдена.");
                    savedMsg = msg.getText();
                }
                response.close();
                preparedStatement.close();
            }
             catch (SQLException ex) {
                System.out.println("Connection Failed! Check output console");
                ex.printStackTrace();
                return;
            }
            if (connection != null) {
                System.out.println("You made it, take control your database now!");
            } else {
                System.out.println("Failed to make connection!");
            }
       /* if (txt.equals("ВСП")) {
            sendMsg(msg, "Внутреннее Структурное Подразделение");
        }*/
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

        try { //Чтобы не крашнулась программа при вылете Exception
            sendMessage(sendReplyMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
