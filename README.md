# VIA: Virtual Identity Aggregator
Android app, making contact sharing easy with QR-codes.

Add your contact information, profile picture and links to social networks (FB, Twitter, LinkedIn) and the app will generate a QR-code for you.

<p align="center">
<img src="/screenshots/my-profile.png?raw=true" width="292" height="500" />
<img src="/screenshots/qr-code.png?raw=true" width="292" height="500" />
</p>

The QR-code contains your ID and basic info. Another user can scan it in his app and add you to their contacts list (works offline too!). If internet connection is available, the profile picture will also get downloaded and they'll be able to follow you on the social media.

<p align="center">
<img src="/screenshots/contacts-list.png?raw=true" width="292" height="500" />
<img src="/screenshots/contact-profile.png?raw=true" width="292" height="500" />
</p>

Uses [ZebraCrossing](https://github.com/zxing/zxing) library for generating and scanning QR-codes.

Uses Google's [Volley](https://github.com/google/volley) library for making HTTP requests to remote DB.
