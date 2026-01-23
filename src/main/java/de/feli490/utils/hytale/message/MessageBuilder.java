package de.feli490.utils.hytale.message;

import com.hypixel.hytale.server.core.Message;
import java.awt.Color;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessageBuilder {

    private final String mainColor;
    private final String secondaryColor;
    private final String errorColor;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")
                                                               .withZone(ZoneId.systemDefault());
    private Message message;

    public MessageBuilder(Message base, String mainColor, String secondaryColor, String errorColor) {
        this.mainColor = mainColor;
        this.secondaryColor = secondaryColor;
        this.errorColor = errorColor;
        message = base;
    }

    public MessageBuilder(Message base) {
        this(base, "#00ff00", "#ffffff", "#ff0000");
    }

    public MessageBuilder() {
        this(Message.empty());
    }

    /**
     * Sets the time formatter pattern for the message builder. Every following timestamp will be formatted with this pattern.
     *
     * @param pattern
     *         The pattern to use for formatting timestamps.
     * @return The message builder instance for method chaining.
     * @see DateTimeFormatter#ofPattern(String)
     */
    public MessageBuilder timestampFormatter(String pattern) {
        this.timeFormatter = DateTimeFormatter.ofPattern(pattern)
                                              .withZone(ZoneId.systemDefault());
        return this;
    }

    public MessageBuilder mainTimestamp(long timestamp) {
        return timestamp(timestamp, mainColor);
    }

    public MessageBuilder secondTimestamp(long timestamp) {
        return timestamp(timestamp, secondaryColor);
    }

    public MessageBuilder errorTimestamp(long timestamp) {
        return timestamp(timestamp, errorColor);
    }

    public MessageBuilder timestamp(long timestamp, String color) {
        return add(timeFormatter.format(Instant.ofEpochMilli(timestamp)), color);
    }

    public MessageBuilder timestamp(long timestamp, Color color) {
        return add(timeFormatter.format(Instant.ofEpochMilli(timestamp)), color);
    }

    public MessageBuilder main(String message) {
        return add(message, mainColor);
    }

    public MessageBuilder second(String message) {
        return add(message, secondaryColor);
    }

    public MessageBuilder error(String message) {
        return add(message, errorColor);
    }

    public MessageBuilder add(String message, String color) {
        this.message = Message.join(this.message,
                                    Message.raw(message)
                                           .color(color));
        return this;
    }

    public MessageBuilder add(String message, Color color) {
        this.message = Message.join(this.message,
                                    Message.raw(message)
                                           .color(color));
        return this;
    }

    public Message build() {
        return message;
    }

    public String buildString() {
        return message.toString();
    }
}
