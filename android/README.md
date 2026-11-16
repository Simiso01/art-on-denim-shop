# Art on Denim Android

The app shows the Art on Denim website full screen.

## Live build

`app/build.gradle.kts` is set to:

```kotlin
"\"https://artondenim.infinityfreeapp.com/api/\""
```

Open this `android` folder in Android Studio, sync, and Run. No Wamp needed for that build.

## Local Wamp build

Change `API_BASE` to:

- Emulator: `http://10.0.2.2/art-on-denim/api/`
- Phone on same Wi-Fi: `http://YOUR-PC-WIFI-IP/art-on-denim/api/`

Then sync and rebuild.

Demo accounts use password `Password123!`:

- admin@artondenim.co.za
- lebo@example.com
