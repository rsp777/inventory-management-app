package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;
import com.pawar.inventory.app.exception.base.BaseException;

public class ParentMenuNotFoundException extends BaseException {
	public ParentMenuNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND, "PARENT_MENU_NOT_FOUND");
	}
}
