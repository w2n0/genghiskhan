package cn.w2n0.genghiskhan.utils.reflect;


import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.objectweb.asm.*;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具类
 *
 * @author 无量
 */
public class ReflectUtil {
    public static List<String> getParameterNameJava8(Class clazz, String methodName) {
        List<String> paramterList = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                Parameter[] params = method.getParameters();
                for (Parameter parameter : params) {
                    paramterList.add(parameter.getName());
                }
            }
        }
        return paramterList;
    }

    /**
     * 获取接口方法的参数名（抽象方法也可以）
     * 编译时增加参数  -parameters
     *
     * @param method
     * @return
     * @throws IOException
     */
    public static List<String> getInterfaceMethodParamNames(final Method method) throws IOException {
        final Class<?>[] methodParameterTypes = method.getParameterTypes();
        final List<String> methodParametersNames = new ArrayList<>();
        final String className = method.getDeclaringClass().getName();
        ClassReader cr = new ClassReader(className);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM6) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(descriptor);
                // 方法名相同并且参数个数相同
                if (!name.equals(method.getName()) || !matchTypes(args, method.getParameterTypes())) {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
                MethodVisitor v = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM6, v) {
                    /**
                     * 获取 MethodParameters 参数
                     */
                    @Override
                    public void visitParameter(String name, int access) {
                        methodParametersNames.add(name);
                        super.visitParameter(name, access);
                    }
                };
            }
        };
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        return methodParametersNames;
    }
    /**
     * 比较参数是否一致
     */
    private static boolean matchTypes(Type[] types, Class<?>[] parameterTypes) {
        if (types.length != parameterTypes.length) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(parameterTypes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }
    public static List<String> getInterfacceParamterName(Class clazz, String methodName) {
        LocalVariableTableParameterNameDiscoverer lvtp = new LocalVariableTableParameterNameDiscoverer();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                String[] params = lvtp.getParameterNames(method);
                return Arrays.asList(params);
            }
        }

        return null;
    }

    public static List<String> getParamterName(Class clazz, String methodName) {
        LocalVariableTableParameterNameDiscoverer lvtp = new LocalVariableTableParameterNameDiscoverer();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                String[] params = lvtp.getParameterNames(method);
                return Arrays.asList(params);
            }
        }

        return null;
    }

    public static List<String> getParamterNameJavassist(Class clazz, String methodName) throws NotFoundException {
        List<String> list = new ArrayList<>();

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(clazz.getName());
        CtMethod cm = cc.getDeclaredMethod(methodName);

        // 使用javaassist的反射方法获取方法的参数名
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            // exception
        }

        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < cm.getParameterTypes().length; i++) {
            String parmName = attr.variableName(i + pos);
            list.add(parmName);
        }
        return list;


    }

    /**
     * 单个对象的某个键的值
     *
     * @param obj 对象
     * @param key 键
     * @return Object 键在对象中所对应得值 没有查到时返回空字符串
     */
    public static Object getValueByKey(Object obj, String key) {
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true);
            try {

                if (f.getName().endsWith(key)) {
                    System.out.println("单个对象的某个键的值==反射==" + f.get(obj));
                    return f.get(obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 没有查到时返回空字符串
        return "";
    }
}
