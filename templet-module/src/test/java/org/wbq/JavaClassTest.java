package org.wbq;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class JavaClassTest {
    private JavaClass javaClass = null;

    @Before
    public void init() {
        this.javaClass = new JavaClass();
    }

    @Test
    public void testJava() {
        assertEquals("hello java", this.javaClass.hello());
    }
}
