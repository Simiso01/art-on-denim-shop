# Art on Denim (AOD)

Art on Denim is a PHP and MariaDB shop. The Android app opens that same website full screen.

## Live demo

- Website: https://artondenim.infinityfreeapp.com/
- Shareable APK (points at the live site): `ArtOnDenim-live.apk` in your Downloads folder
- Demo logins (password `Password123!`):
  - Admin: `admin@artondenim.co.za`
  - Customer: `lebo@example.com`

## What you need (local)

- Windows with [WampServer](https://www.wampserver.com/) (Apache, PHP 7.3 or newer, MariaDB)
- A browser
- Optional: [Android Studio](https://developer.android.com/studio) for rebuilding the app

## Run the website locally

1. Copy this project so `index.php` is directly inside `C:\wamp64\www\art-on-denim`.
2. Start Wamp (Apache + MariaDB running).
3. Open phpMyAdmin, create database `art_on_denim`, import `database/art_on_denim.sql`.
4. Keep `config/database.php` as root / empty password for default Wamp.
5. Open `http://localhost/art-on-denim/`.
6. Admin: `http://localhost/art-on-denim/admin/login.php`.

## Run / rebuild the Android app

The app is a WebView of the shop.

Current project setting in `android/app/build.gradle.kts`:

```kotlin
"\"https://artondenim.infinityfreeapp.com/api/\""
```

That build works for anyone with internet (no Wamp needed).

For local Wamp testing only:

- Emulator: `http://10.0.2.2/art-on-denim/api/`
- Phone on same Wi‑Fi: `http://YOUR-PC-WIFI-ADDRESS/art-on-denim/api/`

Open the `android` folder in Android Studio, sync Gradle, then Run.

## Google sign-in

Off until `config/google.local.php` exists. Copy `config/google.local.php.example`. Do not commit secrets.
