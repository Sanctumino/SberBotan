package Bot;

import java.sql.*;

public class DAO {
    static final String DB_URL = "jdbc:postgresql://185.5.249.120:5432/sber_botan";
    static final String USER = "sb";
    static final String PASS = "qwe123";
    String returnAbbr = "";
    Connection connection;

    DAO (){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ep) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            ep.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver Registered!");
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAbbreviation(String savedMsg){
        //connection = null;
        try {
            PreparedStatement preparedStatement = null;
            preparedStatement = connection.prepareStatement("insert into needdecrypt (abbreviation) values (?)");
            preparedStatement.setString(1,savedMsg);
            preparedStatement.executeQuery();
            preparedStatement.close();
        }
        catch (SQLException ex) {
            System.out.println("Connection Failed! Check output console");
            ex.printStackTrace();
            return;
        }
    }
    public String findDefinition (String abbreviation) {
        //Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = null;
            preparedStatement = connection.prepareStatement("select definition from dictionary where abbreviation = ?");
            preparedStatement.setString(1, abbreviation);
            ResultSet response = preparedStatement.executeQuery();
            int count = 0;
            while (response.next()) {
                String str = response.getString(1);
                count++;
                returnAbbr = str;
            }
            preparedStatement.close();
            response.close();
        } catch (SQLException ex) {
            System.out.println("Connection Failed! Check output console");
            ex.printStackTrace();
        }
        return returnAbbr;
    }
    public void waitMessage(){
        try {
            wait();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
