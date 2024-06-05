package com.music_player.api.common.utils;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class UsernameGenerator {
    private static final String CHAR_DIGITS = "0123456789";
    private static final int MAX_USERNAME_LENGTH = 12;

    public static UsernameGenerator getInstance() {
        return UsernameGeneratorInstance.INSTANCE;
    }
    
    private static class UsernameGeneratorInstance {
        private static final UsernameGenerator INSTANCE = new UsernameGenerator();
    }
    
    public String generateUniqueUsername(String firstName, Set<String> existingUsernames) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name must not be null or empty");
        }

        String usernameBase = firstName.toLowerCase().substring(0, Math.min(firstName.length(), MAX_USERNAME_LENGTH));
        int suffixLength = MAX_USERNAME_LENGTH - usernameBase.length();
        String username;

        do {
            String usernameSuffix = generateRandomString(suffixLength, CHAR_DIGITS);
            username = usernameBase + usernameSuffix;
        } while (existingUsernames.contains(username));

        return username;
    }

    private String generateRandomString(int length, String characterSet) {
        StringBuilder sb = new StringBuilder(length);
        Random random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterSet.length());
            sb.append(characterSet.charAt(index));
        }

        return sb.toString();
    }
}
