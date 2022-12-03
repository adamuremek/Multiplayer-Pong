package com.gameserver;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    public static void main(String[] args) {
        Random random = new Random();
        float randomX = (float) Math.cos((random.nextFloat() * (2 * Math.PI)));
        float randomY = (float) Math.sin(random.nextFloat() * (2 * Math.PI));
        System.out.println(randomX + " " + randomY);
    }
}
