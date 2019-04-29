package net.lzzy.practicesonline.activities.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by lzzy_gxy on 2019/4/24.
 * Description:
 */
public class DateTimeUtils {
    public static final SimpleDateFormat DATM_TIME_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH-mm:ss", Locale.CANADA);
    public static final SimpleDateFormat DATM_FORMAT=new SimpleDateFormat("yyyy-MM-dd ", Locale.CANADA);
}
