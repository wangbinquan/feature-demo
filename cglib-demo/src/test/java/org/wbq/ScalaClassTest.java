package org.wbq;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ScalaClassTest {
    private ScalaCallJava scalaCallJava = null;

    @Before
    public void init() {
        this.scalaCallJava = new ScalaCallJava();
    }

    @Test
    public void testScalaClass() {
        assertEquals("java static", this.scalaCallJava.callJava());
    }

    @Test
    public void testScalaObject() {
        assertEquals("hello java", ScalaCallJava$.MODULE$.callJava());
    }
}
