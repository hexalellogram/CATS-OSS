import com.google.gson.Gson;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class IssueDB
{
    private ArrayList<Issue> myIssueList;
    private SpreadsheetParser myParser;
    private String myID;
    private ArrayList<String> myPriorityWordsList;

    // Constructor for IssueDB class.
    public IssueDB(SpreadsheetParser parser, String sheetID)
    {
        myIssueList = new ArrayList<Issue>();
        myPriorityWordsList = new ArrayList<String>();
        myParser = parser;
        myID = sheetID;
    }

    // Returns the list of Issues.
    public ArrayList<Issue> getIssues()
    {
        return myIssueList;
    }

    // Returns the SpreadsheetParser.
    public SpreadsheetParser getParser()
    {
        return myParser;
    }

    // Returns the spreadsheet ID.
    public String getID()
    {
        return myID;
    }

    // Returns the list of High Priority words.
    public ArrayList<String> getMyPriorityWordsList()
    {
        return myPriorityWordsList;
    }

    // Replaces myIssueList with a new ArrayList of Issues.
    public void setIssues(ArrayList<Issue> newIssueList)
    {
        myIssueList = newIssueList;
    }

    // Sets the parser private variable.
    public void setParser(SpreadsheetParser newParser)
    {
        myParser = newParser;
    }

    /*
    Parses a Google Spreadsheet URL and returns the spreadsheet ID to identify the spreadsheet.
    Example URL: https://docs.google.com/spreadsheets/d/15Gczr6EOOPfnhZEfmDwH77W8wyduHZ83gD06pAt6zYo/edit
    In the example URL, the ID of the spreadsheet is 15Gczr6EOOPfnhZEfmDwH77W8wyduHZ83gD06pAt6zYo.
    */
    public void setID(String spreadsheetURL)
    {
        // find the location of the ID string in the provided URL.
        int locationOfID = spreadsheetURL.indexOf("/spreadsheets/") + 16;

        // trim down the URL to just the ID string and set the ID.
        int end = spreadsheetURL.indexOf("/edit");
        String spreadsheetID = spreadsheetURL.substring(locationOfID, end);
        myID = spreadsheetID;
    }

    // Set the High Priority Words list.
    public void setMyPriorityWordsList(ArrayList<String> newList)
    {
        myPriorityWordsList = newList;
    }

    // Imports the contents of a file containing a JSON representation of Issue objects into myIssueList.
    public void importJSON(File importFile)
    {
        // Initialize a Gson object to convert the String representations of an Issue into an Issue object.
        Gson JSONConverter = new Gson();
        try
        {
            Scanner readFile = new Scanner(importFile);
            while (readFile.hasNextLine()) //  loop through file lines
            {
                // Convert the String into an Issue object.
                Issue toAdd = JSONConverter.fromJson(readFile.nextLine(), Issue.class);

                // add the Issue object to the ArrayList.
                myIssueList.add(toAdd);
            }
        }
        catch(Exception e)
        {
            // Print any error message that results to the console.
            e.printStackTrace();
        }

        // Update the priorities of the Issues according to the High Priority Words list.
        updatePriorities();
    }

    // Exports the contents of myIssueList to a JSON file for later import.
    public void exportJSON(String exportFile)
    {
        // Initialize a new Gson object to convert Issues into JSON strings for addition into the export file.
        Gson JSONConverter = new Gson();
        FileWriter writer;

        try
        {
            // Initialize FileWriter to write Strings into JSON file.
            writer = new FileWriter(exportFile);

            // Loop through the ArrayList.
            for(int i = 0; i < myIssueList.size(); i++)
            {
                // Convert an issue object into a JSON-formatted representation of it, as a String.
                String representationJSON = JSONConverter.toJson(myIssueList.get(i));

                // Write that String to the file.
                writer.write(representationJSON + "\n");
            }
            // Close the FileWriter to conserve system resources.
            writer.close();

        }
        catch (Exception e)
        {
            // Print any error messages that results to the console.
            e.printStackTrace();
        }

    }

    /*
    Method to update the priorities of the Issues in the ArrayList.
     */
    public void updatePriorities()
    {
        // Exit the method early if the High Priority Words list is empty, or if the Issue list is empty.
        if (myPriorityWordsList.size() == 0 || myIssueList.size() == 0)
        {
            return;
        }

        // Loop through the list of Issues and update the priority of each Issue.
        for (Issue issue: myIssueList)
        {
            issue.detectHighPriorityArray(myPriorityWordsList);
        }
    }

    /*
    Find an issue given a timestamp and a title. It is theoretically possible (though unlikely) that two issues could
    be submitted at the same time. It is astronomically unlikely that those two issues would have the same title,
    hence the two parameters needed for searching. Returns null if the Issue does not exist in the ArrayList.
     */
    public Issue findIssueGivenParams(String timestamp, String issueTitle)
    {
        try
        {
            // Get the Date from the timestamp string for easy comparison.
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date dateFromTimestamp = formatter.parse(timestamp);

            // Create an ArrayList to  hold all of the Issues with the same time.
            ArrayList<Issue> matchingTimes = new ArrayList<Issue>();

            // Loop through the ArrayList of Issues.
            for (int i = 0; i < myIssueList.size(); i++)
            {
                // Get the issue at the current position in the Issue list.
                Issue current = myIssueList.get(i);

                // If the current date is equal to the timestamp we are seeking, add the Issue to the list.
                if (current.getTime().compareTo(dateFromTimestamp) == 0)
                {
                    matchingTimes.add(current);
                }
                // If the current date is after the timestamp we are seeking, exit the loop early.
                else if (current.getTime().compareTo(dateFromTimestamp) > 0)
                {
                    i = myIssueList.size();
                }
            }

            // Check if there is only one Issue in the list. If so, return it.
            if (matchingTimes.size() == 1)
            {
                return matchingTimes.get(0);
            }
            // Loop through the list of Issues with matching times, and return the one with the matching title.
            else
            {
                for (int i = 0; i < matchingTimes.size(); i++)
                {
                    Issue current = matchingTimes.get(i);
                    if (current.getTitle().equals(issueTitle))
                    {
                        return current;
                    }
                }

                // The searched-for issue does not exist.
                return null;
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /*
    Method to upload updated statuses to Google Spreadsheets.
     */
    public void uploadData() throws Exception
    {
        // Create a list of String representations of status and populate it with the completion status of each issue.
        // Strings look better in Google Sheets ("Yes" and "No" instead of TRUE and FALSE).
        ArrayList<String> statusList = new ArrayList<String>();
        for (Issue currIssue : myIssueList)
        {
            if (currIssue.getStatus())
            {
                statusList.add("Yes");
            }
            else
            {
                statusList.add("No");
            }
        }

        // Upload the statuses of the issues to Google Sheets.
        myParser.uploadData("status", myID, statusList);
    }

}
