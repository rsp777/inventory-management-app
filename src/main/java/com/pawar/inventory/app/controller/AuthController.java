package com.pawar.inventory.app.controller;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.dto.MenuSignInRequestDTO;
import com.pawar.inventory.app.exception.UnauthorizedException;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Authentication and Authorization Controller
 * Handles user login, logout, and session management
 */
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final MenuService menuService;
    private final MenuAccessService menuAccessService;

    public AuthController(MenuService menuService, MenuAccessService menuAccessService) {
        this.menuService = menuService;
        this.menuAccessService = menuAccessService;
    }

    /**
     * Displays the index/login page
     */
    @GetMapping("/index")
    public String index(Model model) {
        return "index";
    }

    /**
     * Handles user sign-in with credentials
     */
    @PostMapping("/signIn")
    public String signIn(Model model, HttpSession httpSession, RedirectAttributes redirectAttributes,
            @Valid @ModelAttribute MenuSignInRequestDTO requestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("invalidCredMessage", "Invalid username or password");
            return "redirect:/api/auth/index";
        }

        String username = requestDTO.getUsername();
        String password = requestDTO.getPassword();

        try {
            logger.info("Sign-in attempt for user: {}", username);

            // Authenticate with external service
            String signInResponse = menuService.signIn(username, password);

            if (signInResponse == null || signInResponse.isEmpty()) {
                throw new UnauthorizedException("Invalid credentials for user: " + username);
            }

            // Extract token from JSON response if it's wrapped
            String tokenToStore = extractTokenFromResponse(signInResponse);

            // Store token in session
            httpSession.setAttribute(AppConstants.SessionAttribute.DECODED_TOKEN, tokenToStore);
            httpSession.setAttribute(AppConstants.SessionAttribute.JWT_TOKEN, tokenToStore);

            String userName = menuAccessService.getUserName(tokenToStore);
            httpSession.setAttribute(AppConstants.SessionAttribute.USER_NAME, userName);
            httpSession.setAttribute(AppConstants.SessionAttribute.USER_NAME_LEGACY, userName);

            logger.info("Successful sign-in for user: {}", username);
            return "redirect:/api/showMenu";

        } catch (UnauthorizedException unauthorized) {
            logger.error("Unauthorized access for user: {}", username);
            redirectAttributes.addFlashAttribute("invalidCredMessage", "Invalid username or password");
            return "redirect:/api/auth/index";

        } catch (JWTDecodeException jwtError) {
            String errorMsg = "Token decoding error: " + jwtError.getMessage();
            logger.error(errorMsg);
            redirectAttributes.addFlashAttribute("invalidCredMessage", errorMsg);
            return "redirect:/api/auth/index";

        } catch (JsonProcessingException jsonError) {
            logger.error("JSON processing error during sign-in", jsonError);
            redirectAttributes.addFlashAttribute("invalidCredMessage", "Server error processing response");
            return "redirect:/api/auth/index";

        } catch (IOException error) {
            logger.error("HTTP client error during sign-in", error);
            redirectAttributes.addFlashAttribute("invalidCredMessage", "Server communication error");
            return "redirect:/api/auth/index";

        } catch (Exception error) {
            logger.error("Unexpected error during sign-in", error);
            redirectAttributes.addFlashAttribute("invalidCredMessage", "Unexpected server error");
            return "redirect:/api/auth/index";
        }
    }

    /**
     * Extracts JWT token from auth service response.
     * The auth service returns either:
     * <ul>
     * <li>Raw JWT token string</li>
     * <li>JSON object {"token": "..."}</li>
     * </ul>
     *
     * @param response Raw response from auth service
     * @return Extracted token string
     */
    private String extractTokenFromResponse(String response) throws JsonProcessingException {
        // If response is wrapped in JSON, extract the token field
        if (response != null && response.trim().startsWith("{")) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                java.util.Map<String, Object> map = mapper.readValue(response, java.util.Map.class);
                Object token = map.get("token");
                if (token == null) {
                    token = map.get("access_token");
                }

                if (token != null) {
                    logger.debug("Extracted token from JSON response");
                    return token.toString();
                }

                logger.warn("JSON response does not contain token or access_token field");
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse response as JSON: {}", e.getMessage());
                throw e;
            }
        }
        // Return response as-is if it's already the token (plain JWT or pipe-separated)
        logger.debug("Using response as token directly");
        return response;
    }

    /**
     * Handles user logout
     */
    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes, HttpSession httpSession) {
        try {
            logger.info("Logout attempt");

            String response = menuService.signout(httpSession);
            logger.info("Logout response: {}", response);

            httpSession.invalidate();
            logger.info("Session invalidated");

            redirectAttributes.addFlashAttribute("logoutMessage", "Successfully logged out");
            return "redirect:/api/auth/index";

        } catch (IOException error) {
            logger.error("Error during logout", error);
            httpSession.invalidate();
            return "redirect:/api/auth/index";
        }
    }
}
