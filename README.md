# Vis in Hand

Мод-міст між **Thaumaturge** і **Vivecraft** для Minecraft 1.21.1 (NeoForge). Мета — зробити Thaumaturge
зручним у VR.

## Що вже працює (0.1.0)

У VR інструменти Thaumaturge цілять **контролером**, а не шоломом:

| Що | Де в Thaumaturge |
| --- | --- |
| Заклинання з жезла: звідки летять і куди | `SpellTarget.wandOrigin` |
| Ефекти на кінчику жезла | `SpellTarget.visualWandOrigin` |
| Таумометр: сканування й підказка аспектів | `ScanRaycastHelper.performRaycast`, `ScanningManager.blockStack` |
| Висмоктування вузла, очищення кристала | `ItemWand.targetedNode`, `ItemWand.refineTarget` |
| Фокус «Архітектор», Елементальна лопата | `traceAnchor` |
| Налаштування сторони есенцієвої труби | `BlockEssentiaTransport.traceLook` |

Гравці без VR нічого не помітять: для них лишається стара поведінка.

Перевірено (21.11.2026) з Thaumaturge **1.1.0** (CurseForge), Vivecraft 1.3.15, NeoForge 21.1.267: гра
стартує, усі хуки вбудовуються. У 1.1.0 жезл кастує через `SpellTarget.origin`, у новішій версії з GitHub —
через `wandOrigin`; мод підтримує обидва варіанти. Сам VR-геймплей у шоломі ще не тестувався.

Рука визначається так: та, у якій предмет Thaumaturge (спершу основна); для жезла — рука, якою кастують.

## Панель фокусів (0.2.0)

У VR кнопка «Change focus» (Thaumaturge) відкриває **панель фокусів** замість кругового меню, яке
керується мишею. Панель — звичайний екран Minecraft, тож Vivecraft показує її як плаский екран:
цілишся променем контролера й тиснеш курок.

- по колу — усі фокуси з інвентаря, сумок для фокусів (і сумок у слотах Curios); поточний у золотій рамці;
- у центрі — «Зняти фокус» (раніше Shift+F);
- «Розмір області» / «Вісь області» — керування фокусом «Архітектор» (раніше G / Ctrl+G);
- при виборі — коротка вібрація контролера.

Без VR усе як було: кругове меню Thaumaturge працює мишею.

### Як прив'язати кнопки

Клавіші Thaumaturge Vivecraft сам додає в набір дій **Mod**:

- **SteamVR**: Settings → Controllers → Manage Controller Bindings → Minecraft (Vivecraft) → Custom →
  вкладка «Mod» → призначити «Change focus», «Open Thaumonomicon» тощо на кнопки;
- або без SteamVR-налаштувань: у грі Vivecraft → Radial Menu → додати ці дії в кругове меню Vivecraft.

## Збірка

```
./gradlew build
```

Для компіляції потрібен jar Thaumaturge у `libs/` (локально, не комітиться і не входить у мод).

`./gradlew runSelftest` — автотест: відкриває панель фокусів, зберігає `run/screenshots/visinhand_selftest.png`
і сам закриває гру (максимум через 2 хв).

Jar: `build/libs/visinhand-0.2.0.jar`. Потрібен і на клієнті, і на сервері, разом із Thaumaturge та Vivecraft.

## Тест у грі

У `run/mods` мають лежати Thaumaturge, Curios (потребує NeoForge ≥ 21.1.265), Lithostitched і Vivecraft,
потім `./gradlew runClient`. Клієнт вивантажує змінені класи в `run/.mixin.out` — там видно, куди стали хуки
(`javap -c` і шукати `visinhand$`).

## Як влаштовано

Код Thaumaturge **не копіюється** і не потрібен для компіляції: міксини (`@Pseudo`) чіпляються до його
класів за назвою, а позу контролера беремо з публічного API Vivecraft (`org.vivecraft.api.VRAPI`).

## Далі

- HUD жезла й аури на зап'ясті замість екрана
- Вібрація при касті та висмоктуванні вису
- Жести (змах жезлом)
