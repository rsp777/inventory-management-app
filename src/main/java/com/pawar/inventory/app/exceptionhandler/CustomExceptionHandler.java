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
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pawar.inventory.app.exception.DuplicateResourceException;
import com.pawar.inventory.app.exception.ErrorResponse;
import com.pawar.inventory.app.exception.ResourceNotFoundException;
import com.pawar.inventory.app.exception.UnauthorizedException;
import com.pawar.inventory.app.exception.ValidationException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CustomExceptionHandler {

	private final static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

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
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		if (decodedToken != null) {
			DecodedJWT decodedJWT = JWT.decode(decodedToken);
			String[] decodedString = decodedJWT.getSubject().split("\\|");
			String user_name = decodedString[0];
			logger.info("Username : " + user_name);
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

	@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        logger.error("Duplicate resource: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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