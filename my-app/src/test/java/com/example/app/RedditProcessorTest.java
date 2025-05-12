
package com.example.app;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Make sure you have the necessary JUnit 5 and Mockito dependencies in your pom.xml or build.gradle:
// JUnit Jupiter API: org.junit.jupiter:junit-jupiter-api:<version>
// JUnit Jupiter Engine: org.junit.jupiter:junit-jupiter-engine:<version> (for running tests)
// Mockito Core: org.mockito:mockito-core:<version>
// Mockito JUnit Jupiter: org.mockito:mockito-junit-jupiter:<version> (for JUnit 5 integration)
// JSON library: org.json:json:<version>

class RedditProcessorTest {

    private RedditProcessor redditProcessor;
    private String testUrlString = "https://www.reddit.com/r/nosleep/top/.json?t=month";
    private URL testUrl;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize a new RedditProcessor object before each test
        redditProcessor = new RedditProcessor();
        // Set the URL using the actual method to ensure its functionality is tested
        redditProcessor.setUrl(testUrlString);
        try {
          testUrl = new URI(testUrlString).toURL(); 
        } catch (Exception e) {
          System.out.println(e);
        }
        // Store the URL object for verification
    }

    @Test
    void testSetUrlAndGetUrl() {
        // Verify that setUrl correctly sets the URL and getUrl returns it
        assertEquals(testUrl, redditProcessor.getUrl(), "getUrl should return the URL set by setUrl");
    }

    @Test
    void testGetStoriesInitiallyEmpty() {
        // Verify that the stories list is empty initially
        assertNotNull(redditProcessor.getStories(), "Stories list should not be null initially");
        assertTrue(redditProcessor.getStories().isEmpty(), "Stories list should be empty initially");
    }

    @Test
    void testGetStoriesFromURL_Success() throws Exception {
        // Sample JSON response simulating the Reddit API structure
        String sampleJsonResponse = """
                {
                  "data": {
                    "children": [
                      {
                        "data": {
                          "name": "t3_story1",
                          "author": "author1",
                          "title": "Title 1",
                          "selftext": "Content of story 1."
                        }
                      },
                      {
                        "data": {
                          "name": "t3_story2",
                          "author": "author2",
                          "title": "Title 2",
                          "selftext": "Content of story 2."
                        }
                      }
                    ]
                  }
                }
                """;

        // Convert the JSON string to an InputStream
        InputStream mockInputStream = new ByteArrayInputStream(sampleJsonResponse.getBytes());


        URL testURL = mock(URL.class);

        when(testURL.openStream()).thenReturn(mockInputStream);

        ArrayList<Story> stories = redditProcessor.getStoriesFromURL(testURL);

        // Verify the content of the first story
        Story story1 = stories.get(0);
        assertEquals("t3_story1", story1.getName(), "First story name should match JSON");
        assertEquals("author1", story1.getAuthor(), "First story author should match JSON");
        assertEquals("Title 1", story1.getTitle(), "First story title should match JSON");
        assertEquals("Content of story 1.", story1.getStory(), "First story content should match JSON");

        // Verify the content of the second story
        Story story2 = stories.get(1);
        assertEquals("t3_story2", story2.getName(), "Second story name should match JSON");
        assertEquals("author2", story2.getAuthor(), "Second story author should match JSON");
        assertEquals("Title 2", story2.getTitle(), "Second story title should match JSON");
        assertEquals("Content of story 2.", story2.getStory(), "Second story content should match JSON");
        
    }



    @Test
    void testGetStoriesFromURL_JSONException() throws Exception {
        // Sample invalid JSON response
        String invalidJsonResponse = "{ \"data\": { \"children\": [ { \"data\": { \"name\": \"t3_story1\", "; // Incomplete JSON

        InputStream mockInputStream = new ByteArrayInputStream(invalidJsonResponse.getBytes());

        URL testURL = mock(URL.class);

        when(testURL.openStream()).thenReturn(mockInputStream);
        try{
        assertThrows(org.json.JSONException.class, () -> redditProcessor.getStoriesFromURL(testURL), "getStoriesFromURL should throw JSONException on invalid JSON");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }


    @Test
    void testProcessStories_SingleRequest() throws Exception {
        // Spy on the actual redditProcessor object to mock specific methods
        RedditProcessor spyRedditProcessor = spy(RedditProcessor.class);

        // Create a list of mock stories for the first API call
        ArrayList<Story> mockStories1 = new ArrayList<>();
        mockStories1.add(new Story("t3_story1", "author1", "Title 1", "Content 1"));
        mockStories1.add(new Story("t3_story2", "author2", "Title 2", "Content 2"));

        // Configure the spy to return the mock stories for the first call to getStoriesFromURL
        doReturn(mockStories1).when(spyRedditProcessor).getStoriesFromURL(testUrl);

        spyRedditProcessor.setUrl(testUrlString);
        spyRedditProcessor.processStories(2);



        // Verify that the stories list in the processor is populated with the mock stories
        assertEquals(2, spyRedditProcessor.getStories().size(), "Stories list should contain stories from the first call");
        assertEquals("t3_story1", spyRedditProcessor.getStories().get(0).getName());
        assertEquals("t3_story2", spyRedditProcessor.getStories().get(1).getName());
    }

    @Test
    void testProcessStories_MultipleRequests() throws Exception {
        // Spy on the actual redditProcessor object
        RedditProcessor spyRedditProcessor = Mockito.spy(redditProcessor);

        // Create mock stories for the first API call (25 stories)
        ArrayList<Story> mockStories1 = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            mockStories1.add(new Story("t3_story" + i, "author" + i, "Title " + i, "Content " + i));
        }

        // Create mock stories for the second API call (25 stories)
        ArrayList<Story> mockStories2 = new ArrayList<>();
        for (int i = 25; i < 50; i++) {
            mockStories2.add(new Story("t3_story" + i, "author" + i, "Title " + i, "Content " + i));
        }

        // Define the expected URL for the second call with the 'after' parameter
        String afterName = mockStories1.get(mockStories1.size() - 1).getName(); // Get the name of the last story from the first batch
        URL expectedSecondUrl = new URI(testUrlString + "&after=" + afterName).toURL();

        // Configure the spy to return different mock stories for successive calls to getStoriesFromURL
        doReturn(mockStories1).when(spyRedditProcessor).getStoriesFromURL(testUrl); // First call
        doReturn(mockStories2).when(spyRedditProcessor).getStoriesFromURL(expectedSecondUrl); // Second call

        // Call processStories to get 40 posts (requires 2 API calls: ceil(40/25) = 2)
        spyRedditProcessor.processStories(50);

        
        // Verify that the stories list in the processor contains stories from both calls
        assertEquals(50, spyRedditProcessor.getStories().size(), "Stories list should contain stories from both calls");

        // Verify content from the first batch
        assertEquals("t3_story0", spyRedditProcessor.getStories().get(0).getName());
        assertEquals("t3_story24", spyRedditProcessor.getStories().get(24).getName());

        // Verify content from the second batch
        assertEquals("t3_story25", spyRedditProcessor.getStories().get(25).getName());
        assertEquals("t3_story49", spyRedditProcessor.getStories().get(49).getName());
    }

    @Test
    void testProcessStories_ZeroPosts() throws Exception {
        // Spy on the actual redditProcessor object
        RedditProcessor spyRedditProcessor = Mockito.spy(redditProcessor);

        // Call processStories requesting 0 posts
        spyRedditProcessor.processStories(0);

        // Verify that getStoriesFromURL was never called
        verify(spyRedditProcessor, never()).getStoriesFromURL(any(URL.class));

        // Verify that the stories list remains empty
        assertTrue(spyRedditProcessor.getStories().isEmpty(), "Stories list should be empty when requesting 0 posts");
    }

    @Test
    void testProcessStories_ExceptionInGetStoriesFromURL() throws Exception {
        // Spy on the actual redditProcessor object
        RedditProcessor spyRedditProcessor = Mockito.spy(redditProcessor);

        // Configure the spy to throw an exception on the first call to getStoriesFromURL
        doThrow(new IOException("Simulated API error during processing"))
                .when(spyRedditProcessor).getStoriesFromURL(any(URL.class));

        // Verify that calling processStories throws the expected exception
        assertThrows(IOException.class, () -> spyRedditProcessor.processStories(50), "processStories should rethrow exceptions from getStoriesFromURL");

        // Verify that getStoriesFromURL was called at least once (where it threw the exception)
        verify(spyRedditProcessor, atLeastOnce()).getStoriesFromURL(any(URL.class));

        // Verify that the stories list remains empty or contains only partial data if the exception occurred mid-way
        // In this case, the exception is on the first call, so it should be empty.
        assertTrue(spyRedditProcessor.getStories().isEmpty(), "Stories list should be empty if an exception occurs during the first API call");
    }
}
