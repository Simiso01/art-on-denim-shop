# Art on Denim user manual

Art on Denim is one store. The website and the Android app use the same catalogue, prices in rand, accounts, bag, saved pieces, orders, and journal.

## Live website

Open https://artondenim.infinityfreeapp.com/

## Local website

Open `http://localhost/art-on-denim/` while Wamp is running.

- Shop, New, Brands, Sale, Journal, About, and Contact are in the header.
- A product page shows sizes and related pieces. Add a size to the bag or save it.
- The bag accepts the coupon codes already in the store, including WELCOME10.
- Checkout asks for a South African address and Payfast, EFT, or card. This build records the order. It does not charge a real card.
- Orders and tracking are under the account.
- Ask the studio answers from store information. It is not an outside chatbot.

Demo accounts use the password `Password123!`:

- admin@artondenim.co.za
- lebo@example.com

## Android

Install `ArtOnDenim-live.apk` (opens the live site), or open the `android` folder in Android Studio and run it.

Google sign-in appears only after `config/google.local.php` is filled in.
