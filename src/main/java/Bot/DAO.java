package Bot;

import java.sql.*;
import java.util.ArrayList;

public class DAO {
    static final String DB_URL = "jdbc:postgresql://185.5.249.120:5432/sber_botan";
    static final String USER = "sb";
    static final String PASS = "qwe123";
    String returnAbbr = "";
    ArrayList<String> definitionList = new ArrayList<String>();

    public void addAbbreviation(String savedMsg){
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
            preparedStatement.close();
        }
        catch (SQLException ex) {
            System.out.println("Connection Failed! Check output console");
            ex.printStackTrace();
            return;
        }
    }
    public String findDefinition (String abbreviation) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ep) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            ep.printStackTrace();
        }
        System.out.println("PostgreSQL JDBC Driver Registered!");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = null;
            preparedStatement = connection.prepareStatement("select definition from dictionary where abbreviation = ?");
            preparedStatement.setString(1, abbreviation);
            ResultSet response = preparedStatement.executeQuery();
            while (response.next()) {
                String str = response.getString(1);
                definitionList.add(str);
                replaceResultArray(definitionList);
            }
            definitionList.clear();
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
    public void replaceResultArray(ArrayList definitionList){
        returnAbbr = definitionList.toString();
        returnAbbr = returnAbbr.replace("[","");
        returnAbbr = returnAbbr.replace("]","");
        returnAbbr = returnAbbr.replace(",","");
    }
}
