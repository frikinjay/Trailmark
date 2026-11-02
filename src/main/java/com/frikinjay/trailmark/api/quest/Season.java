package com.frikinjay.trailmark.api.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * When a chapter is open: between two dates (every year, or once), optionally only on some weekdays. Outside it the
 * chapter is locked, or hidden when {@code hidden} is set. Dates are inclusive and use the server's time zone.
 *
 * @param from   first day, {@code MM-DD} for every year or {@code YYYY-MM-DD} for once; empty means no start
 * @param until  last day, in the same form; a yearly range may wrap over New Year ({@code 12-20} to {@code 01-06})
 * @param days   weekdays it's open on, such as {@code ["saturday", "sunday"]}; empty means every day
 * @param hidden hide the chapter while it's closed, instead of showing it locked
 */
public record Season(String from, String until, List<String> days, boolean hidden) {

    public static final Codec<Season> CODEC = RecordCodecBuilder.<Season>create(i -> i.group(
            Codec.STRING.optionalFieldOf("from", "").forGetter(Season::from),
            Codec.STRING.optionalFieldOf("until", "").forGetter(Season::until),
            Codec.STRING.listOf().optionalFieldOf("days", List.of()).forGetter(Season::days),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Season::hidden)
    ).apply(i, Season::new)).validate(Season::check);

    private static DataResult<Season> check(Season s) {
        try {
            parse(s.from);
            parse(s.until);
            for (String d : s.days) DayOfWeek.valueOf(d.toUpperCase(Locale.ROOT));
            return DataResult.success(s);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return DataResult.error(() -> "Season dates are MM-DD or YYYY-MM-DD and days are weekday names: " + e.getMessage());
        }
    }

    private record Day(Optional<LocalDate> once, Optional<MonthDay> yearly) {}

    private static Day parse(String text) {
        String t = text.trim();
        if (t.isEmpty()) return new Day(Optional.empty(), Optional.empty());
        if (t.length() > 5) return new Day(Optional.of(LocalDate.parse(t)), Optional.empty());
        return new Day(Optional.empty(), Optional.of(MonthDay.parse("--" + t)));
    }

    /**
     * Whether the chapter is open on {@code date}.
     */
    public boolean isOpen(LocalDate date) {
        if (!days.isEmpty()) {
            boolean match = false;
            for (String d : days) if (DayOfWeek.valueOf(d.toUpperCase(Locale.ROOT)) == date.getDayOfWeek()) match = true;
            if (!match) return false;
        }
        Day start = parse(from);
        Day end = parse(until);
        if (start.once.isPresent() && date.isBefore(start.once.get())) return false;
        if (end.once.isPresent() && date.isAfter(end.once.get())) return false;
        if (start.yearly.isPresent() || end.yearly.isPresent()) {
            MonthDay today = MonthDay.from(date);
            MonthDay a = start.yearly.orElse(MonthDay.of(1, 1));
            MonthDay b = end.yearly.orElse(MonthDay.of(12, 31));
            boolean inside = a.isAfter(b) ? !today.isBefore(a) || !today.isAfter(b) : !today.isBefore(a) && !today.isAfter(b);
            if (!inside) return false;
        }
        return true;
    }

    /**
     * The next day it's open, looking up to a year ahead, or empty if it never opens again.
     */
    public Optional<LocalDate> nextOpen(LocalDate today) {
        for (int i = 0; i <= 366; i++) {
            LocalDate d = today.plusDays(i);
            if (isOpen(d)) return Optional.of(d);
        }
        return Optional.empty();
    }
}
