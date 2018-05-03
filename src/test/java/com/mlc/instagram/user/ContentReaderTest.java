package com.mlc.instagram.user;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class ContentReaderTest {

    @Spy
    private ContentReader contentReader;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void successSearch() {

        StringBuilder sb = new StringBuilder();
        sb.append("a line before\\n");
        sb.append("some text before <script type=\"text/javascript\">window._sharedData = a json content;</script> some text after");
        sb.append("\n a line after");

        String jsonContent = contentReader.search(sb.toString());

        assertEquals(jsonContent, "a json content");

    }

}
