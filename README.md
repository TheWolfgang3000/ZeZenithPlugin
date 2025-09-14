ZeZenithPlugin
Remember the good old days of Minecraft? No beds. Just you, the darkness, and the agonizingly slow passage of time until the sun decided to grace you with its presence. ZeZenithPlugin is here to fix that ancient problem with the most logical solution imaginable: peer pressure.

This is a highly configurable night-skipping plugin designed for classic Minecraft servers where the concept of sleeping through the night was apparently too advanced.

Features
Democratic Night-Skipping
Because tyranny is overrated, the night is only skipped when a configurable percentage of your players finally agree on something. This prevents that one guy who loves the dark from holding everyone else hostage.

Intelligent AFK Detection
Fairness is key. The plugin is smart enough to know that your friend who fell asleep on their keyboard shouldn't get a say in when the sun comes up. Inactive players are gracefully ignored.

Fully In-Game Configuration
Look, we get it. Editing text files is a hassle and you've got better things to do. Nearly every setting can be changed with admin commands directly in the game, because who has time to restart a server for a simple tweak?

Dynamic Message System
By default, the plugin will celebrate your collective impatience with a random witty message from a pool of over 50. Of course, if you think you're funnier, you can set your own single, custom message to be displayed every single time. We won't judge. Much.

Lightweight & Stable
It's a simple plugin for a simple time. It probably won't set your server on fire.

Commands
There's one command for the masses, and a whole control panel for you, the overworked admin.

Player Commands
Command	Description
/gn or /goodnight	The one command your players need to learn.

In Google Sheets exportieren
Admin Commands
Permission: zenith.admin (Given to OPs by default, because we trust you.)

Command	Description
/zenith help	In case you forget the commands you're about to read.
/zenith status	Check what settings you've already messed up.
/zenith toggle	The big on/off switch. Use with caution. Or don't.
/zenith reload	For when you broke something in the config file and need a do-over.
/zenith afkmessages <on/off>	Decide if everyone needs to be notified when someone stops playing.
/zenith setafktime <minutes>	Define the exact amount of time before a player is officially useless.
/zenith setpercentage <1-100>	Dictate the precise threshold of peer pressure required for a sunrise.
/zenith setmessage <id> <text...>	Because our default messages clearly weren't good enough for you.
/zenith setcustommessage <text...>	Set your one, glorious good morning message to rule them all.
/zenith resetcustommessage	For when you realize our random messages were better after all.

In Google Sheets exportieren
Message IDs for /zenith setmessage:
night_start, vote_cast, already_voted, not_night, afk_on, afk_off

Installation
Download the latest ZeZenithPlugin.jar from the Releases page.

Drop the .jar file into your server's /plugins directory.

Start or restart your server.

If the server starts and the plugin loads, congratulations on successfully following instructions. The config.yml is now waiting for you in the plugin's folder.
