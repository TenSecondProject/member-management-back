package org.colcum.admin.global.Error;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException() {
        super("해당 댓글을 찾지 못했습니다.");
    }

    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentNotFoundException(Throwable cause) {
        super(cause);
    }

}
