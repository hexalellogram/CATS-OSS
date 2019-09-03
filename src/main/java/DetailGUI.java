import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import net.miginfocom.swing.*;
/*
 * Created by JFormDesigner on Wed Oct 10 10:27:17 PDT 2018
 */



/**
 * @author
 */
public class DetailGUI extends JPanel
{

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license
    private JFrame detailWindow;
    private JLabel titleLabel;
    private JTextField titleField;
    private JLabel priorityLabel;
    private JTextField priorityField;
    private JLabel timeSubmitLabel;
    private JTextField timeSubmitField;
    private JLabel locationLabel;
    private JTextField locationField;
    private JLabel addressLabel;
    private JTextField addressField;
    private JLabel detailedLocationLabel;
    private JTextField detailedLocationField;
    private JLabel contactInfoLabel;
    private JTextField contactInfoField;
    private JLabel descriptionLabel;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTextArea;
    private JButton closeButton;
    private JButton closeMarkCompleteButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private Issue myIssue;
    private JTable myParentTable;
    private int myRowInTable;
    private IssueDB myIssueDatabase;
    private ArrayList<Issue> myIssueList;

    /*
    Constructor for the DetailGUI class.
     */
    public DetailGUI(Issue issue, JTable parentTable, int rowInTable, IssueDB issueDatabase, ArrayList<Issue> issueList)
    {
        myIssue = issue;
        myParentTable = parentTable;
        myRowInTable = rowInTable;
        myIssueDatabase = issueDatabase;
        myIssueList = issueList;
        initComponents();
        populateFields();
    }

    /*
    Method that executes when the "Close" button is clicked.
     */
    private void closeButtonActionPerformed(ActionEvent e)
    {
        // Close the window.
        detailWindow.setVisible(false);
        detailWindow.dispose();
    }

    /*
    Method that executes when the "Close and Mark Complete" button is clicked.
     */
    private void closeMarkCompleteButtonActionPerformed(ActionEvent e)
    {
        // Get the index of the issue in the ArrayList.
        int index = myIssueList.indexOf(myIssue);

        // Flip the status to the opposite of what it was.
        myIssue.setStatus(!myIssue.getStatus());

        // Update the ArrayList of Issues and the JTable.
        myIssueList.set(index, myIssue);
        myParentTable.setValueAt(new Boolean(myIssue.getStatus()), myRowInTable, 4);

        // Close the DetailGUI window.
        detailWindow.setVisible(false);
    }

    /*
    Method that initializes GUI components.
     */
    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license
        detailWindow = new JFrame();
        titleLabel = new JLabel();
        titleField = new JTextField();
        priorityLabel = new JLabel();
        priorityField = new JTextField();
        timeSubmitLabel = new JLabel();
        timeSubmitField = new JTextField();
        locationLabel = new JLabel();
        locationField = new JTextField();
        addressLabel = new JLabel();
        addressField = new JTextField();
        detailedLocationLabel = new JLabel();
        detailedLocationField = new JTextField();
        contactInfoLabel = new JLabel();
        contactInfoField = new JTextField();
        descriptionLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionTextArea = new JTextArea();
        closeButton = new JButton();
        closeMarkCompleteButton = new JButton();

        //======== detailWindow ========
        {
            detailWindow.setTitle("Issue Details");
            detailWindow.setVisible(true);
            detailWindow.setMinimumSize(new Dimension(425, 610));
            Container detailWindowContentPane = detailWindow.getContentPane();
            detailWindowContentPane.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[115,grow,fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[grow]" +
                "[]"));

            //---- titleLabel ----
            titleLabel.setText("Issue Title:");
            detailWindowContentPane.add(titleLabel, "cell 0 0");

            //---- titleField ----
            titleField.setEditable(false);
            titleField.setBackground(Color.white);
            detailWindowContentPane.add(titleField, "cell 1 0");

            //---- priorityLabel ----
            priorityLabel.setText("Priority:");
            detailWindowContentPane.add(priorityLabel, "cell 0 1");

            //---- priorityField ----
            priorityField.setEditable(false);
            priorityField.setBackground(Color.white);
            detailWindowContentPane.add(priorityField, "cell 1 1");

            //---- timeSubmitLabel ----
            timeSubmitLabel.setText("Time Submitted:");
            detailWindowContentPane.add(timeSubmitLabel, "cell 0 2");

            //---- timeSubmitField ----
            timeSubmitField.setEditable(false);
            timeSubmitField.setBackground(Color.white);
            detailWindowContentPane.add(timeSubmitField, "cell 1 2");

            //---- locationLabel ----
            locationLabel.setText("Location:");
            detailWindowContentPane.add(locationLabel, "cell 0 3");

            //---- locationField ----
            locationField.setEditable(false);
            locationField.setBackground(Color.white);
            detailWindowContentPane.add(locationField, "cell 1 3");

            //---- addressLabel ----
            addressLabel.setText("Address:");
            detailWindowContentPane.add(addressLabel, "cell 0 4");

            //---- addressField ----
            addressField.setEditable(false);
            addressField.setBackground(Color.white);
            detailWindowContentPane.add(addressField, "cell 1 4");

            //---- detailedLocationLabel ----
            detailedLocationLabel.setText("Detailed Location:");
            detailWindowContentPane.add(detailedLocationLabel, "cell 0 5");

            //---- detailedLocationField ----
            detailedLocationField.setEditable(false);
            detailedLocationField.setBackground(Color.white);
            detailWindowContentPane.add(detailedLocationField, "cell 1 5");

            //---- contactInfoLabel ----
            contactInfoLabel.setText("Contact Information:");
            detailWindowContentPane.add(contactInfoLabel, "cell 0 6");

            //---- contactInfoField ----
            contactInfoField.setEditable(false);
            contactInfoField.setBackground(Color.white);
            detailWindowContentPane.add(contactInfoField, "cell 1 6");

            //---- descriptionLabel ----
            descriptionLabel.setText("Description:");
            detailWindowContentPane.add(descriptionLabel, "cell 0 7");

            //======== scrollPane1 ========
            {
                scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- descriptionTextArea ----
                descriptionTextArea.setEditable(false);
                descriptionTextArea.setWrapStyleWord(true);
                descriptionTextArea.setLineWrap(true);
                scrollPane1.setViewportView(descriptionTextArea);
            }
            detailWindowContentPane.add(scrollPane1, "cell 0 8 2 1,growy");

            //---- closeButton ----
            closeButton.setText("Close");
            closeButton.addActionListener(e -> closeButtonActionPerformed(e));
            detailWindowContentPane.add(closeButton, "cell 0 9,alignx left,growx 0");

            //---- closeMarkCompleteButton ----
            closeMarkCompleteButton.setText("Close and Mark Completed");
            closeMarkCompleteButton.addActionListener(e -> closeMarkCompleteButtonActionPerformed(e));
            detailWindowContentPane.add(closeMarkCompleteButton, "cell 1 9,alignx right,growx 0");
            detailWindow.pack();
            detailWindow.setLocationRelativeTo(detailWindow.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    /*
    Method that populates the text fields of a DetailGUI window, and other text-related settings.
     */
    private void populateFields()
    {
        // Set the text fields of the DetailGUI window appropriately.
        titleField.setText(myIssue.getTitle());
        priorityField.setText(myIssue.getPriority());
        timeSubmitField.setText(myIssue.getTimeString(myIssue.getTime()));
        locationField.setText(myIssue.getLocation());
        addressField.setText(myIssue.getAddress());
        detailedLocationField.setText(myIssue.getDetailedLocation());
        contactInfoField.setText(myIssue.getContact());
        descriptionTextArea.setText(myIssue.getDescription());

        // Set the text of the "Close and Mark Complete" button to "Close and Mark Incomplete" if the issue was
        // previously marked as complete.
        if (myIssue.getStatus() == Boolean.TRUE)
        {
            closeMarkCompleteButton.setText("Close and Mark Incomplete");
        }
    }

}
