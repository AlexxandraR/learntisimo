# Learntisimo

**Learntisimo** je aplikácia vytvorená ako tímový projekt na predmet ASOS, ktorá umožňuje jednoduchšiu organizáciu doučovaní medzi študentmi a učiteľmi.

## Funkcionalita

Do aplikácie sa môžu zaregistrovať učitelia aj študenti. Po prihlásení musia učitelia zaslať **žiadosť o udelenie učiteľských oprávnení**. Všetci používatelia si môžu **zmeniť email a heslo**.

### Funkcionalita pre učiteľov:
- Prehľad všetkých poskytovaných doučovaní v aplikácii.
- Vytvorenie alebo zmazanie vlastného doučovania.
- Prehliadanie zapísaných študentov na jednotlivé kurzy.
- Vypisovanie alebo mazanie termínov na konkrétne doučovanie.
- Prehliadanie aktuálnych aj historických termínov.

### Funkcionalita pre študentov:
- Prehľad všetkých poskytovaných doučovaní v aplikácii.
- Zapísanie sa alebo odhlásenie sa z doučovania.
- Prehliadanie informácií o učiteľovi, ktorý dané doučovanie poskytuje.
- Zapísanie sa alebo odhlásenie sa z termínov na konkrétne doučovanie.
- Prehliadanie aktuálnych aj historických termínov.

### Funkcionalita pre administrátora:
Aplikácia obsahuje aj **administrátorskú rolu**, ktorá umožňuje:
- Prehliadanie aktuálnych ponúkaných doučovaní.
- Schvaľovanie alebo zamietanie žiadostí o učiteľské oprávnenia.

## Implementácia

Aplikácia je implementovaná pomocou nasledovných technológií:
- **Java Spring Boot** – Backend.
- **Blazor** – Frontend.
- **Postgres** – Databáza.
- **Docker** – Kontajnerizácia.

## Spustenie aplikácie

Pre spustenie aplikácie je potrebné mať nainštalovaný **Docker** a stiahnutú aplikáciu **Learntisimo**.

1. Spustite Docker.
2. V koreňovom priečinku aplikácie zadajte príkaz:
   ```bash
   docker compose up
   ```
3. Po spustení aplikácie otvorte vo svojom prehliadači nasledujúcu adresu:

   ```
   http://localhost:7142/login
   ```

Teraz môžete začať používať aplikáciu Learntisimo!