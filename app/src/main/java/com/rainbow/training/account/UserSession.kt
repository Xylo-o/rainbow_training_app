package com.rainbow.training.account

/**
 * Tozsamosc uzytkownika. Aplikacja dziala dzis w pelni offline i anonimowo, ale reszta kodu
 * odwoluje sie juz do tego interfejsu, wiec dodanie logowania sprowadzi sie do dostarczenia
 * innej implementacji w [com.rainbow.training.AppContainer] - bez zmian w UI.
 */
interface UserSession {

    /** Identyfikator zalogowanego uzytkownika albo `null` w trybie anonimowym. */
    val userId: String?

    val isSignedIn: Boolean get() = userId != null
}

/** Domyslna implementacja: brak kont, wszystko trzymane lokalnie na urzadzeniu. */
object AnonymousUserSession : UserSession {
    override val userId: String? = null
}
