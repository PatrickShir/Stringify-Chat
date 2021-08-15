package se.nackademin.stringify.util;


import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * A class to generate a randomized alphanumeric Key value as a unique identifier. The length of the key is specifically 6.
 * The Key consists of uppercase letters and at least 1 digit. There are two optional ways of creating a Key object.
 * <ul>
 *     <li>The <strong>{@code generate()}</strong> method creates a Key object with a randomized generated value.</li>
 *     <li>The <strong>{@code fromString()}</strong> method creates a Key object with the given value, expected that the value is valid.</li>
 * </ul>
 *
 * <em><u>WARNING:</u></em> A generated Key could potentially create a duplicate key, even though chances are very slim.
 */
public class Key {

    /**
     * A sorted sequence of characters for an alphanumeric String
     */
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "01234567";
    /**
     * The length of the generated key
     */
    private static final int KEY_LENGTH = 6;
    private final String value;

    public Key(String value) {
        this.value = value;
    }


    /**
     * Generates a randomized alphanumeric key. The given alphanumeric list is at all times shuffled before use,
     * decreasing the chance of generating a duplicate key.
     *
     * @return a generated alphanumeric key with length of 6
     */
    public static Key generate() {
        List<String> alphaNum = alphaNumericToList();
        StringBuilder key = new StringBuilder(KEY_LENGTH);

        for (int i = 0; i < KEY_LENGTH; i++) {
            int index = new Random().nextInt(alphaNum.size());

            if (i == KEY_LENGTH - 1) {
                if (!containsADigit(key.toString())) {
                    key.append(new Random().nextInt(9));
                    return new Key(key.toString());
                }
            }
            key.append(alphaNum.get(index));
        }

        return new Key(key.toString());
    }

    /**
     * Converts the alphanumeric String to a list. For each instance of use it shuffles the list before use.
     *
     * @return a shuffled alphanumeric list
     */
    private static List<String> alphaNumericToList() {
        List<String> sortedAlphaNum = ALPHANUMERIC.chars()
                .mapToObj(Character::toString)
                .collect(Collectors.toList());
        Collections.shuffle(sortedAlphaNum);

        return sortedAlphaNum;
    }

    /**
     * controls if the given string value contains any digit
     *
     * @param value String to be controlled
     * @return {@code true} if the string contains a digit; {@code false}
     * otherwise
     */
    private static boolean containsADigit(String value) {
        char[] characters = value.toCharArray();
        boolean containsDigit = false;
        for (char character : characters) {
            if (Character.isDigit(character))
                containsDigit = true;
        }
        return containsDigit;
    }

    /**
     * Checks if the given value is valid. The result is {@code true} if the argument matches the regex
     * pattern; {@code false} otherwise
     *
     * @param value The value to be controlled
     * @return {@code true} if the String matches the regex pattern; {@code false}
     * otherwise
     */
    public static boolean isValidKey(String value) {
        if (value.length() != 6)
            return false;

        Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])(?!.*[-,.&/():;_*+><=!?#£$%½{}`´ ])(?!.*[a-z]).{0,6}$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * Creates a key with the given String value.
     *
     * @param value If the key is invalid an exception will be thrown; otherwise
     *              a Key will be created with the given value
     * @return a Key object with the given value
     */
    public static Key fromString(String value) {
        if (value == null || !isValidKey(value))
            throw new IllegalArgumentException("Invalid key: Does not look like a Key value. e.g. \"HC94F2\"");

        return new Key(value);
    }

    /**
     * Compares this Key object to the specified Key object. The result is {@code
     * true} if the argument is not {@code null}, is a {@code Key} object and
     * the keys have the same value.
     *
     * @param obj The Key object to be compared
     * @return {@code true} if the objects are the same; {@code false}
     * otherwise
     */
    public boolean equals(Object obj) {
        if ((null == obj) || (obj.getClass() != Key.class))
            return false;

        Key otherKey = (Key) obj;
        return this.value.equals(otherKey.toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
