/**
 * <p>The class that holds the data for cues, timing and other metadata about the songs.</p>
 * <p>Note: A lot of the parameters are UNUSED or have values set manually. A later update will allow them to be set up dynamically. Hopefully.</p>
 * Date: Mar 10, 2023
 *
 * @author Grand-Arcanium
 * @version 1.5
 */
public class SongSequence {

    // Song Metadata (realistically would be set/determined by the given audio files/a lookup table, or be dynamic if tempo is altered in any way)
    private double bpm; // speed of the song (mins)
    private double songBeats; // UNUSED; length of the song (seconds)
    private double bpms; // length of the song in beats (songLength/bpm...I think. I put it manually here)
    private double swing; // UNUSED; if the song is in swing: 0.5 for normal; 0.6 is light swing; 0.66 is med/normal swing; 0.75 is heavy swing


    // Cues
    private double[] cueRanges; // An Array that contains all the end of a given timed range.
    private double leniency; // UNUSED; how lenient the timing is, +/- from a given hit
    private double timeSign; // UNUSED; time signature (basic ones that RH does: "4" = 4/4 and "3" = 3/3)
    private double ace; // UNUSED; the beat that is considered a perfect hit (in ms), normally kept in an array as it depends on the cues

    /**
     * CONSTRUCTOR
     * <p>Establishes song metadata and derives timing for the cues.</p>
     * <p>In a realistic and flexible setup:
     *      <ul>
     *          <li>The properties here are determined dynamically depending on the song file given</li>
     *          <li>Would compensate for change in tempos and etc. -> UNUSED for now</li>
     *          <li>Dynamically determine cues based from the determined metadata</li>
     *      </ul>
     * </p>
     * <p>Instead, properties are set manually or by formula</p>
     *
     * */
    public SongSequence() {
        bpm = 120; // bpm of the specific song
        songBeats = 8; // total length of the song in beats
        timeSign = 4; // equivalent to 4/4

        bpms = (60 / bpm) * 1000; // how many beats per ms
        ace = 3000; // at what beat is a perfect hit, in ms. 500 = 1 beat. Ace is on the 6th (counting from 0).
        leniency = bpms / 16; // how much ahead or behind for a hit to count, this is +/- a 1/xth

        // All the metadata should be put into some calculation, which then determines cueRanges
        // But I am running out of time, so I used a hastily done formula:

        cueRanges = new double[]{500, 875, 906.25, 984.375, 1015.625, 1093.75, 1125, 1500};

        for (int i = 0; i < cueRanges.length; i++) {
            // Ace is when a hit is considered perfect (ms), -500 to compensate for delayed timer
            cueRanges[i] = cueRanges[i] - 500;
        }

    }

    // *** Methods
    /**
     * Gets the ranges for the song scoring.
     *
     * @param index which cue is needed
     * @return the corresponding value at that index
     */
    public double getCueRange(int index) {
        return cueRanges[index];

    }


}
