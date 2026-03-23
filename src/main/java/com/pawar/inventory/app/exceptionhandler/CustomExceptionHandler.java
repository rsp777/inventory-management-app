package com.pawar.inventory.app.exceptionhandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.exception.ErrorResponse;
import com.pawar.inventory.app.exception.base.BaseException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CustomExceptionHandler {

	private final static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

	MenuService menuService;

	CustomExceptionHandler(MenuService menuService) {
		this.menuService = menuService;
	}

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        logger.error("Base exception: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            ex.getHttpStatus().value(),
            ex.getHttpStatus().getReasonPhrase(),
            ex.getMessage(),
            ex.getTimestamp()
        );

        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

	@ModelAttribute("user_name")
	public String getUserName(HttpSession httpSession) {
        String decodedToken = SessionUtil.getSessionToken(httpSession);
		if (decodedToken != null) {
			try {
				DecodedJWT decodedJWT = JWT.decode(decodedToken);
				String subject = decodedJWT.getSubject();
				if (subject != null && !subject.isEmpty()) {
					String user_name = subject.split("\\|")[0];
					logger.info("Username : " + user_name);
					return user_name;
				}
			} catch (Exception e) {
				logger.warn("Could not extract username from token: " + e.getMessage());
			}
		}
		return "unknown user";
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Method argument validation failed");
        
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}