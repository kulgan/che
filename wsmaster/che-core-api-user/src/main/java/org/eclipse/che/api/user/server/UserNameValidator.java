package org.eclipse.che.api.user.server;

import java.util.regex.Pattern;

/**
 * Utils for username validation and normalization.
 *
 * @author Mihail Kuznyetsov
 */
public class UserNameValidator {
    private static final Pattern ILLEGAL_USERNAME_CHARACTERS = Pattern.compile("[^a-zA-Z0-9]");

    /**
     * Validate name, if it doesn't contain illegal characters
     *
     * @param name
     *        username
     * @return true if valid name, false otherwise
     */
    public static boolean isValidUserName(String name) {
        return ILLEGAL_USERNAME_CHARACTERS.matcher(name).matches();
    }

    /**
     * Remove illegal characters from username, to make it URL-friendly.
     *
     * @param name
     *        username
     * @return username without illegal characters
     */
    public static String normalizeUserName(String name) {
        return ILLEGAL_USERNAME_CHARACTERS.matcher(name).replaceAll("");
    }
}
