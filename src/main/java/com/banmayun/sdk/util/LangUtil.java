package com.banmayun.sdk.util;

import java.lang.reflect.*;

public class LangUtil {
  public static RuntimeException mkAssert(String messagePrefix, Throwable cause) {
    RuntimeException ae = new RuntimeException(messagePrefix + ": "
        + cause.getMessage());
    ae.initCause(cause);
    return ae;
  }

  /* not supported before sdk 8, copy from java source */
  @SuppressWarnings("unchecked")
  public static <T, U> T[] copyOf(U[] original, int newLength,
      Class<? extends T[]> newType) {
    T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
        : (T[]) Array.newInstance(newType.getComponentType(), newLength);
    System
        .arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] copyOf(T[] original, int newLength) {
    return (T[]) copyOf(original, newLength, original.getClass());
  }

  public static <T> T[] arrayConcat(T[] a, T[] b) {
    if (a == null)
      throw new IllegalArgumentException("'a' can't be null");
    if (b == null)
      throw new IllegalArgumentException("'b' can't be null");

    T[] r = copyOf(a, a.length + b.length);
    System.arraycopy(b, 0, r, a.length, b.length);
    return r;
  }
}
