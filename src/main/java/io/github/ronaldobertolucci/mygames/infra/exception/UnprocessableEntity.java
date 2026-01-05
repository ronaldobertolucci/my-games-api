package io.github.ronaldobertolucci.mygames.infra.exception;

public class UnprocessableEntity extends RuntimeException {
    public UnprocessableEntity(String message) {
        super(message);
    }
}
