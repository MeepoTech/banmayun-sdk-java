package com.banmayun.sdk;

import java.io.IOException;
import java.io.OutputStream;

public final class NoThrowOutputStream extends OutputStream{
  private final OutputStream underlying;
  private long bytesWritten = 0;
  
  public NoThrowOutputStream(OutputStream underlying){
    this.underlying = underlying;
  }
  
  @Override
  public void close(){
    throw new UnsupportedOperationException("don't call close()");
  }
  
  @Override
  public void flush(){
    try{
      underlying.flush();
    }catch(IOException e){
      throw new HiddenException(e);
    }
  }
  
  @Override
  public void write(byte[] b, int off, int len){
    try{
      bytesWritten+=len;
      underlying.write(b, off, len);
    }catch(IOException e){
      throw new HiddenException(e);
    }
  }
  
  @Override
  public void write(byte[] b){
    try{
      bytesWritten+=b.length;
      underlying.write(b);
    }catch(IOException e){
      throw new HiddenException(e);
    }
  }
  
  @Override
  public void write(int b) throws IOException {
    try{
      bytesWritten+=1;
      underlying.write(b);
    }catch(IOException e){
      throw new HiddenException(e);
    }
  }
  
  public static final class HiddenException extends RuntimeException{
    public final IOException underlying;
    public HiddenException(IOException underlying){
      super(underlying);
      this.underlying = underlying;
    }
    public static final long serialVersionUID = 0L;
  }

  public long getBytesWritten(){
    return bytesWritten;
  }
}
