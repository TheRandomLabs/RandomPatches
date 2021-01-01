# RandomPatches (Fabric)

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
![Build](https://github.com/TheRandomLabs/RandomPatches/workflows/Build/badge.svg?branch=1.16-fabric)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/TheRandomLabs/RandomPatches.svg)](http://isitmaintained.com/project/TheRandomLabs/RandomPatches "Average time to resolve an issue")

[![Downloads](http://cf.way2muchnoise.eu/full_randompatches-fabric_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/randompatches-fabric)
[![Files](https://curse.nikky.moe/api/img/396245/files?logo)](https://www.curseforge.com/minecraft/mc-mods/randompatches-fabric/files)
[![Download](https://curse.nikky.moe/api/img/396245?logo)](https://curse.nikky.moe/api/url/396245)

A bunch of miscellaneous patches for Minecraft. RandomPatches does **not** require Fabric API!

Also check out [RandomTweaks](https://www.curseforge.com/minecraft/mc-mods/randomtweaks)!

**When reporting issues or suggesting enhancements, please use the**
**[GitHub issue tracker](https://github.com/TheRandomLabs/RandomPatches/issues), and make sure**
**you are on the latest version of the mod for your Minecraft version—it's easier to keep track**
**of things this way. Avoid commenting them on the CurseForge project page or sending them to me**
**in a direct message. Thank you!**

## Sponsor

I've partnered with Apex Hosting! In my experience, their servers are lag-free, easy to manage,
and of high quality. Check them out here:

<a href="https://billing.apexminecrafthosting.com/aff.php?aff=3907">
	<img src="https://cdn.apexminecrafthosting.com/img/theme/apex-hosting-mobile.png" width="594" height="100" border="0">
</a>

## Aims

RandomPatches aims to be a highly configurable collection of bug fixes and quality of life
improvements for Minecraft, and additionally to allow several hardcoded settings to be configured.

When installed on a client, RandomPatches should be completely compatible with servers without the
mod, and when installed on a server, it should be completely compatible with clients without the
mod. As a result, clients can connect to a server with a different version of the mod to the one on
the server. Furthermore, RandomPatches contains no features that require the mod to be installed
both on the client and the server.

By default, RandomPatches aims to be as non-invasive as possible—there are no breaking changes to
game mechanics or conspicuous GUI additions. Indeed, with the default settings, the mod should be
virtually unnoticeable when one is not specifically looking for it. In addition, RandomPatches
should automatically disable any of its features that are implemented by another mod in order to
preserve compatibility wherever possible.

### How is RandomPatches different from RandomTweaks?

RandomPatches aims to not add or significantly modify any game mechanics. For instance,
auto-third person is a new minor game mechanic, so it belongs in RandomTweaks.
The distinction is blurrier for certain features—in these instances, features that require mixins
in the Forge version, such as removing the glowing effect from potions, and bug fixes, such as
the cauldron translucency fix, are put in RandomPatches.

## Features

Features without a specified side are server-sided.

### Connection timeouts

In vanilla Minecraft, the connection timeouts are hardcoded, and often not long enough for
slower computers or heavier modded instances. To counter this, RandomPatches allows several
connection timeouts to be configured:

* The connection read timeout
  * Both client and server-sided
  * Raised to 120 seconds from the vanilla value of 30 seconds by default
* The login timeout
  * How long the server waits for a player to log in
  * Raised to 2400 ticks (120 seconds) from the vanilla value of 600 ticks (30 seconds) by default
* The KeepAlive timeout
  * How long the server waits for a player to return a KeepAlive packet before disconnecting them
  * Raised to 120 seconds from the vanilla value of 30 seconds by default

In addition, RandomPatches allows the interval at which KeepAlive packets are sent to clients
to be configured, although it is recommended that this be left at the vanilla value of 15 seconds.
</details>

### Packet size limits

RandomPatches allows several packet size limits to be configured, which by default are raised from
the vanilla limits:

* Maximum compressed packet size
  * This option is both client and server-sided.
  * Setting this to a higher value than the vanilla limit can fix
  [MC-185901](https://bugs.mojang.com/browse/MC-185901), which may cause players to be disconnected.
* Maximum NBT compound tag packet size
  * This option is both client and server-sided.
  * Setting this to a higher value than the vanilla limit may prevent players from being
  disconnected.
* Maximum client custom payload packet size
  * Setting this to a higher value than the vanilla limit may prevent the client from being
  disconnected.

### Player speed limits

In vanilla Minecraft, the player speed limits are hardcoded, and set to values that are often
not high enough in certain cases. As a result, rubber banding occurs, and
`[Player] moved too quickly!` is spammed in the log. To prevent this, RandomPatches changes the
following player speed limits to a higher value by default:

* Default maximum player speed
* Maximum player elytra speed
* Maximum player vehicle speed

This fixes [MC-90062](https://bugs.mojang.com/browse/MC-90062).

### Boat options

The following options related to boats can be modified:

* Boat buoyancy under flowing water
  * In vanilla Minecraft, this is set to a negative value, causing it to be impossible for boats to
  flow up when they move up into a higher block of water.
  * This problem is reported as [MC-91206](https://bugs.mojang.com/browse/MC-91206), and has been
  marked as "Works As Intended".
  * By default, RandomPatches sets this to a positive value to counteract this.
* Underwater boat passenger delay
  * This is how long it takes for a boat passenger to be ejected when underwater.
  * This can be set to -1 to disable underwater boat passenger ejection.

### Disable DataFixerUpper

This disables the execution of DataFixerUpper, reducing RAM usage and decreasing the Minecraft
loading time. However, this feature is disabled by default, and enabling it is **not recommended**,
as DataFixerUpper is responsible for the backwards compatibility of worlds. Even so, if you insist
on disabling DataFixerUpper:

* Ensure you have used the Optimize feature on any worlds from previous versions of Minecraft before
enabling this feature.
* Before migrating worlds to new versions of Minecraft, ensure this feature is disabled, and use the
Optimize feature again before re-enabling it.
* Take regular backups of your worlds.

Although worlds last played on an older or newer version of Minecraft theoretically cannot be loaded
when DataFixerUpper is disabled by RandomPatches, it's better to be safe than sorry.

To be clear, **RandomPatches is not responsible for any damage caused by this feature.**

### Fix tick scheduler desync

In vanilla Minecraft, the tick scheduler occasionally becomes desynchronized, and as a result,
Minecraft crashes, throwing an `IllegalStateException` with the message
`TickNextTick list out of synch`. RandomPatches attempts to fix this issue using the solution
described by malte0811 [here](https://github.com/SleepyTrousers/EnderCore/issues/105).

This bug is reported as [MC-28660](https://bugs.mojang.com/browse/MC-28660).

### Fix [MC-2025](https://bugs.mojang.com/browse/MC-2025)

Because of floating point precision errors, the bounding box of an entity can be calculated as
smaller than the expected value. When the entity is saved then reloaded, the bounding box may be
recomputed such that it intersects a wall. To counter this, RandomPatches stores the bounding box
when an entity is saved, then makes it use the same bounding box when it is loaded.

For more information, see [this Reddit post](https://redd.it/8pgd4q) from which this fix comes from.

### Fix animal breeding hearts

RandomPatches fixes animals which can breed only showing hearts once instead of continuously.
Thanks to [Fuzs_](https://www.curseforge.com/minecraft/mc-mods/breeding-hearts) for finding this
fix!

This bug is reported as [MC-93826](https://bugs.mojang.com/browse/MC-93826).

### Fix duplicate entity UUIDs

RandomPatches fixes duplicate entity UUIDs and the resulting log spam by assigning new UUIDs to the
affected entities. This fix was found by
[CAS_ual_TY](https://www.curseforge.com/minecraft/mc-mods/deuf-duplicate-entity-uuid-fix).

This bug is reported as [MC-95649](https://bugs.mojang.com/browse/MC-95649).

### Fix recipe book not moving ingredients with tags

In vanilla Minecraft, the recipe book does not automatically transfer ingredients with NBT tags to
the crafting grid. RandomPatches fixes this issue.

This bug is reported as [MC-129057](https://bugs.mojang.com/browse/MC-129057).

This feature is disabled if
[Nbt Crafting](https://www.curseforge.com/minecraft/mc-mods/nbt-crafting) is installed.

### Fix entities not being considered wet in cauldrons

In vanilla Minecraft, entities are not considered wet in cauldrons filled with water. RandomPatches
fixes this issue, allowing players to use Riptide in cauldrons filled with water, fixing
[MC-145311](https://bugs.mojang.com/browse/MC-145311). In addition, this fix allows players to
receive the Conduit Power effect in cauldrons filled with water.

### Fix player head stacking

In vanilla Minecraft, player heads from the same player sometimes do not stack. RandomPatches
fixes this issue by forcing Minecraft to treat two player heads as equal if they are from the same
player, and by default, if they have the same texture URL.

This bug is reported as [MC-100044](https://bugs.mojang.com/browse/MC-100044).

### Fix water in cauldrons rendering as opaque (client-sided)

In vanilla Minecraft, water in cauldrons renders as opaque. RandomPatches fixes this issue by
making them render as translucent as intended.

Without RandomPatches:

![Water in cauldron without RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Water%20in%20cauldron%20without%20RandomPatches.png)

With RandomPatches:

![Water in cauldron with RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Water%20in%20cauldron%20with%20RandomPatches.png)

This bug is reported as [MC-13187](https://bugs.mojang.com/browse/MC-13187).

### Fix end portals only rendering from above (client-sided)

In vanilla Minecraft, end portals only render from above. RandomPatches fixes this issue.

Without RandomPatches:

![End portal rendering without RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/End%20portal%20rendering%20without%20RandomPatches.png)

With RandomPatches:

![End portal rendering with RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/End%20portal%20rendering%20with%20RandomPatches.png)

This bug is reported as [MC-3366](https://bugs.mojang.com/browse/MC-3366).

### Fix villager robe textures (client-sided)

In vanilla Minecraft, only 18 out of 20 rows of pixels show of villager robe textures. This issue
also affects witches. RandomPatches fixes this issue.

Without RandomPatches:

![Villager robe textures without RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Villager%20robe%20textures%20without%20RandomPatches.png)

With RandomPatches:

![Villager robe textures with RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Villager%20robe%20textures%20with%20RandomPatches.png)

This bug is reported as [MC-53312](https://bugs.mojang.com/browse/MC-53312).

### Fix invisible player model (client-sided)

In certain instances in vanilla Minecraft, the player model sometimes disappears. This is most
noticeable when flying with elytra in a straight line in third-person mode:

[![Invisible player model bug](http://img.youtube.com/vi/YdbxknpfJHQ/0.jpg)](http://www.youtube.com/watch?v=YdbxknpfJHQ "Invisible Player Model Bug")

RandomPatches fixes this issue. Again, thanks to [Fuzs_](https://www.curseforge.com/members/fuzs_)
for finding this fix!

### Key bindings (client-sided)

RandomPatches makes the narrator toggle key binding configurable in the controls screen,
fixing [MC-122645](https://bugs.mojang.com/browse/MC-122645).
If [Amecs](https://www.curseforge.com/minecraft/mc-mods/amecs) is installed, this key binding is set
to `Control + b` by default, which is the vanilla default. Otherwise, it is unbound by default.

In addition, RandomPatches makes the following key bindings configurable, largely fixing
[MC-147718](https://bugs.mojang.com/browse/MC-147718):

* Pause
  * This is only for pausing and unpausing the game; the Escape key is still used to close GUI
  screens.
* Toggle GUI
* Toggle Debug Info
* The F3 key is still used for F3 actions.

Furthermore, RandomPatches adds a second configurable key binding for sprinting, which allows the
double-tap sprint functionality to be disabled, fixing
[MC-203401](https://bugs.mojang.com/browse/MC-203401). Moreover, RandomPatches allows double-tap
sprinting while flying, fixing [MC-68453](https://bugs.mojang.com/browse/MC-68453).

Additionally, RandomPatches adds a dismount key binding, which allows the dismount key to be
different from the sneak key.

### Window title and icon (client-sided)

By default, RandomPatches removes the annoying `*` in the Minecraft window title that indicates
that the game is modded. In addition, the following window properties can be configured:

* Title
  * Several variables are provided:
    * `${mcversion}`: Minecraft version
    * `${activity}`: Current activity (not available in the normal title)
    * `${username}`: Username
    * `${modsloaded}`: Number of mods loaded
    * `${modversion:modid}`: Version of the mod with the specified ID
  * '$' can be escaped by using an extra '$'.
* 16x16 icon
* 32x32 icon
* 256x256 icon (only takes effect on Mac OS X)

### Optimize bamboo rendering (client-sided)

RandomPatches optimizes bamboo rendering. This works by overriding the method that returns the
ambient occlusion light value for the bamboo block, which runs some expensive logic, but always
returns `1.0F`.

Thanks to [darkevilmac](https://minecraft.curseforge.com/projects/fast-bamboo) for finding this fix!

### Remove glowing effect from potions (client-sided)

By default, RandomPatches removes the glowing effect from potions, making potion colors more
visible.

Without RandomPatches:

![Potions without RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Potions%20without%20RandomPatches.png)

With RandomPatches:

![Potions with RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Potions%20with%20RandomPatches.png)

### Remove glowing effect from enchanted books (client-sided)

RandomPatches can remove the glowing effect from enchanted books. This feature is disabled by
default.

Without RandomPatches:

![Enchanted books without RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Enchanted%20books%20without%20RandomPatches.png)

With RandomPatches:

![Enchanted books with RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Enchanted%20books%20with%20RandomPatches.png)

### Disable experimental settings warning (client-sided)

By default, RandomPatches disables the warning that displays when loading a world that uses
experimental settings:

![Experimental settings warning](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/Experimental%20settings%20warning.png)

### Framerate limit slider step size (client-sided)

In vanilla Minecraft, the framerate limit slider step size is 10.0. RandomPatches changes this to
1.0 by default.

### Return to main menu after disconnect (client-sided)

This feature makes Minecraft return to the main menu screen after disconnecting rather than the
Realms or multiplayer screen.

## Configuration

The RandomPatches configuration can be found at `config/randompatches.toml`.

* All properties and categories should be well-commented such that there is little need for further
explanation.
* All configuration values should be automatically validated and reset if they are invalid.
* A configuration GUI can be accessed from
[Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu).
* In case of unaddressed compatibility issues, individual mixins can be disabled through the mixin
blacklist.
  * Most mixins are not automatically disabled when the features that depend on them are.
  * This is done to allow features to be enabled or disabled in-game without the need for restarts.
  * A list of these mixins can be found in the comments for the mixin blacklist.
  * **Please report an issue if you need to use the mixin blacklist to resolve a conflict.**
* The configuration can be reloaded from disk in-game through the use of a command
(`/rpconfigreload` by default).
