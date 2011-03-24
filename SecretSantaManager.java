/**
 * Created by IntelliJ IDEA.
 * User: test
 * Date: 3/5/11
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */


import sun.swing.MenuItemLayoutHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class SecretSantaManager implements ActionListener{
    private JMenuItem openMenuItem_;
    private JMenuItem newMenuItem_;
    private JMenuItem newRoundMenuItem_;
    private JMenuItem addPartMenuItem_;
    private JFrame topFrame_;
    private JLabel exchangeLabel_;
    private JLabel roundLabel_;
    private DefaultTableModel exchangeTableModel_;
    private JTable exchangeTable_;
    private DefaultTableModel roundTableModel_;
    private JTable roundTable_;
    private String filename_;
    private Connection connection_;
    private int roundId_;

    public SecretSantaManager() {
        filename_=null;
    }

    public void actionPerformed(ActionEvent event){
        if(event.getSource()==openMenuItem_) {
            FileDialog dialog=new FileDialog(topFrame_,"Choose file to open",FileDialog.LOAD);
            dialog.show();
            String fileName=dialog.getFile();
            String directoryName=dialog.getDirectory();

            if(fileName!=null) {
                if(openDataBase(directoryName + fileName)) {
                    updateExchangeFile(directoryName + fileName);
                }
            }
            loadExchangeData();

        } else if (event.getSource()==newMenuItem_) {
            File file;
            String fileName;
            FileDialog dialog=new FileDialog(topFrame_,"Choose file to open",FileDialog.SAVE);
            dialog.show();
            fileName=dialog.getFile();

            if(fileName!=null) {
                String directoryName=dialog.getDirectory();
                file=new File(directoryName + fileName);
                if(file.exists()){
                    int result=JOptionPane.showConfirmDialog(topFrame_,
                            "File already exists, this will delete all existing data\nare you sure you want to do this?",
                            "File Exists!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if(result==JOptionPane.YES_OPTION) {
                        System.out.println("User has chosen to delete file");
                        file.delete();
                    } else {
                        System.out.println("not deleting file, doing nothing");
                        return;
                    }
                }
                if(createDataBase(directoryName + fileName)) {
                    updateExchangeFile(directoryName + fileName);
                }
            }
        } else if (event.getSource()==newRoundMenuItem_) {
            PreparedStatement statement;
            String roundName;
            int roundIndex;
            if(connection_!=null) {
            NewRoundDialog dialog=new NewRoundDialog(topFrame_);
                if(dialog.result_==true) {
                    roundName=dialog.getRoundName();
                    Date startDate=dialog.getStartDate();
                    Date endDate=dialog.getEndDate();

                    InitialParticipantDialog participantDialog = new InitialParticipantDialog(topFrame_);
                    if(participantDialog.getResult()) {
                        exchangeTableModel_.addRow(new Object[]{roundName,dialog.getStartDate(),dialog.getEndDate()});
                        updateRoundLabel(roundName);

                        try {
                            statement=connection_.prepareStatement("insert into round(start,end,name) values(?,?,?);");
                            statement.setDate(1,dialog.getStartDate());
                            statement.setDate(2,dialog.getEndDate());
                            statement.setString(3,roundName);
                        } catch (SQLException e) {
                            System.out.println("SQL Exception while preparing statement:");
                            System.out.println(e.getMessage());
                            return;
                        }

                        try {
                           statement.execute();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception while executing statement:");
                            System.out.println(e.getMessage());
                            System.out.println(e.getSQLState());
                            return;
                        }

                        // now figure out what the automatically generated key was
                        try {
                            statement=connection_.prepareStatement("select id from round where name=?");
                            statement.setString(1,roundName);
                            statement.execute();
                            roundIndex=statement.getResultSet().getInt(1);
                        } catch (SQLException e) {
                            return;
                        }
                        roundId_ = roundIndex;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(topFrame_,"A round may only be created if an exchange is already open","No exchange open",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateRoundLabel(String roundName){
        roundLabel_.setText("Viewing round: " + roundName);
    }

    private void updateExchangeFile(String filename) {
        filename_=filename;
        exchangeLabel_.setText("Exchange loaded: " + filename_);
    }

    private void loadExchangeData(){
        PreparedStatement statement;
        ResultSet resultSet;
        String name;
        Date start;
        Date end;
        try {
            statement = connection_.prepareStatement("select name, start, end from round;");
            statement.execute();
        } catch (SQLException e) {
            System.out.println("SQL error while executing query");
            return;
        }
        try {
            resultSet = statement.getResultSet();
            // advance to first row so we don't read it twice
            resultSet.next();
            while(resultSet.isAfterLast()==false){
                name=resultSet.getString("name");
                start=resultSet.getDate("start");
                end=resultSet.getDate("end");
                exchangeTableModel_.addRow(new Object[]{name,start,end});
                resultSet.next();
            }
        } catch (SQLException e){
            System.out.println("SQL error while fetching data");
            return;
        }
    }

    private boolean openDataBase(String filename) {
        Connection connection;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load JDBC drivers");
            return false;
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:"+filename);
        } catch (SQLException e) {
            System.out.println("Unable to get database connection");
            return false;
        }
        connection_=connection;
        return true;
    }

    private boolean createDataBase(String filename) {
        Connection connection;
        Statement statement;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load JDBC drivers");
            return false;
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:"+filename);
        } catch (SQLException e) {
            System.out.println("Unable to get database connection");
            return false;
        }
        System.out.println("database opened");
        try {
            statement = connection.createStatement();
            statement.executeUpdate("create table participant(id integer primary key, email varchar, name varchar);");
            statement.executeUpdate("create table round(id integer primary key, start date, end date, name varchar unique);");
            statement.executeUpdate("create table round_pairing(round_id int, participant_id int, turned_in int);");
        } catch (SQLException e) {
            System.out.println("Unable to create tables");
            return false;
        }
        connection_=connection;
        return true;
    }

    private void createAndShowGUI(){
        topFrame_ = new JFrame("Secret Santa Manager");
        topFrame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JMenuBar menuBar=createMenuBar();

        exchangeTableModel_ = new CustomTableModel();
        exchangeTableModel_.addColumn("Round Name");
        exchangeTableModel_.addColumn("Start Date");
        exchangeTableModel_.addColumn("End Date");

        exchangeTable_ = new JTable(exchangeTableModel_);
        //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        exchangeTable_.setFillsViewportHeight(true);
        exchangeTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane exchangeScrollPane = new JScrollPane(exchangeTable_);

        exchangeLabel_ = new JLabel("No exchange currently opened");

        roundLabel_ = new JLabel("No round currently loaded");

        roundTableModel_ = new CustomTableModel();
        roundTableModel_.addColumn("Participant");
        roundTableModel_.addColumn("Recipiant");
        roundTableModel_.addColumn("Turned in?");

        roundTable_ = new JTable(roundTableModel_);
        roundTable_.setFillsViewportHeight(true);
        roundTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane roundScrollPane = new JScrollPane(roundTable_);


        panel.add(exchangeLabel_);
        panel.add(exchangeScrollPane);
        panel.add(roundLabel_);
        panel.add(roundScrollPane);

        topFrame_.setJMenuBar(menuBar);
        topFrame_.getContentPane().add(panel);
        topFrame_.pack();
        topFrame_.setVisible(true);
        topFrame_.setSize(500, 500);
    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenuItem menuItem;

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        newMenuItem_ = new JMenuItem("New");
        newMenuItem_.addActionListener(this);
        fileMenu.add(newMenuItem_);

        openMenuItem_ = new JMenuItem("Open");
        openMenuItem_.addActionListener(this);
        fileMenu.add(openMenuItem_);

        JMenu roundMenu = new  JMenu("Round");
        menuBar.add(roundMenu);

        newRoundMenuItem_ = new JMenuItem("New Round");
        newRoundMenuItem_.addActionListener(this);
        roundMenu.add(newRoundMenuItem_);

        addPartMenuItem_ = new JMenuItem("Add Participant");


        menuItem = new JMenuItem("Quit");
        menuItem.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               System.exit(0);
          }
        });
        fileMenu.add(menuItem);


        return menuBar;
    }

    public static void main(String[] args){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SecretSantaManager app = new SecretSantaManager();
                app.createAndShowGUI();
            }
        });

    }
}