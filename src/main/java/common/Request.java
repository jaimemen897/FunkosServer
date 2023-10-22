package common;

public record Request<T>(Type type, T content, String token, String createdAt) {
    public enum Type {
        LOGIN, GETALL, GETBYCODE, GETBYMODELO, GETBYRELEASEDATE, POST, PUT, DELETE, EXIT
    }
}
