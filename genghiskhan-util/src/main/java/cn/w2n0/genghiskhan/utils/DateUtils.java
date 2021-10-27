package cn.w2n0.genghiskhan.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期时间对象
 * @author 无量
 * date: 2021/2/22 18:55
 */
public class DateUtils {
    /**
     * 二分法分割开始和结束时间
     * @param starttime 开始时间
     * @param endtime 结束时间
     * @return 分割时间map
     */
    public static Map<Date,Date> dichotomyDate(Date starttime,Date endtime){
        Map<Date, Date> times = new HashMap(10);
        int seconds = (int)((endtime.getTime() - starttime.getTime()) / 1000L / 2L);
        if(seconds>0)
        {
            Calendar time1 = Calendar.getInstance();
            time1.setTime(starttime);
            time1.add(Calendar.SECOND, seconds);
            times.put(starttime, time1.getTime());
            times.put(time1.getTime(),endtime);
        }
        else {
            times.put(starttime,endtime);
        }
        return times;
    }
}
