/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ultis;

import java.text.Normalizer;
import java.util.Locale;

/**
 *
 * @author Admin
 */
public class SlugUtil {
    private SlugUtil() {
    }

    public static String create(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(
                value.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.replace('đ', 'd');
        normalized = normalized.replaceAll("[^a-z0-9]+", "-");
        normalized = normalized.replaceAll("^-+|-+$", "");
        return normalized;
    }
}
