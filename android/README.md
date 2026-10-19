# Art on Denim Android

The app shows the Art on Denim website full screen. It does not keep a separate shop database. Accounts, the cart, orders, and the wishlist are the rows in the PC’s `art_on_denim` database, and the website is what reads them.

Start the website before the app. The steps are in the project `README.md`. Wamp must stay running while the app is open. If Apache is stopped, the app installs but the shop cannot load.

## Open the project

1. Install Android Studio recent enough for Android Gradle Plugin 9.3.0. The Gradle wrapper is 9.6.0.
2. Choose **File → Open** and open this `android` folder.
3. Wait for **File → Sync Project with Gradle Files** to finish.
4. Use an emulator or phone on Android 7 (API 24) or newer. Older versions will refuse the install.

## Point the app at your PC

`app/build.gradle.kts` has one address, `API_BASE`. The app loads the website from that address with `/api/` removed.

Emulator on the same PC:

```kotlin
"\"http://10.0.2.2/art-on-denim/api/\""
```

Real phone on the same Wi-Fi as the PC:

```kotlin
"\"http://192.168.0.225/art-on-denim/api/\""
```

Replace `192.168.0.225` with the PC’s Wi-Fi IPv4 address from `ipconfig`. Ignore addresses that start with `169.254`. After changing the line, sync Gradle again.

Check the phone’s browser before pressing Run. This address must show the shop:

`http://YOUR-PC-WIFI-ADDRESS/art-on-denim/`

If that page says **Forbidden**, restart Wamp. The website `.htaccess` allows phones on the same network, and Apache only picks that up when it is running the current project files.

## Install

**Emulator:** choose the emulator in the device list and press the green **Run** button.

**Phone:**

1. On the phone, open **Settings → About phone** and tap **Build number** seven times.
2. Open **Developer options** and turn on **USB debugging**.
3. Plug the phone into the PC and tap **Allow**.
4. In Android Studio, select the phone, not the emulator, and press **Run**.

Sign in with a demo account after the database import:

- Admin: `admin@artondenim.co.za` / `Password123!`
- Customer: `lebo@example.com` / `Password123!`

An account that exists only on someone else’s computer is not in your database.

Google sign-in appears only after the website has `config/google.local.php`. The secret stays on the PC. It is not stored in the app.
