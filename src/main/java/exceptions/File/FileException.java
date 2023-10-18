package exceptions.File;

public abstract class FileException extends RuntimeException {
    public FileException(String message) {
        super(message);
    }
}
