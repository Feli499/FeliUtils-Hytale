package de.feli490.utils.hytale.message;

import com.hypixel.hytale.server.core.Message;
import java.awt.Color;
import java.util.Objects;

public class MessageBuilderFactory {

    private String mainColor;
    private String secondaryColor;
    private String errorColor;
    private String timeStampFormatter;
    private Message base;

    public MessageBuilderFactory(Message base, String mainColor, String secondaryColor, String errorColor, String timeStampFormatter) {
        this.base = Objects.requireNonNull(base);
        this.mainColor = Objects.requireNonNull(mainColor);
        this.secondaryColor = Objects.requireNonNull(secondaryColor);
        this.errorColor = Objects.requireNonNull(errorColor);
        this.timeStampFormatter = timeStampFormatter;
    }

    public MessageBuilderFactory(Message base, String mainColor, String secondaryColor, String errorColor) {
        this(base, mainColor, secondaryColor, errorColor, null);
    }

    public MessageBuilderFactory(String mainColor, String secondaryColor, String errorColor) {
        this(Message.empty(), mainColor, secondaryColor, errorColor);
    }

    public MessageBuilderFactory(Message base, String mainColor, String secondaryColor) {
        this(base, mainColor, secondaryColor, "#ff0000", null);
    }

    public MessageBuilderFactory(String mainColor, String secondaryColor) {
        this(Message.empty(), mainColor, secondaryColor);
    }

    public String getMainColor() {
        return mainColor;
    }

    public void setMainColor(String mainColor) {
        this.mainColor = mainColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getErrorColor() {
        return errorColor;
    }

    public void setErrorColor(String errorColor) {
        this.errorColor = errorColor;
    }

    public String getTimeStampFormatter() {
        return timeStampFormatter;
    }

    public void setTimeStampFormatter(String timeStampFormatter) {
        this.timeStampFormatter = timeStampFormatter;
    }

    public Message getBase() {
        return base;
    }

    public void setBase(Message base) {
        this.base = base;
    }

    public Message error(String message) {
        return color(message, errorColor);
    }

    public Message main(String message) {
        return color(message, mainColor);
    }

    public Message second(String message) {
        return color(message, secondaryColor);
    }

    public Message color(String message, String color) {
        return builder().add(message, color)
                        .build();
    }

    public Message color(String message, Color color) {
        return builder().add(message, color)
                        .build();
    }

    public MessageBuilderFactory copy() {
        return new MessageBuilderFactory(base, mainColor, secondaryColor, errorColor, timeStampFormatter);
    }

    public MessageBuilder builder() {
        MessageBuilder messageBuilder = new MessageBuilder(base, mainColor, secondaryColor, errorColor);
        if (timeStampFormatter != null)
            messageBuilder.timestampFormatter(timeStampFormatter);
        return messageBuilder;
    }
}
