package net.lzzy.practicesonline.activities.fragments;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.Practice;
import net.lzzy.practicesonline.activities.models.PracticeFactory;
import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.network.DetectWedService;
import net.lzzy.practicesonline.activities.network.PracticeService;
import net.lzzy.practicesonline.activities.network.QuestionService;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.DateTimeUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */

public class PracticesFragment extends BaseFragment {
    public static final int WHAT_PRACTICE_DONE = 0;
    public static final int WHAT_EXCEPTION = 1;
    public static final int WHAT_QUESTIONS_E = 2;
    private List<Practice> practices;
    private GenericAdapter<Practice> adapter;
    private PracticeFactory factory=PracticeFactory.getInstance();
    private ListView lv;
    private TextView tvHint;
    private TextView tvTime;
    private SwipeRefreshLayout swipe;
    private float touchX1;
    private boolean isDeleting=false;
    private ThreadPoolExecutor executor= AppUtils.getExecutor();
    private OnPracticesSelectedListener listener;

    private CountHandler handler =new CountHandler(this);

    public static class CountHandler extends AbstractStaticHandler<PracticesFragment> {

        CountHandler(PracticesFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, PracticesFragment fragment) {
            switch (msg.what){
                case WHAT_PRACTICE_DONE:
                    fragment.tvTime.setText(DateTimeUtils.DATM_TIME_FORMAT.format(new Date()));
                    UserCookies.getInstance().updateLastRefreshTime();
                    try {
                        List<Practice> practices=PracticeService.getPractices(msg.obj.toString());
                        for (Practice practice:practices){
                            fragment.adapter.add(practice);
                        }
                        Toast.makeText(fragment.getContext(), "同步完成", Toast.LENGTH_SHORT).show();
                        fragment.finishRefresh();
                    }catch (Exception e){
                        e.printStackTrace();
                        fragment.handlePracticeException(e.getMessage());
                    }
                    break;
                case WHAT_EXCEPTION:
                    fragment.handlePracticeException(msg.obj.toString());
                    break;
                 default:
                    break;
            }
        }
    }

     static class PracticeDownloader extends AsyncTask<Void,Void,String>{
        WeakReference<PracticesFragment> fragment;
        PracticeDownloader(PracticesFragment fragment){
            this.fragment=new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PracticesFragment fragment=this.fragment.get();
            fragment.tvHint.setVisibility(View.VISIBLE);
            fragment.tvTime.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return PracticeService.getPracticesFromServer();
            } catch (IOException e) {
                return e.getMessage();
               }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PracticesFragment fragment=this.fragment.get();
            fragment.tvTime.setText(DateTimeUtils.DATM_TIME_FORMAT.format(new Date()));
            UserCookies.getInstance().updateLastRefreshTime();
            try {
                List<Practice> practices=PracticeService.getPractices(s);
                for (Practice practice:practices){
                    fragment.adapter.add(practice);
                      }
                Toast.makeText(fragment.getContext(), "同步完成", Toast.LENGTH_SHORT).show();
                fragment.finishRefresh();
            }catch (Exception e){
                e.printStackTrace();
                fragment.handlePracticeException(e.getMessage());
            }
        }
    }

    static class QuestionDownloader extends AsyncTask<Void,Void,String>{
        WeakReference<PracticesFragment> fragment;
        Practice practice;
        QuestionDownloader(PracticesFragment fragment, Practice practice){
            this.fragment=new WeakReference<>(fragment);
            this.practice=practice;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ViewUtils.showProgress(fragment.get().getContext(),"开始下载题目......");
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return QuestionService.getQuestionsOfPracticeFromService(practice.getApiId());
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           fragment.get().saveQuestion(s,practice.getId());
           ViewUtils.dismissProgress();
        }
    }

    private void saveQuestion(String json,UUID practiceId) {
        try {
            List<Question> questions=QuestionService.getQuestions(json,practiceId);
            factory.saveQuestions(questions,practiceId);
            for (Practice practice:practices){
                if (practice.getId().equals(practiceId)){
                    practice.setDownloaded(true);
                }
            }
            Toast.makeText(getContext(), "下载完成", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(getContext(), "下载失败", Toast.LENGTH_SHORT).show();
        }
    }
    private void handlePracticeException(String message) {
        finishRefresh();
        Snackbar.make(lv,"同步失败\n"+message,Snackbar.LENGTH_LONG)
                .setAction("重试",v -> {
                    swipe.setRefreshing(true);
                    refreshListener.onRefresh();
                }).show();
    }

    private void finishRefresh(){
        swipe.setRefreshing(false);
        tvHint.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        NotificationManager manager= (NotificationManager)Objects.requireNonNull(getContext())
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager!=null){
            manager.cancel(DetectWedService.NOTIFICATION_DETCT_ID);
        }

    }

    public void startRefresh(){
        swipe.setRefreshing(true);
        refreshListener.onRefresh();
    }

    @Override
    protected void populate() {
             initViews();
             loadPractices();
             initSwipe();
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener=this::downloadPracticesAsync;

    private void downloadPractices(){
        tvHint.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.VISIBLE);
        executor.execute(()->{
            try {
                String json= PracticeService.getPracticesFromServer();
                handler.sendMessage(handler.obtainMessage(WHAT_PRACTICE_DONE,json));
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
            }
        });
    }

    private void downloadPracticesAsync() {
        new PracticeDownloader(this).execute();
    }

    private void initSwipe() {
        swipe.setOnRefreshListener(refreshListener);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                 boolean isTop=view.getChildCount()==0||view.getChildAt(0).getTop()>=0;
                 swipe.setEnabled(isTop);

            }
        });
    }

    private void loadPractices() {
        practices=factory.get();
        //排序
        Collections.sort(practices, new Comparator<Practice>() {
            @Override
            public int compare(Practice o1, Practice o2) {
                return o2.getDownloadDate().compareTo(o1.getDownloadDate());
            }
        });
        adapter=new GenericAdapter<Practice>(getActivity(),R.layout.practices_item,practices) {
            @Override
            public void populate(ViewHolder viewHolder, Practice practice) {
                    viewHolder.setTextView(R.id.practices_item_name,practice.getName());
                    TextView tv=viewHolder.getView(R.id.practices_item_main);
                    if (practice.isDownloaded()){
                        tv.setVisibility(View.VISIBLE);
                        tv.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                                 .setMessage(practice.getOutlines())
                                .show());
                    }else {
                        tv.setVisibility(View.GONE);
                    }
                    Button button=viewHolder.getView(R.id.practices_item_btn);
                    button.setVisibility(View.GONE);
                button.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                        .setTitle("删除确认")
                        .setMessage("确定要删除项目吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", (dialogInterface, i) ->
                        {   isDeleting=false;
                            adapter.remove(practice);
                            button.setVisibility(View.GONE);
                        }).show());
                int visibility=isDeleting?View.VISIBLE:View.GONE;
                button.setVisibility(visibility);
                viewHolder.getConvertView().setOnTouchListener(new ViewUtils.AbstractTouchHandler() {
                    @Override
                    public boolean handleTouch(MotionEvent event) {
                        slideToDelete(event, button,practice);
                        return true;
                    }
                });

            }

            @Override
            public boolean persistInsert(Practice practice) {
                return factory.addPractice(practice);
            }

            @Override
            public boolean persistDelete(Practice practice) {
                return factory.deletePracticeAndRelated(practice);
            }
        };
        lv.setAdapter(adapter);
    }

    private void slideToDelete(MotionEvent event, Button button,Practice practice) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX1=event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if(touchX1-event.getX()>100){
                    if (!isDeleting){
                        button.setVisibility(View.VISIBLE);
                        isDeleting=true;
                    }
                }else {
                    if(button.isShown()){
                        button.setVisibility(View.GONE);
                        isDeleting=false;
                    }else {
                        performItemClick(practice);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void performItemClick( Practice practice) {
        if (practice.isDownloaded()){
            //todo:跳转到Question视图
          listener.onPracticesSelected(practice.getId().toString(),practice.getApiId());
        }else {
            new AlertDialog.Builder(getContext())
                    .setMessage("下载章节题目？")
                    .setPositiveButton("下载",(dialog, which) -> downloadQuestion(practice))
                    .setNegativeButton("取消",null)
                    .show();
        }
    }

    //异步下载
    private void downloadQuestion(Practice practice) {
        new QuestionDownloader(this,practice).execute();
    }

    private void initViews(){
        lv = find(R.id.fragment_practices_lv);
        TextView textView = find(R.id.fragment_practices_tv);
        lv.setEmptyView(textView);
        swipe = find(R.id.fragment_practices_swipe);
        tvHint = find(R.id.fragment_practices_tv_hint);
        tvTime = find(R.id.fragment_practices_tv_time);
        tvTime.setText(UserCookies.getInstance().getLastRefreshTime());
        tvHint.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);

        find(R.id.fragment_practices_lv).setOnTouchListener(new ViewUtils.AbstractTouchHandler() {
            @Override
            public boolean handleTouch(MotionEvent event) {
                isDeleting=false;
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_practices;
    }

    @Override
    public void search(String kw) {
        practices.clear();
        if (kw.isEmpty()){
            practices.addAll(factory.get());
        }else {practices.addAll(factory.searchPractice(kw));
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener= (OnPracticesSelectedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    +"必需实现OnPracticesSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
        handler.removeCallbacksAndMessages(null);
    }

    public interface OnPracticesSelectedListener{
        void onPracticesSelected(String practicesId,int apiId);
    }
}
