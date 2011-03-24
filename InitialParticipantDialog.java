
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: test
 * Date: 3/21/11
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class InitialParticipantDialog extends JDialog implements ActionListener {
    private boolean result_;
    private JButton addExistingToList_;
    private JButton removeFromList_;
    private JButton addNewToList_;
    private JTextField nameField_;
    private JTextField emailField_;

    public void actionPerformed(ActionEvent event){
        if(event.getSource() == addNewToList_) {

        }

    }

    public boolean getResult() {
        return result_;
    }

    public InitialParticipantDialog (JFrame parent) {
        super(parent,"Add Initial Participants to Round",true);

        result_=false;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

        JPanel newEntryPanel = new JPanel();
        newEntryPanel.setLayout(new BoxLayout(newEntryPanel,BoxLayout.X_AXIS));
        newEntryPanel.setBorder(BorderFactory.createTitledBorder("Add New Participant"));

        JLabel nameLabel = new JLabel("Name");
        nameField_ = new JTextField();
        JLabel emailLabel = new JLabel("Email");
        emailField_ = new JTextField();
        addNewToList_ = new JButton("Add To Round");
        addNewToList_.addActionListener(this);


        newEntryPanel.add(nameLabel);
        newEntryPanel.add(nameField_);
        newEntryPanel.add(emailLabel);
        newEntryPanel.add(emailField_);
        newEntryPanel.add(addNewToList_);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel,BoxLayout.X_AXIS));
        listPanel.setBorder(BorderFactory.createTitledBorder("Add Existing Participant"));

        mainPanel.add(newEntryPanel);
        mainPanel.add(listPanel);

        DefaultListModel availableListModel = new DefaultListModel();
        JList availableList = new JList(availableListModel);
        JScrollPane availableScroll = new JScrollPane(availableList);

        availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableList.setLayoutOrientation(JList.VERTICAL);
        availableList.setVisibleRowCount(-1);

        JPanel listAlterationPanel = new JPanel();
        listAlterationPanel.setLayout(new BoxLayout(listAlterationPanel,BoxLayout.Y_AXIS));
        addExistingToList_ = new JButton("->");
        removeFromList_ = new JButton("<-");
        listAlterationPanel.add(addExistingToList_);
        listAlterationPanel.add(removeFromList_);

        DefaultListModel inRoundListModel = new DefaultListModel();
        JList inRoundList = new JList(inRoundListModel);
        JScrollPane inRoundScroll = new JScrollPane(inRoundList);

        listPanel.add(availableScroll);
        listPanel.add(listAlterationPanel);
        listPanel.add(inRoundScroll);

        inRoundList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inRoundList.setLayoutOrientation(JList.VERTICAL);
        inRoundList.setVisibleRowCount(-1);

        setContentPane(mainPanel);

        setSize(new Dimension(400,400));

        pack();

        newEntryPanel.setMaximumSize(newEntryPanel.getSize());

        setVisible(true);
    }
}