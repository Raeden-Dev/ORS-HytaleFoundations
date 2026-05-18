# Hytale Custom UI — Bulletproof Construction Guide

> **Purpose of this document.** This is a single-file, end-to-end reference for building any Hytale Custom UI feature from scratch. It is intended to be loaded into Claude Code (or any other coding agent) so it can generate a complete, working pair of `.ui` markup + `.java` page class for any new feature on request.
>
> **Source material.** Distilled from the official Hytale modding docs (`https://hytalemodding.dev/en/docs/official-documentation/custom-ui`), the official plugin UI guide (`https://hytalemodding.dev/en/docs/guides/plugin/ui`), and verified production-grade examples (Send Mail, Player Report, Profile, Settings UIs).
>
> **Scope.** Server-controlled "Custom UI" only — Custom Pages and Custom HUDs. The built-in client UI (main menu, inventory, hotbar, chat) is **not** moddable.

---

## Table of Contents

1. [Mental Model](#1-mental-model)
2. [Project Layout & Manifest](#2-project-layout--manifest)
3. [The `.ui` File — Markup Language](#3-the-ui-file--markup-language)
4. [Layout System (Anchor, Padding, LayoutMode, FlexWeight)](#4-layout-system)
5. [Common.ui — The Shared Style Library](#5-commonui--the-shared-style-library)
6. [Element Catalogue](#6-element-catalogue)
7. [Property Types & Styling](#7-property-types--styling)
8. [Templates, Named Expressions & Document References](#8-templates-named-expressions--document-references)
9. [The Java Side — Page Classes](#9-the-java-side--page-classes)
10. [UICommandBuilder — Manipulating UI From Server](#10-uicommandbuilder)
11. [UIEventBuilder & EventData — Handling User Input](#11-uieventbuilder--eventdata)
12. [BuilderCodec — Wiring UI Values Into Java](#12-buildercodec)
13. [Lifecycle: build → event → handleDataEvent → sendUpdate/rebuild](#13-lifecycle)
14. [Showing & Closing a Page (PageManager)](#14-showing--closing-a-page)
15. [Custom HUDs (Display-Only Overlays)](#15-custom-huds)
16. [Recipe: Building Any Feature From Scratch](#16-recipe-building-any-feature-from-scratch)
17. [Full Worked Examples](#17-full-worked-examples)
18. [Common Pitfalls & Diagnostic Mode](#18-common-pitfalls)
19. [Quick Reference Cheat Sheet](#19-cheat-sheet)

---

## 1. Mental Model

Hytale Custom UI is **declarative, asset-driven, event-driven, and selector-based**.

- **Declarative.** You don't construct UI objects in Java line by line. You write `.ui` files that describe what the UI looks like, and you send the server **commands** that say "append this template", "set this label's text", "clear these children".
- **Asset-driven.** Layout lives in `.ui` files (assets shipped with your plugin), not hardcoded in code. Java only loads them and mutates them.
- **Event-driven.** The client emits events (clicks, value changes, focus, etc.) when the player interacts. The server receives these events, decodes them into a typed data object, and runs your `handleDataEvent` method.
- **Selector-based.** You target elements by ID with selectors that look like CSS: `#Title`, `#List[0] #Name`, `#Label.TextColor`.

### Two kinds of moddable UI

| Kind | Class to extend | What it is | Captures input? | Dismissable? |
|------|-----------------|------------|-----------------|--------------|
| **Custom Page** (no input) | `BasicCustomUIPage` | Full-screen overlay, display-only | No | Optional (via lifetime) |
| **Custom Page** (interactive) | `InteractiveCustomUIPage<T>` | Full-screen overlay with form / buttons | Yes | Optional (via lifetime) |
| **Custom HUD** | `CustomUIHud` (set via `HudManager#setCustomHud`) | Persistent on-screen overlay (quest tracker, mini-map widget, status panel) | No (display only) | Always visible |

Rule of thumb: **anything the player needs to click, type, or pick from** → interactive page. Anything that just **shows information passively while they play** → HUD.

---

## 2. Project Layout & Manifest

A Hytale plugin is a standard Gradle/Maven Java project. The UI-relevant pieces of the layout are:

```
my-plugin/
├── build.gradle.kts             # standard Hytale plugin gradle setup
├── libs/
│   └── HytaleServer.jar         # compile-only dependency
└── src/main/
    ├── java/
    │   └── com/yourname/yourplugin/
    │       ├── YourPlugin.java
    │       ├── commands/        # commands that open pages
    │       └── ui/              # one Java class per page/HUD
    │           ├── ShopPage.java
    │           ├── ShopRowTemplate.java
    │           └── QuestHud.java
    └── resources/
        ├── manifest.json
        └── Common/
            └── UI/
                └── Custom/
                    ├── Pages/
                    │   ├── ShopPage.ui
                    │   └── ShopRow.ui
                    ├── Hud/
                    │   └── QuestHud.ui
                    └── Images/
                        └── shop_icon.png
```

### Critical: `manifest.json` MUST include the asset-pack flag

```json
{
  "Group": "YourGroup",
  "Name": "YourPlugin",
  "Version": "1.0.0",
  "Main": "com.yourname.yourplugin.YourPlugin",
  "IncludesAssetPack": true
}
```

If `"IncludesAssetPack": true` is missing, the client never receives your `.ui` files and every page will fail to load.

### Critical: `.ui` files MUST live under `resources/Common/UI/Custom/`

When you write `uiCommandBuilder.append("Pages/ShopPage.ui")` in Java, the path is resolved **relative to `resources/Common/UI/Custom/`**. So that call loads `resources/Common/UI/Custom/Pages/ShopPage.ui`.

Image paths inside a `.ui` file are resolved relative to **that `.ui` file's own directory** (use `../` to walk up).

### Build packaging

Your `build.gradle.kts` `tasks.jar` block needs `from("src/main/resources")` so the assets are baked into the JAR:

```kotlin
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("YourPlugin")
    archiveVersion.set("1.0.0")
    from("src/main/resources")
}
```

---

## 3. The `.ui` File — Markup Language

`.ui` is **not** HTML. It's a custom DSL that looks somewhat like CSS-nested object literals.

### 3.1 Basic syntax

```ui
ElementType #OptionalID {
    PropertyName: value;
    PropertyName2: (NestedKey: value, NestedKey2: value);

    // children just nest inside
    ChildElement { ... }
}
```

- **Block braces `{ … }`** enclose an element.
- **`#Name`** assigns an ID. IDs are what selectors target later (`#Name`).
- **Properties** are `Key: value;` pairs (semicolon terminated).
- **Objects** are written as parenthesized lists: `(Key: value, Key2: value)`.
- **Comments**: `// single line` and `/* block */`.

### 3.2 Documents and multiple root elements

A `.ui` document may contain **multiple root elements**. This is how the in-game UI gallery shows many side-by-side widgets in one file. Most pages have a single root: a `$Common.@PageOverlay { … }` invocation.

### 3.3 Element IDs vs. element types

```ui
Group #TopBar { … }     // type = Group, id = TopBar
TextButton #SaveButton { Text: "Save"; }
```

Two elements with the same ID under the same parent are fine in theory but make selectors ambiguous — give every interactive element a **unique ID**.

### 3.4 Children

Children are simply nested inside the braces. They are laid out in the order written, according to the parent's `LayoutMode`.

```ui
Group {
    LayoutMode: Top;          // stack children vertically
    Label { Text: "First"; }
    Label { Text: "Second"; }
    Label { Text: "Third"; }
}
```

---

## 4. Layout System

Every element has four layout-controlling concepts.

### 4.1 Container Rectangle

The box your parent allocated for you. You don't write this — it's derived.

### 4.2 `Anchor` — how YOU sit inside the container

`Anchor` is set as a parenthesized object:

```ui
Anchor: (Top: 10, Left: 20, Width: 100, Height: 30);
```

| Keys you can mix | Meaning |
|------------------|---------|
| `Width`, `Height` | Fixed size |
| `Top`, `Bottom`, `Left`, `Right` | Distance from that edge of container |
| `Full: N` | Shortcut for `Top: N, Bottom: N, Left: N, Right: N` (stretch) |

**Anchor recipes:**

| Goal | Anchor |
|------|--------|
| Fixed 200×40 box | `(Width: 200, Height: 40)` |
| Stretched to fill parent (10px inset) | `(Full: 10)` or `(Top: 10, Bottom: 10, Left: 10, Right: 10)` |
| Pinned to bottom-right corner | `(Bottom: 10, Right: 10, Width: 100, Height: 30)` |
| 300px wide column, full height | `(Top: 0, Bottom: 0, Left: 0, Width: 300)` |
| Just gives a fixed height (for a stacked child) | `(Height: 30)` |

### 4.3 `Padding` — inner spacing that pushes YOUR children inward

```ui
Padding: (Full: 20);                            // all sides
Padding: (Top: 10, Bottom: 20, Left: 15, Right: 15);
Padding: (Horizontal: 20, Vertical: 10);        // shorthand pairs
```

### 4.4 `LayoutMode` — how YOU arrange your children

| Value | Behaviour |
|-------|-----------|
| `Top` | Stack vertically, top-aligned (most common) |
| `Bottom` | Stack vertically, bottom-aligned |
| `Left` | Stack horizontally, left-aligned |
| `Right` | Stack horizontally, right-aligned |
| `Center` | Stack horizontally, centered |
| `Middle` | Stack vertically, centered |
| `CenterMiddle` | Horizontal stack, fully centered (X+Y) |
| `MiddleCenter` | Vertical stack, fully centered (X+Y) |
| `Full` | Children use absolute `Anchor` positioning |
| `TopScrolling` / `BottomScrolling` | Same as Top/Bottom but adds vertical scrollbar |
| `LeftScrolling` / `RightScrolling` | Same as Left/Right but adds horizontal scrollbar |
| `LeftCenterWrap` | Horizontal flow with wrap to next line, rows centered |

When `LayoutMode` is `Top`/`Bottom`, only `Height` from a child's `Anchor` is honored (width fills parent unless overridden). Use the child's `Anchor.Bottom`/`Anchor.Right` to add spacing between elements. The common idiom is inserting an **empty spacer Group** instead, which reads clearer:

```ui
Group { Anchor: (Height: 15); }  // 15px vertical spacer
Group { Anchor: (Width: 10); }   // 10px horizontal spacer
```

### 4.5 `FlexWeight` — distribute leftover space

After all explicit widths/heights are subtracted, remaining space is divided among children with `FlexWeight` proportional to their weight numbers.

```ui
Group {
    LayoutMode: Left;
    Anchor: (Width: 400);

    Button { Anchor: (Width: 100); }         // fixed
    Group  { FlexWeight: 1; }                // gets all remaining: 200px
    Button { Anchor: (Width: 100); }         // fixed
}
```

With multiple FlexWeight siblings: each gets `remaining × (weight / totalWeight)`.

### 4.6 `Visible`

```ui
Button #SaveBtn { Visible: false; }
```

A hidden element **does not take up layout space** (unlike CSS `visibility: hidden`). To preserve space, wrap it in a fixed-size container and toggle the child instead.

---

## 5. Common.ui — The Shared Style Library

`Common.ui` ships with the game and provides the **standard look and feel**. **You do NOT create or override it** — just import it.

```ui
$C = "../Common.ui";        // path is relative to your .ui file
// or
$C = "../../Common.ui";     // if you're one folder deeper
```

Use the in-game `/ui-gallery` command to browse every style/template `Common.ui` exposes, live.

### Frequently used Common.ui templates

| Reference | What it is |
|-----------|------------|
| `$C.@PageOverlay { … }` | The standard full-screen page background + dimming. Wrap every page in this. |
| `$C.@Container { … }` | Standard rounded panel with the game's border style. |
| `$C.@DecoratedContainer { … }` | Same as Container but with a `#Title` slot and `#Content` slot, used for proper "windowed" pages. |
| `$C.@Title { @Text = "…"; }` | Standard page title styling (large, uppercase, etc.). Used inside `#Title` slot. |
| `$C.@TextField #Foo { … }` | Standard text input field. |
| `$C.@DropdownBox #Foo { … }` | Standard dropdown picker. |
| `$C.@TextButton #Foo { Text: "…"; }` | Standard button. |
| `$C.@DefaultButtonStyle` | Reusable style object for a default button. |
| `$C.@DefaultScrollbar` | Reusable scrollbar style. |
| `$C.@BackButton {}` | Standard "back" / close icon in the top-left corner. |

### Standard page skeleton

The vast majority of pages follow this skeleton:

```ui
$C = "../Common.ui";

$C.@PageOverlay {
    $C.@DecoratedContainer {
        Anchor: (Width: 600, Height: 540);
        Padding: (Full: 20);

        #Title {
            Group {
                $C.@Title { @Text = "Your Page Title"; }
            }
        }

        #Content {
            LayoutMode: Top;
            // ... your page body here ...
        }
    }

    $C.@BackButton {}
}
```

---

## 6. Element Catalogue

Every element type below is a valid root in any block. They are all documented at `https://hytalemodding.dev/en/docs/official-documentation/custom-ui/type-documentation/elements/<elementname>`.

### 6.1 Containers / structure

| Element | Use |
|---------|-----|
| `Group` | Generic container. Workhorse of every layout — wrap things, add backgrounds, set LayoutMode. |
| `Panel` | Like Group with a built-in panel skin. |
| `DynamicPane` / `DynamicPaneContainer` | Pane that can be swapped at runtime — for tab content, wizard steps. |
| `TabNavigation` + `TabButton` | Built-in tab bar component. |
| `SceneBlur` | Blurs the game world behind your UI. Drop into `$C.@PageOverlay` if you want a stronger backdrop. |

### 6.2 Text

| Element | Use |
|---------|-----|
| `Label` | Plain text display. Supports `TextSpans` arrays for inline-rich text. |
| `HotkeyLabel` | Label that automatically renders the player's current keybind for an action. |
| `TimerLabel` | Counts up/down automatically. Useful for cooldowns, timers. |

### 6.3 Buttons

| Element | Use |
|---------|-----|
| `Button` | Bare clickable element. You provide all visuals (e.g. nest an `AssetImage`). |
| `TextButton` | Button with built-in centered text label. Most common. |
| `ActionButton` | Game-styled action button (e.g. action bar entries). |
| `BackButton` | Standard "go back" arrow. |
| `ToggleButton` | On/off button. Maintains state. |
| `TabButton` | A tab in a `TabNavigation`. |
| `MenuItem` | Row in a side menu. Has Default + Selected styles. |
| `CheckBox` / `LabeledCheckBox` / `CheckBoxContainer` | Standard checkbox primitives. |

### 6.4 Inputs

| Element | Use |
|---------|-----|
| `TextField` | Single-line text input. |
| `CompactTextField` | Smaller text input. |
| `MultilineTextField` | Multi-line textarea with `MaxLines`. |
| `NumberField` | Number-only input. |
| `Slider` / `FloatSlider` | Drag slider. |
| `SliderNumberField` / `FloatSliderNumberField` | Slider with numeric input next to it. |
| `DropdownBox` + `DropdownEntry` | Dropdown picker (see example below). |
| `ColorPicker` / `ColorPickerDropdownBox` / `ColorOptionGrid` | Color selection. |
| `CodeEditor` | Multi-line code-style editor with syntax highlighting. |

### 6.5 Visual / progress

| Element | Use |
|---------|-----|
| `AssetImage` | Static image from your plugin's assets. |
| `Sprite` | Single-frame sprite. Supports `SpriteFrame`. |
| `ProgressBar` | Horizontal/vertical fill bar. Configure with `BarTexturePath`, `ProgressBarDirection`. |
| `CircularProgressBar` | Round progress indicator. |

### 6.6 Game-specific

| Element | Use |
|---------|-----|
| `ItemIcon` | Renders an item by `ClientItemStack`. |
| `ItemSlot` / `ItemSlotButton` | Single inventory-style slot. |
| `ItemGrid` | A grid of `ItemGridSlot`s — inventories, shops. |
| `ItemPreviewComponent` / `CharacterPreviewComponent` | Live 3D previews of items / characters. |
| `BlockSelector` | Picker for block types. |
| `ReorderableList` / `ReorderableListGrip` | Drag-to-reorder list. |

### 6.7 DropdownBox skeleton

```ui
$C.@DropdownBox #DifficultyDropdown {
    Anchor: (Height: 40);
    Value: "NORMAL";   // initial selected value

    DropdownEntry { Value: "EASY";   Text: "Easy"; }
    DropdownEntry { Value: "NORMAL"; Text: "Normal"; }
    DropdownEntry { Value: "HARD";   Text: "Hard"; }
}
```

The `Value:` on the parent is the currently selected entry's `Value:`.

### 6.8 MultilineTextField skeleton

```ui
MultilineTextField #Body {
    Anchor: (Height: 180);
    PlaceholderText: "Write your message…";
    PlaceholderStyle: (TextColor: #6e7da1);
    MaxLines: 32;
    OutlineColor: #424a5e;
    OutlineSize: 2;
    Background: #263047;
    Padding: 10;
    ScrollbarStyle: (Spacing: 6, Size: 6);
}
```

---

## 7. Property Types & Styling

### 7.1 Primitive types

| Type | Example |
|------|---------|
| Boolean | `Visible: false;` |
| Int | `Height: 20;` |
| Float / Double / Decimal | `Min: 0.2;` |
| String | `Text: "Hi!";` |
| Char | `PasswordChar: "*";` (must be a single character) |
| Color | `Background: #ffffff;` |
| Object | `Style: (FontSize: 16, TextColor: #fff);` |
| Array | `TextSpans: [(Text: "Hi", IsBold: true), (Text: "World")];` |
| Translation key | `Text: %ui.general.cancel;` (prefix with `%`) |
| Font name | `Style: (FontName: "Secondary");` (`Default`, `Secondary`, `Mono`) |
| Path | `Background: "MyImage.png";` (relative to this `.ui` file) |

### 7.2 Color literals

| Form | Meaning |
|------|---------|
| `#rrggbb` | Opaque (e.g. `#3a7bd5`) |
| `#rrggbb(a.a)` | Alpha 0.0–1.0 (e.g. `#000000(0.3)` = 30% opaque black). **Preferred form for readability.** |
| `#rrggbbaa` | Alpha as hex byte |

### 7.3 Style objects

Most visual elements take a `Style:` property whose type matches the element (`LabelStyle`, `TextButtonStyle`, etc.).

```ui
Label #Title {
    Text: "Hello";
    Style: LabelStyle(FontSize: 16, TextColor: #ffffff, RenderBold: true);
    // type can be inferred:
    Style: (FontSize: 16, TextColor: #ffffff, RenderBold: true);
}
```

### 7.4 Button styles have state (Default / Hovered / Pressed)

This pattern is universal for `Button`, `TextButton`, `MenuItem`, `ToggleButton`, etc.

```ui
@PrimaryButtonStyle = TextButtonStyle(
    Default: (
        Background: #3a7bd5,
        LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true,
                     HorizontalAlignment: Center, VerticalAlignment: Center)
    ),
    Hovered: (
        Background: #4a8be5,
        LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true,
                     HorizontalAlignment: Center, VerticalAlignment: Center)
    ),
    Pressed: (
        Background: #2a6bc5,
        LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true,
                     HorizontalAlignment: Center, VerticalAlignment: Center)
    )
);

TextButton #Submit {
    Text: "SUBMIT";
    Anchor: (Width: 130, Height: 36);
    Style: @PrimaryButtonStyle;
}
```

`MenuItem` also has a `SelectedStyle` (with its own `Default` / `Hovered` states) for the selected nav state — see the Profile UI example.

### 7.5 Translations

Translation keys are referenced anywhere a string is accepted, with the `%` prefix:

```ui
Label { Text: %ui.profile.title; }
TextButton { Text: %ui.general.cancel; }
```

Translations themselves live in the game's localization system. From Java you can resolve a key with whatever language helper your project uses (e.g. `Lang.tr(playerUuid, key, fallback, args...)`).

---

## 8. Templates, Named Expressions & Document References

These are the three reuse mechanisms that turn `.ui` from "verbose markup" into "real programming".

### 8.1 Named expressions (`@Name`)

Constants scoped to a block. Declared at the **top** of a block, before any properties or children, with the `@` prefix.

```ui
@Title = "Hytale";
@ExtraSpacing = 5;

Label {
    Text: @Title;
    Style: (LetterSpacing: 2 + @ExtraSpacing);  // arithmetic works
}
```

### 8.2 Spread operator (`...`)

Reuses an object while overriding fields:

```ui
@BaseStyle = LabelStyle(FontSize: 24, LetterSpacing: 2);

Label {
    Style: (...@BaseStyle, FontSize: 36);   // size bumped to 36, letterspacing kept
}
```

Multiple spreads layer left-to-right:

```ui
Label { Style: (...@BigText, ...@SpacedText); }
```

### 8.3 Templates (reusable elements)

A named expression whose value is an **entire element block** — instantiate it as many times as you like.

```ui
// definition
@Row = Group {
    Anchor: (Height: 50);
    Label #Label { Anchor: (Left: 0, Width: 100); Text: @LabelText; }
    Group #Content { Anchor: (Left: 100); }
};

// usage
Group #Rows {
    LayoutMode: TopScrolling;

    @Row #FirstRow {
        @LabelText = "First row";        // override the local @LabelText
        #Content { TextInput {} }        // insert additional children into a slot
    }
    @Row #SecondRow {
        @LabelText = "Second row";
    }
}
```

Key rules:
- The template's body becomes the body of every instance.
- Overrides of local `@Name` values must appear at the **top** of the instance block.
- Children added in the instance are **inserted into the matching slot** (matched by `#Id`).

### 8.4 Document references (`$Name`)

Pulls in another `.ui` file as a namespace. Declared with `$` prefix at the top of the document.

```ui
$C = "../Common.ui";
$Nav = "../Nav/LeftNavPanel.ui";

$C.@PageOverlay { … }
$Nav.@LeftNavPanel {}
```

Paths are relative to the **current `.ui` file**.

---

## 9. The Java Side — Page Classes

Every page is a Java class that extends one of the page base classes.

### 9.1 Choice of base class

| Base class | When |
|------------|------|
| `BasicCustomUIPage` | Static page, no input. You only override `build` with a single `UICommandBuilder` argument. |
| `InteractiveCustomUIPage<T>` | Page with input/events. Generic `T` is your data class; you override `build` AND `handleDataEvent`. |
| `CustomUIHud` (subclass for HUDs) | Persistent overlay shown via `HudManager#setCustomHud`. |

### 9.2 Required imports

```java
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
```

### 9.3 `CustomPageLifetime`

Passed to `super(...)` in the constructor. Controls whether the player can close the page with ESC.

| Value | Meaning |
|-------|---------|
| `CustomPageLifetime.CanDismiss` | Player can close with ESC. Most common. |
| `CustomPageLifetime.CannotDismiss` | Forced-modal — useful for unavoidable choices (death screen, EULA). Use sparingly. |

### 9.4 Minimal `BasicCustomUIPage` (no input)

```java
public class WelcomePage extends BasicCustomUIPage {

    public WelcomePage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(@Nonnull UICommandBuilder cmd) {
        cmd.append("Pages/WelcomePage.ui");
        cmd.set("#Title.Text", "Welcome, " + playerRef.getUsername() + "!");
    }
}
```

### 9.5 Minimal `InteractiveCustomUIPage<T>` (with input)

```java
public class ConfirmPage extends InteractiveCustomUIPage<ConfirmPage.Data> {

    public static final String OK_BTN = "OkButton";
    public static final String CANCEL_BTN = "CancelButton";

    public ConfirmPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        cmd.append("Pages/ConfirmPage.ui");

        events.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#OkButton",
            EventData.of("ClickedButton", OK_BTN)
        );
        events.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#CancelButton",
            EventData.of("ClickedButton", CANCEL_BTN)
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull Data data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        if (OK_BTN.equals(data.clickedButton)) {
            // ... do the confirmed action ...
        }
        // Either way, close the page (otherwise client shows "Loading…" forever):
        player.getPageManager().setPage(ref, store, Page.None);
    }

    /** Data class — describes what the client can send back. */
    public static class Data {
        public String clickedButton;

        public static final BuilderCodec<Data> CODEC =
            BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                        (Data o, String v) -> o.clickedButton = v,
                        (Data o) -> o.clickedButton)
                .add()
                .build();
    }
}
```

---

## 10. UICommandBuilder

`UICommandBuilder` is how the server tells the client what to do with the UI tree. Every method returns `this`, so calls chain.

### 10.1 Commands

| Method | Effect |
|--------|--------|
| `append(String path)` | Load a `.ui` file into the page (path relative to `Common/UI/Custom/`). |
| `append(String selector, String path)` | Append a `.ui` file as a child of the selected element. |
| `appendInline(String selector, String inlineMarkup)` | Append raw `.ui` markup as a child. Server does **not** validate the markup. |
| `insertBefore(String selector, String path)` | Insert a UI file before the selected element. |
| `insertBeforeInline(String selector, String markup)` | Insert raw markup before selected element. |
| `set(String selector, String / int / float / double / boolean / Message value)` | Set a property. Many overloads. |
| `setNull(String selector)` | Set a property to null. |
| `clear(String selector)` | Remove all children of an element. |
| `remove(String selector)` | Remove the selected element itself. |

### 10.2 Selectors (CSS-ish)

| Selector | Means |
|----------|-------|
| `#Id` | Element with ID `Id`. |
| `#A #B` | Descendant `B` inside `A`. |
| `#List[0]` | First child of `#List`. `[1]` second, etc. |
| `#List[0] #Title` | `#Title` inside the first child of `#List`. |
| `#Label.TextColor` | The `TextColor` **property** of `#Label`. |

### 10.3 Common patterns

```java
// Load a page
ui.append("Pages/Shop.ui");

// Set static text and styles
ui.set("#Title.Text", "Welcome");
ui.set("#Title.Style.TextColor", "#ffffff");
ui.set("#Sidebar.Visible", false);

// Append a row template repeatedly to build a list, then address children by index
ui.clear("#ItemList");
for (int i = 0; i < items.size(); i++) {
    ui.append("#ItemList", "Pages/Shop/ItemRow.ui");
    String base = "#ItemList[" + i + "]";
    ui.set(base + " #Name.Text", items.get(i).name);
    ui.set(base + " #Price.Text", items.get(i).price + " coins");
}

// Append inline markup (cheap one-off)
ui.appendInline("#Messages", "Label { Text: \"No results\"; Style: (TextColor: #888); }");
```

### 10.4 Localized strings

You can pass a `Message` (translation) instead of a raw string:

```java
Message msg = Message.translation("ui.shop.title");
ui.set("#Title.Text", msg);
```

---

## 11. UIEventBuilder & EventData

### 11.1 Binding events

```java
events.addEventBinding(
    CustomUIEventBindingType.Activating,     // event type
    "#SaveButton",                            // selector
    EventData.of("Action", "save"),           // optional payload
    false                                      // locksInterface (optional)
);
```

| Param | Meaning |
|-------|---------|
| `CustomUIEventBindingType` | The kind of event to listen for. |
| `selector` | Which element to attach the listener to. |
| `EventData` (optional) | Static + dynamic values bundled with the event when it fires. |
| `locksInterface` (optional, default `true`) | If true, UI is locked ("Loading…") until server responds; if false, UI stays responsive. **Use `false` for inputs that fire constantly (sliders, text changes); use `true` for actions that submit forms.** |

### 11.2 Event types (`CustomUIEventBindingType`)

The enum has ~25 values. The ones you'll actually use:

| Value | Fires when |
|-------|------------|
| `Activating` | Button clicked / menu item picked / hotkey pressed. **The default "click" event.** |
| `RightClicking` | Right-click on element. |
| `DoubleClicking` | Double-click. |
| `MouseEntered` / `MouseExited` | Hover begin / end. |
| `ValueChanged` | Text field typed in, slider dragged, dropdown changed, checkbox toggled. |
| `FocusGained` / `FocusLost` | Input field focus changes. |
| `KeyDown` | Key press while focused. |
| `Dismissing` | The page is closing (ESC). |
| `Validating` | Form validation hook. |
| `ElementReordered` | A `ReorderableList` was rearranged. |
| `SlotClicking` | An `ItemSlot` was clicked. |
| `SelectedTabChanged` | `TabNavigation` switched tab. |

### 11.3 `EventData`

A `Map<String,String>` of payload values. **All values are strings** — convert numbers/bools yourself.

```java
EventData data = new EventData()
    .append("Action", "buy")
    .append("ItemId", String.valueOf(itemId))
    .append("Quantity", "1");

events.addEventBinding(CustomUIEventBindingType.Activating, "#BuyButton", data);
```

### 11.4 Reading current values from the UI

A payload value can be either a **literal string** (`"buy"`) or a **selector that resolves at event-fire time** (`"#TextField.Value"`). The client resolves these references at the moment the event fires and sends them back.

```java
// Static action label + dynamic field values together:
new EventData()
    .append("@ReceiverName", "#ToInput.Value")     // resolved from UI at fire time
    .append("@MailSubject",  "#SubjectInput.Value")
    .append("@MailBody",     "#MessageInput.Value")
    .append("ClickedButton", "SendButton")          // static literal
```

### 11.5 Keys and the `@` convention

- Keys that start with a **letter** must be **Uppercase**. Lowercase first letter will be rejected by the keyed codec.
- The `@` prefix on keys (e.g. `"@MailBody"`) is the convention for "this corresponds to a UI input value", and the corresponding `KeyedCodec` in your Data class uses the same `@MailBody` key. It's not magic — it's just consistency.

### 11.6 EventData.of(...) shortcut

```java
EventData.of("Action", "save")
// equivalent to:
new EventData().append("Action", "save")
```

---

## 12. BuilderCodec

For `InteractiveCustomUIPage<T>`, the generic `T` is your Data class, and you must provide a `BuilderCodec<T>` so the framework knows how to deserialize the event payload.

### 12.1 Anatomy

```java
public static class Data {
    public String receiverName;
    public String mailSubject;
    public String mailBody;
    public List<String> mailGifts;
    public String clickedButton;

    public static final BuilderCodec<Data> CODEC =
        BuilderCodec.builder(Data.class, Data::new)

            // String fields:
            .append(new KeyedCodec<>("@ReceiverName", Codec.STRING),
                    (Data o, String v) -> o.receiverName = v,
                    (Data o) -> o.receiverName)
            .add()

            .append(new KeyedCodec<>("@MailSubject", Codec.STRING),
                    (Data o, String v) -> o.mailSubject = v,
                    (Data o) -> o.mailSubject)
            .add()

            .append(new KeyedCodec<>("@MailBody", Codec.STRING),
                    (Data o, String v) -> o.mailBody = v,
                    (Data o) -> o.mailBody)
            .add()

            // Array field:
            .append(new KeyedCodec<>("@MailGifts", Codec.STRING_ARRAY),
                    (Data o, String[] v) -> o.mailGifts = Arrays.asList(v),
                    (Data o) -> o.mailGifts.toArray(new String[0]))
            .add()

            // Plain literal field:
            .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                    (Data o, String v) -> o.clickedButton = v,
                    (Data o) -> o.clickedButton)
            .add()

            .build();
}
```

### 12.2 Each entry's three pieces

| Piece | Purpose |
|-------|---------|
| `new KeyedCodec<>(key, primitiveCodec)` | Maps the event-payload key (`"@MailBody"`) to a primitive codec (`Codec.STRING`, `Codec.INT`, etc.). |
| Setter lambda `(Data, T) -> …` | Stores the decoded value on the Data object. |
| Getter lambda `(Data) -> T` | (Used for re-serialization symmetry.) |

### 12.3 Available `Codec.*` primitives

The common ones (this isn't exhaustive — see the SDK):

- `Codec.STRING`
- `Codec.STRING_ARRAY`
- `Codec.INT`
- `Codec.LONG`
- `Codec.FLOAT`
- `Codec.DOUBLE`
- `Codec.BOOLEAN`

For enums, your project will typically have a helper to wrap them. If you only have a string in the codec, decode it manually in `handleDataEvent`:

```java
MyEnum mode = data.modeRaw == null ? MyEnum.DEFAULT : MyEnum.valueOf(data.modeRaw);
```

### 12.4 Pattern: a single `action` field for everything

Many production pages funnel **all** events through a single `Action` string and dispatch on its value. This avoids exploding the Data class as you add buttons:

```java
public static class Data {
    public String action;

    public static final BuilderCodec<Data> CODEC =
        BuilderCodec.builder(Data.class, Data::new)
            .append(new KeyedCodec<>("Action", Codec.STRING),
                    (Data o, String v) -> o.action = v,
                    (Data o) -> o.action)
            .add()
            .build();
}
```

And on the binding side, encode the parameter in the action string:

```java
events.addEventBinding(CustomUIEventBindingType.Activating,
    "#SelectButton", EventData.of("Action", "profile:select:" + slot), false);
events.addEventBinding(CustomUIEventBindingType.Activating,
    "#DeleteButton", EventData.of("Action", "profile:delete:" + slot), false);
```

Then in `handleDataEvent`:

```java
if (data.action == null) return;
if (data.action.startsWith("profile:select:")) {
    int slot = Integer.parseInt(data.action.substring("profile:select:".length()));
    selectProfile(slot);
}
```

This is the "command bus" pattern and is what the Profile / Settings examples use.

---

## 13. Lifecycle

```
┌────────────────────────────────────────────────────────────────────────┐
│  1. Player command / NPC interaction triggers Java to construct the   │
│     page and call player.getPageManager().openCustomPage(...)          │
│                                                                        │
│  2. Framework calls your build(...)                                   │
│       - You call uiCommandBuilder.append("Pages/Foo.ui")               │
│       - You call uiEventBuilder.addEventBinding(...) for every event   │
│       - You set initial values with uiCommandBuilder.set(...)          │
│                                                                        │
│  3. Client renders. Page is interactive.                              │
│                                                                        │
│  4. Player clicks button / changes input → client fires event         │
│       - Client serializes EventData (resolving #Foo.Value references)  │
│       - Server decodes via your BuilderCodec<Data>                     │
│                                                                        │
│  5. Framework calls your handleDataEvent(ref, store, data)            │
│       - You inspect `data` and decide what to do                       │
│       - You MUST do ONE of:                                            │
│           a) switch to a new page (setPage / openCustomPage)           │
│           b) close the page (setPage(..., Page.None))                  │
│           c) call sendUpdate(...) to patch existing UI                 │
│           d) call rebuild() to fully re-run build()                    │
│         Otherwise the client will hang on "Loading..." forever.        │
│                                                                        │
│  6. (Optional) onDismiss() runs when the player closes the page.      │
└────────────────────────────────────────────────────────────────────────┘
```

### 13.1 `sendUpdate(...)` — partial patch

The cheap update: send a few new commands without re-running `build()`.

```java
UICommandBuilder patch = new UICommandBuilder();
patch.set("#BalanceLabel.Text", newBalance + " coins");
sendUpdate(patch);   // signature on InteractiveCustomUIPage: (UICommandBuilder, boolean clear)
                    // or (UICommandBuilder, UIEventBuilder, boolean clear)
```

Use `sendUpdate` after a value change when you only need to refresh a couple of fields. Cheap, no flicker.

### 13.2 `rebuild()` — full re-render

```java
this.rebuild();
```

Re-runs your entire `build()` from scratch. Use after a structural change (added/removed list items, switched tab, toggled visibility of a section).

### 13.3 Event handlers MUST acknowledge

This is the #1 gotcha. If your `handleDataEvent` doesn't call **at least one of** `sendUpdate`, `rebuild`, `setPage`, or `openCustomPage`, the player sits on a "Loading…" overlay until they ESC. The conventional pattern:

```java
@Override
public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, Data data) {
    super.handleDataEvent(ref, store, data);   // always call super first
    // ... your dispatch ...
    if (changed) {
        rebuild();           // OR sendUpdate(patchBuilder)
    } else {
        sendUpdate();        // bare ack
    }
}
```

When in doubt: rebuild at the end. It's correct; just not the most efficient.

---

## 14. Showing & Closing a Page

### 14.1 Opening a page

You need a `Player` component to access `PageManager`. From inside a command handler:

```java
Player player = store.getComponent(ref, Player.getComponentType());
if (player == null) return;
player.getPageManager().openCustomPage(ref, store, new ShopPage(playerRef, …));
```

### 14.2 Closing the active page

```java
player.getPageManager().setPage(ref, store, Page.None);
```

### 14.3 Replacing with another page

```java
player.getPageManager().openCustomPage(ref, store, new NextPage(playerRef));
```

### 14.4 From inside `handleDataEvent`

You already have `ref` and `store`, so:

```java
Player player = store.getComponent(ref, Player.getComponentType());
if (player != null) {
    player.getPageManager().setPage(ref, store, Page.None);   // close
}
```

---

## 15. Custom HUDs

HUDs are display-only persistent overlays. They share the `.ui` markup language with pages but use a different Java base class and are shown through `HudManager`.

### 15.1 Java side

```java
public class QuestHud extends CustomUIHud {

    public QuestHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,   // typically unused
                      @Nonnull Store<EntityStore> store) {
        cmd.append("Hud/QuestHud.ui");
        cmd.set("#QuestName.Text", "Slay the Hedera");
        cmd.set("#Progress.Value", 0.4);
    }
}
```

### 15.2 Show / hide

```java
player.getHudManager().setCustomHud(ref, store, new QuestHud(playerRef));
// later
player.getHudManager().setCustomHud(ref, store, null);  // remove
```

### 15.3 Hiding built-in HUD components

```java
player.getHudManager().hideHudComponents(ref, store, HudComponent.Hotbar, HudComponent.Crosshair);
```

### 15.4 Tip: HUDs don't capture mouse

Don't put interactive elements (buttons, inputs) in a HUD; the player can see them but can't interact. If you need interaction, it's a Page, not a HUD.

### 15.5 Showing multiple HUDs

Only one custom HUD can be set at a time per player via the core API. To stack multiple HUDs use the community **MultipleHUD** mod (https://www.curseforge.com/hytale/mods/multiplehud).

---

## 16. Recipe: Building Any Feature From Scratch

Follow this checklist every time. It produces a working page on the first try.

### Step 1 — Specify

Write down on paper:

1. **What the player sees.** Title, sections, fields, buttons.
2. **Every input.** ID + element type for each (e.g. `#NameInput: TextField`, `#PrivacyDropdown: DropdownBox`).
3. **Every button.** ID for each, what each one DOES on click.
4. **Initial values.** Anything that needs to be filled in from server state when the page opens.
5. **Updates.** Anything that should refresh while the page is open (live counters, etc.).

### Step 2 — Create the `.ui` file

Path: `src/main/resources/Common/UI/Custom/Pages/<FeatureName>.ui`

Skeleton:

```ui
$C = "../Common.ui";

@PrimaryButtonStyle = TextButtonStyle(
    Default: (Background: #3a7bd5, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Hovered: (Background: #4a8be5, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Pressed: (Background: #2a6bc5, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center))
);

@SecondaryButtonStyle = TextButtonStyle(
    Default: (Background: #2b3542, LabelStyle: (FontSize: 14, TextColor: #96a9be, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Hovered: (Background: #3b4552, LabelStyle: (FontSize: 14, TextColor: #b6c9de, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Pressed: (Background: #1b2532, LabelStyle: (FontSize: 14, TextColor: #96a9be, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center))
);

$C.@PageOverlay {
    $C.@DecoratedContainer {
        Anchor: (Width: 600, Height: 500);
        Padding: (Full: 20);

        #Title {
            Group {
                $C.@Title { @Text = "Feature Title"; }
            }
        }

        #Content {
            LayoutMode: Top;

            // ===== Your form goes here =====
            // Pattern repeated for each field:
            //   Label  → spacer  → input  → spacer  → divider  → spacer

            Label #FieldOneLabel {
                Style: (FontSize: 14, TextColor: #b4c8c9, RenderBold: true);
                Anchor: (Height: 10);
                Text: "Field One:";
            }
            Group { Anchor: (Height: 15); }
            $C.@TextField #FieldOneInput {
                Anchor: (Height: 40);
                PlaceholderText: "Enter value…";
            }
            Group { Anchor: (Height: 10); }
            Group { Anchor: (Height: 3); Background: #2b3542; }
            Group { Anchor: (Height: 10); }

            // ... more fields ...

            Group { FlexWeight: 1; }   // push buttons to bottom

            // ===== Button row =====
            Group #BottomButtons {
                LayoutMode: Left;
                Anchor: (Height: 40);

                Group { FlexWeight: 1; }   // right-align

                TextButton #SubmitButton {
                    Text: "SUBMIT";
                    Anchor: (Width: 130, Height: 36);
                    Style: @PrimaryButtonStyle;
                }
                Group { Anchor: (Width: 10); }
                TextButton #CancelButton {
                    Text: "CANCEL";
                    Anchor: (Width: 130, Height: 36);
                    Style: @SecondaryButtonStyle;
                }
            }
        }
    }

    $C.@BackButton {}
}
```

### Step 3 — Create the Java page class

Path: `src/main/java/com/yourname/yourplugin/ui/<FeatureName>Page.java`

```java
public class FeatureNamePage extends InteractiveCustomUIPage<FeatureNamePage.Data> {

    public static final String SUBMIT_BTN = "SubmitButton";
    public static final String CANCEL_BTN = "CancelButton";

    public FeatureNamePage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder ui,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        // 1) Load the .ui markup
        ui.append("Pages/FeatureName.ui");

        // 2) Fill initial values
        // ui.set("#FieldOneInput.Value", currentValue);

        // 3) Bind every interactive element to handleDataEvent
        events.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#SubmitButton",
            new EventData()
                .append("@FieldOne", "#FieldOneInput.Value")
                .append("ClickedButton", SUBMIT_BTN)
        );
        events.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#CancelButton",
            EventData.of("ClickedButton", CANCEL_BTN)
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull Data data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        if (CANCEL_BTN.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        if (SUBMIT_BTN.equals(data.clickedButton)) {
            // validate, do the work, give feedback
            if (data.fieldOne == null || data.fieldOne.isEmpty()) {
                player.sendMessage(Message.raw("Field One is required").color("#ff6666"));
                rebuild();   // re-render with cleared loading state
                return;
            }
            doTheWork(player, data);
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    private void doTheWork(Player player, Data data) {
        // ... business logic ...
    }

    public static class Data {
        public String fieldOne;
        public String clickedButton;

        public static final BuilderCodec<Data> CODEC =
            BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("@FieldOne", Codec.STRING),
                        (Data o, String v) -> o.fieldOne = v,
                        (Data o) -> o.fieldOne)
                .add()
                .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                        (Data o, String v) -> o.clickedButton = v,
                        (Data o) -> o.clickedButton)
                .add()
                .build();
    }
}
```

### Step 4 — Add a command (or other trigger) to open the page

```java
// Inside your command handler:
Player player = store.getComponent(ref, Player.getComponentType());
if (player != null) {
    player.getPageManager().openCustomPage(ref, store, new FeatureNamePage(playerRef));
}
```

### Step 5 — Verify with Diagnostic Mode

In game, **Settings → General → Diagnostic Mode → On**. Now any `.ui` load error gives a real diagnostic rather than a generic loading hang.

### Step 6 — Iterate

For every visual tweak: edit the `.ui` file, repackage your jar, replace it on the server, rejoin. For every logic change: only the Java needs recompiling.

---

## 17. Full Worked Examples

### 17.1 "Send Mail" Page

**`resources/Common/UI/Custom/Pages/SendMail.ui`**

```ui
$C = "../Common.ui";

@PrimaryButtonStyle = TextButtonStyle(
    Default: (Background: #3a7bd5, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Hovered: (Background: #4a8be5, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Pressed: (Background: #2a6bc5, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center))
);

@SecondaryBtnStyle = TextButtonStyle(
    Default: (Background: #2b3542, LabelStyle: (FontSize: 14, TextColor: #96a9be, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Hovered: (Background: #3b4552, LabelStyle: (FontSize: 14, TextColor: #b6c9de, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
    Pressed: (Background: #1b2532, LabelStyle: (FontSize: 14, TextColor: #96a9be, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center))
);

$C.@PageOverlay {
    $C.@Container {
        Anchor: (Width: 600, Height: 540);
        Padding: (Full: 20);

        #Title {
            Group { $C.@Title { @Text = "Send Mail"; } }
        }

        #Content {
            LayoutMode: Top;

            Group #Main {
                LayoutMode: Top;

                Label #ToLabel {
                    Style: (FontSize: 14, TextColor: #b4c8c9, RenderBold: true);
                    Anchor: (Height: 10);
                    Text: "To:";
                }
                Group { Anchor: (Height: 15); }
                $C.@TextField #ToInput {
                    Anchor: (Height: 40);
                    PlaceholderText: "Type receiver name here...";
                }
                Group { Anchor: (Height: 10); }
                Group { Anchor: (Height: 3); Background: #2b3542; }
                Group { Anchor: (Height: 10); }

                Label #SubjectLabel {
                    Style: (FontSize: 14, TextColor: #b4c8c9, RenderBold: true);
                    Anchor: (Height: 10);
                    Text: "Subject:";
                }
                Group { Anchor: (Height: 15); }
                $C.@TextField #SubjectInput {
                    Anchor: (Height: 40);
                    PlaceholderText: "Type mail subject here...";
                }
                Group { Anchor: (Height: 10); }
                Group { Anchor: (Height: 3); Background: #2b3542; }
                Group { Anchor: (Height: 10); }

                Label #MessageLabel {
                    Style: (FontSize: 14, TextColor: #b4c8c9, RenderBold: true);
                    Anchor: (Height: 10);
                    Text: "Message:";
                }
                Group { Anchor: (Height: 15); }
                MultilineTextField #MessageInput {
                    Anchor: (Height: 180);
                    PlaceholderText: "Write your message here...";
                    PlaceholderStyle: (TextColor: #6e7da1);
                    MaxLines: 32;
                    OutlineColor: #424a5e;
                    OutlineSize: 2;
                    Background: #263047;
                    Padding: 10;
                    ScrollbarStyle: (Spacing: 6, Size: 6);
                }
                Group { Anchor: (Height: 10); }

                Group #BottomButtons {
                    LayoutMode: Left;
                    Anchor: (Height: 40);

                    TextButton #GiftButton {
                        Text: "GIFT";
                        Anchor: (Width: 90, Height: 36);
                        Style: @PrimaryButtonStyle;
                    }
                    Group { FlexWeight: 1; }
                    TextButton #SendButton {
                        Text: "SEND";
                        Anchor: (Width: 130, Height: 36);
                        Style: @PrimaryButtonStyle;
                    }
                    Group { Anchor: (Width: 10); }
                    TextButton #CancelButton {
                        Text: "CANCEL";
                        Anchor: (Width: 130, Height: 36);
                        Style: @SecondaryBtnStyle;
                    }
                }
            }
        }
    }
}
```

**`SendMailPage.java`**

```java
package com.yourname.yourplugin.ui.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class SendMailPage extends InteractiveCustomUIPage<SendMailPage.SendMailData> {

    public static final String SEND_BTN   = "SendButton";
    public static final String CANCEL_BTN = "CancelButton";
    public static final String GIFT_BTN   = "GiftButton";

    private final MailManager mailManager;   // your plugin's mail manager

    public SendMailPage(@Nonnull PlayerRef playerRef, @Nonnull MailManager mailManager) {
        super(playerRef, CustomPageLifetime.CanDismiss, SendMailData.CODEC);
        this.mailManager = mailManager;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder ui,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        ui.append("Pages/SendMail.ui");

        events.addEventBinding(
            CustomUIEventBindingType.Activating, "#SendButton",
            new EventData()
                .append("@ReceiverName", "#ToInput.Value")
                .append("@MailSubject",  "#SubjectInput.Value")
                .append("@MailBody",     "#MessageInput.Value")
                .append("ClickedButton", SEND_BTN)
        );
        events.addEventBinding(
            CustomUIEventBindingType.Activating, "#CancelButton",
            EventData.of("ClickedButton", CANCEL_BTN)
        );
        events.addEventBinding(
            CustomUIEventBindingType.Activating, "#GiftButton",
            EventData.of("ClickedButton", GIFT_BTN)
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull SendMailData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        if (CANCEL_BTN.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }
        if (GIFT_BTN.equals(data.clickedButton)) {
            // open gift sub-page or simply ack
            sendUpdate();
            return;
        }
        if (SEND_BTN.equals(data.clickedButton)) {
            if (!isValid(player, data)) {
                rebuild();   // re-show form
                return;
            }
            mailManager.send(player.getDisplayName(),
                             data.receiverName,
                             data.mailSubject,
                             data.mailBody);
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    private boolean isValid(Player player, SendMailData data) {
        if (data.receiverName == null || data.receiverName.isEmpty() ||
            data.mailSubject  == null || data.mailSubject.isEmpty()  ||
            data.mailBody     == null || data.mailBody.isEmpty()) {
            player.sendMessage(/* "Please fill all fields" */);
            return false;
        }
        return true;
    }

    public static class SendMailData {
        public String receiverName;
        public String mailSubject;
        public String mailBody;
        public List<String> mailGifts;
        public String clickedButton;

        public static final BuilderCodec<SendMailData> CODEC =
            BuilderCodec.builder(SendMailData.class, SendMailData::new)
                .append(new KeyedCodec<>("@ReceiverName", Codec.STRING),
                        (SendMailData o, String v) -> o.receiverName = v,
                        (SendMailData o) -> o.receiverName).add()
                .append(new KeyedCodec<>("@MailSubject", Codec.STRING),
                        (SendMailData o, String v) -> o.mailSubject = v,
                        (SendMailData o) -> o.mailSubject).add()
                .append(new KeyedCodec<>("@MailBody", Codec.STRING),
                        (SendMailData o, String v) -> o.mailBody = v,
                        (SendMailData o) -> o.mailBody).add()
                .append(new KeyedCodec<>("@MailGifts", Codec.STRING_ARRAY),
                        (SendMailData o, String[] v) -> o.mailGifts = Arrays.asList(v),
                        (SendMailData o) -> o.mailGifts == null
                            ? new String[0] : o.mailGifts.toArray(new String[0])).add()
                .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                        (SendMailData o, String v) -> o.clickedButton = v,
                        (SendMailData o) -> o.clickedButton).add()
                .build();
    }
}
```

### 17.2 Dynamic List with Row Template

```ui
// resources/Common/UI/Custom/Pages/ItemList.ui
$C = "../Common.ui";

$C.@PageOverlay {
    $C.@DecoratedContainer {
        Anchor: (Width: 700, Height: 600);
        #Title { Group { $C.@Title { @Text = "Items"; } } }
        #Content {
            LayoutMode: Top;
            Group #ItemList {
                LayoutMode: TopScrolling;
                ScrollbarStyle: $C.@DefaultScrollbar;
                FlexWeight: 1;
            }
        }
    }
}
```

```ui
// resources/Common/UI/Custom/Pages/ItemListRow.ui
Group {
    Anchor: (Height: 50);
    LayoutMode: Left;
    Background: #1a2030(0.6);
    Padding: (Horizontal: 12, Vertical: 8);

    Label #Name {
        Style: (FontSize: 14, TextColor: #ffffff);
        FlexWeight: 1;
        Text: "(unset)";
    }
    Label #Price {
        Style: (FontSize: 14, TextColor: #f4d35e);
        Anchor: (Width: 100);
        Text: "0";
    }
    TextButton #BuyButton {
        Anchor: (Width: 80, Height: 32);
        Text: "BUY";
    }
}
```

```java
// In ItemListPage#build:
ui.append("Pages/ItemList.ui");
ui.clear("#ItemList");
for (int i = 0; i < items.size(); i++) {
    ui.append("#ItemList", "Pages/ItemListRow.ui");
    String base = "#ItemList[" + i + "]";
    ui.set(base + " #Name.Text", items.get(i).name);
    ui.set(base + " #Price.Text", items.get(i).price + " gold");
    events.addEventBinding(
        CustomUIEventBindingType.Activating,
        base + " #BuyButton",
        EventData.of("Action", "buy:" + items.get(i).id),
        false
    );
}
```

### 17.3 Live HUD that updates

```java
public class XPHud extends CustomUIHud {
    public XPHud(PlayerRef playerRef) { super(playerRef); }

    @Override
    public void build(Ref<EntityStore> ref, UICommandBuilder ui,
                      UIEventBuilder events, Store<EntityStore> store) {
        ui.append("Hud/XPHud.ui");
        refresh(ref, store);
    }

    /** Call this from your XP-change listener. */
    public void refresh(Ref<EntityStore> ref, Store<EntityStore> store) {
        PlayerData data = getPlayerData();
        UICommandBuilder patch = new UICommandBuilder();
        patch.set("#XpBar.Value", data.xpProgress());
        patch.set("#LevelLabel.Text", "Lv " + data.level());
        sendUpdate(patch, false);
    }
}
```

---

## 18. Common Pitfalls

| Symptom | Cause | Fix |
|---------|-------|-----|
| Client stuck on "Loading…" indefinitely | `handleDataEvent` didn't call `sendUpdate` / `rebuild` / `setPage` | Always acknowledge — at minimum call `sendUpdate()` |
| `.ui file not found` error | Path doesn't match `resources/Common/UI/Custom/...` | Double-check folder + filename casing |
| Asset pack missing | `manifest.json` missing `"IncludesAssetPack": true` | Add the flag and rebuild |
| Empty page renders / page is blank | Invalid `.ui` markup (server doesn't validate) | Enable Diagnostic Mode in client; check syntax: semicolons, matching braces, quoted strings |
| Event payload field is always null | Lowercase first letter in payload key, or codec key mismatch | Use uppercase first letter (`"Action"` not `"action"`), and match keys exactly between `EventData.append("X", …)` and `KeyedCodec<>("X", …)` |
| List items render but events don't fire | Forgot to bind events for each indexed selector | Bind inside the loop, using `"#List[" + i + "] #Button"` selectors |
| Image not appearing | Path in `.ui` is wrong (paths are relative to the `.ui` file, not to the project root) | Use `"../Images/foo.png"` or co-locate images |
| UI freezes while typing in a field | `ValueChanged` binding has default `locksInterface = true` | Pass `false` as the 4th arg to `addEventBinding` |
| Profile / nav state not reflecting after action | Forgot to `rebuild()` after mutating state | Add `this.rebuild();` at the end of the handler branch |
| `Style: (… )` doesn't compile / look right | Object syntax uses **commas** between fields, not semicolons | `Style: (FontSize: 14, TextColor: #fff)` ✅ |
| Spacing between stacked items not working | Used `Anchor: (Bottom: 10)` in a child of LayoutMode `Top` — works for between-element spacing, but spacer groups are clearer | Insert `Group { Anchor: (Height: 10); }` between elements |
| Common.ui file "missing" | You don't create `Common.ui` — it ships with the game | Just write `$C = "../Common.ui";` and trust the import |

### Diagnostic Mode

In Hytale: **Settings → General → Diagnostic Mode** = On. This turns generic "loading" failures into real error overlays that point at the offending file/line.

---

## 19. Cheat Sheet

### File locations

```
resources/manifest.json                                  # "IncludesAssetPack": true
resources/Common/UI/Custom/Pages/<X>.ui                  # pages
resources/Common/UI/Custom/Hud/<X>.ui                    # huds
resources/Common/UI/Custom/Pages/<X>/<row>.ui            # sub-templates / row partials
```

### Java boilerplate (1-button confirm dialog)

```java
public class XPage extends InteractiveCustomUIPage<XPage.Data> {
    public XPage(PlayerRef p) { super(p, CustomPageLifetime.CanDismiss, Data.CODEC); }

    @Override public void build(Ref<EntityStore> r, UICommandBuilder ui,
                                UIEventBuilder ev, Store<EntityStore> s) {
        ui.append("Pages/X.ui");
        ev.addEventBinding(CustomUIEventBindingType.Activating, "#OkButton",
            EventData.of("Action", "ok"));
    }

    @Override public void handleDataEvent(Ref<EntityStore> r,
                                          Store<EntityStore> s, Data d) {
        super.handleDataEvent(r, s, d);
        Player p = s.getComponent(r, Player.getComponentType());
        if (p != null) p.getPageManager().setPage(r, s, Page.None);
    }

    public static class Data {
        public String action;
        public static final BuilderCodec<Data> CODEC =
            BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Action", Codec.STRING),
                        (Data o, String v) -> o.action = v,
                        (Data o) -> o.action).add()
                .build();
    }
}
```

### `.ui` boilerplate (1-button confirm dialog)

```ui
$C = "../Common.ui";

$C.@PageOverlay {
    $C.@DecoratedContainer {
        Anchor: (Width: 400, Height: 200);
        Padding: (Full: 20);
        #Title  { Group { $C.@Title { @Text = "Confirm"; } } }
        #Content {
            LayoutMode: Top;
            Label { Anchor: (Height: 30); Text: "Are you sure?"; Style: (FontSize: 14, TextColor: #fff); }
            Group { FlexWeight: 1; }
            $C.@TextButton #OkButton {
                Anchor: (Height: 36);
                Text: "OK";
            }
        }
    }
    $C.@BackButton {}
}
```

### Selector quick reference

```
#Id                  element with that id
#A #B                B descendant of A
#List[0]             1st child of #List
#List[i] #Foo        #Foo inside i-th child
#Label.Text          property `Text` of #Label
#Btn.Style.Default.LabelStyle.TextColor   deep property path
```

### Event types most used

```
Activating        click
ValueChanged      input changed (use locksInterface=false)
RightClicking     right-click
MouseEntered      hover begin
SlotClicking      ItemSlot click
SelectedTabChanged   tab switched
Dismissing        ESC pressed
```

### Color & font quick

```
#rrggbb            opaque
#rrggbb(0.5)       50% alpha (preferred)
FontName: "Default" | "Secondary" | "Mono"
```

### After ANY input event

Pick exactly one:
- `sendUpdate(patch)` — fast partial refresh
- `rebuild()` — full re-render
- `player.getPageManager().setPage(ref, store, Page.None)` — close
- `player.getPageManager().openCustomPage(ref, store, new OtherPage(...))` — switch

Otherwise the client hangs forever.

---

## 20. Source links

- Official UI docs: https://hytalemodding.dev/en/docs/official-documentation/custom-ui
- Official plugin UI guide: https://hytalemodding.dev/en/docs/guides/plugin/ui
- Element reference index: https://hytalemodding.dev/en/docs/official-documentation/custom-ui/type-documentation
- Visual UI editor (community): https://hytale.ellie.au/
- HyUI (Java-builder lib, community): https://www.curseforge.com/hytale/mods/hyui
- MultipleHUD (community): https://www.curseforge.com/hytale/mods/multiplehud
- `/ui-gallery` in-game command — browse every Common.ui style live

---

*End of guide. If something here ever conflicts with the official docs, the official docs win — they are the source of truth and may evolve.*
