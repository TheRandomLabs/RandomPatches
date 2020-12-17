# RandomPatches

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Build](https://jitci.com/gh/TheRandomLabs/RandomPatches/svg?branch=1.16-forge)](https://jitci.com/gh/TheRandomLabs/RandomPatches)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/TheRandomLabs/RandomPatches.svg)](http://isitmaintained.com/project/TheRandomLabs/RandomPatches "Average time to resolve an issue")

[![Downloads](http://cf.way2muchnoise.eu/full_randompatches_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/randompatches)
[![Files](https://curse.nikky.moe/api/img/285612/files?logo)](https://www.curseforge.com/minecraft/mc-mods/randompatches/files)
[![Download](https://curse.nikky.moe/api/img/285612?logo)](https://curse.nikky.moe/api/url/285612)

A bunch of miscellaneous patches for Minecraft.

On Forge, RandomPatches only contains features that require Mixins. Tweaks that *don't* require
Mixins go in [RandomTweaks](https://www.curseforge.com/minecraft/mc-mods/randomtweaks).

**When reporting issues or suggesting enhancements, please use the**
**[GitHub issue tracker](https://github.com/TheRandomLabs/RandomPatches/issues)—it's easier to**
**keep track of things this way. Avoid commenting them on the CurseForge project page or sending**
**them to me in a direct message. Thank you!**

## Sponsors

I've partnered with BisectHosting! In my experience, their servers are lag-free, easy to manage and
of high quality. Check them out here:

<a href="https://bisecthosting.com/TheRandomLabs">
	<img src="https://www.bisecthosting.com/images/logos/dark_text@1538x500.png" width="385" height="125" border="0">
</a>

Use the code "**TheRandomLabs**" to get 25% off your first month!

I've also partnered with Apex Hosting, and in my experience, their servers are *also* lag-free,
easy to manage, and of high quality. Check them out here:

<a href="https://billing.apexminecrafthosting.com/aff.php?aff=3907">
	<img src="https://cdn.apexminecrafthosting.com/img/theme/apex-hosting-mobile.png" width="594" height="100" border="0">
</a>

## Aims

RandomPatches aims to be a highly configurable collection of minor vanilla-compatible tweaks and
bug fixes for Minecraft.

When installed on a client, RandomPatches should be completely compatible with servers without the
mod, and when installed on a server, it should be completely compatible with clients without the
mod. As a result, clients can connect to a server with a different version of the mod to the one on
the server.

By default, RandomPatches aims to be as non-invasive as possible—there are no breaking changes to
game mechanics or conspicuous GUI additions. Indeed, with the default settings, the mod should be
virtually unnoticeable when one is not specifically looking for it. In addition, RandomPatches
should automatically disable any of its features that are implemented by another mod in order to
preserve compatibility wherever possible.

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

### Fix recipe book not moving ingredients with tags

In vanilla Minecraft, the recipe book does not automatically transfer ingredients with NBT tags to
the crafting grid. RandomPatches fixes this issue.

This bug is reported as [MC-129057](https://bugs.mojang.com/browse/MC-129057).

### Fix player head stacking

In vanilla Minecraft, player heads from the same player sometimes do not stack. RandomPatches
fixes this issue by forcing Minecraft to treat two player heads as equal if they are from the same
player, and by default, if they have the same texture URL.

This bug is reported as [MC-100044](https://bugs.mojang.com/browse/MC-100044).

### Fix end portal rendering

In vanilla Minecraft, end portals only render from above and not below. RandomPatches fixes this
issue.

Without RandomPatches:

![End portal rendering without RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/End%20Portal%20Rendering%20without%20RandomPatches.png)

With RandomPatches:

![End portal rendering with RandomPatches](https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/End%20Portal%20Rendering%20with%20RandomPatches.png)

This bug is reported as [MC-3366](https://bugs.mojang.com/browse/MC-3366).

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

To make this clear, **RandomPatches is not responsible for any damage caused by this feature.**

This feature does nothing if
[DataFixerSlayer](https://www.curseforge.com/minecraft/mc-mods/datafixerslayer) is installed.

### Window title and icon

By default, RandomPatches removes the annoying `*` in the Minecraft window title that indicates
that the game is modded. In addition, the following window properties can be configured:

* Title (the Minecraft version and current activity can be included)
* 16x16 icon
* 32x32 icon
* 256x256 icon (only takes effect on Mac OS X)

### Bamboo rendering optimization

RandomPatches optimizes bamboo rendering. This works by overriding the method that returns the
ambient occlusion light value for the bamboo block, which runs some expensive logic, but always
returns `1.0F`.

Thanks to [darkevilmac](https://minecraft.curseforge.com/projects/fast-bamboo) for finding this fix!

### Framerate limit slider step size

In vanilla Minecraft, the framerate limit slider step size is 10.0. RandomPatches changes this to
1.0 by default.

## Configuration

The RandomPatches configuration can be found at `config/randompatches.toml`.

* All properties and categories should be well-commented such that there is little need for further
explanation.
* All configuration values should be automatically validated and reset if they are invalid.
* If [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config-forge) is
installed, a configuration GUI can be accessed from the mod menu.
* In case of unaddressed compatibility issues, individual mixins can be disabled through the mixin
blacklist.
  * Most mixins are not automatically disabled when the features that depend on them are.
  * This is done to allow features to be enabled or disabled in-game without the need for restarts.
  * A list of these mixins can be found in the comments for the mixin blacklist.
* The configuration can be reloaded from disk in-game through the use of a command
(`/rpconfigreload` by default).
