package se.nackademin.stringify.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class KeyTest {

    private Key key;

    @BeforeEach
    void setUp() {
        key = Key.generate();
    }

    @DisplayName("Generated key is Instance of Key class")
    @Test
    void generateIsInstanceOfKeyClassTest() {
        assertThat(key).isInstanceOf(Key.class);
    }

    @DisplayName("Generated key should have a length of 6 characters")
    @Test
    void generatedKeyHasLengthOf6Test() {

        int actualLength = key.toString().length();
        assertThat(actualLength).isEqualTo(6);
    }

    @DisplayName("Generated key should have at least 1 digit")
    @Test
    void generatedHasAtLeastOneDigitTest() {
        String keyValue = key.toString();
        char[] chars = keyValue.toCharArray();

        boolean containsAtLeastOneDigit = false;
        for (char aChar : chars) {
            if (Character.isDigit(aChar)) {
                containsAtLeastOneDigit = true;
                break;
            }
        }
        assertThat(containsAtLeastOneDigit).isTrue();
    }

    @DisplayName("Letters should only be uppercase in a generated key")
    @Test
    void allLettersShouldBeUppercase() {
        char[] chars = key.toString().toCharArray();

        boolean allIsUppercase = true;
        for (char aChar : chars) {
            if (Character.isLetter(aChar) && !Character.isUpperCase(aChar)) {
                allIsUppercase = false;
            }
        }
        assertThat(allIsUppercase).isTrue();
    }

    @DisplayName("\"6FT2P7\" should create a Key with the same value")
    @Test
    void DF62P7_isValid_and_shouldCreateAKeyWithTheSameValue() {
        String value = "DF62P7";
        assertThatCode(() -> Key.fromString(value)).doesNotThrowAnyException();
        Key key = Key.fromString(value);
        assertThat(key.toString()).isEqualTo(value);
    }

    @DisplayName("Invalid argument will throw exception when attempting to create key")
    @ParameterizedTest
    @ValueSource(strings = {"", "1", "AP41FT98", "CU 43 S", "sfg6qe", "\t", "123456", "BBBBBB", "AF!56M", "6_2BLPS", "Gh6Sz9"})
    void invalidArgumentShouldThrowException(String value) {
        assertThatThrownBy(() -> Key.fromString(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Keys with equal value and same Type of class should return true")
    @Test
    void testShouldReturnTrueWhenValueAreEquals() {
        Key k1 = Key.fromString("ABC412");
        Key k2 = Key.fromString("ABC412");

        assertThat(k1.equals(k2)).isTrue();
    }

    @DisplayName("Keys with different values and same Type of class should return false")
    @Test
    void testShouldReturnFalseWhenValueAreDifferent() {
        Key k1 = Key.fromString("HLD65C");
        Key k2 = Key.fromString("H25JKA");

        assertThat(k1.equals(k2)).isFalse();
        assertThat(k1.equals(null)).isFalse();
    }

}
