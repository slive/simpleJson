//==============================================================================
//
//	@author Slive
//	@date  2020-11-27
//
//==============================================================================
package slive.simpleJson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 描述：
 * 
 */
public class MethodUtils {

    public static Field getField(Class<?> clazz, String fName) {
        if (clazz == null || clazz.isInterface() || clazz.isPrimitive() || clazz.isArray()) {
            return null;
        }

        try {
            return clazz.getDeclaredField(fName);
        } catch (Exception e) {
            e.printStackTrace();
            CommonUtils.warnLog("getFied error, field is " + fName + "," + e.getMessage());
        }
        return getField(clazz.getSuperclass(), fName);
    }

    public static String getFieldName(Method m, String preFix) {
        if (m == null) {
            return null;
        }
        if (preFix == null) {
            preFix = "";
        }
        String mName = m.getName();
        if (mName.startsWith(preFix) && mName.length() > 3) {
            int indexOf = preFix.length();
            String fName = mName.substring(indexOf, indexOf + 1).toLowerCase();
            if (mName.length() > indexOf + 1) {
                fName += mName.substring(indexOf + 1);
            } 
            return fName;
        }
        return null;
    }

}
