package com.apherox.kafka.exception;

import lombok.Getter;

/**
 * @author apherox
 */
public class SongRetrievalException extends Exception {

	@Getter
	private final int errorCode;

	public SongRetrievalException(int code, String message) {
		super(message);
		this.errorCode = code;
	}

}
