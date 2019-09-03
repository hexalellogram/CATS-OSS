import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

// List of things that still need to be done:

/*
 * Created by JFormDesigner on Tue Oct 09 10:28:07 PDT 2018
 */
public class MainGUI extends JPanel
{
    // Private GUI objects
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license
    private JFrame mainWindow;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuChooseSpreadsheet;
    private JMenuItem menuSelectPriorityFile;
    private JMenuItem menuExportHumanText;
    private JMenuItem menuExportJSON;
    private JMenuItem menuImportJSON;
    private JMenuItem menuUploadData;
    private JMenuItem menuExitUpload;
    private JMenuItem menuExit;
    private JLabel catsLabel;
    private JButton reloadButton;
    private JButton loginButton;
    private JLabel onlineOfflineStatusLabel;
    private JLabel loadingLabel;
    private JLabel googleEmailLabel;
    private JLabel priorityFileLabel;
    private JScrollPane scrollPane;
    private JTable issueTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private IssueDB issueDatabase;
    private SpreadsheetParser parser;
    private File priorityWordsFile;

    /*
    Constructor for the MainGUI class.
     */
    public MainGUI()
    {
        try
        {
            // Set the Look and Feel to the System LAF, which results in a more native look for the application.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        }
        catch (Exception e)
        {
            // Print any error message that results to the console.
            e.printStackTrace();
        }
        // Initialize GUI components.
        initComponents();
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        issueDatabase = new IssueDB(null, "");
        priorityWordsFile = null;
        try
        {
            setupDateColumn();
        }
        catch(IllegalArgumentException e)
        {
            /*
            Empty catch block, because this WILL throw an exception by design. The date column does not have a value in
            it upon first run (which is intentional since there is no data available). Attempting to format this empty
            value will result in the IllegalArgument Exception.
             */
        }
        setupTableSorting();
    }



    /*
    Method that executes when the Login to Google button in the UI is clicked.
     */
    private void loginButtonActionPerformed(ActionEvent e)
    {
        // This if statement executes only if the user is not currently logged in.
        if (loginButton.getText().equals("Login to Google"))
        {
            // Set the email label to indicate that there is a login in progress.
            googleEmailLabel.setText("Google login in progress...");
            googleEmailLabel.repaint();

            // Create a new SpreadsheetParser.
            parser = new SpreadsheetParser();

            // Use parser to create a new IssueDB. Sheet ID is blank until the JOptionPane receives a URL later.
            issueDatabase = new IssueDB(parser, "");

            // Define the list of GUI objects to be used in threaded execution.
            ArrayList<JComponent> listGUIObjects = defineGUIComponentsArray();

            // Create a new thread to handle login without freezing up the GUI.
            ThreadManager loginManager = new LoginManager(issueDatabase, parser, listGUIObjects, mainWindow,
                    this, priorityWordsFile);
            Thread loginThread = new Thread(loginManager);

            // Start the thread. This will run the run() method which downloads new data from Google Sheets.
            loginThread.start();
        }
        else
        {
            // Clear the table as the user is now logged out, and we do not want to retain their data.
            clearTable();

            // Destroy the SpreadsheetParser, which contains the Credential and other critical login data.
            parser = null;

            // Reset the email label to no longer display the user's email.
            googleEmailLabel.setText("Not Signed Into Google");

            // Reset the login button's behavior to the log in behavior.
            loginButton.setText("Login to Google");

            // Disable the "Reload Issues" button and the "Upload Statuses", "Choose Spreadsheet", and "Upload Changes
            // and Exit" menu selections as the user is now signed out.
            reloadButton.setEnabled(false);
            menuChooseSpreadsheet.setEnabled(false);
            menuUploadData.setEnabled(false);
            menuExitUpload.setEnabled(false);

            // Reset loading label.
            loadingLabel.setText("No data download in progress.");

            // Reset Online Mode Label.
            onlineOfflineStatusLabel.setText("Working in Offline Mode");
        }
    }

    /*
    Method that executes when the "Exit Without Uploading Changes" menu selection is clicked.
     */
    private void menuExitActionPerformed(ActionEvent e)
    {
        // Set the window to be invisible.
        mainWindow.setVisible(false);

        // Dispose of the window to conserve system resources.
        mainWindow.dispose();

        // Exit the program. Status 0 means a clean exit.
        System.exit(0);
    }

    /*
    Method that executes when the Reload Issues button is clicked.
     */
    private void reloadButtonActionPerformed(ActionEvent e)
    {
        // Define the list of GUI objects to be used in threaded execution.
        ArrayList<JComponent> listGUIObjects = defineGUIComponentsArray();

        // Create a new thread to handle reload without freezing up the GUI.
        ReloadManager reloadManager = new ReloadManager(issueDatabase, parser, listGUIObjects, mainWindow,
                this, priorityWordsFile);
        Thread reloadThread = new Thread(reloadManager);

        // Start the thread. This will run the run() method which downloads new data from Google Sheets.
        reloadThread.start();
    }

    /*
    Method that executes when a row in the table is clicked.
     */
    private void issueTableMouseClicked(MouseEvent e)
    {
        try
        {
            // Determine the column that was clicked.
            JTable tableClicked = (JTable) e.getSource();
            int col = tableClicked.columnAtPoint(e.getPoint());

            // If the table row was double-clicked, then this if statement will execute.
            if (e.getClickCount() == 2 && col != 4)
            {
                // Get the row so we can find the Issue in the ArrayList.);
                int row = tableClicked.getSelectedRow();

                // Get the Issue from the ArrayList.
                Date timestampDate = (Date)tableClicked.getValueAt(row, 2);
                String timestamp = Issue.getTimeString(timestampDate);
                String title = (String)tableClicked.getValueAt(row, 0);
                Issue selectedIssue = issueDatabase.findIssueGivenParams(timestamp, title);

                // Create a new DetailGUI with the Issue.
                DetailGUI details = new DetailGUI(selectedIssue, tableClicked, row, issueDatabase,
                        issueDatabase.getIssues());
            }

            /*
            If the column is the "Issue Addressed?" column and it was only a single click,
            this if statement executes. We want to ignore double clicks since those are intended to open the details
            GUI instead of changing the state of the checkbox.
             */
            else if (col == 4 && e.getClickCount() == 1)
            {
                // Get the Issue in this row, and its index in the ArrayList.
                int row = tableClicked.rowAtPoint(e.getPoint());
                Date timestampDate = (Date)tableClicked.getValueAt(row, 2);
                String timestamp = Issue.getTimeString(timestampDate);
                String title = (String)tableClicked.getValueAt(row, 0);
                Issue currentIssue = issueDatabase.findIssueGivenParams(timestamp, title);
                int index = issueDatabase.getIssues().indexOf(currentIssue);

                // Flip the status to the opposite.
                currentIssue.setStatus(!currentIssue.getStatus());

                // Update the database with the updated issue.
                issueDatabase.getIssues().set(index, currentIssue);
            }
        }
        catch (NullPointerException npe)
        {
            // Nothing needs to happen here. This catch is only here to prevent errors if the parser is null, which
            // happens if the user has not logged in yet.
        }
    }
    /*
    Method that executes when the "Choose Spreadsheet" menu selection is clicked.
     */
    private void menuChooseSpreadsheetActionPerformed(ActionEvent e)
    {
        // Show the input dialog and store the result as a String.
        String newURL = JOptionPane.showInputDialog("Enter a Google Spreadsheet URL:");

        // Update the Issue Database with the new URL.
        issueDatabase.setID(newURL);

        // Clear out the table and re-populate it.
        clearTable();
        issueDatabase.setIssues(fillDatabase());
        updatePrioritiesInTableAndList(priorityWordsFile);
        populateTable();
    }

    /*
    Method that executes when the "Export to JSON File" menu selection is clicked.
     */
    private void menuExportJSONActionPerformed(ActionEvent e)
    {
        /*
        Set up a new JFileChooser with the following parameters:
        - Default directory is the path to the .jar.
        - Dialog type is a Save dialog.
        - User can select files only. Directories cannot be selected.
        - Multiple item selection is disabled.
        - Item filtering is enabled, with only JSON files are accepted.
         */
        try
        {
            String parentDirectory = new File(MainGUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getAbsolutePath();
            JFileChooser fileChooser = new JFileChooser(parentDirectory);
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter typeFilter = new FileNameExtensionFilter("JSON Files", "json");
            fileChooser.setFileFilter(typeFilter);

            // Show the previously created JFileChooser.
            int result = fileChooser.showSaveDialog(mainWindow);

            // If the user clicks save, then this if statement executes.
            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the selected file and filename.
                File exportFile = fileChooser.getSelectedFile();
                String fileName = exportFile.getName();

                // If the filename that the user has provided does not have a .json extension, add it.
                if (!exportFile.getName().substring(fileName.length() - 5, fileName.length()).toLowerCase().equals(".json")) {
                    exportFile = new File(exportFile.getAbsoluteFile() + ".json");
                }
                try {
                    // Create a new file at this location.
                    exportFile.createNewFile();
                } catch (Exception exception) {
                    // Print any error message that results to the console.
                    exception.printStackTrace();
                }

                // Write the contents of the Issue database to the JSON file.
                issueDatabase.exportJSON(exportFile.getAbsolutePath());
            }
        }
        catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        }
    }

    /*
    Method that executes when the "Import from JSON File" menu selection is clicked.
     */
    private void menuImportJSONActionPerformed(ActionEvent e)
    {
        /*
        Set up a new JFileChooser with the following parameters:
        - Default directory is the path to the .jar.
        - Dialog type is an Open dialog.
        - User can select files only. Directories cannot be selected.
        - Multiple item selection is disabled.
        - Item filtering is enabled, with only JSON files are accepted.
         */
        try {
            String parentDirectory = new File(MainGUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getAbsolutePath();
            JFileChooser fileChooser = new JFileChooser(parentDirectory);
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter typeFilter = new FileNameExtensionFilter("JSON Files", "json");
            fileChooser.setFileFilter(typeFilter);

            // Show the previously created JFileChooser.
            int result = fileChooser.showOpenDialog(mainWindow);

            // If the user clicks the "open" option, this if statement executes.
            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the file that the user selected.
                File importFile = fileChooser.getSelectedFile();

                // Import the contents of the JSON file into the table.
                issueDatabase.importJSON(importFile);
            }

            // Populate the GUi table with the contents of the Issue database.
            populateTable();
        }
        catch (URISyntaxException ex)
            {
                ex.printStackTrace();
            }
    }

    /*
    Method that executes when the "Export to Human Readable Text File" option is clicked in the menu.
     */
    private void menuExportHumanTextActionPerformed(ActionEvent e)
    {
        /*
        Set up a new JFileChooser with the following parameters:
        - Default directory is the path to the .jar.
        - Dialog type is an Open dialog.
        - User can select files only. Directories cannot be selected.
        - Multiple item selection is disabled.
        - Item filtering is enabled, with only text files are accepted.
         */
        try {
            String parentDirectory = new File(MainGUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getAbsolutePath();
            JFileChooser fileChooser = new JFileChooser(parentDirectory);
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter typeFilter = new FileNameExtensionFilter("Text Files", "txt");
            fileChooser.setFileFilter(typeFilter);

            // Show the newly created JFileChooser.
            int result = fileChooser.showSaveDialog(mainWindow);

            // If the user clicks the "save" option then this if statement executes
            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the selected file and filename.
                File exportFile = fileChooser.getSelectedFile();
                String fileName = exportFile.getName();

                // If the filename that the user provided does not have a .txt extension, append one to the filename.
                if (!exportFile.getName().substring(fileName.length() - 4, fileName.length()).toLowerCase().equals(".txt")) {
                    exportFile = new File(exportFile.getAbsoluteFile() + ".txt");
                }
                try {
                    // Create a new file at the location that the user selected.
                    exportFile.createNewFile();

                    // Create a FileWriter to write to the text file.
                    FileWriter writer = new FileWriter(exportFile);

                    // Get the list of Issues to run through and loop through it.
                    ArrayList<Issue> issueList = issueDatabase.getIssues();
                    for (int i = 0; i < issueList.size(); i++) {
                        // Get the current issue.
                        Issue currentIssue = issueList.get(i);

                        // Get the current issue as a string. The \r\n\r\n is appended to it since Windows text files use
                        // CRLF style carriage returns, so \n is insufficient for displaying the text file in Windows
                        // Notepad. Without the \r, Notepad will display all the text as a single line.
                        String toWrite = currentIssue.toString() + "\r\n\r\n";

                        // Write the issue to the text file.
                        writer.write(toWrite);
                    }
                    // Close the writer to save system resources.
                    writer.close();
                } catch (Exception exception) {
                    // Print any error message that results to the console.
                    exception.printStackTrace();
                }
            }
        }
        catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        }
    }

    /*
    Method that executes when the "Upload Data to Google Spreadsheet" menu selection is clicked.
     */
    private void menuUploadDataActionPerformed(ActionEvent e)
    {
        try
        {
            // Upload the new statuses to the Google Sheet. Whatever is in the CATS GUI takes precedence and overwrites
            // any conflicting data in Google Sheets.
            issueDatabase.uploadData();
        }
        catch (Exception exception)
        {
            // Print any error message that results to the console.
            exception.printStackTrace();
        }

    }

    /*
    Method that executes when the "Upload Changes and Exit" menu selection is clicked.
     */
    private void menuExitUploadActionPerformed(ActionEvent e)
    {
        try
        {
            // Upload the new statuses to the Google Sheet. Whatever is in the CATS GUI takes precedence and overwrites
            // any conflicting data in Google Sheets.
            issueDatabase.uploadData();
        }
        catch (Exception exception)
        {
            // Print any error message that results to the console.
            exception.printStackTrace();
        }

        // Set the window to be invisible.
        mainWindow.setVisible(false);

        // Dispose of the window to conserve system resources.
        mainWindow.dispose();

        // Exit the program. Status 0 means a clean exit.
        System.exit(0);
    }

    /*
    Method that executes when the "Select High Priority Words File" menu selection is clicked.
     */
    private void menuSelectPriorityFileActionPerformed(ActionEvent e)
    {
        /*
        Set up a new JFileChooser with the following parameters:
        - Default directory is the path to the .jar.
        - Dialog type is an Open dialog.
        - User can select files only. Directories cannot be selected.
        - Multiple item selection is disabled.
        - Item filtering is enabled, with only text files are accepted.
         */
        try {
            String parentDirectory = new File(MainGUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getAbsolutePath();
            JFileChooser fileChooser = new JFileChooser(parentDirectory);
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter typeFilter = new FileNameExtensionFilter("Text Files", "txt");
            fileChooser.setFileFilter(typeFilter);

            int result = fileChooser.showOpenDialog(mainWindow);

            // If the user clicks the "open" option, this if statement executes.
            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the file that the user selected.
                priorityWordsFile = fileChooser.getSelectedFile();

                // Set the priority file label.
                priorityFileLabel.setText("High Priority Words File Set To: " + priorityWordsFile.getAbsolutePath());

                // Update the priorities in the table.
                updatePrioritiesInTableAndList(priorityWordsFile);
            }
        }
        catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        }
    }

    /*
    Method that initializes all the components of the GUI.
     */
    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license
        mainWindow = new JFrame();
        menuBar = new JMenuBar();
        menu = new JMenu();
        menuChooseSpreadsheet = new JMenuItem();
        menuSelectPriorityFile = new JMenuItem();
        menuExportHumanText = new JMenuItem();
        menuExportJSON = new JMenuItem();
        menuImportJSON = new JMenuItem();
        menuUploadData = new JMenuItem();
        menuExitUpload = new JMenuItem();
        menuExit = new JMenuItem();
        catsLabel = new JLabel();
        reloadButton = new JButton();
        loginButton = new JButton();
        onlineOfflineStatusLabel = new JLabel();
        loadingLabel = new JLabel();
        googleEmailLabel = new JLabel();
        priorityFileLabel = new JLabel();
        scrollPane = new JScrollPane();
        issueTable = new JTable();

        //======== mainWindow ========
        {
            mainWindow.setVisible(true);
            mainWindow.setTitle("Cyber Activity Tracking System (CATS)");
            mainWindow.setMinimumSize(new Dimension(685, 485));
            Container mainWindowContentPane = mainWindow.getContentPane();
            mainWindowContentPane.setLayout(new MigLayout(
                "fill,hidemode 3",
                // columns
                "[fill]" +
                "[50:n,grow,fill]" +
                "[100:n,grow,shrink 0,fill]" +
                "[75:n,grow,shrink 0,right]",
                // rows
                "[]0" +
                "[]0" +
                "[]" +
                "[]" +
                "[grow,fill]" +
                "[]"));

            //======== menuBar ========
            {

                //======== menu ========
                {
                    menu.setText("Menu");

                    //---- menuChooseSpreadsheet ----
                    menuChooseSpreadsheet.setText("Choose Google Spreadsheet");
                    menuChooseSpreadsheet.setEnabled(false);
                    menuChooseSpreadsheet.addActionListener(e -> menuChooseSpreadsheetActionPerformed(e));
                    menu.add(menuChooseSpreadsheet);
                    menu.addSeparator();

                    //---- menuSelectPriorityFile ----
                    menuSelectPriorityFile.setText("Select High Priority Words File");
                    menuSelectPriorityFile.addActionListener(e -> menuSelectPriorityFileActionPerformed(e));
                    menu.add(menuSelectPriorityFile);
                    menu.addSeparator();

                    //---- menuExportHumanText ----
                    menuExportHumanText.setText("Export to Human Readable Text File");
                    menuExportHumanText.addActionListener(e -> menuExportHumanTextActionPerformed(e));
                    menu.add(menuExportHumanText);

                    //---- menuExportJSON ----
                    menuExportJSON.setText("Export to JSON File");
                    menuExportJSON.addActionListener(e -> menuExportJSONActionPerformed(e));
                    menu.add(menuExportJSON);

                    //---- menuImportJSON ----
                    menuImportJSON.setText("Import from JSON File");
                    menuImportJSON.addActionListener(e -> menuImportJSONActionPerformed(e));
                    menu.add(menuImportJSON);
                    menu.addSeparator();

                    //---- menuUploadData ----
                    menuUploadData.setText("Upload Data to Google Spreadsheet");
                    menuUploadData.setEnabled(false);
                    menuUploadData.addActionListener(e -> menuUploadDataActionPerformed(e));
                    menu.add(menuUploadData);

                    //---- menuExitUpload ----
                    menuExitUpload.setText("Upload Changes and Exit");
                    menuExitUpload.setEnabled(false);
                    menuExitUpload.addActionListener(e -> menuExitUploadActionPerformed(e));
                    menu.add(menuExitUpload);

                    //---- menuExit ----
                    menuExit.setText("Exit Without Uploading Changes");
                    menuExit.addActionListener(e -> menuExitActionPerformed(e));
                    menu.add(menuExit);
                }
                menuBar.add(menu);
            }
            mainWindow.setJMenuBar(menuBar);

            //---- catsLabel ----
            catsLabel.setText("Cyber Activity Tracking System (CATS)");
            mainWindowContentPane.add(catsLabel, "cell 0 1,align left center,grow 0 0");

            //---- reloadButton ----
            reloadButton.setText("Reload Issues");
            reloadButton.setEnabled(false);
            reloadButton.addActionListener(e -> reloadButtonActionPerformed(e));
            mainWindowContentPane.add(reloadButton, "cell 1 1,alignx left,growx 0");

            //---- loginButton ----
            loginButton.setText("Login to Google");
            loginButton.addActionListener(e -> loginButtonActionPerformed(e));
            mainWindowContentPane.add(loginButton, "cell 3 1");

            //---- onlineOfflineStatusLabel ----
            onlineOfflineStatusLabel.setText("Working in Offline Mode");
            mainWindowContentPane.add(onlineOfflineStatusLabel, "cell 0 2");

            //---- loadingLabel ----
            loadingLabel.setText("No data download in progress.");
            mainWindowContentPane.add(loadingLabel, "cell 1 2,alignx left,growx 0");

            //---- googleEmailLabel ----
            googleEmailLabel.setText("Not Signed Into Google");
            mainWindowContentPane.add(googleEmailLabel, "cell 3 2,alignx right,growx 0");

            //---- priorityFileLabel ----
            priorityFileLabel.setText("No High Priority Words File Selected");
            mainWindowContentPane.add(priorityFileLabel, "cell 0 3 4 1");

            //======== scrollPane ========
            {
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- issueTable ----
                issueTable.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null, true},
                    },
                    new String[] {
                        "Issue Title", "Priority", "Time Submitted", "Location", "Issue Addressed?"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        String.class, String.class, Date.class, String.class, Boolean.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false, false, true
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                issueTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        issueTableMouseClicked(e);
                    }
                });
                scrollPane.setViewportView(issueTable);
            }
            mainWindowContentPane.add(scrollPane, "cell 0 4 4 1,growx");
            mainWindow.pack();
            mainWindow.setLocationRelativeTo(mainWindow.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    /*
    Method that fills the table with Issues and text.
     */
    public void populateTable()
    {
        // Get the ArrayList of Issues.
        ArrayList<Issue> issueArrayList = issueDatabase.getIssues();

        // Set a counter variable to keep track of where we are in the rows.
        int rows = issueTable.getRowCount();
        int currRow = 0;

        // Loop through the ArrayList of Issues.
        for (Issue currIssue : issueArrayList)
        {
            // If a new row needs to be added, this if statement executes.
            if(currRow == rows)
            {
                // Access the table model to modify the table structure.
                DefaultTableModel model = (DefaultTableModel)issueTable.getModel();

                // Add a new row to the table.
                model.addRow(new Object[]{"Title", "Priority", "Time Submitted", "Location", "Issue Addressed?"});

                // Trigger the GUI to update with the new row.
                model.fireTableDataChanged();

                // Update the row count.
                rows = issueTable.getRowCount();
            }

            // Set a row of the table to be the contents of this issue.
            issueTable.setValueAt(currIssue.getTitle(), currRow, 0);
            issueTable.setValueAt(currIssue.getPriority(), currRow, 1);
            issueTable.setValueAt(currIssue.getTime(), currRow, 2);
            issueTable.setValueAt(currIssue.getLocation(), currRow, 3);
            issueTable.setValueAt(new Boolean(currIssue.getStatus()), currRow, 4);

            // Update the counter variable to move us on to the next row.
            currRow++;
        }
    }

    /*
    Method that clears the table and resets it to have 0 rows and no Issues.
     */
    public void clearTable()
    {
        // Access the table model to modify the table structure.
        DefaultTableModel model = (DefaultTableModel)issueTable.getModel();

        // Clear existing rows away.
        model.setRowCount(0);

        // Trigger the GUI to update with the new cleared table data.
        model.fireTableDataChanged();
    }

    /*
    Method that fills the IssueDB with Issues parsed from the Google Spreadsheet.
     */
    public ArrayList<Issue> fillDatabase()
    {
        // Create an ArrayList that we will eventually fill with Issues.
        ArrayList<Issue> toReturn = new ArrayList<Issue>();
        try
        {
            // Parse the first row to generate a HashMap for navigation. This method must be called before parseRow.
            parser.parseFirstRow(issueDatabase.getID());

            // Set a boolean to false to keep looping as long as we have not reached the end.
            boolean reachedEnd = false;

            // Initialize a counter variable for the rows.
            int rows = 2;

            // Loop until we have reached an empty row.
            while (!reachedEnd)
            {
                try
                {
                    /*
                    Get the contents of the row in issue format. If the row is empty this line will throw an exception.
                    An empty row signals that we are at the end of the responses in the spreadsheet.
                    */
                    Issue temp = parser.parseRow(Integer.toString(rows), issueDatabase.getID());

                    // Add the Issue parsed from the row to the ArrayList.
                    toReturn.add(temp);

                    // Prepare to count an additional row.
                    rows++;
                }
                // If it encounters an empty row, the method parseRow will throw an exception and the catch block will
                // intercept it.
                catch (Exception e)
                {
                    // Set this boolean to true so we can break out of the loop.
                    reachedEnd = true;

                    // Return the number of rows that we have counted.
                    return toReturn;
                }
            }

        }
        catch (Exception exception)
        {
            // Empty catch block, since we don't need to do anything major if there's an exception.
        }
        // Return the ArrayList filled with Issues.
        return toReturn;
    }

    /*
    Method to populate the table, alongside updating the priorities.
     */
    public void updatePrioritiesInTableAndList(File priorityFile)
    {
        try
        {
            if (priorityFile != null)
            {
                // Create a Scanner to move through each line in the file.
                Scanner scan = new Scanner(priorityWordsFile);

                // Create a new ArrayList to hold all the high priority words.
                ArrayList<String> wordsList = new ArrayList<String>();

                // Loop through each line in the file, and add it to the ArrayList.
                while (scan.hasNextLine())
                {
                    String wordToAdd = scan.nextLine();
                    wordsList.add(wordToAdd);
                }

                // Set the priority words list, and update the priorities of each existing Issue in the list.
                issueDatabase.setMyPriorityWordsList(wordsList);
                issueDatabase.updatePriorities();
            }

            // Update the table with the new values.
            clearTable();
            populateTable();
        }
        catch (FileNotFoundException fnf)
        {
            fnf.printStackTrace();
        }
    }

    /*
    Method to enable sorting in the table.
     */
    private void setupTableSorting()
    {
        // Create a new TableRowSorter that will define how rows get sorted (alphabetically by default)
        TableRowSorter<TableModel> tSorter = new TableRowSorter<TableModel>(issueTable.getModel());

        // Add the row sorter to the issue table.
        issueTable.setRowSorter(tSorter);

    }

    /*
    Sets up the Time Submitted column of the table with proper formatting for the Dates.
     */
    private void setupDateColumn() throws IllegalArgumentException
    {
        TableCellRenderer renderer = new DefaultTableCellRenderer()
        {

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column)
            {
                try
                {
                    value = formatter.format(value);
                }
                catch (IllegalArgumentException e)
                {
                    // empty
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        issueTable.getColumnModel().getColumn(2).setCellRenderer(renderer);
    }

    /*
    Method to define an ArrayList of GUI components that are needed outside of the class, for Thread execution.
     */
    public ArrayList<JComponent> defineGUIComponentsArray()
    {
        // Create a new ArrayList to hold the GUI components.
        ArrayList<JComponent> listGUIObjects = new ArrayList<JComponent>();

        // Add GUI components to the ArrayList.
        listGUIObjects.add(loginButton);
        listGUIObjects.add(loadingLabel);
        listGUIObjects.add(googleEmailLabel);
        listGUIObjects.add(reloadButton);
        listGUIObjects.add(menuChooseSpreadsheet);
        listGUIObjects.add(menuUploadData);
        listGUIObjects.add(onlineOfflineStatusLabel);
        listGUIObjects.add(menuExitUpload);

        // Return the ArrayList that we have generated.
        return listGUIObjects;
    }
}
