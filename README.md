# RandomPatches

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

[![Build](https://jitci.com/gh/TheRandomLabs/RandomPatches/svg?branch=1.16-forge)](https://jitci.com/gh/TheRandomLabs/RandomPatches)
[![Downloads](http://cf.way2muchnoise.eu/full_randompatches_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/randompatches)

[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/TheRandomLabs/RandomPatches.svg)](http://isitmaintained.com/project/TheRandomLabs/RandomPatches "Average time to resolve an issue")

A bunch of miscellaneous patches for Minecraft.

On Forge, RandomPatches only contains features that require Mixins. Tweaks that *don't* require
Mixins go in [RandomTweaks](https://www.curseforge.com/minecraft/mc-mods/randomtweaks).

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

Features without a side specified are server-sided.

### Connection timeouts

In vanilla Minecraft, the connection timeouts are hardcoded, and often not long enough for
slower computers or heavier modded instances. To counter this, RandomPatches allows several
connection timeouts to be configured:

* The connection read timeout (this is both client and server-sided)
* The login timeout (how long the server waits for a player to log in)
* The KeepAlive timeout (how long the server waits for a player to return a KeepAlive packet
before disconnecting them)

In addition, RandomPatches allows the interval at which KeepAlive packets are sent to clients
to be configured.
</details>

### Player speed limits

In vanilla Minecraft, the player speed limits are hardcoded, and set to values that are often
not high enough in certain cases. As a result, rubber banding occurs, and
`[Player] moved too quickly!` is spammed in the log. To prevent this, RandomPatches changes the
following player speed limits to a higher value by default:

* Default maximum player speed
* Maximum player elytra speed
* Maximum player vehicle speed

### Tick scheduler desync fix

Occasionally, the tick scheduler becomes desynchronised, and as a result, Minecraft crashes,
throwing an `IllegalStateException` with the message `TickNextTick list out of synch`.
RandomPatches attempts to fix this issue using the solution described by malte0811
[here](https://github.com/SleepyTrousers/EnderCore/issues/105).

### Window title and icon

By default, RandomPatches removes the annoying `*` in the Minecraft window title that indicates
that the game is modded. In addition, the following window properties can be configured:

* Title (the Minecraft version and current activity can be included)
* 16x16 icon
* 32x32 icon
* 256x256 icon (only takes effect on Mac OS X)

## Configuration

The RandomPatches configuration can be found at `config/randompatches.toml`.

* All properties and categories should be well-commented such that there is little need for further
explanation.
* All configuration values should be automatically validated and reset if they are invalid.
* If [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config-forge) is
installed, a configuration GUI can be accessed from the mod menu.
* In case of unaddressed compatibility issues, individual mixins can be disabled through the mixin
blacklist.
* The configuration can be reloaded from disk in-game through the use of a command
(`/rpconfigreload` by default).
