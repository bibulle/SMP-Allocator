package com.em.cemetery.exception;

public class HoleInThePathException extends PathNotClearException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3130283723048271435L;

	@Override
	public String getMessage() {
		return "there is hole in the ground...";
	}
}
