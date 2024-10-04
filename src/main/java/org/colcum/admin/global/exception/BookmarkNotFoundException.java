package org.colcum.admin.global.exception;

public class BookmarkNotFoundException extends RuntimeException {

    public BookmarkNotFoundException() {
        super("해당 북마크를 찾지 못했습니다.");
    }

    public BookmarkNotFoundException(String message) {
        super(message);
    }

    public BookmarkNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookmarkNotFoundException(Throwable cause) {
        super(cause);
    }

}
