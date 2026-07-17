package demo.common;

public class Result<T> {
    private final int code;
    private final String message;
    private final T data;
    private final long timestamp;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success(T data) { return new Result<T>(200, "success", data); }
    public static <T> Result<T> failure(int code, String message) { return new Result<T>(code, message, null); }
    public static <T> Result<T> error(int code, String message) { return failure(code, message); }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public long getTimestamp() { return timestamp; }
}
