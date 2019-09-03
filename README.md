# CATS
Cyber Activity Tracking System

System to track issues for cyber competitions submitted through Google Forms.

## Google API Setup

To allow this application to work, you must first do the following steps:

1. Create a Google API project at the [Google API Dashboard](https://console.developers.google.com/apis/dashboard).
2. After the project is created, go to Library in the sidebar and enable the Gmail API and the Google Sheets API. 
3. Go to Credentials in the sidebar under APIs & Services, and click Create credentials. The type should be `OAuth client ID`. The Application type should be Other.
4. Return to the credentials screen and click on the credential you just made, then click Download JSON.
5. Save this file as `credentials3.json` in the root of the repository.
6. If this does not allow the program to work, copy `credentials3.json` to `src/main/resources/credentials3.json`.

## Explanation of how to use CATS

Please refer to the video in the folder `Explanation Video` for a full explanation and tutorial in using CATS.

An abbreviated tutorial can be found below.

Before starting this tutorial, the user must have created a Google Form with at least 9 questions, asking the Title of the issue, its priority, time the issue was submitted, the location (such as a college name), the address (of the location detailed previously), a detailed location (such as the room number), some sort of submitter contact information, and a description of the issue. This Google Form must have the option to write submissions to a Google Sheet enabled, and the Google Account used to log into CATS must have permission to read and write to that Google Sheet.

See the explanation video for more details.

1. Open CATS by double-clicking on the jar file.
2. Login to Google by clicking on the `Login to Google` button.
3. Paste the URL of the Google Sheet where the results are into CATS.
4. Issues will show up in the table.
5. Issues can be sorted by clicking on the headers in the table.
6. Issues can be marked addressed by using the checkbox in the table.
7. The user can double click the issue to see more details.
8. The `Reload Issues` button will fetch new issues from Google Sheets.
9. The Menu contains more options, including the ability to export or import from JSON, so that the user may work offline.

## A Note on User Privacy

The CATS program makes use of 2 Google APIs for Google Sheets and for Gmail. CATS only uses the Gmail API and the associated permissions to fetch the name of the user (e.g. “John Doe”).

The Google+ API was previously used for this functionality, but it has since been removed since the Google+ service will be shut down in April 2019. The Gmail API replaced the Google+ API in CATS.

It is important to note that at no point does CATS access the user’s email inbox, or other emails.

CATS also has no connectivity back to a central server of any kind (no analytics, advertisement tracking, etc.). So even if it were accessing inboxes (which it is not), there would be nowhere to send that data.

The only online service that CATS uploads data to is Google Sheets. The only Google Sheet that CATS can modify is the one specified via the URL provided by the user during the login sequence.
