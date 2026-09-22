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

Osiem barw rozłożonych równomiernie po kole barw; każdą można wyłączyć w ustawieniach
(minimum dwie aktywne).

| | Kolor | Hex |
| --- | --- | --- |
| 🔴 | Czerwony | `#E53935` |
| 🟠 | Pomarańczowy | `#FB8C00` |
| 🟡 | Żółty | `#FDD835` |
| 🟢 | Zielony | `#43A047` |
| 🩵 | Turkusowy | `#00ACC1` |
| 🔵 | Niebieski | `#1E88E5` |
| 🟣 | Fioletowy | `#8E24AA` |
| 🩷 | Różowy | `#D81B60` |

### Kolor cyfry: negatyw barwny

Cyfra dostaje **barwę z przeciwnej strony koła barw** niż tło — nie czerń ani biel. Liczy to
`Contrast.complementary()`: bierze odcień tła, obraca o 180°, wymusza nasycenie i dobiera
jasność tak, żeby zachować jak najwięcej koloru przy kontraście co najmniej 3,0 (próg WCAG AA
dla dużego tekstu — a cyfra zajmuje ponad połowę wysokości ekranu).

| Tło | Cyfra | Kontrast |
| --- | --- | --- |
| Czerwony `#E53935` | cyjan `#5AEEF2` | 3,0 |
| Pomarańczowy `#FB8C00` | niebieski `#004FB2` | 3,2 |
| Żółty `#FDD835` | niebieski `#0231FC` | 5,3 |
| Zielony `#43A047` | purpura `#760A71` | 3,1 |
| Turkusowy `#00ACC1` | czerwony `#991100` | 3,2 |
| Niebieski `#1E88E5` | brąz `#5E3008` | 3,0 |
| Fioletowy `#8E24AA` | zieleń `#40EC13` | 4,4 |
| Różowy `#D81B60` | mięta `#13EC9D` | 3,2 |

Do tego dochodzi **obrys** — czarny lub biały, zależnie od tego, co mocniej odcina się od tła
(kontrast obrysu do tła: 4,9–15,1). To on daje ostrą krawędź znaku i ratuje czytelność tam,
gdzie barwa dopełniająca ma jasność zbliżoną do tła, jak przy parze niebieski/pomarańczowy.

Nic z tego nie jest wpisane na sztywno: po dodaniu nowego koloru do palety obie wartości
wyliczą się same, a testy w `ContrastTest` pilnują progów.

### Czcionka

Cyfry i cały interfejs używają kroju **Inter** (400 / 600 / 800), wbudowanego w aplikację
(`app/src/main/res/font/`) — bez Google Fonts Provider, więc aplikacja działa w pełni offline
i nie potrzebuje uprawnienia do sieci. Cyfry rysowane są wagą ExtraBold. Szczegóły licencyjne:
`third_party/Inter/README.md`.

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
│   ├── model/        Contrast, StimulusColor, StimulusMode, Stimulus, TrainingSettings
│   ├── engine/       StimulusGenerator, SessionEngine, SessionEvent
│   ├── data/         SettingsRepository (+ implementacja na DataStore)
│   └── platform/     Haptics
├── feature/
│   ├── setup/        ekran ustawień
│   ├── session/      pełnoekranowy ekran treningu
│   ├── summary/      ekran podsumowania
│   ├── TrainingViewModel, SessionPhase, TrainingEvent
├── ui/               motyw, konwersje kolorów, formatowanie, komponenty wspólne
├── monetization/     AdBannerSlot — pusty slot na reklamy
├── account/          UserSession — interfejs tożsamości użytkownika
├── AppContainer      ręcznie składane zależności
├── RainbowApp        Application
└── MainActivity      host Compose
```

Warstwa `core/` nie zna Androida ani Compose: kolory są zwykłymi `Long` (ARGB), zegar i źródło
losowości są wstrzykiwane. Dlatego cała logika — losowanie, kontrast, walidacja, przebieg sesji —
jest pokryta zwykłymi testami jednostkowymi na JVM, a `SessionEngine` testuje się na wirtualnym
czasie korutyn (sesja 60-sekundowa wykonuje się w milisekundy).

## Co zostawiono na później

Aplikacja nie ma reklam ani kont — celowo. Miejsca na jedno i drugie są już wpięte:

- **Reklamy** — `monetization/AdBannerSlot.kt` to pusty composable wywoływany na ekranie ustawień
  i podsumowania (ekran sesji celowo go nie ma). Dodanie AdMob to: zależność + inicjalizacja
  w `RainbowApp.onCreate()` + podmiana ciała tej jednej funkcji.
- **Konta** — `account/UserSession.kt` to interfejs, dziś realizowany przez `AnonymousUserSession`.
  Wystarczy dostarczyć inną implementację w `AppContainer`.
- **Synchronizacja ustawień** — `SettingsRepository` jest interfejsem, więc implementację
  lokalną można zastąpić lub obudować wersją sieciową bez zmian w UI.
