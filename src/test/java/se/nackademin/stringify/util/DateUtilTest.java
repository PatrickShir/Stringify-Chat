package se.nackademin.stringify.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateUtilTest {

    @DisplayName("the given Timestamp object should be converted to an instance of a String")
    @Test
    void dateShouldReturnAStringInstance() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        assertThat(DateUtil.dateToString(timestamp)).isInstanceOf(String.class);
    }

    @DisplayName("null Date should throw IllegalArgumentException")
    @Test
    void nullDateShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> DateUtil.dateToString(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Timestamp converted to String should have format yyyy-MM-dd HH:mm")
    @Test
    void stringDateShouldHaveSameValueAsTimestamp() {
        Timestamp timestamp = Timestamp.valueOf("2020-09-15 15:16:00.016");

        String actual = DateUtil.dateToString(timestamp);

        assertThat(actual).isEqualTo("2020-09-15 15:16");
        assertThat(actual.matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$")).isTrue();
    }
}
