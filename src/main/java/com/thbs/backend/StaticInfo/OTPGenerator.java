package com.thbs.backend.StaticInfo;

import java.util.Random;

public class OTPGenerator {
    public static int generateRandom6DigitNumber() {
        Random random = new Random();

        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min + 1) + min;
    }
}

