package com.apherox.kafka.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rest error model used to map exceptions into REST HTTP status
 * 
 * @author apherox
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class ErrorResponse {
	
	/**
	 * Unique error code
	 */
	private ErrorCode errorCode;
  
	/**
	 * This is user friendly error message
	 */
	private String message;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonInclude(value = Include.NON_EMPTY)
	public static class SubError {

		/**
		 * Shows object or resource in error
		 */
		private String resource;

		/**
		 * Explicitly refers to a field/element in error
		 */
		private String field;

		/**
		 * Validation error coming from for example javax validation
		 */
		private String reason;

	}
	
}
