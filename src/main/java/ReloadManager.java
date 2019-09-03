import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class ReloadManager extends ThreadManager
{
    /*
    Constructor for the ReloadManager class.
     */
    public ReloadManager(IssueDB issueDatabase, SpreadsheetParser parser, ArrayList<JComponent> listGUIObjects,
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
        try
        {
            // Define local variables based on the objects passed in to create this ReloadManager that will be used
            // later.

            IssueDB issueDatabase = getMyIssueDatabase();
            ArrayList<JComponent> listGUIObjects = getMyGUIObjects();
            MainGUI mainGUI = getMyMainGUI();
            File priorityFile = getMyPriorityFile();
            JLabel loadingLabel = (JLabel)listGUIObjects.get(1);
            JButton reloadButton = (JButton)listGUIObjects.get(3);

            // Disable the reload button temporarily.  It will be re-enabled at the end of this method.
            reloadButton.setEnabled(false);

            issueDatabase.uploadData();

            // Set text of the loading status label to indicate that a download is in progress.
            loadingLabel.setText("Downloading data from Google Sheets...");

            // Get the issues from the Google Spreadsheet and put them into the database's ArrayList.
            issueDatabase.setIssues(mainGUI.fillDatabase());

            // Update the priorities of each issue in the database before we move further.
            mainGUI.updatePrioritiesInTableAndList(priorityFile);

            // Set text of the loading status label to indicate that data has been downloaded from Google Sheets.
            loadingLabel.setText("No data download in progress.");

            // Re-enable the reload button, as the reload process is now complete.
            reloadButton.setEnabled(true);
        }
        catch (Exception e)
        {
            // Print any error message that results to the console.
            e.printStackTrace();
        }



    }
}
