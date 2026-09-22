# Rainbow Trening

Natywna aplikacja na Androida (Kotlin + Jetpack Compose) do treningu obserwacji i refleksu
wzrokowego. Pokazuje na pełnym ekranie pojedyncze bodźce — kolory i cyfry — zmieniające się
w losowych odstępach czasu.

Aplikacja **tylko wyświetla bodźce**. Nie mierzy czasu reakcji i nie wymaga dotykania ekranu
w trakcie sesji — reakcja (głosem, ruchem, czymkolwiek) dzieje się poza aplikacją.

## Jak działa

1. **Ekran ustawień** — tryb, paleta, zakres odstępów, długość sesji.
2. **Odliczanie 3 · 2 · 1** — czas na odłożenie telefonu i przygotowanie się.
3. **Sesja** — pełny ekran, ukryte paski systemowe, ekran nie gaśnie. U góry cienki pasek
   postępu. Wyjście w trakcie: przycisk / gest „wstecz”.
4. **Podsumowanie** — czas, liczba bodźców, średni odstęp; przyciski „Powtórz” i
   „Zmień ustawienia”. Koniec sesji sygnalizuje krótka wibracja.

### Tryby

| Tryb | Co widać |
| --- | --- |
| **Kolory** | Cały ekran w jednym kolorze z palety. |
| **Cyfry** | Duża cyfra na kolorowym tle, w kolorze o maksymalnym kontraście. |
| **Mieszany** | Losowo przeplatane: raz sam kolor, raz kolor z cyfrą. |

### Paleta

Sześć barw rozłożonych po kole barw plus biel i czerń; każdą można wyłączyć w ustawieniach
(minimum dwie aktywne). Palecie celowo brakuje dwóch odcieni tej samej barwy — przy ekspozycji
rzędu pół sekundy czerwony i różowy albo niebieski i turkusowy zlewają się ze sobą.

| | Kolor | Hex |
| --- | --- | --- |
| 🔴 | Red | `#E53935` |
| 🟠 | Orange | `#FB8C00` |
| 🟡 | Yellow | `#FDD835` |
| 🟢 | Green | `#43A047` |
| 🔵 | Blue | `#1E88E5` |
| 🟣 | Purple | `#8E24AA` |
| ⚪ | White | `#FFFFFF` |
| ⚫ | Black | `#000000` |

### Kolor cyfry: negatyw barwny

Cyfra dostaje **barwę z przeciwnej strony koła barw** niż tło — nie czerń ani biel. Liczy to
`Contrast.complementary()`: bierze odcień tła, obraca o 180°, wymusza nasycenie i dobiera
jasność tak, żeby zachować jak najwięcej koloru przy kontraście co najmniej 3,0 (próg WCAG AA
dla dużego tekstu — a cyfra zajmuje 62% krótszego boku ekranu).

| Tło | Cyfra | Kontrast |
| --- | --- | --- |
| Red `#E53935` | cyjan `#5AEEF2` | 3,0 |
| Orange `#FB8C00` | niebieski `#004FB2` | 3,2 |
| Yellow `#FDD835` | niebieski `#0231FC` | 5,3 |
| Green `#43A047` | purpura `#760A71` | 3,1 |
| Blue `#1E88E5` | brąz `#5E3008` | 3,0 |
| Purple `#8E24AA` | zieleń `#40EC13` | 4,4 |
| White `#FFFFFF` | czerń `#000000` | 21,0 |
| Black `#000000` | biel `#FFFFFF` | 21,0 |

Biel i czerń nie mają własnej barwy, więc obracanie ich odcienia dawałoby kolor wzięty z niczego
— ich prawdziwym negatywem jest druga skrajność jasności.

Na barwnych tłach dochodzi **obrys** — czarny lub biały, zależnie od tego, co mocniej odcina się
od tła (kontrast obrysu do tła: 5,0–15,1). To on daje ostrą krawędź znaku i ratuje czytelność
tam, gdzie barwa dopełniająca ma jasność zbliżoną do tła, jak przy parze niebieski/pomarańczowy.
Na bieli i czerni cyfra ma już kolor obrysu, więc obrys jest pomijany.

Nic z tego nie jest wpisane na sztywno: po dodaniu nowego koloru do palety wszystkie trzy
wartości wyliczą się same, a testy w `ContrastTest` pilnują progów.

### Czcionki

Interfejs używa kroju **Space Grotesk** (400 / 500 / 700) — groteska z charakterem, kątowymi
ścięciami i nietypowymi cyframi. Same cyfry-bodźce rysowane są krojem **Outfit ExtraBold (800)**:
na pełnym ekranie cyfra jest elementem graficznym, nie tekstem, a Outfit ma niemal idealnie
kołowe kształty i wagę 800, której Space Grotesk w ogóle nie oferuje. Oba fonty są wbudowane
w aplikację. Szczegóły licencyjne: `third_party/README.md`.

### Losowość

Odstęp jest losowany **osobno przed każdą zmianą**, równomiernie w całym zakresie ustawionym
przez użytkownika — nie jest stały przez sesję. Bodźce są ciągnięte bezpamięciowo, więc ten sam
kolor lub ta sama cyfra mogą wypaść dwa razy pod rząd.

### Zakresy i wartości domyślne

| Ustawienie | Domyślnie | Zakres |
| --- | --- | --- |
| Odstęp między bodźcami | 0,5 – 3,0 s | 0,1 – 10,0 s (krok 0,1 s) |
| Długość sesji | 2 min | 10 s – 60 min (gotowe: 30 s, 1, 2, 5, 10 min + własna) |
| Kolory | wszystkie 8 | min. 2 aktywne |
| Cyfry | 0 – 9 | min. 2 aktywne |

Ustawienia są zapamiętywane między uruchomieniami (Jetpack DataStore).

## Budowanie

```bash
./gradlew assembleRelease     # app/build/outputs/apk/release/app-release.apk
./gradlew testDebugUnitTest   # testy jednostkowe
```

Wymagania: JDK 17, Android SDK (compileSdk 35). `minSdk` to 24 (Android 7.0).

`applicationId` (identyfikator widoczny dla Google Play) jest ustawiany w jednym miejscu —
we właściwości `rainbow.applicationId` w `gradle.properties`. **Po pierwszej publikacji
w Play nie da się go już zmienić**, więc podmień go na własną domenę zanim tam trafi.

APK jest podpisywany kluczem debug, żeby dało się go zainstalować bez własnego keystore.
**Przed publikacją w Google Play** podmień `signingConfig` w `app/build.gradle.kts` na własny
klucz i włącz z powrotem `isMinifyEnabled` / `isShrinkResources` (są wyłączone, bo tej wersji
nie testowano na fizycznym urządzeniu po minifikacji).

Każdy push buduje aplikację w GitHub Actions (`.github/workflows/build-apk.yml`); gotowy plik
APK ląduje jako artefakt `rainbow-trening-apk`.

## Struktura kodu

```
app/src/main/java/com/rainbow/training/
├── core/
│   ├── model/        Contrast, StimulusColor, StimulusMode, Stimulus, TrainingSettings,
│   │                 SettingsError
│   ├── engine/       StimulusGenerator, SessionEngine, SessionEvent
│   ├── data/         SettingsRepository (+ implementacja na DataStore)
│   └── platform/     Haptics
├── feature/
│   ├── setup/        ekran ustawień
│   ├── session/      pełnoekranowy ekran treningu
│   ├── summary/      ekran podsumowania
│   ├── TrainingViewModel, SessionPhase, TrainingEvent
├── ui/               motyw, czcionki, etykiety (Labels), formatowanie, komponenty wspólne
├── monetization/     AdHost — interfejs reklam (dziś NoAds)
├── account/          UserSession — interfejs tożsamości użytkownika
├── AppContainer      ręcznie składane zależności
├── RainbowApp        Application
└── MainActivity      host Compose
```

Warstwa `core/` nie zna Androida, Compose ani języka interfejsu — przechowuje identyfikatory
(`StimulusColor.id`) i przyczyny błędów (`SettingsError`), a cały widoczny tekst mieszka
w `res/values/strings.xml` i jest tłumaczony w `ui/Labels.kt`. Dodanie kolejnego języka to
założenie katalogu `values-xx/` i nic poza tym.

Poza tym `core/` nie zna Androida ani Compose: kolory są zwykłymi `Long` (ARGB), zegar i źródło
losowości są wstrzykiwane. Dlatego cała logika — losowanie, kontrast, walidacja, przebieg sesji —
jest pokryta zwykłymi testami jednostkowymi na JVM, a `SessionEngine` testuje się na wirtualnym
czasie korutyn (sesja 60-sekundowa wykonuje się w milisekundy).

## Co zostawiono na później

Aplikacja nie ma reklam ani kont — celowo. Miejsca na jedno i drugie są już wpięte:

- **Reklamy** — `monetization/AdHost.kt` to interfejs z dwoma punktami wpięcia: `Banner()`
  (wołany na ekranie ustawień i podsumowania) oraz `onSessionCompleted()` (wołany między
  sesjami, miejsce na reklamę pełnoekranową co `INTERSTITIAL_EVERY_N_SESSIONS` sesji). Ekran
  sesji nie woła żadnego z nich — w trakcie treningu nic nie ma prawa pojawić się na ekranie.
  Dziś wpięta jest implementacja `NoAds`; dodanie AdMob to zależność, inicjalizacja
  w `RainbowApp.onCreate()` i podmiana jednej linijki w `AppContainer`.
- **Konta** — `account/UserSession.kt` to interfejs, dziś realizowany przez `AnonymousUserSession`.
  Wystarczy dostarczyć inną implementację w `AppContainer`.
- **Synchronizacja ustawień** — `SettingsRepository` jest interfejsem, więc implementację
  lokalną można zastąpić lub obudować wersją sieciową bez zmian w UI.
