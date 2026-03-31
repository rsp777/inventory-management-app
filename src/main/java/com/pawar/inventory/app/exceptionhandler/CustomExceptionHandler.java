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

import com.pawar.inventory.app.exception.ErrorResponse;
import com.pawar.inventory.app.exception.base.BaseException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.base.TokenService;
import com.pawar.inventory.app.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CustomExceptionHandler {

	private final static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    private final MenuService menuService;
    private final TokenService tokenService;

    CustomExceptionHandler(MenuService menuService, TokenService tokenService) {
		this.menuService = menuService;
        this.tokenService = tokenService;
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
        String sessionUserName = SessionUtil.getSessionUserName(httpSession);
        if (sessionUserName != null && !sessionUserName.isBlank()) {
            return sessionUserName;
        }

        String token = SessionUtil.getSessionToken(httpSession);
        if (token != null && !token.isBlank()) {
            String decodedUserName = tokenService.getUserName(token);
            if (decodedUserName != null && !decodedUserName.isBlank()) {
                return decodedUserName;
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