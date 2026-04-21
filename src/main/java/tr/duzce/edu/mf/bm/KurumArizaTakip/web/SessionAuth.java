package tr.duzce.edu.mf.bm.KurumArizaTakip.web;

import jakarta.servlet.http.HttpSession;

public final class SessionAuth {

    private SessionAuth() {
    }

    public static final String USER_ID = "AUTH_USER_ID";

    public static Long getUserId(HttpSession session) {
        Object v = session.getAttribute(USER_ID);
        if (v instanceof Long id) return id;
        if (v instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static void setUserId(HttpSession session, Long userId) {
        session.setAttribute(USER_ID, userId);
    }

    public static void clear(HttpSession session) {
        session.removeAttribute(USER_ID);
    }
}

