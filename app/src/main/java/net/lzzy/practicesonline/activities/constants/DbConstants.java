package net.lzzy.practicesonline.activities.constants;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.sqllib.DbPackager;

/**
 * @author lzzy_gxy
 * @date 2019/3/11
 * Description:
 */

public final class DbConstants {
    private DbConstants(){}
    private static final String DB_NAME = "practices.db";
    private static final int DB_VERSION = 1;
    public static DbPackager packager;
    static {
        packager = DbPackager.getInstance(AppUtils.getContext(),DB_NAME,DB_VERSION,
                R.raw.models);
    }
}
