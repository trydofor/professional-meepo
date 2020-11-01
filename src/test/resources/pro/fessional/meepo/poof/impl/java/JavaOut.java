package pro.fessional.meepo.poof.impl.java;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.impl.JavaEngine;

import java.util.Map;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author trydofor
 * @since 2020-10-31
 */
public class JavaOut implements JavaEngine.Java {
    @Override
    public Object eval(@NotNull Map<String, Object> ctx) {
        LocalDate date = LocalDate.parse("2020-07-09");
        LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ldt.format(fmt);
    }
}