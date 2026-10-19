# Art on Denim Android

The app is the same store as the website: chalk pages, ink type, brass labels, rand prices, and the same account.

Open this `android` folder in Android Studio. The emulator talks to `http://10.0.2.2/art-on-denim/api/`. A phone must use the PC LAN address in `API_BASE` inside `app/build.gradle.kts`.

Sign in with the website account. Google appears only when the server has `config/google.local.php`. The app receives a one-time code on `aod://auth` and exchanges it for the same bearer token. The Google secret stays on the server.

Screens follow the website: home, shop, product, bag, checkout, orders, tracking, saved, notifications, profile, journal, about, contact, and Ask the studio.
