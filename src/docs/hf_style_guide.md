# Hytale Foundations — Style Guide & Build Instructions

This document is the authoritative style and architecture reference for the **Hytale Foundations** plugin by One Raid Studio (author: Raeden / Nafees Munsarim). Any assistant (Claude Code or otherwise) working in this codebase must follow these rules to the letter. The goal is consistency with the existing code, not "best practices in general."

If something here conflicts with a generic Java convention, **this document wins**.

---

## 0. External References (Read Before Coding)

Before writing any non-trivial Hytale API code, consult these:

- **Primary docs:** https://hytalemodding.dev/ (and `en/docs/guides/plugin/...` subpaths)
- **Plugin template:** https://github.com/HytaleModding/plugin-template
- **Community wiki:** https://britakee-studios.gitbook.io/hytale-modding-documentation/
- **The server jar** (will be provided): treat as source of truth for API signatures. The jar is unobfuscated — decompile and read it when in doubt rather than guessing.

The Hytale API is still moving. **Never invent method names.** If you're not sure a method exists, say so and ask, or grep the jar.

Hytale requires **Java 25**. Don't use language features below that bar gratuitously, but also don't use anything we don't need.

---

## 1. Top-Level Architecture

### 1.1 The Main Class (`HytaleFoundations`)

`HytaleFoundations` is the plugin entry point and the **service locator** for every manager. There is exactly one of it. It:

- Extends `JavaPlugin`.
- Holds private fields for every manager, with public getters (`getConfigManager()`, `getChatManager()`, etc.).
- Exposes three `public static` fields: `myLogger`, `random`, `GSON`, plus `ERROR_LOG_DIRECTORY` (set in `setup()`).
- Exposes `public static LangManager LM` — see §3.4.
- Has three lifecycle methods: `setup()` (called on load), `start()` (called once ready), `shutdown()` (called on unload).
- Has three private setup methods called from `setup()`: `registerManagers()`, `registerListeners()`, `registerCommands()`. Always in that order.

### 1.2 The Manager Pattern (Service Locator via Constructor Injection)

**Every non-main class that needs access to other managers receives `HytaleFoundations` through its constructor and stores it as `private final`.**

```java
public class FooManager {
    private final HytaleFoundations hytaleFoundations;

    public FooManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        // ...initialization...
    }
}
```

Rules:

- **Field name is always `hytaleFoundations`.** Never `plugin`, `main`, `hf`, or `instance`.
- **Pass it only when you need it.** A class that does not access any manager (e.g. `AnvilCommand`, `NafsmunCommand`, `HFMainMenu`) does not take it. Don't pass it "just in case."
- **No static singletons for managers.** Access them through the main class instance: `hytaleFoundations.getChatManager().getAffixManager()`.
- Managers are instantiated in `HytaleFoundations.registerManagers()` in dependency order. Core managers first (`ConfigManager`, `DataGroupManager`, `PermissionManager`, `LangManager`, `SchedulerUtils`, `PluginActionManager`, `PlayerDataManager`, `CommandAliasManager`, `UtilityManager`), then module managers gated by config toggles.

### 1.3 The Module Toggle Pattern

Every optional feature module is a **module** controlled by a boolean in `Config.java` (`toggleXyzModule`). The pattern in `HytaleFoundations.registerManagers()`:

```java
if(configManager.getDefaultConfig().isToggleXyzModule()) {
    if(xyzManager == null) xyzManager = new XyzManager(this);
} else {
    if(xyzManager != null) xyzManager = null;
}
```

The same toggle gates command registration in `registerCommands()` and event listener registration in `registerListeners()`.

**When adding a new module:**

1. Add `module_xyz` `@SerializedName` field + `isToggleXyzModule()` / `setToggleXyzModule(...)` to `Config.java`.
2. Add `config.setToggleXyzModule(true);` (or false) inside `ConfigManager.createDefaultConfig()`.
3. Add the manager field + null-toggle block in `HytaleFoundations.registerManagers()`.
4. Add a public getter `getXyzManager()`.
5. Gate command registration in `HytaleFoundations.registerCommands()` with the same toggle.
6. Gate event listener registration similarly if applicable.
7. If the module has its own config/data file, gate its reload inside `ConfigManager.reloadPlugin()`.

### 1.4 The Manager / File / Default Trio

This is the **core architectural pattern** for any system that loads and saves data. Almost every manager that owns persistent state follows it:

| Manager           | Persistence Holder (inner class) | Defaults (enum)        |
|-------------------|----------------------------------|------------------------|
| `AffixManager`    | `AffixHolder`                    | `DefaultAffix`         |
| `CommandAliasManager` | `CommandFile`                | `DefaultCommands`      |
| `PermissionManager` | `PermissionFile`               | `Permissions`, `PermissionGroups` |
| `RankManager`     | `RankFile`                       | `DefaultRank`          |
| `ColorManager`    | `ColormapHolder`                 | `DefaultColors`        |

**The trio works like this:**

1. **The Manager** holds an in-memory `Map<String, Thing>` (always `ConcurrentHashMap`).
2. **The File/Holder** is a `public static class` (or `private static class` if internal-only) nested inside the manager. It has a `@SerializedName("VERSION")` final string version, plus the actual data field (also `@SerializedName`'d). It exists purely for Gson serialization.
3. **The Defaults enum** lives in the same package as the manager. Each constant holds the default data (id, display text, permission, priority, etc.) used to seed the manager on first load.

**Lifecycle methods every such manager has:**

- `initializeXyzManager()` — called from constructor. Pre-fills the map with defaults, then either `loadXyz()` if the file exists or `saveDefaultXyz()` if not.
- `saveXyzFile()` — public; serializes the current map to disk via `FileUtils.saveJsonFile(...)`.
- `saveDefaultXyzFile()` — private; seeds defaults and saves them. Used on first run.
- `loadXyz()` — public; reads the file, merges with current map (don't blow away unknown keys), logs `LOAD_SUCCESS` counts (new items, updated items, removed items separately).

**When you add a new persistent system, replicate this trio. Don't invent a new pattern.**

### 1.5 Package Layout

```
com.raeden.hytale
├── HytaleFoundations.java         # main class, lives at root
├── core/                          # plugin-wide infrastructure used by all modules
│   ├── alias/                     # command alias system
│   │   └── commands/
│   ├── commands/                  # core /foundation, /permission
│   ├── config/
│   │   └── containers/            # POJOs for Gson (Config, ChatConfig, MailConfig, RankConfig)
│   ├── datagroups/
│   ├── events/
│   │   └── playerEvents/
│   ├── lang/                      # LangKey + LangManager
│   ├── pages/                     # core UI pages (HFMainMenu, etc.)
│   ├── permission/                # PermissionManager, Permissions, PermissionGroups
│   └── player/                    # PlayerDataManager + POJOs (PlayerProfile, PlayerStats, ...)
├── modules/                       # optional, toggleable feature modules
│   ├── admin/
│   │   ├── commands/
│   │   └── pages/
│   ├── analytics/
│   ├── chat/
│   │   ├── commands/
│   │   └── events/
│   ├── friend/
│   ├── home/
│   │   ├── commands/
│   │   └── pages/
│   ├── mail/
│   │   ├── commands/
│   │   └── pages/
│   ├── rank/
│   │   └── commands/
│   └── utility/
│       ├── commands/
│       └── pages/
└── utils/                         # FileUtils, PlayerUtils, SchedulerUtils, TimeUtils
```

**Rules:**

- New module → new top-level folder under `modules/`. It owns a `XyzManager`, optional `commands/`, optional `pages/`, optional `events/`.
- New core system (something every module relies on) → new folder under `core/`.
- Pure helpers with no state → `utils/`. Static methods only.
- UI pages always live in their owning module's `pages/` subfolder.

Resources live under `src/main/resources/Common/UI/Custom/Pages/` matching the module the page belongs to. Use `HF_<Name>.ui` for top-level pages, organize multi-file forms in subfolders (see `Reporting/`).

---

## 2. Naming Conventions

### 2.1 Classes

| Kind                          | Convention                                  | Examples                            |
|-------------------------------|---------------------------------------------|-------------------------------------|
| Main class                    | `<Plugin>` (PascalCase)                     | `HytaleFoundations`                 |
| Manager                       | `<Domain>Manager`                           | `ChatManager`, `RankManager`        |
| Persistence holder (Gson POJO)| `<Domain>File` or `<Domain>Holder`          | `RankFile`, `AffixHolder`           |
| Defaults enum                 | `Default<Domain>` (singular)                | `DefaultAffix`, `DefaultRank`       |
| Permissions enum              | `Permissions` (plural, sits in `permission/`) | `Permissions`                     |
| Config POJO                   | `<Name>Config`                              | `Config`, `ChatConfig`, `MailConfig`|
| Command (single)              | `<Action>Command`                           | `AnvilCommand`, `PlaytimeCommand`   |
| Command collection            | `<Domain>Command` (extends `AbstractCommandCollection`) | `RankCommand`, `MailCommand` |
| Sub-command (nested)          | `<Domain><Action>Command` (private static)  | `RankSetCommand`, `MailInboxCommand`|
| Event listener                | `Player<Event>Listener`                     | `PlayerChatListener`, `PlayerDeathListener` |
| UI Page                       | `<Name>Page` (or `<Name>Menu` for top-level menus) | `MailBoxPage`, `HFMainMenu`  |
| Util                          | `<Domain>Utils`                             | `FileUtils`, `TimeUtils`            |

### 2.2 Fields, Methods, Parameters

- **Fields:** `camelCase`, `private final` whenever possible. Concurrent maps named after their content: `playerProfiles`, `commandMap`, `activeMessengers`.
- **Methods:** `camelCase`. Verbs: `getX`, `setX`, `addX`, `removeX`, `loadX`, `saveX`, `createX`, `initializeXManager()`.
- **Booleans:** `is<X>` getter, `set<X>(boolean)` setter (e.g. `isMuted` / `setMuted`). For "is enabled" feature flags: `isToggle<X>Module`.
- **Parameters:** `targetUsername`, `senderUsername`, `playerRef`, `commandContext`, `commandID`, `affixId` — be specific. Never `name`, `n`, `u`, `s`. Single-letter only inside short lambdas / loops.
- **Constants (`public static final` / enum-style):** `SCREAMING_SNAKE_CASE`. Examples: `PROFILE_FILENAME`, `COMMAND_FILE_NAME`, `CONFIG_VERSION`.

### 2.3 Enums

- Constants are `SCREAMING_SNAKE_CASE`.
- Each enum constant carries the data for one default (id, display text, permission, priority, aliases).
- Provide `getX()` accessors for every field. No setters (enums are immutable).
- `DefaultCommands`, `DefaultAffix`, `DefaultRank`, `DefaultColors`, `Permissions`, `PermissionGroups` are all template examples.

### 2.4 Gson `@SerializedName`

Every persisted field gets an explicit `@SerializedName`. Naming convention is **`snake_case`** for player/system data fields, **`SCREAMING_SNAKE_CASE`** for top-level structural keys in file POJOs.

```java
@SerializedName("VERSION")        private final String version = CONFIG_VERSION;
@SerializedName("LANG")           private String lang;
@SerializedName("debug_mode")     private boolean debugMode;
@SerializedName("module_admin")   private boolean toggleAdminModule;
@SerializedName("RANK_LIST")      private List<Rank> rankList = new ArrayList<>();
@SerializedName("data_groups")    private final Map<String, List<String>> dataGroups = new ConcurrentHashMap<>();
```

The Java field name uses `camelCase`; the JSON key uses the `@SerializedName` value. **Do not rely on default field-name serialization** — always annotate.

### 2.5 Packages

Lowercase, no underscores, plural for "kinds of things" (`commands/`, `pages/`, `events/`, `containers/`, `playerEvents/`). Singular for systems (`alias/`, `chat/`, `mail/`, `rank/`).

---

## 3. Hard Rules (Non-Negotiable)

### 3.1 No Hardcoded User-Facing Strings — Ever

**Every** message a user or the console sees goes through `LangManager`. This means:

1. Add a new `LangKey` enum constant with a config key and a sane English default.
2. Send via `LM.getPlayerMessage(username, LangKey.X, args...)`, `LM.getConsoleMessage(LangKey.X, args...)`, or `LM.getAbstractMessage(commandContext, LangKey.X, args...)`.

Placeholder syntax in the default message: `{0}`, `{1}`, `{2}`, etc. — they're substituted in order from the `args` varargs.

```java
// CORRECT
commandContext.sender().sendMessage(
    LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername)
);

// WRONG — hardcoded string
commandContext.sender().sendMessage(Message.raw("Player not found!"));
```

The only acceptable exceptions: bootstrap log lines that run **before** `LM` is initialized, and developer-only `System.out.println` inside a `TestPlayerCommand`. Even there, prefer adding a `LangKey`.

When sending a "this is a list" header followed by items, use `LangKey.LIST_CONTEXT` for the header and `LangKey.LIST_ITEM` (or `LIST_NUMBERED_ITEM`) for each entry.

### 3.2 Always Use the `LM` Static Import

```java
import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;
```

Never write `hytaleFoundations.getLangManager().getPlayerMessage(...)`. The public getter exists for symmetry only; in practice always go through `LM`. This applies to `myLogger` and `random` too.

### 3.3 Color Codes Go Through `ColorManager`

Don't embed raw hex or section signs in code. Use `&`-prefixed legacy codes (e.g. `&c&l`, `&r&e`, `&-`) inside `LangKey` defaults and let `ColorManager.parseText(...)` handle resolution. `LM` already routes through `ColorManager` when building messages, so for normal flows you just write the `&` codes in the lang default and trust the chain.

`&r` resets, `&l` is bold, `&o` is italic. `&-` followed by a code escapes color parsing for the following segment (used for display-name color codes the player chose themselves). Don't invent codes — the legal set lives in `DefaultColors` + the special codes in `ColorManager.specialCodes`.

### 3.4 Error Handling: Wrap, Log, Continue

Every potentially-throwing block is wrapped in `try / catch (Exception e)` and routed through `FileUtils.logError(...)`. The pattern:

```java
try {
    // work
} catch (Exception e) {
    logError("MethodOrClassName", e);
}
```

The `at` string is short and identifies the location for the error-log filename (`error_log{id}_{at}_<timestamp>.txt`). Use the class name or class-name + method when ambiguous (e.g. `"PlayerKillListener"`, `"saveIssueReport - isUpdated"`).

After logging, **continue normal flow**. Don't rethrow unless the surrounding contract demands it.

Use `myLogger.atInfo()`, `atWarning()`, `atSevere()` for in-process logs (they go through `HytaleLogger`). Always log the result via `LM.getConsoleMessage(LangKey.X, ...).getAnsiMessage()` for consistency.

### 3.5 Concurrent Collections by Default

Maps and sets that may be touched from multiple threads (which is most of them, given the scheduler) use `ConcurrentHashMap` / `ConcurrentHashMap.newKeySet()`. Lists stay as `ArrayList` unless concurrent access is proven. Don't use `Collections.synchronizedX(...)`.

### 3.6 Never Push to `master`

The author's hard rule from `nafees.txt`. Default branch is whatever the project uses (`main` or feature branches). If Claude Code is committing, it pushes to a feature branch and opens a PR.

---

## 4. Commands

### 4.1 Single vs. Collection

- **Single command (no sub-commands):** one top-level class extending `AbstractPlayerCommand` (or `AbstractAsyncCommand`, or `AbstractCommand`) in `<module>/commands/`. Examples: `AnvilCommand`, `PlaytimeCommand`, `BlockPlayerCommand`.
- **Command with sub-commands:** one top-level class extending `AbstractCommandCollection` in `<module>/commands/`, with every sub-command nested as a `private static class` inside it.

Examples of the collection pattern: `CoreCommand`, `AliasCommand`, `PermissionCommand`, `RankCommand`, `NicknameCommand`, `PrefixCommand`, `SuffixCommand`, `AffixCommand`, `MailCommand`, `HomeCommand`.

**Never split sub-commands into separate files.** They live nested inside the collection class.

### 4.2 Command Class Skeleton

```java
public class FooCommand extends AbstractCommandCollection {
    public FooCommand(HytaleFoundations hytaleFoundations) {
        super("foo", "Argument for all foo related commands.");
        this.requirePermission(Permissions.FOO.getPermission());
        this.addAliases("f", "fo");
        this.addSubCommand(new FooBarCommand(hytaleFoundations));
        this.addSubCommand(new FooBazCommand(hytaleFoundations));
    }

    private static class FooBarCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;

        public FooBarCommand(HytaleFoundations hytaleFoundations) {
            super("bar", "Does the bar thing.");
            this.requirePermission(Permissions.FOO_BAR.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            this.targetPlayer = withRequiredArg("Target", "Target player.", ArgTypes.STRING);
        }

        @Override
        protected void execute(@Nonnull CommandContext commandContext,
                               @Nonnull Store<EntityStore> store,
                               @Nonnull Ref<EntityStore> ref,
                               @Nonnull PlayerRef playerRef,
                               @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetUsername = commandContext.get(this.targetPlayer);
            // ...
        }
    }
}
```

### 4.3 Command Registration

- All commands are registered exactly once, in `HytaleFoundations.registerCommands()`.
- Group commands by module under `if(configManager.getDefaultConfig().isToggleXyzModule()) { ... }`.
- Core commands (those that should always exist) live above all toggled blocks.
- Every command added in code that the user can rebind through `/cm` must also be listed in `DefaultCommands` (the alias enum). When you add a new command, add a matching `DefaultCommands` entry with `name`, `targetCommand`, `permission`, and aliases.

### 4.4 Arguments

- Use `withRequiredArg(...)`, `withOptionalArg(...)`, `withListRequiredArg(...)`, `withListOptionalArg(...)`, `withFlagArg(...)` — store the result as `private final` fields on the command.
- Use `ArgTypes.STRING`, `ArgTypes.INTEGER`, etc.
- The description strings here are dev-facing (they show in `/help`-style output) and follow the same `LangKey` discipline if they're substantive, but short descriptions are fine inline.
- For free-form trailing text (chat messages, mail bodies), use `this.setAllowsExtraArguments(true)` and split `commandContext.getInputString().split("\\s+", N)` to recover the tail.

### 4.5 Async vs. Sync

- `AbstractAsyncCommand` is for work that must not block the main loop (file I/O, heavy iteration). Examples: `ReloadPluginCommand`, `SaveAllDataCommand`, `DebugCommand`.
- `AbstractPlayerCommand` is for player-only commands that interact with the player's world/store.
- `AbstractCommand` is for console-compatible commands.

When in doubt, look at the closest existing command and copy its base class choice.

### 4.6 Permission Guards

Every command should call `this.requirePermission(Permissions.X.getPermission())` in its constructor. The `Permissions` enum is the single source of truth — never write a raw permission node string at a call site.

---

## 5. Events / Listeners

Two flavors exist in this codebase:

**1. Lambda-style global listeners** (registered in `registerListeners()`):

```java
this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, e -> {
    PlayerServerJoinListener.onPlayerJoin(e, this);
});
```

These delegate to a static method on a `Player<X>Listener` class. The static method takes the event and the main class. Keep the lambda body to one line — all logic lives in the listener class.

**2. ECS Systems** (registered via `EntityStore.REGISTRY.registerSystem(...)`):

```java
PlayerDeathListener deathListener = new PlayerDeathListener(this);
EntityStore.REGISTRY.registerSystem(deathListener);
```

These extend `EntityEventSystem<EntityStore, ...>` or specific systems like `DeathSystems.OnDeathSystem`, `DamageEventSystem`. The constructor takes `HytaleFoundations`. Override `handle(...)` and `getQuery()`.

**Naming:** always `Player<Verb>Listener` (`PlayerChatListener`, `PlayerDeathListener`, `PlayerBlockBreakListener`, ...). Lives in `core/events/playerEvents/`.

When toggling stats collection or other module-gated behavior, check the relevant `PlayerStats` flag (`stats.isCollectStats()`) before mutating state.

---

## 6. UI Pages

### 6.1 Basic vs. Interactive

- **`BasicCustomUIPage`** — pure display, no two-way data. Example: `HFMainMenu`. Just appends a `.ui` file.
- **`InteractiveCustomUIPage<DataClass>`** — has buttons, inputs, dropdowns, two-way binding. The vast majority of pages in this codebase. Examples: `MailBoxPage`, `SendMailPage`, `PlayerReportPage`, `HomesPage`, `CreateAliasPage`, `NafsmunPage`.

### 6.2 Page Class Skeleton

```java
public class FooPage extends InteractiveCustomUIPage<FooPage.FooData> {
    private final HytaleFoundations hytaleFoundations;  // only if needed

    public static final String SUBMIT_BUTTON_ID = "SubmitButton";
    public static final String CANCEL_BUTTON_ID = "CancelButton";

    public static class FooData {
        public String inputValue;
        public String clickedButton;

        public static final BuilderCodec<FooData> CODEC =
            BuilderCodec.builder(FooData.class, FooData::new)
                .append(new KeyedCodec<>("@InputValue", Codec.STRING),
                        (obj, val) -> obj.inputValue = val,
                        obj -> obj.inputValue).add()
                .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                        (obj, val) -> obj.clickedButton = val,
                        obj -> obj.clickedButton).add()
                .build();
    }

    public FooPage(HytaleFoundations hytaleFoundations, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, FooData.CODEC);
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/HF_Foo.ui");
        events.addEventBinding(CustomUIEventBindingType.Activating, "#SubmitButton",
            new EventData()
                .append("@InputValue", "#InputField.Value")
                .append("ClickedButton", SUBMIT_BUTTON_ID));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton",
            new EventData().append("ClickedButton", CANCEL_BUTTON_ID));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull FooData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        if (CANCEL_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        if (SUBMIT_BUTTON_ID.equals(data.clickedButton)) {
            // validate, do work, send confirmation via LM, close page
        }
    }
}
```

### 6.3 Conventions

- **Button ID constants** are `public static final String <NAME>_BUTTON_ID = "<Name>Button";` at the top of the class.
- **Bound input field keys** start with `@` (`@InputValue`, `@ReceiverName`, `@MailSubject`) — this matches the `.ui` element's `Value` accessor.
- **Action keys** (button presses) use `ClickedButton`, with the value being a button-ID constant.
- The `Data` inner class is `public static`, has public fields (no getters/setters — they're transport objects), and exposes its `CODEC` as `public static final BuilderCodec<Data> CODEC`.
- Always check `player == null` and return early. Always handle the cancel/close path first.
- Cancel buttons set the page to `Page.None`.
- For submit paths, **always validate before acting**, send a `LangKey` success/failure message, then close the page.
- The `.ui` resource file is named `HF_<PageName>.ui` in `src/main/resources/Common/UI/Custom/Pages/` (for top-level) or a subfolder like `Reporting/` for multi-file forms.

### 6.4 Multi-Form Pages (Switching Visible Sub-Pages)

When a page has multiple "modes" (like `PlayerReportPage` switching between issue types), follow that file's pattern: keep `current<Thing>` state on the page class, build all sub-templates with `Visible: false`, and flip visibility in a helper method that takes a `UICommandBuilder` (or creates one and calls `this.sendUpdate(builder)`).

---

## 7. Player Data

### 7.1 The Four Files

Every player has a folder under `<datagroup>/players/<username>/` containing:

- `profile.json` — `PlayerProfile` (account state, settings, prefs)
- `stats.json` — `PlayerStats` (counters, playtime, kills)
- `mailbox.json` — `PlayerMailbox`
- `history.json` — `PlayerHistory` (offences/punishments)

Plus a per-data-group `usermap.json` mapping `UUID` → username.

### 7.2 Accessing Player Data

- **Online players:** `dataManager.getOnlinePlayerProfile(username)`, `getOnlinePlayerStats(username)` — pulls from in-memory `ConcurrentHashMap`.
- **Online or offline:** `dataManager.getPlayerProfile(username)`, `getPlayerStats(username)` — tries memory first, falls back to disk.
- **UUID lookup:** `dataManager.getPlayerUUID(username)` or `getPlayerUUID(sender, username)` (the second overload also messages `sender` if not found).
- **Existence checks:** `doesPlayerExist(username)` (online OR data on disk), `doesPlayerDataExist(username)` (disk only).

### 7.3 Adding New Player Fields

If you add a field to `PlayerProfile` or `PlayerStats`:

1. Add a `@SerializedName("snake_case_name") private <type> <camelCaseField>;`.
2. Add `getX()` / `setX(...)` accessors (and `addX()` / convenience mutators if it's a counter or list).
3. If it's a counter, also add `addX()` that increments.
4. Set a sane default inside `PlayerDataManager.createPlayerProfile(...)` or `createPlayerStats()`.
5. **Don't break existing saves.** Gson will read missing fields as `0` / `null` / `false`, which is usually fine. If you need a non-trivial default for old data, handle it in `loadPlayerData(...)` or via `FileUtils.updateJsonFile(...)`.

### 7.4 Saving

- Online players auto-save on logout via `playerLogout(...)` and `savePlayTime(...)`.
- Bulk save: `saveAllPlayerData()`.
- Single save: `savePlayerData(username, PROFILE_FILENAME, profile)` or `savePlayerData(username, PROFILE_FILENAME, profile, false)` to suppress info logging.

---

## 8. Config Files

### 8.1 Container POJOs

Live in `core/config/containers/`. Each one:

- Has a `@SerializedName("version") private String version;` field.
- Uses `@SerializedName("snake_case")` on every persisted field.
- Has only getters and setters — no logic.
- Top-level structural keys may use `SCREAMING_SNAKE_CASE` (e.g. `"VERSION"`, `"COMMAND_LIST"`, `"PERMISSION_LIST"`); leaf data fields use `snake_case`.

### 8.2 ConfigManager Methods to Use

- **Path constants** live as `public static Path` fields in `ConfigManager`. When adding a new file:
  1. Add `public static final String XYZ_FILE_NAME = "xyz.json";`
  2. Add `public static final String XYZ_VERSION = "v1.0";`
  3. Add `public static Path XYZ_FILE_PATH;` and assign it in the constructor: `XYZ_FILE_PATH = dataDirectory.resolve(XYZ_FILE_NAME);`
- **Loading:** `loadJsonFile(NAME, PATH, Class.class, showInfo)` — returns null if file missing or malformed.
- **Saving:** `saveJsonFile(NAME, PATH, dataObject, showInfo)`.
- **Updating** (adds new fields from defaults without overwriting user changes): `updateJsonFile(NAME, PATH, defaultDataObject, showInfo)`. Always offered to users via the `/foundation update` admin command.

### 8.3 The Version String

Every persisted config has its version as a `public static final String` in `ConfigManager` (e.g. `CONFIG_VERSION = "v1.0"`). When you make a breaking schema change, bump it. The `FileUtils.syncJsonObjects(...)` logic preserves user data through additive changes — bumping the version is documentation, not enforcement.

---

## 9. Utilities

### 9.1 FileUtils

The only thing that touches the filesystem directly. Already provides:

- `createDirectory(path, showInfo)`
- `loadJsonFile(...)` (multiple overloads)
- `getJsonObject(...)` for raw `JsonObject` access
- `saveJsonFile(...)` (multiple overloads)
- `updateJsonFile(...)` for additive schema sync
- `syncJsonObjects(existing, target)` — recursive merge
- `logError(...)` (multiple overloads) — see §3.4

**Never write to disk via raw `Files.write` / `FileWriter` in module code.** Use `FileUtils`. Acceptable exceptions exist (e.g. plain-text `.txt` report logs in `ReportManager`), but JSON always goes through `FileUtils`.

### 9.2 TimeUtils

Use for all duration parsing and formatting:

- `parseDuration("1d8h5m33s")` → millis
- `formatDuration(millis)` → human string
- `getTimeNow()` → `dd/MM/yyyy HH:mm:ss`
- `getFileSafeTime()` → `dd-MM-yyyy_HH-mm-ss` (for filenames)
- `getCurrentDate()` → `dd/MM/yy`
- `getDate(millis)` → format an arbitrary timestamp
- `getTimeSince(pastTimestamp)` → human "since" string

### 9.3 SchedulerUtils

Wrapper over `ScheduledExecutorService`. Use it instead of raw executors:

- `runTaskAsync(Runnable)` — fire-and-forget.
- `runTaskLater(name, runnable, delay, unit)` — one-shot delayed.
- `runTaskTimer(name, runnable, initialDelay, period, unit)` — repeating.
- `shutdownScheduler(name)` — cancel one.
- `shutdown()` — cancel all (called from `HytaleFoundations.shutdown()`).

Names are unique. Re-registering replaces the old task. Use descriptive names like `"chatSaveScheduler"`, `"MovementTracker"`, `"clearActiveMessageCache"`.

### 9.4 PlayerUtils

- `findPlayerByName(username)` or `findPlayerByName(caller, username)` — returns `PlayerRef` or null.
- `isPlayerOnline(username)` — boolean.

Always import statically when used heavily: `import static com.raeden.hytale.utils.PlayerUtils.findPlayerByName;`.

---

## 10. Permissions

### 10.1 Adding a New Permission

1. Add an entry to the `Permissions` enum (path: `core/permission/Permissions.java`). Format: `XYZ("hytalefoundations.<category>.<action>")`.
2. Reference it as `Permissions.XYZ.getPermission()` everywhere — never write the string.
3. If it should belong to a default group, add it to `PermissionGroups.<GROUP>.getPermissionSet()`.

### 10.2 Categories

- `hytalefoundations.access` — base access (everyone)
- `hytalefoundations.admin` — full admin
- `hytalefoundations.admin.<feature>` — admin sub-permissions
- `hytalefoundations.<feature>` — feature-level player permissions
- `hytalefoundations.utils.<thing>` — utility permissions

### 10.3 Checking

Always via `PermissionManager`:

- `hytaleFoundations.getPermissionManager().hasPermission(playerRef, "node")` — generic.
- `hytaleFoundations.getPermissionManager().isPlayerAdmin(playerRef)` — convenience.
- Command-level guard: `this.requirePermission(Permissions.X.getPermission())` in the constructor.

---

## 11. Language Files

### 11.1 LangKey Enum

Located at `core/lang/LangKey.java`. **Adding a new message:**

1. Pick the right section header in the enum (`// Core System`, `// Reporting System`, etc.). If none fits, add a new commented section near the related ones.
2. Add the constant in `SCREAMING_SNAKE_CASE`, with a dotted `key` (`category.action.detail`) and a `defaultMessage` containing `&`-prefix color codes and `{0}`, `{1}` placeholders.
3. Use it via `LM.getPlayerMessage(...)` / `getConsoleMessage(...)` / `getAbstractMessage(...)`.

### 11.2 Language Files on Disk

- Located in `<dataDir>/lang/<lang-code>.lang`.
- `en-us.lang` is auto-generated from the enum on first run.
- Format: `category.action.detail = Message text with &c color and {0} placeholders\n`.
- Lines starting with `#` are comments. Section headers are auto-grouped by the leading dot segment.
- Reload via `/foundation lang reload`.

### 11.3 Prefix Behavior

Player messages automatically get prefixed with `CHAT_MSG_PREFIX` (or `CHAT_MSG_PREFIX_ADMIN` for admins). Some keys are in `LangManager.prefixExclusionList` and skip the prefix (list items, PM formats, headers). When adding a new key that's a sub-line (list item, inline format), add it to the exclusion list at the top of `LangManager`.

---

## 12. Style Conventions

### 12.1 Imports

- Group static imports last, separated by a blank line.
- Heavy static imports for `LM`, `myLogger`, and `FileUtils.*` are encouraged.
- Wildcard imports are fine for internal packages (`import static com.raeden.hytale.utils.FileUtils.*;`).

### 12.2 Braces & Spacing

- Opening brace on the same line. Allman style is forbidden.
- One space after `if`, `for`, `while`, `catch`. Existing code is sometimes inconsistent here (`if(...)` with no space) — **match the surrounding file**.
- No space inside parens: `if(x.isEmpty())`.
- Always brace `if` / `else` blocks even for one-liners, with one exception: single-statement guards on the same line as the `if` (e.g. `if(profile == null) return;`).

### 12.3 Null Checks & Early Returns

Liberal early returns. The pattern:

```java
if(profile == null) return;
if(!commandManager.getCommandMap().containsKey(commandID)) {
    commandContext.sender().sendMessage(LM.getPlayerMessage(...));
    return;
}
// happy path
```

Don't deeply nest `if` blocks. Bail early.

### 12.4 String Concatenation in Messages

Inline color codes and concatenation are fine for ad-hoc admin output:

```java
"&r&e&lID: &r&b&l" + entry.getKey()
    + "&r&7&l [Target: &r&a" + entry.getValue().getTargetCommand() + "&r&7&l| "
    + ...
```

This is the established style for `/list`-style admin commands. **Player-facing** messages still go through `LangKey`.

### 12.5 `final` Discipline

- Method params: not marked `final` unless required.
- Fields holding the main class or other managers: **always `private final`**.
- Local variables: `final` is optional and rare in this codebase. Match the file.

### 12.6 `@Nonnull`

Use `javax.annotation.Nonnull` (matching the rest of the codebase, per the Hytale plugin template / TheNullicorn guide referenced in the docs). All command/listener/page override parameters are `@Nonnull` to match the API contracts.

### 12.7 Javadoc / Comments

The existing codebase is light on Javadoc. Don't add it unless something is genuinely non-obvious. Prefer naming things well over commenting.

Section-divider comments (`// Loading and Saving`, `// Helper classes`, `// Getters and Setters`) are used inside large manager classes. Keep them when they help reading flow.

---

## 13. Devlog

The project keeps a personal devlog at `src/docs/devlogs/nafees.txt`. The format:

```
=========
DD/MM/YY:
=========
** Context: <what was being worked on>
** Goal: <what was being attempted>

> <change 1>
> <change 2>
> [EXP] <experimental change>
> [UT] <untested feature>
```

**Rules when updating the devlog (from `nafees.txt`):**

1. Note down every change made in the session.
2. Note new files created or removed.
3. Mention specific bug fixes.
4. Mention variable name changes.
5. Use `[EXP]` prefix for experimental features.
6. Use `[UT]` prefix for untested features.
7. Write the date correctly for the day the work was done (DD/MM/YY).
8. Include Context and Goal lines before the entries.
9. **Never push to a `master` branch.**

**When generating a devlog entry, keep it short and straightforward.** No marketing-speak. No technical jargon walls. One-line bullets that describe what changed in plain English. Match the existing tone (see Nafees's `'26:` entry — short, factual, casual).

---

## 14. When Building Something New — The Checklist

Before opening a PR (or finishing a session), confirm:

- [ ] No hardcoded user-facing strings — every message has a `LangKey`.
- [ ] Used static `LM` import, not `getLangManager()`.
- [ ] `private final HytaleFoundations hytaleFoundations` field if the class needs access to managers; constructor takes it.
- [ ] No new static singletons. Managers reached via `hytaleFoundations.get<X>Manager()`.
- [ ] If the new thing is persistent: Manager + File/Holder + Default trio is in place.
- [ ] If the new thing is a module: toggle in `Config`, gated registration in `HytaleFoundations`, included in `ConfigManager.reloadPlugin()`.
- [ ] If the new thing is a command:
  - [ ] Top-level if single, nested inside `AbstractCommandCollection` if has sub-commands.
  - [ ] Permission guarded via `requirePermission(Permissions.X.getPermission())`.
  - [ ] Registered in `HytaleFoundations.registerCommands()`.
  - [ ] Entry added to `DefaultCommands` enum.
- [ ] If the new thing is a UI page:
  - [ ] `.ui` file at `Common/UI/Custom/Pages/HF_<Name>.ui` (or sub-folder for multi-file forms).
  - [ ] Button-ID constants at top of class.
  - [ ] Cancel path handled first in `handleDataEvent`.
  - [ ] Validation before action; success/failure via `LangKey`.
- [ ] If the new thing is an event listener: lives in `core/events/playerEvents/`, named `Player<X>Listener`, registered in `registerListeners()`.
- [ ] All filesystem JSON I/O goes through `FileUtils`.
- [ ] All scheduling goes through `SchedulerUtils`.
- [ ] All errors caught and logged via `FileUtils.logError(...)`.
- [ ] All collections that may be touched off-thread are `ConcurrentHashMap` / `ConcurrentHashMap.newKeySet()`.
- [ ] Devlog updated for the day's work (short, plain-English bullets).
- [ ] Not pushed to `master`.

---

## 15. Things to Ask the User About

When in doubt, **ask Raeden before guessing**:

- Whether to make a new file or extend an existing one.
- Whether something is a "core" system or a "module."
- The exact wording of a user-facing `LangKey` default.
- Whether a new permission should belong to a default permission group.
- Whether to bump a config version.
- Whether to expose a new manager method publicly or keep it package-private.

It's better to halt and ask than to invent and have to redo it.

---

## 16. Things to NEVER Do

- Never invent Hytale API method names. Look them up in the server jar or `hytalemodding.dev`.
- Never use a Map/Set that's not concurrent for state shared across threads.
- Never write a hardcoded user-facing string.
- Never write a raw permission node — use `Permissions.X.getPermission()`.
- Never `System.out.println` outside of dev/test stubs.
- Never bypass `FileUtils` for JSON I/O.
- Never bypass `SchedulerUtils` for scheduling.
- Never split sub-commands of an `AbstractCommandCollection` into separate files.
- Never push to a branch named `master`.
- Never silently swallow exceptions. Always `logError(...)`.
- Never use static singletons for managers — go through the main class.

---

*End of guide. When this document and the existing code disagree, the code is the source of truth — file an issue or ask before "fixing" the code to match this document.*
