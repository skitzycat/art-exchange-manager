import sun.util.calendar.BaseCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * User: test
 * Date: 3/9/11
 * Time: 7:03 PM
 * To change this template use File | Settings | File Templates.
 */

public class NewRoundDialog extends JDialog implements ActionListener {
    private JButton okButton_;
    private JButton cancelButton_;
    private JSpinner startEditor_;
    private JSpinner endEditor_;
    private JTextField nameField_;
    public boolean result_;

    public void actionPerformed(ActionEvent event) {
        if(event.getSource()==okButton_){
            result_=true;
            dispose();
        } else if (event.getSource()==cancelButton_){
            result_=false;
            dispose();
        }
    }

    public java.sql.Date getStartDate(){
        Date jDate = ((Date)startEditor_.getModel().getValue());
        java.sql.Date sqlDate = new java.sql.Date(jDate.getTime());
        return sqlDate;
    }

    public java.sql.Date getEndDate(){
        Date jDate = ((Date)endEditor_.getModel().getValue());
        java.sql.Date sqlDate = new java.sql.Date(jDate.getTime());
        return sqlDate;
    }

    public String getRoundName(){
        return nameField_.getText();
    }

    public NewRoundDialog(JFrame parent) {

        super(parent,"Create New Exchange",true);
        result_=false;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,2));

        JLabel nameLabel = new JLabel("Name of round");
        nameField_ = new JTextField();

        JLabel startLabel = new JLabel("Start date");

        startEditor_ = new JSpinner(new SpinnerDateModel());
        startEditor_.setEditor(new JSpinner.DateEditor(startEditor_, "MM/dd/yyyy"));

        JLabel endLabel = new JLabel("End date");

        endEditor_ = new JSpinner(new SpinnerDateModel());
        endEditor_.setEditor(new JSpinner.DateEditor(endEditor_,"MM/dd/yyyy"));

        okButton_ = new JButton("OK");
        okButton_.addActionListener(this);

        cancelButton_ = new JButton("Cancel");
        cancelButton_.addActionListener(this);

        setContentPane(panel);

        panel.add(nameLabel);
        panel.add(nameField_);
        panel.add(startLabel);
        panel.add(startEditor_);
        panel.add(endLabel);
        panel.add(endEditor_);
        panel.add(okButton_);
        panel.add(cancelButton_);

        //add(panel);
        pack();
        setVisible(true);
    }
}