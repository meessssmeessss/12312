# MinetopiaPlugin

Een Paper/Spigot 1.21.x plugin met economy, levels/prefixes, agenten en een wiet-systeem,
gebouwd voor een RP-stadsserver zoals MineTopia.

## Wat zit erin

- **Economy**: `/maakover <speler> <bedrag>`, `/kijksaldo [speler]`, `/zetsaldo <speler> <bedrag>` (admin).
- **Scoreboard**: elke seconde ververst, toont live Nederlandse datum/tijd (Europe/Amsterdam),
  saldo, level en rang.
- **Levels & prefixes**: level stijgt door plots te upgraden (`/plot upgrade <naam>`).
  De prefix (Burger → Ondernemer → ... → Legende) is zichtbaar in chat, tab en scoreboard.
  Spelers met de `minetopia.agent` permissie krijgen er automatisch een rode **[Agent]**-tag bij.
- **Agenten**: `/handboei <speler>` (boeien/losmaken, geboeide spelers kunnen niet lopen) en
  `/fouilleer <speler>` (opent een alleen-lezen kopie van de inventory van het doelwit).
- **Plots**: `/plot pos1`, `/plot pos2`, `/plot create <naam>`, `/plot add/remove <naam> <speler>`,
  `/plot info`, `/plot upgrade <naam>`, `/plot delete <naam>`. Alleen eigenaar + toegevoegde leden
  mogen bouwen/breken binnen het plot.
- **Wiet-systeem**: planten (Wietzaadje op boerenland, groeit vanzelf) → oogsten (Wietblaadjes) →
  verwerken bij een Crafting Table (`WietManager.startVerwerken`, kost 5 seconden) →
  verkopen bij de dealer-NPC (`/dealer spawn`, een villager genaamd "Man met Bivakmuts").

## De .jar krijgen (zonder zelf iets te installeren)

Dit project bevat een kant-en-klare GitHub Actions-workflow die de plugin automatisch compileert.
Zo krijg je een `.jar` zonder Java, Maven of een IDE te installeren:

1. Maak (indien nodig) een gratis account op https://github.com.
2. Klik rechtsboven op **+** → **New repository**, geef een naam (bv. `minetopia-plugin`) en klik
   **Create repository**. Laat hem gerust **Private** staan.
3. Klik op **uploading an existing file** (of ga naar de repo → **Add file** → **Upload files**) en
   sleep de hele inhoud van deze zip erin (inclusief de verborgen `.github`-map — pak lokaal eerst
   de zip uit en upload dan alle bestanden/mappen).
4. Klik **Commit changes**. GitHub start nu automatisch de build (te zien onder het tabblad
   **Actions** van je repository).
5. Wacht tot het groene vinkje verschijnt (duurt ongeveer 1 minuut), open die run, en download
   onderaan bij **Artifacts** het bestand **MinetopiaPlugin** — dat is een zip met daarin de
   `.jar`.
6. Zet `MinetopiaPlugin.jar` in de `plugins/`-map van je Paper-server (1.21.x) en herstart de
   server.

### Alternatief: lokaal bouwen

Als je liever lokaal bouwt: installeer Java 21 en Maven, en draai in de projectmap:

```bash
mvn clean package
```

De jar komt dan in `target/MinetopiaPlugin.jar` te staan.

> In de omgeving waarin ik dit project heb gemaakt, heb ik geen Maven en geen internettoegang om
> de Paper-API te downloaden — daarom kon ik de jar hier niet zelf compileren. De GitHub-methode
> hierboven doet dat compileren voor je, gratis en zonder installatie.

## Belangrijke keuzes / simplificaties

- **Opslag**: alles staat in simpele YAML-bestanden in de plugin-datamap (`economy.yml`,
  `levels.yml`, `plots.yml`). Voor een grote server is een database (MySQL/SQLite) beter, maar YAML
  is de eenvoudigste start.
- **Plots**: rechthoekige gebieden tussen twee door jou gekozen hoekpunten (zoals WorldEdit
  `//pos1`/`//pos2`). Er is geen overlap-detectie tussen plots ingebouwd — voeg dat toe als dat
  nodig is.
- **Wiet-plant**: gebruikt `NETHER_WART` als basis omdat dat van nature op boerenland/zielzand groeit
  met leeftijdsstadia, zonder dat je een resource pack nodig hebt. Wil je een unieke look (echte
  wiet-textures), dan heb je een resource pack en custom model data nodig.
- **Dealer-NPC**: een gewone, onkwetsbare Villager met custom naam en leren helm ("bivakmuts").
  Voor een mooiere NPC (skins, dialoog, meerdere NPC's) is een plugin als Citizens een betere basis
  — deze kun je er dan naast draaien en gewoon herkennen op naam, net als nu.
- **Prefixes/rangen**: de levelgrenzen en namen staan in `PrefixManager.java` — pas de map daar aan
  om je eigen rangen/kleuren te bepalen.
- **Chat-formatting** gebruikt `AsyncPlayerChatEvent`. Dat werkt nog in Paper 1.21, maar staat
  officieel als verouderd; wil je de nieuwste Adventure-chat-API gebruiken, laat het weten dan
  zet ik het om naar `AsyncChatEvent`.

## Volgende stappen die je zelf kunt toevoegen

- Permissiegroepen automatisch koppelen aan levels (bv. via LuckPerms) i.p.v. losse prefix-tekst.
- Een GUI voor het verwerkproces i.p.v. een simpele timer.
- Cooldowns/limieten op hoeveel wiet je per uur kan verkopen (voor balans).
- Database-opslag i.p.v. YAML zodra de spelersaantallen groeien.
