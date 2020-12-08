//==============================================================================
//
//	@author Slive
//	@date  2020-11-26
//
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
package slive.simpleJson;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 描述：
 */
public class JsonTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String jsonMap = "{a:1,b:[2,3],c:\"abcd\",d:{d1:[1,2,3,\"d1\"],d2:2},"
                + "e:4,f:{f1:1,f2:2,f3:\"f3\"},g:[1,g:{g2:\"g2\"}]}";
        CommonUtils.debugLog(jsonMap);
        CommonUtils.debugLog(Json.parseObject(jsonMap, Map.class));

        String jsonArray = "[1,1,\"abcd\",{g2:\"g2\",g3:[333,444,\"ggg\"]},[2,3,\"kkk\",{a:2,b:\"33\"}],[2,3,\"kkk\",{a:2,b:\"33\"}]]";
        CommonUtils.debugLog(Json.parseObject(jsonArray, List.class));
        CommonUtils.debugLog(Json.parseObject(jsonArray, Set.class));

        String jsonDto1 = "{sa:\"abc\",sb:2343,sc:[2,3,4,3333]}";
        CommonUtils.debugLog(Json.parseObject(jsonDto1, SubDemoDto.class));

        String jsonDto2 = "{sa:\"2abc2\",sb:32343,sc:[2322,33,44,223333]}";
        String jsonDto3 = "{b1:\"true\",bb2:\"false\",a:\"abc\",b:3332,d:6542,el:[\"ddd\",\"rrr\"],sdt:" + jsonDto1 + ",sl:[" + jsonDto2 + ","
                + jsonDto1 + "],sdm:{aaa:" + jsonDto1 + ",bbb:" + jsonDto2 + "}}";
        CommonUtils.debugLog("jsonDto3:" + jsonDto3);
        DemoDto retObj = Json.parseObject(jsonDto3, DemoDto.class);
        CommonUtils.debugLog(retObj.isB1());
//        CommonUtils.debugLog(Json.toJsonString(retObj));

        // 性能测试
        long currentTime = System.currentTimeMillis();
        for (int index = 0; index < 100000; index++) {
//            Json.parseObject(jsonDto3, DemoDto.class);
            Json.toJsonString(retObj);
        }
        System.out.println("spendTime:" + (System.currentTimeMillis() - currentTime));
    }

}
