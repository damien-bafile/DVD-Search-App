package dvd.search.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DVDSearchApp extends JFrame {

    JTextField txtTitle;
    JButton butSearch;
    JList list;
    JScrollPane scrollResults;
    ArrayList<String> dvdList;
    InputStream inputStream;

    public DVDSearchApp() throws FileNotFoundException, IOException {

        setTitle("DVD Search App");
        setLayout(null);

        DefaultListModel dlm = new DefaultListModel();
        list = new JList(dlm);
        dvdList = new ArrayList();
        txtTitle = new JTextField();
        txtTitle.setBounds(30, 30, 150, 25);
        butSearch = new JButton("Title Search");
        butSearch.setBounds(200, 30, 120, 25);
        scrollResults = new JScrollPane(list);
        scrollResults.setBounds(30, 85, 290, 150);

        Properties prop = new Properties();
        String propFileName = "config.properties";
        inputStream = new FileInputStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found.");
        }
        String driver = prop.getProperty("driver");
        String databaseType = prop.getProperty("database_type");
        String ip = prop.getProperty("database_ip");
        int port = Integer.parseInt(prop.getProperty("port"));
        String dbName = prop.getProperty("database_name");
        
        String connector = driver + ":" + databaseType + "://" + ip + ":" + port + "/" + dbName;
        System.out.println(connector);
        Connection con = null; // JDBC connection
        Statement stmt = null; // SQL statement object
        String query; // SQL query string
        ResultSet resultData = null; // results after SQL execution
        try {
            con = DriverManager.getConnection(connector, "root", ""); // connect to MySQL
            stmt = con.createStatement();
            query = "SELECT * FROM `titles`;";       // SQL SELECT statement
            resultData = stmt.executeQuery(query); // execute the SQL query

            while (resultData.next()) {
                dvdList.add(resultData.getString("TITLES"));
            }

            boolean next = true;
            while ((next = resultData.next()) != false) {
                dvdList.add(resultData.getString(1));
                System.out.println(dvdList.toString());
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        butSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    dlm.removeAllElements();

                    for (String curVal : dvdList) {
                        String tempVal = curVal.toLowerCase();
                        if (tempVal.contains(txtTitle.getText().toLowerCase())) {
                            dlm.addElement(curVal);
                        }
                    }
                } catch (Exception e) {
                    txtTitle.setText("Something Went Wrong!");
                    System.out.println(e);
                }
            }
        });

        add(txtTitle);
        add(butSearch);
        add(scrollResults);

        setSize(360, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            new DVDSearchApp();
        } catch (IOException ex) {
            Logger.getLogger(DVDSearchApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
