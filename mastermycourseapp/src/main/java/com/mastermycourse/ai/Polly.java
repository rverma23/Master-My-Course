package com.mastermycourse.ai;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.VoiceId;
import com.mastermycourse.settings.AWSPolyCredentials;

import java.io.InputStream;

/**
 * Author: James DeCarlo.
 * This class is for communications with the ai interface amazon poly for converting text to speech.
 *
 */

public class Polly {

    private AmazonPolly awsPolly;

    public Polly(){
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWSPolyCredentials.accessKey, AWSPolyCredentials.secretKey);
        awsPolly = AmazonPollyClientBuilder.standard()
                .withClientConfiguration(new ClientConfiguration())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.US_EAST_1).build();
    }

    /**
     * Transforms text to speech using amazon polly api
     * @param text the text we want to transform.
     * @return
     */
    public InputStream getTextToSpeech(String text){
        SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest().withText(text)
                .withVoiceId(VoiceId.Joanna).withOutputFormat(OutputFormat.Mp3);

        SynthesizeSpeechResult synthesizeSpeechResult = awsPolly.synthesizeSpeech(synthesizeSpeechRequest);
        return synthesizeSpeechResult.getAudioStream();

    }

    public void close(){
        awsPolly.shutdown();
    }
}
