
package com.example.app;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class StoryTest {

    private Story story;
    private String testName = "Test Name";
    private String testAuthor = "Test Author";
    private String testTitle = "Test Title";
    private String testStoryContent = "This is a test story.";

    // Mock the TextToSpeechClient
    TextToSpeechClient mockTextToSpeechClient;

    @BeforeEach
    void setUp() {
        // Initialize a new Story object before each test
        story = new Story(testName, testAuthor, testTitle, testStoryContent);

        // Create a mock TextToSpeechClient
        mockTextToSpeechClient = mock(TextToSpeechClient.class);
    }

    @Test
    void testStoryConstructor() {
        // Verify that the constructor correctly initializes the fields
        assertEquals(testName, story.getName(), "Name should be initialized correctly");
        assertEquals(testAuthor, story.getAuthor(), "Author should be initialized correctly");
        assertEquals(testTitle, story.getTitle(), "Title should be initialized correctly");
        assertEquals(testStoryContent, story.getStory(), "Story content should be initialized correctly");
        assertNull(story.getAudiofile(), "Audio file should be null initially");
    }

    @Test
    void testGetName() {
        // Verify the getName method returns the correct value
        assertEquals(testName, story.getName(), "getName should return the correct name");
    }

    @Test
    void testGetAuthor() {
        // Verify the getAuthor method returns the correct value
        assertEquals(testAuthor, story.getAuthor(), "getAuthor should return the correct author");
    }

    @Test
    void testGetTitle() {
        // Verify the getTitle method returns the correct value
        assertEquals(testTitle, story.getTitle(), "getTitle should return the correct title");
    }

    @Test
    void testGetStory() {
        // Verify the getStory method returns the correct value
        assertEquals(testStoryContent, story.getStory(), "getStory should return the correct story content");
    }

    @Test
    void testGetAudiofileInitiallyNull() {
        // Verify the getAudiofile method returns null before createAudioFile is called
        assertNull(story.getAudiofile(), "getAudiofile should return null initially");
    }

    @Test
    void testCreateAudioFile() throws Exception {
        // Mock the response from the TextToSpeechClient
        SynthesizeSpeechResponse mockResponse = SynthesizeSpeechResponse.newBuilder()
                .setAudioContent(ByteString.copyFromUtf8("fake audio data"))
                .build();

        try (MockedStatic<TextToSpeechClient> mockedStatic = mockStatic(TextToSpeechClient.class)) {
            // Configure the static mock to return our mock client when create() is called
            mockedStatic.when(TextToSpeechClient::create).thenReturn(mockTextToSpeechClient);

            // Configure the mock client to return our mock response when synthesizeSpeech is called
            when(mockTextToSpeechClient.synthesizeSpeech(any(SynthesisInput.class), any(VoiceSelectionParams.class), any(AudioConfig.class)))
                    .thenReturn(mockResponse);

            // Call the method under test
            story.createAudioFile();

            // Verify that the audiofile field is set and is not null
            assertNotNull(story.getAudiofile(), "audiofile should not be null after createAudioFile is called");
            // Optionally, verify the content if needed, but checking for non-null is usually sufficient for this test
            assertEquals(ByteString.copyFromUtf8("fake audio data"), story.getAudiofile(), "audiofile should contain the mocked audio data");

            // Verify that the close method was called on the mock client
            verify(mockTextToSpeechClient).close();
        }
    }


    @Test
    void testSanitiseStory() {
        // The current sanitiseStory method is empty.
        // This test simply verifies that calling it doesn't change the story content
        // and doesn't throw an exception.
        String originalStory = story.getStory();
        story.sanitiseStory();
        assertEquals(originalStory, story.getStory(), "sanitiseStory should not change the story content when empty");

        // If you implement sanitisation logic later, update this test
        // to check for the expected sanitised output.
        // Example (if you added a simple replacement):
        // story = new Story("Name", "Author", "Title", "<script>alert('xss')</script> story");
        // story.sanitiseStory();
        // assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt; story", story.getStory(), "sanitiseStory should handle script tags");
    }
}
