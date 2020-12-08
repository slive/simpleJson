//==============================================================================
//
//	@author Slive
//	@date  2020-11-26
//
//==============================================================================
package slive.simpleJson;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述：
 * 
 */
public class CommonUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public static boolean isEmpty(Object obj) {
        return !isNotEmpty(obj);
    }

    public static boolean isNotEmpty(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                return !"".equals(obj);
            }
        }
        return false;
    }

    public static void debugLog(Object log) {
        System.out.println("debug log " + sdf.format(new Date()) + "  " + log);
    }

    public static void warnLog(Object log) {
        System.err.println("warn log " + sdf.format(new Date())+ "  " + log);
    }

    public static void errLog(Object log) {
        System.err.println("error log " + sdf.format(new Date()) + "  " + log);
    }

}
