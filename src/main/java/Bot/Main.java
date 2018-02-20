package Bot;

import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import java.sql.*;
import java.lang.String;

public class Main extends TelegramLongPollingBot{
    static final String DB_URL = "jdbc:postgresql://185.5.249.120:5432/sber_botan";
    static final String USER = "sb";
    static final String PASS = "qwe123";

    public static void main(String[] args) {
        ApiContextInitializer.init(); // Инициализируем апи
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new Main());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getBotUsername() {
        return "SberBotan_bot";
        //возвращаем юзера
    }

    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage(); // Это нам понадобится
        String txt = msg.getText();
        if (txt.equals("/start")) {
            sendMsg(msg, "Привет! Введите аббревиатуру, без кавычек");
        } else {
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
                Statement request = connection.createStatement();
                ResultSet response = request.executeQuery("select definition from dictionary where abbreviation = '" + txt + "'");
                int count = 0;
                while (response.next()) {
                        String str = response.getString(1);
                        sendMsg(msg, str);
                        count++;
                        }
                if (count==0){
                    sendMsg(msg, "Аббреваиатура не найдена.");
                }
                response.close();
                request.close();
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
}
