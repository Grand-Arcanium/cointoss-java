# PROJECT: COIN TOSS

This file contains the game's Documentation as well as sources, processes, and future updates taken from the original READFIRST.txt and from comments in the app.

----------

## Metadata

Author: Grand-Arcanium (Name Redacted)\
Version: 1.5\
Due Date: 2023/03/10\
For: COMP10062 (Java), Mohawk College\
Time Taken: Approx. three 6+ hour sessions + a couple of 1-2 hour Sessions (I got really fixated on this project)

----------

## Author Notes
1) If this was the final assignment and/or I am not terrible with my time, I would add EVEN MORE STUFF with a project like this
    - especially since this could be the base of one of my personal projects I've been wanting to start for a while now.
    - thank goodness for the extension, or I wouldve handed in a Dice game.

2) I added the `throws FileNotFoundException` as recommended by the JavaFX images reference, but I only have a general idea about it from Assignment 1 and the use of `Thread.sleep()`.
    - _UPDATE_: I ended up adding the throw Exceptions and Try-Catches as recommended by IntelliJ, ever since I started using `toURI()`
    - Hopefully they are contextually appropriate as they are written, because I don't know how to change them otherwise.

3) I learned that `setOnAction` is actually triggering my sfx on the release of the button, not on press. It's really weird having to time the button release instead,
    - but I don't want to figure out controller events on top of everything else so it stays
    - _UPDATE_: `setOnMouseClicked` does what I was looking for! Though it means being restricted to mouse-only

4) With all that's going on, it gets hard to tell whether the stuff I do in the event handlers are considered logic or not. I sure hope they are not so I don't lose marks.

----------

## Future Updates

A list of features that would be neat to add if I ever come back to this project, or to adopt it into a new one. Also contains updates that never made it into the original assignment.
- Decorate ending
    - stars = record, tears/swirls = lose
- Fix up the Timeline to be more precise instead of handling checks at the end of a range
    - possibly have a keyframe every 1/16 or 1/32 beat instead of a given range.
    - Then assigning more than one Keyframe to the ranges of the Hits and etc.
- Animate the Intro, Game Hand, and End Scene manually with JavaFX, just as originally intended.
- Separate Practice Mode, either before playing that can be skipped or as a separate menu option
- Harder Endless: Different tempos for the same song (start normal then going faster/slower).
    - If possible, the tempo is edited in real-time rather than having to create multiple audio files and adjusting all cues with varying tempo.
- Non-Endless: Different songs (set length with a standard scoring out of 100, or out of how many possible hits)
- Volume Adjuster & Test
- Save high scores after closing app
- Actually check if the use of reinitializing (ie. `Thing x = new Thing()`) and all the drawing does not take memory as you stack restarts, or whatever
- A proper way to clear the image viewer
- Formulas and other methods that will allow for dynamic cue and timing setup.

----------

## Timeline Process
Making a timeline for a Rhythm Game: My Steps & Analysis (because it's nice writing things out)

1) Base Calculations     
   - 1 BPM = 1 beat per 1 min = 1 beat per 60s = 1 beat per 60000ms 
   - 120 BPM = 120 Beats per min | 1 beat per 0.5s = 1 beat per 500ms

2) Using a Timeline()
   Create a keyframe, which does some event for every keyframe
    - This is adjusted by a Duration, which usually takes its own parameters
    - Thanks to the song chosen, the numbers will be simple to work with (120BPM), so I don't have to define my own Durations for some reason. Now I can work with seconds (which the tutorial of the original game does anyway!)

But what about the scoring? leniency? ranges?
- Possibly determine scoring and overall timing in beats first, so that the leniency can be determined this way. Then convert to ms.
- I don't know what the leniency amount is in RH, I do know its different for each minigame and changes when tempo/bpm does
- Dynamically tightening leniency to increase difficulty will be for next time, instead, the simplest difficulty change would be randomizing audio files.

3) Scoring + Leniency
   Leniency: A leeway for hits to be considered good or not
    - Initially, I am doing by 8ths, as well as to demonstrate how the scoring goes
    - But for leniency. just in case of factors beyond my current scope/ability to handle (app lag, input delay, pc specs, etc.), it'll be slightly more lenient		
      - unfortunately if the player is running the ideal setup, this won't affect them and may mean the game is easier...
      - IF!!!! only a Hit (successful) or a Miss (unsuccessful) are the only options.
      - which means, it should be encouraged to still hit as accurate as possible for max score, hence the Ace (on exact beat) and Barelys (almost too late or early).
    - UPDATE: Now that the audio delay is removed for all songs via .delay() mostly, I am able to compensate for one of the sources of lag. Seems pretty good now so I'll leave it as is.

So, assuming Ace is beat 1 (B1) in an 8ths timeline (8th notes are x/8, or x/):
Miss (MM), Hit (HH), Ace (AA), Barely (BB)

```
| B0 | 1/ | 2/ | 3/ | 4/ | 5/ | 6/ | 7/ | B1 | 1/ | 2/ | 3/ | 4/ | 5/ | 6/ | 7/ | B2 | ...
| -- | -- | -- | -- | -- | MM | BB | HH | AA | HH | BB | MM | -- | -- | -- | -- | -- | ...
```

- This should be in range, hopefully I implement it that way:
        - eg. an early Barely in the (B0 at 6/) = counts as a Barely from 6/ inclusive to 7/ exclusive, then at 7/ inclusive to etc., it counts as a Hit.
- BUT! I was hoping to shift it so that the possible actions are in the middle of the range
    - as in, if I hit an Ace at B1, I could hit it either -250ms before and +250ms after, and it would count as an Ace.
- BUT#2!! a range of 500ms is too big, it's one whole beat! So I'll be doing them in parts of a beat
    - consider that, Hits should be more common to get than Barelys and Aces; But Barelys and Aces should be the same, only with Barelys split into one "Early" and one "Late". While the timing of Aces is all in the center.

So now the diagram is more like:\
```
... MM | BB | HH | HH | HH | AA | AA | HH | HH | HH | BB | MM ...
                                ^- B1 = a Frame-perfect Ace
```

3) CALCULATIONS:
   - 120 BPM = 2 beats per 1s | per 1000ms = 1 beat per 0.5s | per 500ms
   - 1/2 beat -> 250ms
   - 1/4 beat -> 125ms
   - 1/8 beat -> 62.5ms
   - 1/16 beat -> 31.25ms
   - 1/32 beat -> 15.625ms
   - Starting from B0 (0s), the Ace of Coin Toss is on B6 (3s | 3000ms into the song)
      - Frame-perfect Ace = On exactly B6

All these assume that an Ace is on B1, and is adjusted via .delay():

With a Leniency of +/- 1/16 beat (31.25 beats) (manually done)

- Ace Range: 968.75ms to 1031.25ms
- Hit Range: 906.25ms to 968.75ms before Ace | 1031.25ms to 1093.75ms after Ace
- Barely Range: 875ms to 906.25 before Hit | 1093.75ms to 1125ms after Hit
  Total Hit Range = 1125 - 875 = 250ms
  - This is actually a HUGE range, 250ms is half a beat. or 1/4 before and after.
  - I'll redo this for 1/32s, but in a formula and not by calculating manually LOL

With a Leniency of +/- 1/32 beat (15.625 beats)
- Ace Range: 984.375 to 1015.625
- Hit Range: 953.125 to 984.375 | 1015.625 to 1046.875
- Barely Range: 937.5 to 953.125 | 1046.875 to 1062.5
- Total Range: 125.0
    - 125ms is 1/4 a beat, or 1.8 beat before and after. This is much better.
    - _Update_: it was not. It was too strict LOL, going back to 1/16
    - _Update_: Narrowing the Ace by 1/32th on both sides makes it a bit more challenging to get good points, I think.

**FINAL**: Leniency = 1/16ths + Ace range is adjusted to have a Leniency of 1/32ths, giving its extra on to the Hit range on either side.

----------

## Resources

1) Minna no Rhythm Tengoku (Rhythm Heaven Fever) Official Soundtrack and the extracted Soundtrack of Rhythm Heaven Megamix

2) From the Rhythm Heaven Remix Editor's [SFX database](https://github.com/chrislo27/RHRE-database), which compiles all RH sound effects from the games for use in the fan-made RH Remix Editor

3) "[Coin Toss](https://rhythmheaven.fandom.com/wiki/Coin_Toss)", the RH rhythm game this program is based on

----------

## References

As per usual, I documented almost all the sites I've referenced for subjects/concepts not covered in the lectures as of when this assignment was created.
1) [All of JavaFX Animations](https://docs.oracle.com/javafx/2/animations/basics.htm), just in case I do end up animating

2) Importing Resources
   - [General](https://www.jetbrains.com/help/idea/add-items-to-project.html#import-items)
   - [Pathing Audio](https://stackoverflow.com/questions/49643126/javafx-pathing-and-getting-audio-files-to-work-in-my-jar)
   - [More Audio](https://stackoverflow.com/a/41291987)

3) Using Audio
   - [Site 1](https://www.javatpoint.com/javafx-playing-audio)
   - [Site 2](https://www.geeksforgeeks.org/play-audio-file-using-java/)

4) Using Images? (might be UNUSED)
   - [Site](https://www.tutorialspoint.com/javafx/javafx_images.htm)

5) Breaking down SFX and music metadata using the [Rhythm Heaven Remix Editor](https://github.com/chrislo27/RhythmHeavenRemixEditor)

7) Finding width of a Text component (to center components)
   - [Site](https://stackoverflow.com/a/13020490)

8) Changing font size
   - [Site](https://stackoverflow.com/a/50305689) 
   - (tried using `text.setFont(Font.font(newsize))` and similar code, but it gave me warnings)

9) This cool canvas guide
    - [Site](https://edencoding.com/javafx-canvas/)

10) A sample of a game loop with frames
    - [Site](https://edencoding.com/game-loop-javafx/)

11) To play audio clips with low latency
    - [Site](https://docs.oracle.com/javase/10/docs/api/javafx/scene/media/AudioClip.html)

12) Clear Canvas (see answer), or parts (see comment):
    - [Site](https://stackoverflow.com/questions/49216396/clearing-the-scene-in-javafx)

13) Handling real-time events using Timeline, Keyframes and Duration
    - [Site 1](https://edencoding.com/javafxanimation-transitions-timelines-and-animation-timers/)
    - [Site 2](https://stackoverflow.com/a/60685975)
    - [Site 3](https://stackoverflow.com/a/35512859)

14) Image Change
    - [Site](https://stackoverflow.com/questions/35366628/how-to-change-imagesrc-of-imageview-with-button-mouse-click-on-javafx)
