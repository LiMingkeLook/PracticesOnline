package net.lzzy.practicesonline.activities.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragments.PracticesFragment;
import net.lzzy.practicesonline.activities.models.PracticeFactory;
import net.lzzy.practicesonline.activities.network.DetectWedService;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class PracticesActivity extends BaseActivity implements PracticesFragment.OnPracticesSelectedListener {

    public static final String PRACTICES_ID = "practicesId";
    public static final String API_ID = "apiId";
    public static final String LOCAL_COUNT = "locaCount";
    /**④Activity中创建ServiceConnection对象*/
    private ServiceConnection connection;

    private boolean refresh=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        if (getIntent()!=null){
             refresh=getIntent().getBooleanExtra(DetectWedService.EXTRA_REFRESH,false);
        }
        /**⑤Activity中启动Service(bindService/startService)，销毁时结束Service*/
        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DetectWedService.DetectWedBinder binder= (DetectWedService.DetectWedBinder) service;
                binder.detect();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        int localCount= PracticeFactory.getInstance().get().size();
        Intent intent=new Intent(this,DetectWedService.class);
        intent.putExtra(LOCAL_COUNT,localCount);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refresh){
            ((PracticesFragment)getFragment()).startRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("是否退出应用")
                .setPositiveButton("退出",(dialog, which) -> AppUtils.exit())
                .show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void initViews() {
        SearchView search=findViewById(R.id.bar_title_search);
        search.setQueryHint("请输入关键词搜索");
        search.setOnQueryTextListener(new ViewUtils.AbstractQueryListener() {
            @Override
            public void handleQuery(String kw) {
                ( (PracticesFragment)getFragment()).search(kw);
            }
        });
        //todo:在fragment实现
        SearchView.SearchAutoComplete auto=search.findViewById(R.id.search_src_text);
        auto.setHintTextColor(Color.WHITE);
        auto.setTextColor(Color.WHITE);
        ImageView icon=search.findViewById(R.id.search_button);
        ImageView icX=search.findViewById(R.id.search_close_btn);
        ImageView icG=search.findViewById(R.id.search_go_btn);
        icon.setColorFilter(Color.WHITE);
        icX.setColorFilter(Color.WHITE);
        icG.setColorFilter(Color.WHITE);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_practices;
    }

    @Override
    protected int getContainerId() {
        return R.id.activity_practices_container;
    }

    @Override
    protected Fragment createFragment() {
        return new PracticesFragment();
    }

    @Override
    public void onPracticesSelected(String practicesId,int apiId) {
        Intent intent=new Intent(this,QuestionActivity.class);
        intent.putExtra(PRACTICES_ID,practicesId);
        intent.putExtra(API_ID,apiId);
        startActivity(intent);
    }

}
