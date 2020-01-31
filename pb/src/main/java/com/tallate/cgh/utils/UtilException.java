package com.tallate.cgh.utils;

/**
 * 普通异常类，在应用中调用时会强制要求捕捉
 * @author tallate
 */
public class UtilException extends Exception {

  public UtilException(String msg) {
    super(msg);
  }

  public UtilException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public UtilException(Throwable cause) {
    super(cause);
  }
}
