package org.colcum.admin.global.exception;

public class EmojiNotFoundException extends RuntimeException {

    public EmojiNotFoundException() {
        super("해당 이모지를 찾지 못했습니다.");
    }

    public EmojiNotFoundException(String message) {
        super(message);
    }

    public EmojiNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmojiNotFoundException(Throwable cause) {
        super(cause);
    }

}