# Komponenty zewnętrzne

Te elementy nie są objęte licencją projektu (`LICENSE` w katalogu głównym) — każdy zachowuje
własną licencję.

## Kroje pism

Oba fonty pobrane z oficjalnego CDN Google Fonts (`fonts.gstatic.com`) i wbudowane w aplikację
(`app/src/main/res/font/`) — bez Google Fonts Provider, więc aplikacja działa w pełni offline
i nie potrzebuje uprawnienia do sieci. Licencja SIL OFL 1.1 pozwala dołączać je do dowolnego
oprogramowania, także komercyjnego, pod warunkiem że tekst licencji podróżuje razem z fontami.

| Krój | Pliki | Wersja | Copyright | Licencja |
| --- | --- | --- | --- | --- |
| Space Grotesk | `space_grotesk_{regular,medium,bold}.ttf` | 2.000 | 2020 The Space Grotesk Project Authors | [`SpaceGrotesk-OFL.txt`](SpaceGrotesk-OFL.txt) |
| Outfit | `outfit_extra_bold.ttf` | 1.100 | 2021 The Outfit Project Authors | [`Outfit-OFL.txt`](Outfit-OFL.txt) |

Linie copyright pochodzą z tablicy `name` samych plików `.ttf`. Teksty licencji pochodzą
z paczek npm `@fontsource/space-grotesk@5.3.0` i `@fontsource/outfit@5.3.0` (suma sha512
zweryfikowana z rejestrem) i są słowo w słowo zgodne z tekstem OFL-1.1 z oficjalnej listy SPDX
(`spdx-license-list@6.12.0`) — jedyna różnica to zapis myślnika: `--` jak w oryginalnym pliku
tekstowym SIL, zamiast pauzy `—` w wersji SPDX.

## Biblioteki

Biblioteki AndroidX, Jetpack Compose i kotlinx.coroutines są dołączane przez Gradle na licencji
Apache 2.0. Ich pełna lista jest w `gradle/libs.versions.toml`.
