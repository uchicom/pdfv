// (C) 2014 uchicom
package com.uchicom.pdfv.util;

/**
 * @author Shigeki Uchiyama
 */
public class Pipe {
  public static void main(String[] args) {
    Pipe pipe = new Pipe(5);
    pipe.addL("a");
    for (Object obj : pipe.toArray()) {
      System.out.print(obj);
    }
    System.out.println("");
    pipe.addR("b");
    for (Object obj : pipe.toArray()) {
      System.out.print(obj);
    }
    System.out.println("");
    pipe.addL("c");
    for (Object obj : pipe.toArray()) {
      System.out.print(obj);
    }
    System.out.println("");
    pipe.addL("d");
    for (Object obj : pipe.toArray()) {
      System.out.print(obj);
    }
    System.out.println("");
    pipe.addR("e");
    for (Object obj : pipe.toArray()) {
      System.out.print(obj);
    }
    System.out.println("");
    pipe.addR("f");
    for (Object obj : pipe.toArray()) {
      System.out.print(obj);
    }
    System.out.println("");
    pipe.addL("g");
    for (Object obj : pipe.toArray()) {
      System.out.print(obj);
    }
    System.out.println("");
  }

  private int size;
  private int rIndex;
  private int lIndex;
  private Object[] objects;
  private int length;

  public Pipe(int length) {
    this.length = length;
    objects = new Object[length];
  }

  public void addR(Object obj) {
    if (objects[rIndex] == null) {
      if (size == 0) {
        lIndex = (lIndex + length - 1) % length;
      }
    } else {
      lIndex = (lIndex + 1) % length;
    }
    objects[rIndex] = obj;
    rIndex = (rIndex + 1) % length;
    size++;
  }

  public void addL(Object obj) {
    if (objects[lIndex] == null) {
      if (size == 0) {
        rIndex = (lIndex + 1) % length;
      }
    } else {
      rIndex = (rIndex + length - 1) % length;
    }
    objects[lIndex] = obj;
    lIndex = (lIndex + length - 1) % length;
    size++;
  }

  public int getSize() {
    return size > length ? length : size;
  }

  public Object[] toArray() {
    System.out.println(lIndex + ":" + rIndex);
    int max = getSize();
    Object[] ret = new Object[max];
    int start = (lIndex + 1) % length;
    for (int i = start; i < start + max; i++) {
      ret[i - start] = objects[i % length];
    }
    return ret;
  }
}
