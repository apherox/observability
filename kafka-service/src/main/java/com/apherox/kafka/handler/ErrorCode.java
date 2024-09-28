package com.apherox.kafka.handler;

/**
 * Error code enum. Used to define detail error codes returned to the client.
 *
 * @author apherox
 */
public enum ErrorCode {

	//400
	BAD_REQUEST,

	//401
	UNAUTHORIZED,

	//403
	FORBIDDEN,

	// 404
	NOT_FOUND,

	// 409
	CONFLICT,

	//500
	SERVER_ERROR,

	// 503
	SERVICE_UNAVAILABLE;

}
