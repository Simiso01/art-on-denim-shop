# Art on Denim (AOD)

Art on Denim is a PHP and MariaDB shop. The Android app opens that same website, so both use one database on the PC that is running Wamp.

Each computer has its own database. An account created on one PC does not appear on another PC until that person registers again, or signs in with a demo account after importing the SQL file.

## What you need

- Windows with [WampServer](https://www.wampserver.com/) (Apache, PHP 7.3 or newer, MariaDB). This project runs on PHP 7.3.12.
- A browser.
- For the app: [Android Studio](https://developer.android.com/studio) current enough for Android Gradle Plugin 9.3.0, and a phone or an emulator on Android 7 (API 24) or newer.

## Run the website

1. Copy this project so `index.php` is directly inside `C:\wamp64\www\art-on-denim`. If you see another `art-on-denim` folder inside that folder, move the inner files up one level.
2. Start Wamp and wait until Apache and MariaDB are both running.
3. Open `http://localhost/` and click **phpMyAdmin**.
4. Create a database named `art_on_denim`.
5. Select that database, open **Import**, and import `database/art_on_denim.sql`.
6. Leave `config/database.php` as it is when Wamp’s MySQL user is `root` with an empty password, host `127.0.0.1`, and port `3306`. If your MySQL user or password is different, change that file to match.
7. Open `http://localhost/art-on-denim/`.

The homepage should load. Sign in with either demo account:

| Role | Email | Password |
| --- | --- | --- |
| Admin | admin@artondenim.co.za | Password123! |
| Customer | lebo@example.com | Password123! |

The admin pages are at `http://localhost/art-on-denim/admin/login.php`.

## Run the app

The website must already load in the PC browser. The app is that website full screen, so Wamp has to stay running while the app is open.

1. In Android Studio, choose **File → Open** and open the `android` folder, not the whole website folder.
2. Open `android/app/build.gradle.kts` and set `API_BASE` for the device you are using. The line looks like this:

```kotlin
"\"http://10.0.2.2/art-on-denim/api/\""
```

- Emulator on the same PC: use `http://10.0.2.2/art-on-denim/api/`. `10.0.2.2` means “this PC” from inside the emulator.
- Real phone: use `http://YOUR-PC-WIFI-ADDRESS/art-on-denim/api/`. Find that address by running `ipconfig` and reading the Wi-Fi IPv4 address, for example `192.168.0.225`. Ignore addresses that start with `169.254`. The phone and the PC must be on the same Wi-Fi.

3. Save the file, then choose **File → Sync Project with Gradle Files**.
4. For a phone, turn on **USB debugging** (**Settings → About phone**, tap **Build number** seven times, then **Developer options**). Plug the phone in and tap **Allow**.
5. Choose the emulator or the phone in the device list and press **Run**.

Before running on a phone, open this in the phone’s browser:

`http://YOUR-PC-WIFI-ADDRESS/art-on-denim/`

The shop should appear there. The project `.htaccess` allows other devices on your network to open the site. If the phone still says **Forbidden**, restart Wamp so Apache reloads that file, then try the address again.

More detail is in `android/README.md`.

## Google sign-in

Google sign-in stays off until `config/google.local.php` exists with a real Google client id and secret. Copy `config/google.local.php.example`. Do not commit the real secret. That file is listed in `.gitignore`.
