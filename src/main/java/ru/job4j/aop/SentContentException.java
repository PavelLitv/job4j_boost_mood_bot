package ru.job4j.aop;

public class SentContentException extends RuntimeException {
    public SentContentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}