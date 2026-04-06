package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;
import com.pawar.inventory.app.exception.base.BaseException;

public class MenuNotFoundException extends BaseException {
	public MenuNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND, "MENU_NOT_FOUND");
    }
}
