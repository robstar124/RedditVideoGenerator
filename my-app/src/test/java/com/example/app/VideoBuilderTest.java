package com.example.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

class VideoBuilderTest {

    private VideoBuilder videoBuilder;
    private String testAudioFile = "input_audio.mp3";
    private String testVideoFile = "input_video.mp4";
    

    @BeforeEach
    void setUp() {
        // Initialize a new VideoBuilder object before each test
        videoBuilder = new VideoBuilder(testAudioFile, testVideoFile);
    }

    @Test
    void testVideoBuilderConstructor() {
        // Verify that the constructor correctly initializes the fields
        assertEquals(testAudioFile, videoBuilder.getAudioFile(), "Audio file should be initialized correctly");
        assertEquals(testVideoFile, videoBuilder.getVideoFile(), "Video file should be initialized correctly");

    }

    @Test
    void testGetSetAudioFile() {
        String newAudioFile = "new_audio.wav";
        videoBuilder.setAudioFile(newAudioFile);
        assertEquals(newAudioFile, videoBuilder.getAudioFile(), "setAudioFile and getAudioFile should work correctly");
    }

    @Test
    void testGetSetVideoFile() {
        String newVideoFile = "new_video.avi";
        videoBuilder.setVideoFile(newVideoFile);
        assertEquals(newVideoFile, videoBuilder.getVideoFile(), "setVideoFile and getVideoFile should work correctly");
    }
}

