package dk.bank.app.exceptions;

public class BadRequestException extends Exception {

    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }
}