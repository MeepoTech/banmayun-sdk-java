package com.banmayun.sdk.util;

public abstract class Dumpable {
  public final String toString(){
    StringBuilder buf = new StringBuilder();
    toString(buf);
    return buf.toString();
  }
  
  public final void toString(StringBuilder buf){
    new DumpWriter.Plain(buf).value(this);
  }
  
  public final String toStringMultiline(){
    StringBuilder buf = new StringBuilder();
    toStringMultiline(buf,0,true);
    return buf.toString();
  }
  
  public final void toStringMultiline(StringBuilder buf, int currentIndent, boolean nl)
  {
      new DumpWriter.Multiline(buf, 2, currentIndent, nl).value(this);
  }

  protected String getTypeName() { return null; }
  protected abstract void dumpFields(DumpWriter out);
}
