package com.sengled.media.player;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    public static void main(String[] args) {
        Date now = new Date();
        now.setTime(1494126731000l);
        System.out.println(now.toLocaleString());
    }
}