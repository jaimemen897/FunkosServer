package exceptions.bbdd;

public class BDException extends RuntimeException {
    public BDException(String message) {
        super(message);
    }
}
