//==============================================================================
//
//	@author Slive
//	@date  2020-11-26
//
//==============================================================================
package slive.simpleJson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 描述：json格式描述见http://www.json.org/ <br>
 * 简单的Json解析工具类
 */
@SuppressWarnings("rawtypes")
public class Json {

    /**
     * 将类转换为json格式字符串
     */
    public static String toJsonString(Object jsonObj) {
        if (jsonObj == null) {
            return null;
        }

        Class<?> clazz = jsonObj.getClass();
        if (TypeUtils.isSimpleType(clazz)) {
            return toSimpleString(jsonObj);
        } else if (TypeUtils.isComplexType(clazz)) {
            // 复合类型对应的值的类型只有可能是Map，否则不解析
            return toComplexString(jsonObj);
        } else if (TypeUtils.isMap(clazz)) {
            return toMapString((Map) jsonObj);
        } else if (TypeUtils.isArray(clazz)) {
            return toArrayString(jsonObj);
        } else if (TypeUtils.isCollection(clazz)) {
            return toCollectString((Collection) jsonObj);
        }
        return null;
    }

    private static String toMapString(Map jsonObj) {
        StringBuilder sbd = new StringBuilder();
        sbd.append("{");
        for (Object key : jsonObj.keySet()) {
            sbd.append("\"").append(key).append("\":");
            String val = toJsonString(jsonObj.get(key));
            if (val != null) {
                sbd.append(val);
            }
            sbd.append(",");
        }
        if (sbd.length() > 2) {
            sbd.deleteCharAt(sbd.length() - 1);
        }
        sbd.append("}");
        return sbd.toString();
    }

    private static String toCollectString(Collection jsonObj) {
        StringBuilder sbd = new StringBuilder();
        sbd.append("[");
        for (Object obj : jsonObj) {
            String val = toJsonString(obj);
            if (val != null) {
                sbd.append(val);
            }
            sbd.append(",");
        }
        if (sbd.length() > 2) {
            sbd.deleteCharAt(sbd.length() - 1);
        }
        sbd.append("]");
        return sbd.toString();

    }

    private static String toSimpleString(Object jsonObj) {
        if (TypeUtils.isStringType(jsonObj.getClass())) {
            return "\"" + jsonObj + "\"";
        } else {
            return jsonObj + "";
        }
    }

    private static String toArrayString(Object jsonObj) {
        StringBuilder sbd = new StringBuilder();
        sbd.append("[");
        int len = Array.getLength(jsonObj);
        for (int index = 0; index < len; index++) {
            String val = toJsonString(Array.get(jsonObj, index));
            if (val != null) {
                sbd.append(val);
            }
            sbd.append(",");
        }
        if (sbd.length() > 2) {
            sbd.deleteCharAt(sbd.length() - 1);
        }
        sbd.append("]");
        return sbd.toString();

    }

    private static String toComplexString(Object jsonObj) {
        Class<? extends Object> jClass = jsonObj.getClass();
        Method[] ms = jClass.getMethods();
        StringBuilder sbd = new StringBuilder();
        sbd.append("{");
        for (Method m : ms) {
            // 同时存在getter和setter方法时，才做处理
            String fieldName = MethodUtils.getFieldName(m, "get");
            if (fieldName == null) {
                fieldName = MethodUtils.getFieldName(m, "is");
            }

            if (fieldName != null && !fieldName.equals("class") && !fieldName.equals("proxyClass")
                    && !fieldName.equals("invocationHandler")) {
                Method setM = null;
                try {
                    String upperField = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    setM = jClass.getMethod("set" + upperField, m.getReturnType());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    if (setM != null) {
                        if(!m.isAccessible()){
                            m.setAccessible(true);
                        }
                        Object val = m.invoke(jsonObj);
                        if (val != null) {
                            // 递归调用
                            sbd.append("\"").append(fieldName).append("\":").append(toJsonString(val)).append(",");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (sbd.length() > 2) {
            sbd.deleteCharAt(sbd.length() - 1);
        }
        sbd.append("}");
        return sbd.toString();

    }

    /**
     * 将json字符串解析为对应的类
     * 
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        JsonObject jb = toJsonObject(jsonStr);
        if (jb == null) {
            return null;
        }

        Object vObj = jb.getVal();
        if (vObj == null) {
            return null;
        }
        return parseObjectInner(clazz, vObj, null);
    }

    /**
     * 解析类，底层本质都是解析简单类或者复合类，所以会嵌套处理。
     */
    private static <T> T parseObjectInner(Class<?> clazz, Object vObj, Class<?> componentType) {
        if (vObj == null) {
            return null;
        }
        CommonUtils.debugLog("clazz is " + clazz);
        CommonUtils.debugLog("componentType is " + componentType);
        if (TypeUtils.isSimpleType(clazz)) {
            return parseSimpleType(vObj, clazz);
        } else if (TypeUtils.isComplexType(clazz)) {
            // 复合类型对应的值的类型只有可能是Map，否则不解析
            return parseComplexType(vObj, clazz);
        } else if (TypeUtils.isMap(clazz)) {
            return parseMap(vObj, clazz, componentType);
        } else if (TypeUtils.isArray(clazz)) {
            return parseArray(vObj, clazz, componentType);
        } else if (TypeUtils.isList(clazz)) {
            return parseList(vObj, clazz, componentType);
        } else if (TypeUtils.isSet(clazz)) {
            return parseSet(vObj, clazz, componentType);
        }
        return null;
    }

    private static <T> T parseSimpleType(Object vObj, Class<?> clazz) {
        // 如果是同一类型，直接赋值
        if (vObj.getClass().equals(clazz)) {
            return (T) vObj;
        }
        return (T) TypeUtils.toSimpleVal(clazz, vObj);
    }

    /**
     * 复合类需通过发射调用set方法进行处理
     */
    private static <T> T parseComplexType(Object vObj, Class<?> clazz) {
        Class<? extends Object> vClazz = vObj.getClass();
        Object insObj = null;
        if (TypeUtils.isMap(vClazz)) {
            insObj = newInstance(clazz);
            Map vMap = (Map) vObj;
            if (insObj != null) {
                parseComplexType(vMap, clazz, insObj);
            } else {
                // 代理实现
                insObj = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz },
                        new JsonInvocationHandler(vMap));
            }
        }
        return (T) insObj;
    }

    private static void parseComplexType(Map vMap, Class<?> clazz, Object insObj) {
        String fName = null;
        Field field = null;
        Object retVal = null;
        Object vItemVal = null;
        Class<?> componentType = null;
        Method[] ms = clazz.getMethods();
        for (Method m : ms) {
            fName = MethodUtils.getFieldName(m, "set");
            if (fName != null) {
                vItemVal = vMap.get(fName);
                if (vItemVal != null) {
                    field = MethodUtils.getField(clazz, fName);
                }
                if (field != null) {
                    componentType = getComponentType(field.getGenericType());
                    retVal = parseObjectInner(field.getType(), vItemVal, componentType);
                    if (retVal != null) {
                        try {
                            if(!m.isAccessible()){
                                m.setAccessible(true);
                            }
                            m.invoke(insObj, retVal);
                        } catch (Exception e) {
                            CommonUtils.warnLog("method invoke error, mName is " + m.getName() + ",field is " + fName
                                    + "," + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private static Class<?> getComponentType(Type gType) {
        CommonUtils.debugLog("type is " + gType);
        if (gType instanceof Class) {
            Class<?> clazz = (Class<?>) gType;
            if (TypeUtils.isArray(clazz)) {
                return clazz.getComponentType();
            }
        }
        if (gType != null) {
            if (gType instanceof ParameterizedType) {
                Type[] ctgs = ((ParameterizedType) gType).getActualTypeArguments();
                if (ctgs.length > 0) {
                    // collection，map等，只取最后一个参数
                    return (Class<?>) ctgs[ctgs.length - 1];
                }
            } else if (gType instanceof WildcardType) {
                return (Class<?>) ((WildcardType) gType).getUpperBounds()[0];
            } else if (gType instanceof GenericArrayType) {
                return (Class<?>) ((GenericArrayType) gType).getGenericComponentType();
            } else if (gType instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable) gType).getBounds()[0];
            }
        }
        return Object.class;
    }

    private static <T> T parseMap(Object vObj, Class<?> clazz, Class<?> componentType) {
        if (TypeUtils.isMap(vObj.getClass())) {
            // 子类型转换
            boolean isContainComponentType = true;
            if (componentType == null || componentType.equals(Object.class)) {
                isContainComponentType = false;
            }
            Map ret = (Map) newInstance(clazz);
            Map objMap = (Map) vObj;
            Object itemVal = null;
            Object itemDestVal = null;
            for (Object key : objMap.keySet()) {
                itemVal = objMap.get(key);
                if (!isContainComponentType) {
                    componentType = itemVal.getClass();
                }

                // 嵌套解析
                itemDestVal = parseObjectInner(componentType, itemVal, null);
                if (itemDestVal != null) {
                    ret.put(key, itemDestVal);
                }
            }
            return (T) ret;
        }
        return null;
    }

    private static <T> T parseArray(Object vObj, Class<?> clazz, Class<?> componentType) {
        Collection ret = parseCollection(vObj, List.class, componentType);
        if (ret != null && !ret.isEmpty()) {
            Object retArray = newArrayInstance(componentType, ret.size());
            int index = 0;
            for (Object obj : ret) {
                Array.set(retArray, index, obj);
                index++;
            }
            return (T) retArray;
        }
        return null;
    }

    private static <T> T parseList(Object vObj, Class<?> clazz, Class<?> componentType) {
        return parseCollection(vObj, clazz, componentType);

    }

    private static <T> T parseSet(Object vObj, Class<?> clazz, Class<?> componentType) {
        return parseCollection(vObj, clazz, componentType);
    }

    private static <T> T parseCollection(Object vObj, Class<?> clazz, Class<?> componentType) {
        // 子类型转换
        boolean isContainComponentType = true;
        if (componentType == null || componentType.equals(Object.class)) {
            isContainComponentType = false;
        }
        Collection ret = (Collection) newInstance(clazz);
        Collection objC = (Collection) vObj;
        Object itemDestVal = null;
        for (Object itemVal : objC) {
            if (!isContainComponentType) {
                componentType = itemVal.getClass();
            }
            itemDestVal = parseObjectInner(componentType, itemVal, null);
            if (itemDestVal != null) {
                ret.add(itemDestVal);
            }
        }
        return (T) ret;
    }

    private static Object newInstance(Class<?> clazz) {
        Object ret = null;
        if (!clazz.isInterface()) {
            if (!clazz.isArray()) {
                try {
                    ret = clazz.newInstance();
                } catch (Exception e) {
                    CommonUtils.warnLog("newInstance error," + e.getMessage());
                }
            } else {
                ret = Array.newInstance(clazz, 0);
            }
        } else {
            // 处理Jdk中能对应上的
            if (Map.class.equals(clazz)) {
                return new HashMap();
            } else if (List.class.equals(clazz)) {
                return new ArrayList();
            } else if (Set.class.equals(clazz)) {
                return new HashSet();
            } else if (SortedSet.class.equals(clazz)) {
                return new TreeSet();
            } else if (SortedMap.class.equals(clazz)) {
                return new TreeMap();
            } else {
                // TODO 代理Proxy+InvocationHandler实现
                CommonUtils.warnLog("unsupported \'" + clazz + "\' instance.");
            }
        }
        return ret;
    }

    private static Object newArrayInstance(Class<?> componentType, int len) {
        try {
            return Array.newInstance(componentType, len);
        } catch (Exception e) {
            CommonUtils.warnLog("newArrayInstance error," + e.getMessage());
        }
        return null;
    }

    private static JsonObject toJsonObject(String jsonStr) {
        if (CommonUtils.isEmpty(jsonStr)) {
            return null;
        }
        JsonObject jb = null;
        StringBuilder sbd = new StringBuilder();
        char[] jcs = jsonStr.toCharArray();
        int jLen = jcs.length;
        String key = null;
        String val = null;
        Object jVal = null;
        char jc;
        for (int index = 0; index < jLen; index++) {
            jc = jcs[index];
            if (jc == '{') {
                // 初始化为Map
                jb = new JsonObject(jb, key, new HashMap<String, Object>());
            } else if (jc == '[') {
                // 初始化为List
                jb = new JsonObject(jb, key, new ArrayList<Object>());
            } else if (jc == ':') {
                // 获取key
                key = getAndCleanSbdStr(sbd);
            } else if (jc == ',' || jc == '}' || jc == ']') {
                // 获取val
                val = getAndCleanSbdStr(sbd);
                // 处理值
                if (jb != null && CommonUtils.isNotEmpty(val)) {
                    jVal = jb.getVal();
                    if (jVal instanceof Map && CommonUtils.isNotEmpty(key)) {
                        ((Map<String, Object>) jVal).put(key, val);
                    } else if (jVal instanceof List) {
                        ((List<Object>) jVal).add(val);
                    }
                }

                // 当前对象处理介绍，回退到上一个对象
                if (jc == '}' || jc == ']') {
                    jb = jb.getParent();
                    key = jb.getKey();
                }
            } else {
                sbd.append(jc);
            }
        }
        return jb;
    }

    private static String getAndCleanSbdStr(StringBuilder sbd) {
        String val = sbd.toString();
        if (CommonUtils.isNotEmpty(val)) {
            val = val.replaceAll("\"", "").trim();
        }
        sbd.delete(0, sbd.length());
        // CommonUtils.debugLog("sbdVal:" + val);
        return val;
    }

    /**
     * 代理实例化类
     * @author Slive 2017-11-4
     */
    static class JsonInvocationHandler implements InvocationHandler {

        private Map srcVals = null;

        private Map<String, Object> vals = new HashMap<String, Object>();

        public JsonInvocationHandler(Map srcVals) {
            this.srcVals = srcVals;
        }

        @Override
        public int hashCode() {
            int code = 1;
            for (Object obj : vals.values()) {
                if (obj != null) {
                    code = code * 31 + obj.hashCode();
                }
            }
            return code;
        }

        @Override
        public Object invoke(Object ins, Method m, Object[] valObjs) throws Throwable {
            // 重点代理实现getter和setter方法
            try {
                String mName = m.getName();
                Type[] pts = m.getGenericParameterTypes();
                Class<?> retType = m.getReturnType();
                if (pts.length == 1 && retType.equals(void.class)) {
                    if (mName.startsWith("set")) {
                        String fieldName = MethodUtils.getFieldName(m, "set");
                        vals.put(fieldName, valObjs[0]);
                    }
                } else if (pts.length == 0 && !retType.equals(void.class)) {
                    if (mName.startsWith("get")) {
                        String fieldName = MethodUtils.getFieldName(m, "get");
                        if (fieldName != null) {
                            if (!vals.containsKey(fieldName)) {
                                return convertVal(pts, retType, fieldName);
                            } else {
                                return vals.get(fieldName);
                            }
                        }
                    } else if (mName.startsWith("is")) {
                        String fieldName = MethodUtils.getFieldName(m, "is");
                        if (fieldName != null) {
                            if (!vals.containsKey(fieldName)) {
                                return convertVal(pts, retType, fieldName);
                            } else {
                                return vals.get(fieldName);
                            }
                        }
                    } else if (mName.startsWith("toString")) {
                        Set<String> keySet = srcVals.keySet();
                        for (String fileName : keySet) {
                            if (!vals.containsKey(fileName)) {
                                Object srcVal = srcVals.get(fileName);
                                if (srcVal != null) {
                                    Method getM = null;
                                    String upperField = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
                                    try {
                                        getM = srcVal.getClass().getMethod("get" + upperField, null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        try {
                                            getM = srcVal.getClass().getMethod("is" + upperField, null);
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }
                                    }
                                    if (getM != null) {
                                        convertVal(null, getM.getReturnType(), fileName);
                                    }
                                }
                            }
                        }
                        return vals.toString();
                    } else if (mName.startsWith("hashCode")) {
                        return this.hashCode();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private Object convertVal(Type[] pts, Class<?> retType, String fieldName) {
            // 值获取不到时，初始化转换
            Object retSrcVal = srcVals.get(fieldName);
            if (retSrcVal != null) {
                // 做值转换
                Class<?> cType = getComponentType((pts != null && pts.length > 0) ? pts[0] : retType);
                Object retVal = parseObjectInner(retType, retSrcVal, cType);
                if (retVal != null) {
                    vals.put(fieldName, retVal);
                }
                return retVal;
            }
            return null;
        }
    }

    /**
     * json中间转换类，嵌套存储，只存在Map->{}，List->[]，Object&String三种结构（底层实际只有Map，List，String）
     * 
     */
    static class JsonObject {

        private JsonObject parent;

        private String key;

        private Object val;

        public JsonObject(JsonObject parent, String key, Map<String, Object> val) {
            this.parent = parent;
            this.key = key;
            this.val = val;
            relateParent(parent, key, val);
        }

        public JsonObject(JsonObject parent, String key, List<Object> val) {
            this.parent = parent;
            this.key = key;
            this.val = val;
            relateParent(parent, key, val);
        }

        /**
         * 与父类建立关系
         */
        @SuppressWarnings("unchecked")
        private void relateParent(JsonObject parent, String key, Object val) {
            if (parent != null && val != null) {
                Object pVal = parent.getVal();
                if (pVal != null) {
                    if (pVal instanceof Map && CommonUtils.isNotEmpty(key)) {
                        ((Map<String, Object>) pVal).put(key, val);
                    } else if (pVal instanceof List) {
                        ((List<Object>) pVal).add(val);
                    }
                }
            }
        }

        public JsonObject getParent() {
            if (parent == null) {
                parent = this;
            }
            return parent;
        }

        public String getKey() {
            return key;
        }

        public Object getVal() {
            return val;
        }

        @Override
        public String toString() {
            return val != null ? val.toString() : null;
        }

    }
}
