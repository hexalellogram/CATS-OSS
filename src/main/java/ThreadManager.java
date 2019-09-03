import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public abstract class ThreadManager implements Runnable
{
    private IssueDB myIssueDatabase;
    private SpreadsheetParser myParser;
    private ArrayList<JComponent> myGUIObjects;
    private JFrame myMainWindow;
    private MainGUI myMainGUI;
    private File myPriorityFile;


    /*
    Constructor for the ThreadManager class.
     */
    public ThreadManager(IssueDB issueDatabase, SpreadsheetParser parser, ArrayList<JComponent> listGUIObjects,
                         JFrame mainWindow, MainGUI mainGUI, File priorityFile)
    {
        myIssueDatabase = issueDatabase;
        myParser = parser;
        myGUIObjects = listGUIObjects;
        myMainWindow = mainWindow;
        myMainGUI = mainGUI;
        myPriorityFile = priorityFile;
    }

    /*
    Abstract run method to ensure that Runnable is implemented.
     */
    public abstract void run();

    /*
    Returns the SpreadsheetParser associated with this ThreadManager.
     */
    public SpreadsheetParser getMyParser()
    {
        return myParser;
    }

    /*
    Returns the issue database associated with this ThreadManager.
     */
    public IssueDB getMyIssueDatabase()
    {
        return myIssueDatabase;
    }

    /*
    Returns the list of GUI components associated with this ThreadManager.
     */
    public ArrayList<JComponent> getMyGUIObjects()
    {
        return myGUIObjects;
    }

    /*
    Returns the main window JFrame associated with this ThreadManager.
     */
    public JFrame getMyMainWindow()
    {
        return myMainWindow;
    }

    /*
    Returns the MainGUI associated with this ThreadManager.
     */
    public MainGUI getMyMainGUI()
    {
        return myMainGUI;
    }

    /*
    Returns the priorityFile associated with this ThreadManager.
     */
    public File getMyPriorityFile()
    {
        return myPriorityFile;
    }
}
