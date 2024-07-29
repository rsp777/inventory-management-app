package com.pawar.inventory.app.exceptionhandler;

import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pawar.inventory.app.exception.UnauthorizedException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CustomExceptionHandler {

    private final static Logger logger = Logger.getLogger(CustomExceptionHandler.class.getName());

    MenuService menuService;

    CustomExceptionHandler(MenuService menuService) {
        this.menuService = menuService;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        // Customize the response (e.g., return an error message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }

    @ModelAttribute("user_name")
    public String getUserName(HttpSession httpSession) {
        // String decodedToken = (String)
        // redirectAttributes.getFlashAttributes().get("decodedtoken");
        String decodedToken = (String) httpSession.getAttribute("decodedtoken");
        if (decodedToken != null) {
            DecodedJWT decodedJWT = JWT.decode(decodedToken);
            String[] decodedString = decodedJWT.getSubject().split("\\|");
            String user_name=decodedString[0];
//            for (int i = 0; i < decodedString.length - 1; i++) {
//               if (!decodedString[i].contains("Role")) {
//            	   logger.info("decodedString["+i+"] : "+decodedString[i]);
//            	   user_name = decodedString[i];
//			  }
//            }
            logger.info("Username : "+user_name);
            return user_name;
        } else {
            return "unknown user";
        }
    }

    @ModelAttribute("logout_url")
    public Menu getLogoutAuthUrl(HttpSession httpSession) {
        Menu menuLogout = menuService.getMenu("Logout");
        return menuLogout;
    }
    @ModelAttribute("settings_url")
    public Menu getSettingsUrl(HttpSession httpSession) {
        Menu menuLogout = menuService.getMenu("Settings");
        return menuLogout;
    }
}