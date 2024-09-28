package com.apherox.kafka.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Exception handler that maps exceptions to HTTP error status codes 400+ or 500
 *
 * @author apherox
 */
@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationErrorException(IllegalArgumentException e) {
		log.info("ValidationErrorException received, message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationErrorException(EnumConstantNotPresentException e) {
		log.info("ValidationErrorException received, message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMissingServletRequestPartException(MissingServletRequestPartException e) {
		log.info("MissingServletRequestPartException received, message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidFormatException(InvalidFormatException e) {
		log.info("InvalidFormatException has occurred, with message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.info("HttpMessageNotReadableException has occurred, with message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		List<ErrorResponse.SubError> subErrors = e.getBindingResult().getFieldErrors()
				.stream()
				.map(fieldError -> new ErrorResponse.SubError(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage()))
				.toList();
		log.info("Method arguments not valid. Validation errors: {}", subErrors);
		return new ErrorResponse(ErrorCode.BAD_REQUEST,  subErrors.toString());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
		List<ErrorResponse.SubError> subErrors = e.getConstraintViolations()
				.stream()
				.map(constraintError -> new ErrorResponse.SubError(constraintError.getRootBeanClass().getName(), constraintError.getPropertyPath().toString(), constraintError.getMessage()))
				.toList();
		log.info("Constraint arguments not valid. Validation errors: {}", subErrors);
		return new ErrorResponse(ErrorCode.BAD_REQUEST, "Invalid input parameters");
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
		log.info("Resource not found exception has occurred with message {}", exception.getMessage());
		return new ErrorResponse(ErrorCode.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
		log.info("Operation not permitted, message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.FORBIDDEN, e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		log.warn("Request method not supported message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.BAD_REQUEST, "Request method not supported");
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleIllegalStateException(IllegalStateException exception) {
		log.info("Operation not acceptable, message: {}", exception.getMessage());
		return new ErrorResponse(ErrorCode.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public ErrorResponse handleHttpRequestMethodNotSupportedException(HttpMediaTypeNotSupportedException e) {
		log.warn("Request media type not supported message: {}", e.getMessage());
		return new ErrorResponse(ErrorCode.BAD_REQUEST, "Unsupported media type");
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleServerError(Exception e) {
		log.error("Unhandled exception, message: {} ", e.getMessage());
		log.error("Unhandled exception, stacktrace: ", e);
		return new ErrorResponse(ErrorCode.SERVER_ERROR, "Server error");
	}

}
