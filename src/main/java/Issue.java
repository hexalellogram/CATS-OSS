import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class  Issue implements Comparable {
    private String myTitle;
    private String myLocation;
    private String myPriority;
    private Date myTimeSubmit;
    private String myAddress;
    private String myLocationDetail;
    private String myContact;
    private String myDescription;
    private boolean myCompletedStatus;

    /*
    Detailed field constructor for the issue class with parameters for each private variable.
     */
    public Issue(String title, String priority, String time, String location, String address, String detailedLocation,
                 String contact, String description, boolean status)
    {
        setTitle(title);
        setPriority(priority);
        setMyTime(time);
        setLocation(location);
        setAddress(address);
        setDetailedLocation(detailedLocation);
        setContact(contact);
        setDescription(description);
        setStatus(status);
    }

    /*
    Generic constructor for the Issue class.
     */
    public Issue()
    {
        myTitle = "";
        myPriority = "";
        myTimeSubmit = null;
        myLocation = "";
        myAddress = "";
        myLocationDetail = "";
        myContact = "";
        myDescription = "";
        myCompletedStatus = false;
    }

    /*
    Returns the title of the Issue.
     */
    public String getTitle() {
        return myTitle;
    }

    /*
    Returns the priority level of the Issue (e.g. Low, Medium, High).
     */
    public String getPriority()
    {
        return myPriority;
    }

    /*
    Returns the time that the issue was submitted, determined via Google Spreadsheet.
     */
    public Date getTime() {
        return myTimeSubmit;
    }

    /*
    Returns the location name of the Issue (e.g. "ABC Community College").
     */
    public String getLocation() {
        return myLocation;
    }

    /*
    Returns the street address of the Issue (e.g. "123 Example Street").
     */
    public String getAddress() {
        return myAddress;
    }

    /*
    Returns the detailed location of the issue e.g. a room inside a community college (e.g. "Room 401").
     */
    public String getDetailedLocation() {
        return myLocationDetail;
    }

    /*
    Returns the contact information of the submitter: e.g. a phone number or an email address.
     */
    public String getContact() {
        return myContact;
    }

    /*
    Returns the description of the Issue. The description differs from the title in that the title is a short summary
    of what is wrong, whereas the description is more detailed.
     */
    public String getDescription() {
        return myDescription;
    }

    /*
    Returns the completion status of the Issue. A complete issue is marked true and an incomplete issue is marked
    false.
     */
    public boolean getStatus() {
        return myCompletedStatus;
    }

    /*
    Sets the title of the issue.
     */
    public void setTitle(String newTitle) {
        myTitle = newTitle;
    }

    /*
    Sets the priority level of the issue. These values can be "Low," "Medium," or "High."
     */
    public void setPriority(String priority)
    {
        priority = priority.toLowerCase();
        if (priority.equals("high"))
        {
            myPriority = "1 - High";
        }
        else if (priority.equals("medium"))
        {
            myPriority = "2 - Medium";
        }
        else if (priority.equals("low"))
        {
            myPriority = "3 - Low";
        }
        else
        {
            myPriority = "Undefined";
        }
    }

    /*
    Sets the time that the issue was submitted during. Takes in a String and outputs a Date object.
     */
    public void setMyTime(String newTime)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            myTimeSubmit = formatter.parse(newTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /*
    Sets the location of the issue. This is different from address in that location is the name of a location, while
    address is the street location. So location could be something like "ABC Community College" while address would be
    something like "123 Example Street, State, Country, ZIP Code"
    */
    public void setLocation(String newLocation) {
        myLocation = newLocation;
    }

    /*
    Sets the street address of the issue.
     */
    public void setAddress(String newAddress) {
        myAddress = newAddress;
    }

    /*
    Sets the detailed location (e.g. a room in a community college such as "Room 401") for the issue.
     */
    public void setDetailedLocation(String newDetailedLocation) {
        myLocationDetail = newDetailedLocation;
    }

    /*
    Sets the contact information (phone number, email) for the Issue.
     */
    public void setContact(String newContact) {
        myContact = newContact;
    }

    /*
    Sets the description of the Issue.
     */
    public void setDescription(String newDescription) {
        myDescription = newDescription;
    }

    /*
    Sets the status of the Issue.
     */
    public void setStatus(boolean newStatus) {
        myCompletedStatus = newStatus;
    }

    /*
    Allows for automatic detection of high priority issues.
    Words that will trigger a high priority are assigned through priorityWords.
    If the description or the title contain any words or phrases that are also found in priorityWords, then the issue
    is automatically marked as high priority.
    */
    public void detectHighPriorityArray(ArrayList<String> priorityWords) {
        for (int i = 0; i < priorityWords.size(); i++) {
            String word = priorityWords.get(i).toLowerCase();
            if (getDescription().toLowerCase().contains(word) || getTitle().toLowerCase().contains(word))
            {
                setPriority("High");
                i = priorityWords.size();
            }
        }
    }

    /*
    Converts the Issue into a human readable string representation of it. Used to export issues into a human readable
    text file.
     */
    public String toString()
    {
        String toReturn = "Issue Title: " + getTitle() +
                "\r\n=====================" +
                "\r\nPriority: " + getPriority() +
                "\r\nSubmit Time: " + getTimeString(myTimeSubmit) +
                "\r\nLocation: " + getLocation() +
                "\r\nAddress: " + getAddress() +
                "\r\nDetailed Location: " + getDetailedLocation() +
                "\r\nContact Info: " + getContact() +
                "\r\nDescription: " + getDescription() +
                "\r\nStatus: " + getStatus();

        return toReturn;
    }

    /*
    Returns a value < 0 if this is less than the object passed in.
    Returns a value = 0 if this is equal to the value passed in.
    Returns a value > 0 if this is greater than the object passed in.

    Compares two Issue objects based on their Priority (all other Issue fields are Strings or Booleans and can be
    compared directly.

    High Issues are the least (and thus in a list sorted from least to greatest, would be listed first), then Medium
    Issues, then Low Issues are highest (and thus in a list sorted from least to greatest, would be listed last).
     */
    @Override
    public int compareTo(Object o)
    {
        // Get the passed in object as an Issue for easier access.
        Issue issueToCompareTo = (Issue) o;

        // Get the priority of this Issue as an integer for easier comparison.
        int myPriorityInt = -100;
        if (myPriority.toLowerCase().equals("high")) {
            myPriorityInt = 0;
        } else if (myPriority.toLowerCase().equals("medium")) {
            myPriorityInt = 1;
        } else if (myPriority.toLowerCase().equals("low")) {
            myPriorityInt = 2;
        }

        // Get the priority of the Issue to compare to as an integer for easier comparison.
        int theirPriorityInt = -100;
        if (issueToCompareTo.getPriority().toLowerCase().equals("1 - high")) {
            theirPriorityInt = 0;
        } else if (issueToCompareTo.getPriority().toLowerCase().equals("2 - medium")) {
            theirPriorityInt = 1;
        } else if (issueToCompareTo.getPriority().toLowerCase().equals("3 - low")) {
            theirPriorityInt = 2;
        }

        // Return the comparison between these two Issues.
        return myPriorityInt - theirPriorityInt;

    }

    /*
    Convert the Date of submission into a concise string for display.
     */
    public static String getTimeString(Date submissionTime)
    {
        String timeStr = "";

        // Construct the time string:
        // Add the month.
        timeStr += Integer.toString(submissionTime.getMonth() + 1) + "/";
        // Add the date.
        timeStr += submissionTime.getDate() + "/";
        // Add the year.
        timeStr += Integer.toString(submissionTime.getYear() + 1900) + " ";
        // Add the hours.
        timeStr += addZero(Integer.toString(submissionTime.getHours())) + ":";
        // Add the minutes.
        timeStr += addZero(Integer.toString(submissionTime.getMinutes())) + ":";
        // Add the seconds.
        timeStr += addZero(Integer.toString(submissionTime.getSeconds()));

        return timeStr;
    }

    /*
    Method to add a 0 to a String. Used to add a 0 to accurately represent minutes, hours, and seconds for times.
     */
    private static String addZero(String str)
    {
        if (str.length() == 1)
            str = "0" + str;
        return str;
    }
}
