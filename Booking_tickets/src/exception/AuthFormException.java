package exception;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * error ที่ส่งกลับไปให้หน้าจอ ตอน Login / Sign up ไม่ผ่าน
 * เก็บ error ได้ช่องละไม่เกิน 1 ตัว (ช่องชื่อ 1, ช่องรหัส 1)
 */
public class AuthFormException extends Exception {

    // key = ช่อง, value = error ของช่องนั้น
    private final Map<AuthField, AuthError> errors = new EnumMap<>(AuthField.class);

    /**
     * @param list error ที่เจอ อย่างน้อย 1 ตัว และห้ามมี 2 ตัวในช่องเดียวกัน
     */
    public AuthFormException(AuthError... list) {
        super(joinMessages(list));
        if (list.length == 0) {
            throw new IllegalArgumentException("ต้องมี error อย่างน้อย 1 ตัว");
        }
        for (AuthError error : list) {
            if (errors.containsKey(error.field())) {
                throw new IllegalArgumentException("ช่อง " + error.field() + " มี error ซ้ำ");
            }
            errors.put(error.field(), error);
        }
    }

    /** @return error ของช่องนี้ หรือว่างถ้าช่องนี้ไม่ผิด (ใช้ในเทส) */
    public Optional<AuthError> errorFor(AuthField field) {
        return Optional.ofNullable(errors.get(field));
    }

    /** @return ข้อความที่จะขึ้นใต้ช่องนี้ หรือ "" ถ้าช่องนี้ไม่ผิด (ใช้ในหน้าจอ) */
    public String messageFor(AuthField field) {
        AuthError error = errors.get(field);
        return error == null ? "" : error.message();
    }

    // รวมข้อความทุกตัวเป็นบรรทัดเดียว ให้ getMessage() อ่านรู้เรื่องเวลา debug
    private static String joinMessages(AuthError[] list) {
        StringBuilder text = new StringBuilder();
        for (AuthError error : list) {
            if (text.length() > 0) text.append(", ");
            text.append(error.message());
        }
        return text.toString();
    }
}
