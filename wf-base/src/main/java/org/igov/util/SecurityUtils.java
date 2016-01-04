package org.igov.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.UUID;

/**
 * User: goodg_000
 * Date: 01.11.2015
 * Time: 15:18
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String generateSecret() {
        return RandomStringUtils.random(20, true, true);
    }

    /**
     * @return random 4xDigits answer code
     */
    public static String generateAnswer() {
        return String.format("%04d", new Random().nextInt(10000));
    }
}
