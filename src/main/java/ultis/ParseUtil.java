/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ultis;

/**
 *
 * @author Admin
 */
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public final class ParseUtil {

    private ParseUtil() {
    }

    public static Integer toNonNegativeInteger(String value) {
        if (ValidationUtil.isBlank(value)) {
            return null;
        }
        try {
            int number = Integer.parseInt(value.trim());
            return number >= 0 ? number : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static Integer toPositiveInteger(String value) {
        if (ValidationUtil.isBlank(value)) {
            return null;
        }
        try {
            int number = Integer.parseInt(value.trim());
            return number > 0 ? number : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static BigDecimal toBigDecimal(String value) {
        if (ValidationUtil.isBlank(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static BigDecimal toNonNegativeBigDecimal(String value) {
        if (ValidationUtil.isBlank(value)) {
            return null;
        }
        try {
            BigDecimal number = new BigDecimal(value.trim());
            return number.compareTo(BigDecimal.ZERO) >= 0 ? number : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static LocalDateTime toLocalDateTime(String value) {
        if (ValidationUtil.isBlank(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    public static LocalDate toLocalDate(String value) {
        if (ValidationUtil.isBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
