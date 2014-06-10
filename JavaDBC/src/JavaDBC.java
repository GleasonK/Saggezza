// File: JavaDBC.java
// Author: Kevin Gleason
// Date: 6/10/14
// Use: Learn to use JDBC


import org.postgresql.ssl.NonValidatingFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Properties;

public class JavaDBC {
    private String host,
            username,
            pass,
            port,
            dbName,
            dbms;


    public JavaDBC(String host, String username, String pass, String port, String dbName, String dbms)
            throws ClassNotFoundException{
        Class.forName("org.postgresql.Driver");
        this.host = host;
        this.username = username;
        this.pass = pass;
        this.port = port;
        this.dbName = dbName;
        this.dbms = dbms;
    }


    public Connection getConnection() throws  SQLException{
        Connection conn = null;
        Properties connProps = new Properties();
        connProps.put("user", this.username);
        connProps.put("password", this.pass);
        connProps.put("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        if (this.dbms.equals("mysql")){
            conn = DriverManager.getConnection("jdbc:" + this.dbms + "://" + this.host +  ":" +
                    this.port + "/", connProps);
        }
        if (this.dbms.equals("postgres")){
            // jdbc:postgresql://host:port/database
            conn = DriverManager.getConnection("jdbc:postgresql://" + this.host + ":" + this.port +
                    "/" + this.dbName, connProps);
        }
        if (this.dbms.equals("heroku")){
            try {
                URI dbUri = new URI("postgres://whuqviooogekcz:brKZUToptGzsVYlWohifAVUFxX@ec2-54-225-182-133.compute-1.amazonaws.com:5432/d9jqcvpddpasv3");
                System.out.println(dbUri.toString());
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                return DriverManager.getConnection(dbUrl, connProps);
                //return DriverManager.getConnection(dbUrl, username, password);
            }
            catch (URISyntaxException u) {}
        }
        System.out.println("Connected to DB " + this.dbName);
        return conn;
    }


    public void createSchema(String name, String authorized) throws SQLException{
        String queryString = "CREATE SCHEMA IF NOT EXISTS "+name+" AUTHORIZATION "+ authorized;
        executeQuery(queryString);
    }

    public void createCoffeeTable(String schema) throws SQLException{
        String queryString = "CREATE TABLE " + schema +".COFFEES" +
                "(COF_NAME varchar(32) NOT NULL, " +
                "SUP_ID int NOT NULL, " +
                "PRICE float NOT NULL, " +
                "SALES integer NOT NULL, " +
                "TOTAL integer NOT NULL, " +
                "PRIMARY KEY (COF_NAME));";
        executeQuery(queryString);
    }

    public void executeQuery(String query) throws SQLException{
        Statement stmt = null;
        Connection con = getConnection();
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Query Success");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public ResultSet executeQueryResults(String query) throws SQLException {
        Statement stmt = null;
        Connection con = getConnection();
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                for (int i = 1; i <= 5; i++)
                    System.out.print(rs.getString(i) + " \t ");
                System.out.println();
            }
            System.out.println("Query Success");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return rs;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException{
        //QUERIES
        String table = "data.coffees";
        String insertQuery = "INSERT INTO "+table+" (cof_name, sup_id, price, sales, total) VALUES ('KAG', 20, 2.0, 200, 2000);";
        String selectAllQuery = "SELECT * FROM " + table;


        //POSTGRES
        JavaDBC postgres = new JavaDBC("localhost", "postgres", "saggezza", "5432", "sampledata", "postgres");
//        postgres.createCoffeeTable("Data");
        ResultSet rs = postgres.executeQueryResults(selectAllQuery);

        //HEROKU
//        JavaDBC herokuPostgres = new JavaDBC("ec2-54-225-182-133.compute-1.amazonaws.com", "whuqviooogekcz",
//                "brKZUToptGzsVYlWohifAVUFxX", "5432", "d9jqcvpddpasv3", "heroku");
//        Connection conn = herokuPostgres.getConnection();
//

        //MYSQL
//        JavaDBC mysql = new JavaDBC("fdb3.biz.nf", "1532736_ds", "gleason20k", "3306", "1532736_ds","mysql");
//        Connection conn = mysql.getConnection();
    }
}
