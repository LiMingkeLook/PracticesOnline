package net.lzzy.practicesonline.activities.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author lzzy_gxy
 * @date 2019/3/11
 * Description:
 */
public class AppUtils extends Application {
    private static final String SP_SETTING = "spSetting";
    private static final String URL_IP = "urlIp";
    private static final String URL_PORT = "urlPort";
    /** AppUtils中Activity的集中管理 1.定义activity集合**/
    private static List<Activity> activities=new LinkedList<>();

    private static WeakReference<Context> wContext;
    private static String runningActivity;



    @Override
    public void onCreate() {
        super.onCreate();
        wContext = new WeakReference<>(this);
    }

    public static Context getContext(){
        return wContext.get();
    }

    /** 添加方法**/
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    /** 移除方法**/
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }



    /** 退出方法**/
    public static void exit(){
        for (Activity activity:activities){
            if (activity!=null){
                activity.finish();
            }
        }
        System.exit(0);
    }

    /** 遍历获取运行时Activity**/
    public static Activity getRunningActivity(){
        for (Activity activity:activities){
            String name=activity.getLocalClassName();
            if (AppUtils.runningActivity.equals(name)){
              return activity;
            }
        }
        return null;
    }

    /** 运行时赋值 **/
    public static void setRunning(String runningActivity){
        AppUtils.runningActivity=runningActivity;
    }

    /** 停止时清空 **/
    public static void setStopped(String stoppedActivity){
        if (stoppedActivity.equals(AppUtils.runningActivity)){
            AppUtils.runningActivity="";
        }
    }
    /** 检查网络是否可用 **/
    public static boolean isNetworkAvailable(){
        ConnectivityManager manager= (ConnectivityManager) getContext()
                .getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager != null ? manager.getActiveNetworkInfo():null;
        return info != null && info.isConnected();
    }

    //region 创建线程池执行

    private static final int CPU_COUNT=Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE =Math.max(2, Math.min(CPU_COUNT - 1,4));
    private static final int MAX_POOL_SIZE =CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final ThreadFactory THREAD_FACTORY=new ThreadFactory() {
        private final AtomicInteger count=new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"thread #" +count.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> POOL_QUEUE=new LinkedBlockingQueue<>(128);

    public static ThreadPoolExecutor getExecutor(){
        ThreadPoolExecutor executor=new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,POOL_QUEUE,THREAD_FACTORY);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
    //endregion


    /**  尝试连接服务器  启动倒计时线程**/
    public static void tryConnectServer(String address) throws IOException {
        URL url=new URL(address);
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.getContent();
    }


    /** 数据存储保存 IP和端口 **/
    public static void saveServerSetting(String ip,String port,Context context){
        SharedPreferences spSetting=context.getSharedPreferences(SP_SETTING,MODE_PRIVATE);
        spSetting.edit()
                .putString(URL_IP,ip)
                .putString(URL_PORT,port)
                .apply();
    }

    /** 读取保存的IP和端口 **/
    public static Pair<String,String> loadServerSetting(Context context){
        SharedPreferences spSetting=context.getSharedPreferences(SP_SETTING,MODE_PRIVATE);
        String ip=spSetting.getString(URL_IP,"10.88.91.102");
        String port=spSetting.getString(URL_PORT,"8888");
        return new Pair<>(ip,port);
    }
}
