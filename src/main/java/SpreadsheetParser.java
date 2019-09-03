import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Profile;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpreadsheetParser
{
    private HashMap<String, Integer> myFirstRowMap;
    private Sheets spreadsheet;
    private static JsonFactory jsonFactory;
    private static NetHttpTransport httpTransport;
    private static MemoryDataStoreFactory dataStoreFactory;
    private Credential loginInfo;
    private static List<String> SCOPES;
    private String myUserInfo;
    private boolean loggedIn;

    // Constructor for the SpreadsheetParser class
    public SpreadsheetParser()
    {
        loggedIn = false;
    }

    /*
    Returns whether the current user is logged into Google or not.
     */
    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    /*
    Runs the login process for Google services.
     */
    public void invokeLogin() throws Exception
    {
        // Do all the stuff that Google wants to log in.
        SCOPES = new ArrayList<>();
        SCOPES.add(SheetsScopes.SPREADSHEETS);
        SCOPES.add(GmailScopes.GMAIL_READONLY);
        SCOPES.add("profile");
        myFirstRowMap = new HashMap<String, Integer>();
        jsonFactory = JacksonFactory.getDefaultInstance();
        dataStoreFactory = new MemoryDataStoreFactory();
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        loginInfo = createCredential();
        spreadsheet = new Sheets.Builder(httpTransport, jsonFactory, loginInfo).setApplicationName("CATS").build();

        // Set the logged in status to true.
        loggedIn = true;

        // Get the user's email.
        try
        {
            myUserInfo = generateUserInfo();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
    Parses a row in a spreadsheet and creates an Issue object out of the data in that row.
    Note that before this method is run for the first time, method parseFirstRow must be run first to generate
    the HashMap that this method relies on to detect what cells go into what fields for the Issue object.
    */
    public Issue parseRow(String row, String spreadsheetID) throws Exception
    {
        // Check to make sure that we are logged in, since parsing the spreadsheet won't work unless the user is
        // logged in.
        if (!loggedIn)
            throw new Exception("Error! Valid user not logged in!");

        // Get columns for each field that will go into the Issue.
        // Columns are noted here as integers, but in Google Spreadsheets they are represented alphanumerically.
        // If column = 1, then the column in Google Spreadsheets is A, if it's 2 it's B, 3 = C, and so on.
        int titleCol = myFirstRowMap.get("title");
        int priorityCol = myFirstRowMap.get("priority");
        int timeCol = myFirstRowMap.get("time");
        int locationCol = myFirstRowMap.get("location");
        int addressCol = myFirstRowMap.get("address");
        int detailedLocationCol = myFirstRowMap.get("detailed location");
        int contactCol = myFirstRowMap.get("contact");
        int descriptionCol = myFirstRowMap.get("description");
        int statusCol = myFirstRowMap.get("status");

        // Define the row that we need to be looking at.
        String cellRange = row + ":" + row;

        // Fetch the spreadsheet's selected cells from Google Spreadsheets and place into a List for easy access.
        ValueRange result = spreadsheet.spreadsheets().values().get(spreadsheetID, cellRange).execute();
        List<List<Object>> valueList = result.getValues();

        if (valueList == null || valueList.isEmpty())
        {
            // There are no values here; i.e. the row is empty so we can't read it.
            throw new Exception();
        }
        else
        {
            // Get the current row in the spreadsheet.
            List currentRow = valueList.get(0);

            // Assign variables for fields that will go into the Issue, parsing the spreadsheet values.
            String title = (String)currentRow.get(titleCol);
            String priority = (String)currentRow.get(priorityCol);
            String time = (String)currentRow.get(timeCol);
            String location = (String)currentRow.get(locationCol);
            String address = (String)currentRow.get(addressCol);
            String detailedLocation = (String)currentRow.get(detailedLocationCol);
            String contact = (String)currentRow.get(contactCol);
            String description = (String)currentRow.get(descriptionCol);

            // Get the status information.
            String statusString = ((String)currentRow.get(statusCol)).toLowerCase();
            boolean status;

            // If the status information contains these key words, then it should be true, marking the issue complete.
            if (statusString.contains("yes") || statusString.contains("true"))
            {
                status = true;
            }
            else
            {
                // Status should be false by default. We should assume that something is not completed.
                status = false;
            }

            // Create and return the Issue object
            return new Issue(title, priority, time, location, address, detailedLocation, contact,
                    description, status);
        }
    }

    /*
    Parses the first row of the given spreadsheet. If the given spreadsheet is created using Google Forms, then
    this row contains headers that we can parse to determine which values go into what fields for the Issue object.
    This method creates a HashMap using this data. The method parseRow uses this HashMap to generate Issue objects.
    */
    public void parseFirstRow(String spreadsheetID) throws Exception
    {
        // Check to make sure that we are logged in, since parsing the spreadsheet won't work unless the user is
        // logged in.
        if (!loggedIn)
            throw new Exception("Error! Valid user not logged in!");

        // Get the first row of the spreadsheet.
        ValueRange result = spreadsheet.spreadsheets().values().get(spreadsheetID, "1:1").execute();
        List<List<Object>> valueList = result.getValues();
        List firstRow = valueList.get(0);

        // Loop through the cells in the first row of the spreadsheet, and find what is inside them.
        // If statements are designed to determine what the content of this column should match to.
        for(int i = 0; i < firstRow.size(); i++)
        {
            // Get the cell content and turn it lowercase to ensure that capitalization does not interfere.
            String cellContent = (String)firstRow.get(i);
            cellContent = cellContent.toLowerCase();

            // If statements to detect what cells have what data, and to populate the HashMap accordingly.
            if (cellContent.contains("title"))
            {
                myFirstRowMap.put("title", i);
            }
            else if (cellContent.contains("priority"))
            {
                myFirstRowMap.put("priority", i);
            }
            else if (cellContent.contains("timestamp"))
            {
                myFirstRowMap.put("time", i);
            }
            else if (cellContent.contains("detailed"))
            {
                myFirstRowMap.put("detailed location", i);
            }
            else if (cellContent.contains("location"))
            {
                myFirstRowMap.put("location", i);
            }
            else if (cellContent.contains("address"))
            {
                myFirstRowMap.put("address", i);
            }
            else if (cellContent.contains("contact"))
            {
                myFirstRowMap.put("contact", i);
            }
            else if (cellContent.contains("description"))
            {
                myFirstRowMap.put("description", i);
            }
            else if (cellContent.contains("completed") || cellContent.contains("status")
                    || cellContent.contains("solved"))
            {
                myFirstRowMap.put("status", i);
            }
        }
    }

    // Method to create a Credential object in order to authenticate into Google Spreadsheets.
    private static Credential createCredential() throws IOException
    {
        // Import the credentials from the credentials JSON file.
        InputStream in = SpreadsheetParser.class.getResourceAsStream("credentials3.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(in));

        /*
        Authenticate the credentials with Google and wait for a response from Google using LocalServerReceiver
        When Google gives a response, the Credential object is completed and constructed.
        */
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /*
    Method to get the user information (email) from Google.
     */
    private String generateUserInfo() throws Exception
    {
        // Check to make sure that we are logged in, since getting the user's email won't work unless the user is
        // logged in.
        if (!loggedIn)
            throw new Exception("Error! Valid user not logged in!");

        // Create a new Gmail object to access the Gmail API.
        Gmail gmailInstance = new Gmail.Builder(httpTransport, jsonFactory, loginInfo)
                .setApplicationName("CATS")
                .build();

        // Get the currently logged in user's profile from Gmail.
        Gmail.Users.GetProfile response = gmailInstance.users().getProfile("me");
        Profile prof = response.execute();

        // Return the currently logged in user's email address.
        return prof.getEmailAddress();
    }

    /*
    Method to return the user information (email) of the logged-in user.
     */
    public String getUserInfo()
    {
        return myUserInfo;
    }

    /*
    Method to upload status changes (e.g. Completion Status changing from true to false) to a specified Row in
    a Google Sheets spreadsheet.
     */
    public void uploadData(String fieldID, String spreadsheetID, ArrayList<String> dataListSend) throws Exception
    {
        // Check to make sure that we are logged in, since uploading to the spreadsheet won't work unless the user is
        // logged in.
        if (!loggedIn)
            throw new Exception("Error! Valid user not logged in!");

        // Get the column of the selected field in the Google spreadsheets.
        // Columns are noted here as integers, but in Google Spreadsheets they are represented alphanumerically.
        // If column = 1, then the column in Google Spreadsheets is A, if it's 2 it's B, 3 = C, and so on.
        String colInSheet = numToStr(myFirstRowMap.get(fieldID) + 1);
        for (int i = 0; i < dataListSend.size(); i++)
        {
            // Define the row that we need to be looking at.
            String cellRange = colInSheet + Integer.toString(i+2) + ":" +
                    colInSheet + Integer.toString(i+2);

            // Create a List in one dimension containing the data.
            List<Object> listA = new ArrayList<Object>();
            listA.add(dataListSend.get(i));

            // Create a List containing a List to make a two dimensional matrix of sorts, but involving Lists
            // instead of arrays. Google Sheets requires a two dimensional list.
            List<List<Object>> listB = new ArrayList<List<Object>>();
            listB.add(listA);

            // Set the ValueRange to upload to Google Sheets to the status value list.
            ValueRange range = new ValueRange();
            range.setValues(listB);
            range.setRange(cellRange);

            // Create a new request and set the data that is going to be sent with it.
            BatchUpdateValuesRequest requestBody = new BatchUpdateValuesRequest();
            List<ValueRange> dataList = new ArrayList<ValueRange>();
            dataList.add(range);
            requestBody.setData(dataList);
            requestBody.setValueInputOption("RAW");

            // Upload the updated statuses to Google Sheets.
            Sheets.Spreadsheets.Values.BatchUpdate request = spreadsheet.spreadsheets().values()
                    .batchUpdate(spreadsheetID, requestBody);
            BatchUpdateValuesResponse response = request.execute();
        }

    }

    /*
    Helper method to convert an integer into a String, where A = 1, B = 2, and so on. AA = 27, AB = 28, and so on.
    Used to convert integers into Google Sheets column notation.
     */
    public static String numToStr(int num)
    {
        String toReturn = "";
        if (num / 26 == 1 && num % 26 == 0)
        {
            return toReturn + "Z";
        }
        else if (num <= 26) // base condition
        {
            num--;
            int rem = num % 26;
            char character = (char) (rem + 65);
            toReturn = character + toReturn;
            return toReturn;
        }
        else
        {
            num--;
            int div = num / 26;
            int rem = num % 26;
            char character = (char) (rem + 65);
            toReturn += numToStr(div);
            return toReturn + character;
        }
    }
}
