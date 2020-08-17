/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import java.sql.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


/**
 *
 * @author Tennyson
 */
public class SQLiteJDBC {
    //JDBC driver name and Database URL
    public static final String JDBC_DRIVER  = "org.sqlite.JDBC";
    public static final String DB_URL       = "jdbc:sqlite:mycasematch.db";
    private static boolean hasData          = false;
    public static Connection connection     = null;
    public static Statement statement       = null;





    public static ResultSet displayRating() throws ClassNotFoundException, SQLException{
        if(connection == null){
            createConnection();
        }
        
        statement = connection.createStatement();
        String sql_select = "SELECT employee FROM case_match";
        ResultSet result_set = statement.executeQuery(sql_select);
        
        return result_set; 
        
        
    }
    
    public static void createConnection() throws ClassNotFoundException, SQLException{
       
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL);
            initialise();
    }
    
    public static void initialise()throws SQLException{
        if (!hasData){
            hasData = true;
            String sqlite_master    =   "SELECT name FROM sqlite_master WHERE type='table' AND name='case_match'";
            String sql              =   "CREATE TABLE case_match" +
                                        "(id integer PRIMARY KEY AUTOINCREMENT," +
                                        "employee text)";

            String sqlite_master_user    =   "SELECT name FROM sqlite_master WHERE type='table' AND name='user_session'";
            String sql_user              =   "CREATE TABLE user_session" +
                                        "(id integer PRIMARY KEY AUTOINCREMENT," +
                                        "email text," +
                                        "logged_in int," +
                                        "login_count int)";


            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(sqlite_master);
            ResultSet res_user = statement.executeQuery(sqlite_master_user);

            if(!res.next()){
                System.out.println("Building the case_match table with pre-populated values");
                Statement statement_2 = connection.createStatement();
                statement_2.execute(sql);
                statement_2.execute(sql_user);

                //inserting some sample data
                String sql_2 = "INSERT INTO case_match (employee) VALUES(?)";
                PreparedStatement prep = connection.prepareStatement(sql_2);
                prep.setString(1, "[hello]");
                prep.execute();
            }
            
        }
    }
    
    public static void updateNames(String employee) throws ClassNotFoundException, SQLException{
        if(sample.SQLiteJDBC.connection == null){
            createConnection();
        }
        String update_case_match = "UPDATE case_match SET employee=? WHERE id=?";
        PreparedStatement prep_2 = connection.prepareStatement(update_case_match);
        prep_2.setString(1, employee);
        prep_2.close();
    }


    
    public static ResultSet selectRecords()throws ClassNotFoundException, SQLException{
        if(connection == null){
            createConnection();
        }
        
        statement = connection.createStatement();
        String sql_select = "SELECT employee FROM case_match WHERE id= (SELECT MAX(id) FROM case_match Limit 1) Limit 1";
       
        ResultSet res = statement.executeQuery(sql_select);
        return res; 
    }

    public static ResultSet selectUserSession(String email)throws ClassNotFoundException, SQLException{
        if(connection == null){
            createConnection();
        }

        statement = connection.createStatement();
        String sql_select = "SELECT logged_in FROM user_session WHERE email = ?  Limit 1";

        PreparedStatement ps = connection.prepareStatement(sql_select);
        ps.setString(1,email);
        ResultSet res = ps.executeQuery();
        while ( res.next() )
        {
           Controller.isLoggedIn = res.getBoolean("logged_in");
        }
        res.close();
        ps.close();

        return res;
    }




    public static void insertNames(String employeeNames)throws ClassNotFoundException,SQLException{

        if(connection == null){
            createConnection();
        }

        System.out.println(employeeNames);
        String sql_3 = "INSERT INTO case_match (employee) VALUES (?)";
                PreparedStatement prep = connection.prepareStatement(sql_3);
                prep.setString(1, employeeNames);
                prep.execute();
    }


    public static void insertUserSession(String email, Boolean logged_in, int login_count)throws ClassNotFoundException,SQLException{

        if(connection == null){
            createConnection();
        }

        System.out.println(email);
        String sql_3 = "INSERT INTO user_session (email,logged_in, login_count) VALUES (?,?,?)";
        PreparedStatement prep = connection.prepareStatement(sql_3);
        prep.setString(1, email);
        prep.setBoolean(2, true);
        prep.setInt(3, login_count);
        prep.execute();

    }
    
}
