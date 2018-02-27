package test

import com.amazonaws.regions.Regions
import com.mastermycourse.ai.Polly
import javazoom.jl.player.advanced.AdvancedPlayer
import javazoom.jl.player.advanced.PlaybackEvent
import javazoom.jl.player.advanced.PlaybackListener
/**
 * Author: James DeCarlo.
 */
class PollyTest extends GroovyTestCase {

    private String SAMPLE = "Hello James I am Working Just Fine Congratulations!";

    void testGetTextToSpeech() {
        System.out.println(Regions.US_EAST_1.name);
        Polly polly = new Polly();
        //get the audio stream
        InputStream speechStream = polly.getTextToSpeech(SAMPLE);

        //create an MP3 player
        AdvancedPlayer player = new AdvancedPlayer(speechStream,
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());

        player.setPlayBackListener(new PlaybackListener() {

            @Override
            public void playbackStarted(PlaybackEvent evt) {
                System.out.println("Playback started");
                System.out.println(SAMPLE);
            }

            @Override
            public void playbackFinished(PlaybackEvent evt) {
                System.out.println("Playback finished");
            }
        });


        // play it!
        player.play();

    }
}
