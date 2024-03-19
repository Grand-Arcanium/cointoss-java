# PROJECT: COIN TOSS
A remake of "Coin Toss", a [rhythm minigame of the same name from Nintendo's Rhythm Heaven DS/Megamix](https://rhwiki.net/wiki/Coin_Toss), written in Java and with JavaFX.

This was made as an assignment for COMP10062 (Java) in Mohawk College. As such, other than redacting personal info and moving TODOs and other comments into `README.md` and `Documentation.md`, the app remains entirely unchanged at first init.\
Documentation, process, sources, and potential updates can be found in `Documentation.md`.

An HTML/JS version was also made as an assignment for a different class and can be found here (insert link here).

----------

## How to Use
This app is not initially intended to be in a Git repo, so files have to be manually taken and built. (Yes, this is partly uploaded to a repo to learn Git)\
Changing how the app can be accessed from the repo may be something I'll update later.

If you wish to try anyway, here is what you need:
- The project uses IntelliJ IDEA (Java 19 SDK) and requires JavaFX SDK 21
- There is a RESOURCES File called `resources`: it must be in the same file level as `src` and must be marked as a resource root folder.
  - Right-click on the `resources` folder as shown in the project structure -> `mark directory as` -> `resource root`
- Build with `CoinToss.java` as the `Main()`

----------

## How To Play
Basic instructions are shown on the app.
But essentially, the `Toss` button starts a song, and to catch the coin you have to time it and press the `Catch` button. I won't tell you the timing ;) *

+1 point = **Barely** -> Barely caught the coin in time\
+2 points = **Hit** -> Caught the coin in time\
+3 points = **Ace** -> "Perfectly" caught the coin in time\
+0 points/**Miss** -> You lose!

The Practice teaches you when to hit it, and then requires you to not miss for 3 times in a row before the main game plays.
The Main Game is endless, keep going until you miss!

**Important Tip!** This game is doable without visuals, and I recommended not to rely on visuals! The game's cue visuals was the last thing I implemented and so it is not as in-depth and reliable as the song timings.

( * You're supposed to figure it out on your own, but here is the trick: Catch it after 3 seconds, or on the 7th beat (if the start of the song is considered as the 1st beat))

----------

# Features
- **Practice Mode**: Before playing, the game gives you a chance to practice the timing with a metronome beat. Missing does not end the game, but to progress to the main game, 3 catches in a row (regardless of accuracy) is needed.
- **Endless**: The game does not end (though not tested for integer overflow, good luck trying to get there manually). The "song" gets harder for up to 6 levels of difficulty as you play. After all 6 were played once, the difficulty is randomized.
- **High Scores**: The game keeps track of a high score, though only for a given session.

----------

## Legal Notes
For legal reasons and blah, this is made under Fair Use and for educational/personal use. Assets, musics and concepts are taken from Nintendo's Rhythm Heaven DS.\
(Don't sue me Nintendo, but I am willing to trade this project in for a Rhythm Heaven entry on the Switch :D)

----------

## Special thanks
To my Java Prof for the extension (I still needed to use an extra day) on this assignment as well as being very excited for this assignment as a whole!

To the devs and contributors of both [Rhythm Heaven Remix Editor](https://github.com/chrislo27/RhythmHeavenRemixEditor) and the [SFX Database](https://github.com/chrislo27/RHRE-database), as their apps/dbs are very useful in helping me figure out timings as well as get sfx.\
Big thanks to the [Rhythm Heaven Wiki](https://rhwiki.net/wiki/Home) too!





