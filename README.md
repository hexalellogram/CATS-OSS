# CATS
Cyber Activity Tracking System

System to track issues for cyber competitions submitted through Google Forms.

To allow this application to work, you must first do the following steps:
1. Create a Google API project at the [Google API Dashboard](https://console.developers.google.com/apis/dashboard).
2. After the project is created, go to Library in the sidebar and enable the Gmail API and the Google Sheets API. 
3. Go to Credentials in the sidebar under APIs & Services, and click Create credentials. The type should be `OAuth client ID`. The Application type should be Other.
4. Return to the credentials screen and click on the credential you just made, then click Download JSON.
5. Save this file as `credentials3.json` in the root of the repository.
6. If this does not allow the program to work, copy `credentials3.json` to `src/main/resources/credentials3.json`.