package org.wbq;

public class JavaCallScala {
    public String callScalaObject(){
//        return "scala object";
        //Can't Call Scala
        return ScalaClass$.MODULE$.helloObject();
    }

    public String callScalaClass(){
        return (new ScalaClass()).hello();
    }

}
