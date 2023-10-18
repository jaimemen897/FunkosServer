package common;

public record Request<T>(Type type, T content, String token, String createdAt) {
    public enum Type{
        FINDALL, FINDBYCODE, FINDBYMODELO, FINDBYRELEASEDATE, INSERT, UPDATE, DELETE, EXIT
    }
}
