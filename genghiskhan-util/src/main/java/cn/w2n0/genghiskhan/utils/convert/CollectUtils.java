package cn.w2n0.genghiskhan.utils.convert;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合转换类
 * @author 无量
 * data:2020-10-01
 */
public class CollectUtils {
    /**
     * 切分list
     *
     * @param sourceList 源列表
     * @param groupSize  每组定长
     * @return 列表
     */
    public static List<List> splitList(List sourceList, int groupSize) {
        int length = sourceList.size();
        int num = (length + groupSize - 1) / groupSize;
        List<List> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            int fromIndex = i * groupSize;
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(sourceList.subList(fromIndex, toIndex));
        }
        return newList;
    }
}
