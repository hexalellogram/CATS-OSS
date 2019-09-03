import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class LoginManager extends ThreadManager
{
    /*
    Constructor for the LoginManager class.
     */
    public LoginManager(IssueDB issueDatabase, SpreadsheetParser parser, ArrayList<JComponent> listGUIObjects,
                        JFrame mainWindow, MainGUI mainGUI, File priorityFile)
    {
        // Invoke the ThreadManager constructor.
        super(issueDatabase, parser, listGUIObjects, mainWindow, mainGUI, priorityFile);
    }

    /*
    Method that runs when a Thread is started for this class. Implements Runnable.
     */
    public void run()
    {
        // Define local variables based on the objects passed in to create this LoginManager that will be used later.
        SpreadsheetParser parser = getMyParser();
        IssueDB issueDatabase = getMyIssueDatabase();
        ArrayList<JComponent> listGUIObjects = getMyGUIObjects();
        JFrame mainWindow = getMyMainWindow();
        MainGUI mainGUI = getMyMainGUI();
        File priorityFile = getMyPriorityFile();
        JButton loginButton = (JButton)listGUIObjects.get(0);
        JLabel loadingLabel = (JLabel)listGUIObjects.get(1);
        JLabel googleEmailLabel = (JLabel)listGUIObjects.get(2);
        JButton reloadButton = (JButton)listGUIObjects.get(3);
        JMenuItem menuChooseSpreadsheet = (JMenuItem)listGUIObjects.get(4);
        JMenuItem menuUploadData = (JMenuItem)listGUIObjects.get(5);
        JLabel onlineOfflineStatusLabel = (JLabel)listGUIObjects.get(6);
        JMenuItem menuExitUpload = (JMenuItem)listGUIObjects.get(7);


        try
        {
            // Check to make sure that the user is not already logged in. The behavior of this method should be
            // different depending if the user is logged in or not. If the user is not already logged in, then we
            // should invoke the login process. If the user is not already logged in, then this method should do
            // nothing.
            if (!parser.isLoggedIn())
            {
                // Disable the login button temporarily to prevent other login attempts while a login is in progress.
                loginButton.setEnabled(false);

                // Set the text of the login button to reflect that the user is now signed in, and the user can click
                // this button to log out.
                loginButton.setText("Log Out");

                // Begin the login process.
                parser.invokeLogin();

                // Create a popup that asks for a URL of a Google Spreadsheet.
                String url = JOptionPane.showInputDialog(mainWindow, "Enter a Google Spreadsheet URL:");

                if(url == null || url.equals(""))
                {
                    // Clear the table as the user is now logged out, and we do not want to retain their data.
                    mainGUI.clearTable();

                    // Destroy the SpreadsheetParser, which contains the Credential and other critical login data.
                    issueDatabase.setParser(null);

                    // Reset the email label to no longer display the user's email.
                    googleEmailLabel.setText("Not Signed Into Google");

                    // Reset the login button's behavior to the log in behavior.
                    loginButton.setText("Login to Google");
                    loginButton.setEnabled(true);

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
                    return;
                }

                // Set text of the loading status label to indicate that a download is in progress.
                loadingLabel.setText("Downloading data from Google Sheets...");

                // Get the user's email and set the Email label to the correct text.
                String email = parser.getUserInfo();
                googleEmailLabel.setText(email);

                // Set the IssueDB's URL to what was entered in the popup.
                issueDatabase.setID(url);

                // Get the issues from the Google Spreadsheet and put them into the database's ArrayList.
                issueDatabase.setIssues(mainGUI.fillDatabase());

                // Update the priorities of each issue in the database before we move further, and fill the table.
                mainGUI.updatePrioritiesInTableAndList(priorityFile);

                // Set text of the loading status label to indicate that data has been downloaded from Google Sheets.
                loadingLabel.setText("No data download in progress.");

                // Enable the "Reload Issues" button and the "Upload Statuses", "Choose Spreadsheet", and "Upload
                // Changes and Exit" menu items as the user is now signed in.
                reloadButton.setEnabled(true);
                menuChooseSpreadsheet.setEnabled(true);
                menuUploadData.setEnabled(true);
                menuExitUpload.setEnabled(true);

                // Set Online status label to online mode.
                onlineOfflineStatusLabel.setText("Working in Online Mode");

                // Re-enable the Login Button (it will now serve a log out functionality).
                loginButton.setEnabled(true);
            }
        }
        catch (Exception e)
        {
            // Print any error message that results to the console.
            e.printStackTrace();
        }
    }
}
