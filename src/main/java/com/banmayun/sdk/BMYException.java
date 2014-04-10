package com.banmayun.sdk;

import java.io.IOException;

import com.banmayun.sdk.core.ErrorResponse;

public class BMYException extends Exception {

  private static final long serialVersionUID = 0L;

  public BMYException(String message) {
    super(message);
  }

  public BMYException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public static final class MeePoServerResponseException extends BMYException{
    private static final long serialVersionUID = 1L;
    public ErrorResponse response;

    public MeePoServerResponseException(ErrorResponse response) {
      super("Server Error Response");
      this.response = response;
    }
    
  }
  
  //403
  public static final class AccessDenied extends BMYException{

    private static final long serialVersionUID = 0;

    public AccessDenied(String message) {
      super(message);
    }
  }
  
  //404
  public static final class NotFound extends BMYException{
    private static final long serialVersionUID = 0;
    public NotFound(String message){
      super(message);
    }
  }
  
  //409
  public static final class AlreadyExists extends BMYException{
    private static final long serialVersionUID = 0;
    public AlreadyExists(String message){
      super(message);
    }
  }
  
  //422
  public static final class UnacceptableRequest extends BMYException{
    private static final long serialVersionUID = 0;
    public UnacceptableRequest(String message){
      super(message);
    }
  }
  
  //424
  public static final class OperationNotAllowed extends BMYException{
    private static final long serialVersionUID = 0;
    public OperationNotAllowed(String message){
      super(message);
    }
  }

  public static final class ServerError extends BMYException {
    public ServerError(String message) {
      super(message);
    }
    public static final long serialVersionUID = 0;
  }

  //503
  public static final class RetryLater extends BMYException {
    public RetryLater(String message) {
      super(message);
    }

    public static final long serialVersionUID = 0;
  }
  
  //507 
  public static final class QuotaOutage extends BMYException{
    public QuotaOutage(String message){
      super(message);
    }
    public static final long serialVersionUID = 0;
  }

  public static abstract class ProtocolError extends BMYException {
    public ProtocolError(String message) {
      super(message);
    }

    public ProtocolError(String message, Throwable cause) {
      super(message, cause);
    }

    public static final long serialVersionUID = 0;
  }

  // 400
  public static final class BadRequest extends ProtocolError {
    public BadRequest(String message) {
      super(message);
    }
    public static final long serialVersionUID = 0;
  }

  public static class BadResponse extends ProtocolError {
    public BadResponse(String message) {
      super(message);
    }

    public BadResponse(String message, Throwable cause) {
      super(message, cause);
    }

    public static final long serialVersionUID = 0;
  }

  public static class BadResponseCode extends BadResponse {
    public final int statusCode;

    public BadResponseCode(String message, int statusCode) {
      super(message);
      this.statusCode = statusCode;
    }

    public BadResponseCode(String message, int statusCode, Throwable cause) {
      super(message, cause);
      this.statusCode = statusCode;
    }

    public static final long serialVersionUID = 0;
  }

  public static final class NetworkIO extends BMYException {
    public final IOException underlying;

    public NetworkIO(IOException underlying) {
      super(underlying.toString(), underlying);
      this.underlying = underlying;
    }

    public static final long serialVersionUID = 0;
  }

  //401
  public static final class InvalidAccessToken extends BMYException {
    public InvalidAccessToken(String message) {
      super(message);
    }

    public static final long serialVersionUID = 0;
  }

}
