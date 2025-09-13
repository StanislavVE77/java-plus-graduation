package ru.practicum.request.exception;

public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException(Long compId) {
        super("Compilation with id=" + compId + " was not found");
    }
}
