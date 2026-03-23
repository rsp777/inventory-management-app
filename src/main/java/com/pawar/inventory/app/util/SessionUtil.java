package com.pawar.inventory.app.util;

import com.pawar.inventory.app.config.AppConstants;

import jakarta.servlet.http.HttpSession;

public final class SessionUtil {

    private SessionUtil() {
        // Utility class
    }

    public static String getSessionToken(HttpSession session) {
        String token = (String) session.getAttribute(AppConstants.SessionAttribute.DECODED_TOKEN);
        if (token == null) {
            token = (String) session.getAttribute(AppConstants.SessionAttribute.JWT_TOKEN);
        }
        return token;
    }

	public static String getSessionUserName(HttpSession session) {
		Object userName = session.getAttribute(AppConstants.SessionAttribute.USER_NAME);
		if (userName == null) {
			userName = session.getAttribute(AppConstants.SessionAttribute.USER_NAME_LEGACY);
		}
		return userName == null ? null : userName.toString();
	}
}