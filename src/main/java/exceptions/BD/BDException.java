package exceptions.BD;

public abstract class BDException extends RuntimeException {
    public BDException(String message) {
        super(message);
    }
}
