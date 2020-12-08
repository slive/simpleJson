//==============================================================================
//
//	@author Slive
//	@date  2020-11-26
//
//==============================================================================
package slive.simpleJson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 描述：此次分为如下几种类型：
 * <ul>
 * <li>简单类型：原始类型，原始类型的包装类及String类型
 * <li>集合：set,list
 * <li>map:map
 * <li>数组：array
 * <li>复合类型：非以上类型
 * </ul>
 * 
 */
public class TypeUtils {

    /**
     * 处理包括：AtomicInteger, AtomicLong, BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short,Boolean,String
     */
    public static Object toSimpleVal(Class<?> destClass, Object vObj) {

        if (destClass == null || vObj == null) {
            return null;
        }

        String vStr = vObj + "";
        Object t = null;
        if (Integer.class.equals(destClass) || int.class.equals(destClass)) {
            t = Integer.parseInt(vStr);
        } else if (Byte.class.equals(destClass) || byte.class.equals(destClass)) {
            t = Byte.parseByte(vStr);
        } else if (Short.class.equals(destClass) || short.class.equals(destClass)) {
            t = Short.parseShort(vStr);
        } else if (Long.class.equals(destClass) || long.class.equals(destClass)) {
            t = Long.parseLong(vStr);
        } else if (Character.class.equals(destClass) || char.class.equals(destClass)) {
            t = vStr.charAt(0);
        } else if (Double.class.equals(destClass) || double.class.equals(destClass)) {
            t = Double.parseDouble(vStr);
        } else if (Float.class.equals(destClass) || float.class.equals(destClass)) {
            t = Float.parseFloat(vStr);
        } else if (Boolean.class.equals(destClass) || boolean.class.equals(destClass)) {
            t = Boolean.parseBoolean(vStr.trim());
        } else if (isSimpleType(destClass)) {
            t = vStr;
        } else if (BigInteger.class.equals(destClass)) {
            t = new BigInteger(vStr);
        } else if (BigDecimal.class.equals(destClass)) {
            t = new BigDecimal(vStr);
        } else if (AtomicInteger.class.equals(destClass)) {
            t = new AtomicInteger(Integer.parseInt(vStr));
        } else if (AtomicLong.class.equals(destClass)) {
            t = new AtomicLong(Long.parseLong(vStr));
        } else {
            t = vObj;
        }
        return t;
    }

    /**
     * 即 boolean、byte、char、short、int、long、float 和 double
     * @see Class.isPrimitive()
     */
    public static boolean isPrimitiveType(Class<?> clazz) {
        return clazz != null ? clazz.isPrimitive() : false;
    }

    public static boolean isPrimitiveWrapperType(Class<?> clazz) {
        if (clazz != null) {
            // 抽象类 Number 是 BigDecimal、BigInteger、Byte、Double、Float、Integer、Long 和 Short 类的超类
            if (Number.class.isAssignableFrom(clazz)) {
                return true;
            } else if (Boolean.class.equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStringType(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    public static boolean isArray(Class<?> clazz) {
        return clazz != null ? clazz.isArray() : false;
    }

    public static boolean isMap(Class<?> clazz) {
        return isContainType(clazz, Map.class);
    }

    public static boolean isSet(Class<?> clazz) {
        return isContainType(clazz, Set.class);
    }

    public static boolean isList(Class<?> clazz) {
        return isContainType(clazz, List.class);
    }

    public static boolean isCollection(Class<?> clazz) {
        return isContainType(clazz, Collection.class);
    }

    private static boolean isContainType(Class<?> clazz, Class<?> cClazz) {
        return cClazz.isAssignableFrom(clazz);
    }

    /**
     * 简单类型:原始类型，原始类型的包装类及String类型都归类为
     */
    public static boolean isSimpleType(Class<?> clazz) {
        return isStringType(clazz) || isPrimitiveWrapperType(clazz) || isPrimitiveType(clazz);
    }

    /**
     * 复合类型：简单类型，集合，数组，Map除外
     */
    public static boolean isComplexType(Class<?> clazz) {
        return !(isSimpleType(clazz) || isMap(clazz) || isList(clazz) || isSet(clazz) || isArray(clazz));
    }
}
