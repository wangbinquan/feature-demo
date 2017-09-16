package org.wbq.cglib;

import net.sf.cglib.beans.*;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.*;
import net.sf.cglib.reflect.*;
import net.sf.cglib.util.ParallelSorter;
import net.sf.cglib.util.StringSwitcher;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

//copy from http://blog.csdn.net/danchu/article/details/70238002
public class CglibTest {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Test(expected = ClassCastException.class)
    public void testFixedValue() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback(new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "Hello cglib";
            }
        });
        SampleClass proxy = (SampleClass) enhancer.create();
        LOG.info(proxy.test(null)); //拦截test，输出Hello cglib
        LOG.info(proxy.toString());
        LOG.info(proxy.getClass().toString());
        LOG.info(String.valueOf(proxy.hashCode()));//throw
    }

    @Test
    public void testInvocationHandler() throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                    return "hello cglib";
                } else {
                    if (method.getReturnType() == String.class) {
                        return "...";
                    }
                    throw new RuntimeException("Do not know what to do");
                }
            }
        });
        SampleClass proxy = (SampleClass) enhancer.create();
        Assert.assertEquals("hello cglib", proxy.test(null));
        Assert.assertNotEquals("Hello cglib", proxy.toString());//throw
    }

    @Test
    public void testCallbackFilter() throws Exception {
        Enhancer enhancer = new Enhancer();
        CallbackHelper callbackHelper = new CallbackHelper(SampleClass.class, new Class[0]) {
            @Override
            protected Object getCallback(Method method) {
                if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                    return new FixedValue() {
                        @Override
                        public Object loadObject() throws Exception {
                            return "Hello cglib";
                        }
                    };
                } else {
                    return NoOp.INSTANCE;
                }
            }
        };
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallbackFilter(callbackHelper);
        enhancer.setCallbacks(callbackHelper.getCallbacks());
        SampleClass proxy = (SampleClass) enhancer.create();
        Assert.assertEquals("Hello cglib", proxy.test(null));
        Assert.assertNotEquals("Hello cglib", proxy.toString());
        LOG.info(String.valueOf(proxy.hashCode()));
    }

    @Test(expected = IllegalStateException.class)
    public void testImmutableBean() throws Exception {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world");
        SampleBean immutableBean = (SampleBean) ImmutableBean.create(bean); //创建不可变类
        Assert.assertEquals("Hello world", immutableBean.getValue());
        bean.setValue("Hello world, again"); //可以通过底层对象来进行修改
        Assert.assertEquals("Hello world, again", immutableBean.getValue());
        immutableBean.setValue("Hello cglib"); //直接修改将throw exception
    }

    @Test
    public void testBeanGenerator() throws Exception {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("value", String.class);
        Object myBean = beanGenerator.create();
        Method setter = myBean.getClass().getMethod("setValue", String.class);
        setter.invoke(myBean, "Hello cglib");

        Method getter = myBean.getClass().getMethod("getValue");
        Assert.assertEquals("Hello cglib", getter.invoke(myBean));
    }

    @Test
    public void testBeanCopier() throws Exception {
        BeanCopier copier = BeanCopier.create(SampleBean.class, OtherSampleBean.class, false);//设置为true，则使用converter
        SampleBean myBean = new SampleBean();
        myBean.setValue("Hello cglib");
        OtherSampleBean otherBean = new OtherSampleBean();
        copier.copy(myBean, otherBean, null); //设置为true，则传入converter指明怎么进行转换
        assertEquals("Hello cglib", otherBean.getValue());
    }

    @Test
    public void testBulkBean() throws Exception {
        BulkBean bulkBean = BulkBean.create(SampleBean.class,
                new String[]{"getValue"},
                new String[]{"setValue"},
                new Class[]{String.class});
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world");
        Object[] propertyValues = bulkBean.getPropertyValues(bean);
        assertEquals(1, bulkBean.getPropertyValues(bean).length);
        assertEquals("Hello world", bulkBean.getPropertyValues(bean)[0]);
        bulkBean.setPropertyValues(bean, new Object[]{"Hello cglib"});
        assertEquals("Hello cglib", bean.getValue());
    }

    @Test
    public void testBeanMap() throws Exception {
        BeanGenerator generator = new BeanGenerator();
        generator.addProperty("username", String.class);
        generator.addProperty("password", String.class);
        Object bean = generator.create();
        Method setUserName = bean.getClass().getMethod("setUsername", String.class);
        Method setPassword = bean.getClass().getMethod("setPassword", String.class);
        setUserName.invoke(bean, "admin");
        setPassword.invoke(bean, "password");
        BeanMap map = BeanMap.create(bean);
        Assert.assertEquals("admin", map.get("username"));
        Assert.assertEquals("password", map.get("password"));
    }

    @Test
    public void testKeyFactory() throws Exception {
        SampleKeyFactory keyFactory = (SampleKeyFactory) KeyFactory.create(SampleKeyFactory.class);
        Object key = keyFactory.newInstance("foo", 42);
        Object key1 = keyFactory.newInstance("foo", 42);
        Assert.assertEquals(key, key1);//测试参数相同，结果是否相等
    }

    interface Interface1 {
        String first();
    }

    interface Interface2 {
        String second();
    }

    class Class1 implements Interface1 {
        @Override
        public String first() {
            return "first";
        }
    }

    class Class2 implements Interface2 {
        @Override
        public String second() {
            return "second";
        }
    }

    interface MixinInterface extends Interface1, Interface2 {
    }

    @Test
    public void testMixin() throws Exception {
        Mixin mixin = Mixin.create(new Class[]{Interface1.class, Interface2.class,
                MixinInterface.class}, new Object[]{new Class1(), new Class2()});
        MixinInterface mixinDelegate = (MixinInterface) mixin;
        assertEquals("first", mixinDelegate.first());
        assertEquals("second", mixinDelegate.second());
    }

    @Test
    public void testStringSwitcher() throws Exception {
        String[] strings = new String[]{"one", "two"};
        int[] values = new int[]{10, 20};
        StringSwitcher stringSwitcher = StringSwitcher.create(strings, values, true);
        assertEquals(10, stringSwitcher.intValue("one"));
        assertEquals(20, stringSwitcher.intValue("two"));
        assertEquals(-1, stringSwitcher.intValue("three"));
    }

    @Test
    public void testInterfaceMarker() throws Exception {
        Signature signature = new Signature("foo", Type.DOUBLE_TYPE, new Type[]{Type.INT_TYPE});
        InterfaceMaker interfaceMaker = new InterfaceMaker();
        interfaceMaker.add(signature, new Type[0]);
        Class iface = interfaceMaker.create();
        assertEquals(1, iface.getMethods().length);
        assertEquals("foo", iface.getMethods()[0].getName());
        assertEquals(double.class, iface.getMethods()[0].getReturnType());
    }

    interface BeanDelegate {
        String getValueFromDelegate();
    }

    @Test
    public void testMethodDelegate() throws Exception {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello cglib");
        BeanDelegate delegate = (BeanDelegate) MethodDelegate.create(bean, "getValue", BeanDelegate.class);
        assertEquals("Hello cglib", delegate.getValueFromDelegate());
    }

    public interface DelegatationProvider {
        void setValue(String value);
    }

    public class SimpleMulticastBean implements DelegatationProvider {
        private String value;

        @Override
        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void testMulticastDelegate() throws Exception {
        MulticastDelegate multicastDelegate = MulticastDelegate.create(DelegatationProvider.class);
        SimpleMulticastBean first = new SimpleMulticastBean();
        SimpleMulticastBean second = new SimpleMulticastBean();
        multicastDelegate = multicastDelegate.add(first);
        multicastDelegate = multicastDelegate.add(second);

        DelegatationProvider provider = (DelegatationProvider) multicastDelegate;
        provider.setValue("Hello world");

        assertEquals("Hello world", first.getValue());
        assertEquals("Hello world", second.getValue());
    }

    interface SampleBeanConstructorDelegate {
        Object newInstance(String value);
    }

    /**
     * 对构造函数进行代理
     *
     * @throws Exception
     */
    @Test
    public void testConstructorDelegate() throws Exception {
        SampleBeanConstructorDelegate constructorDelegate = (SampleBeanConstructorDelegate) ConstructorDelegate.create(
                SampleBean.class, SampleBeanConstructorDelegate.class);
        SampleBean bean = (SampleBean) constructorDelegate.newInstance("Hello world");
        assertTrue(SampleBean.class.isAssignableFrom(bean.getClass()));
        System.out.println(bean.getValue());
    }

    @Test
    public void testParallelSorter() throws Exception {
        Integer[][] value = {
                {4, 3, 9, 0},
                {2, 1, 6, 0}
        };
        ParallelSorter.create(value).mergeSort(0);
        for (Integer[] row : value) {
            int former = -1;
            for (int val : row) {
                assertTrue(former < val);
                former = val;
            }
        }
    }

    @Ignore
    public void testFastClass() throws Exception {
        FastClass fastClass = FastClass.create(SampleBean.class);
        FastMethod fastMethod = fastClass.getMethod("getValue", new Class[0]);
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world");
        assertEquals("Hello world", fastMethod.invoke(bean, new Object[0]));
    }
}
