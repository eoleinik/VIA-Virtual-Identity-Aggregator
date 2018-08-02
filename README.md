# VIA: Virtual Identity Aggregator
Android app, making contact sharing easy with QR-codes.

Add your contact information, profile picture and links to social networks and the app will generate a QR-code for you.
<img src="/screenshots/my-profile.png?raw=true" width="292" height="500" />
<img src="/screenshots/qr-code.png?raw=true" width="292" height="500" />


<img src="/screenshots/contacts-list.png?raw=true" width="292" height="500" />
<img src="/screenshots/contact-profile.png?raw=true" width="292" height="500" />

Uses ZXing library for generating and scanning QR-codes.

People would complete their profiles in an app, indicating their name, email(s), phone number, photo and link to social networks. The data would be pushed to the server and a person would get a unique generated visual QR-code in the app, which would contain their ID.
Whenever the user would like to exchange contacts with someone else, he would scan the other person’s QR-code, and the app would fetch that person’s details from the server. The app will also add the person to the contacts list, and send invitations to connect on social networks, using Facebook, Twitter and/or LinkedIn API. The key advantages of this solution is the speed, ease of use and ability to work offline. If there is no internet connection, the IDs are stored locally, and when the connection is restored, they can be added.
