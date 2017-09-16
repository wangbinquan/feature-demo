package org.wbq.cglib;

public class SourceClass {
    public String fun1(String str){
        return str;
    }

    public ClassA fun2(ClassA clazz){
        clazz.setA("abc");
        return clazz;
    }

    @Override
    public String toString() {
        return "##Source##" + getClass().hashCode();
    }
}
