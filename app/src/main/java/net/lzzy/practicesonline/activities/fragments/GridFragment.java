package net.lzzy.practicesonline.activities.fragments;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AndroidException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.activities.PracticesActivity;
import net.lzzy.practicesonline.activities.activities.QuestionActivity;
import net.lzzy.practicesonline.activities.activities.ResultActivity;
import net.lzzy.practicesonline.activities.models.Option;
import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.view.PractceResult;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class GridFragment extends BaseFragment {
    public static final String RESULT = "result";
    private List<QuestionResult> results;
    private GridView gv;
    private OnGridSelectedListener listener;
    private TextView tvCircle;

    public static GridFragment newInstance( List<QuestionResult> results){
        GridFragment fragment=new GridFragment();
        Bundle args=new Bundle();
        args.putParcelableArrayList(RESULT,(ArrayList<? extends Parcelable>)results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            results= getArguments().getParcelableArrayList(RESULT);
        }
    }

    @Override
    protected void populate() {
        gv = find(R.id.fragment_grid_gv);
        GenericAdapter<QuestionResult> adapter=new GenericAdapter<QuestionResult>(getContext(),
                R.layout.grid_item,results) {
            @Override
            public void populate(ViewHolder viewHolder, QuestionResult result) {
                    TextView tv=viewHolder.getView(R.id.grid_item_tv);
                    viewHolder.setTextView(R.id.grid_item_tv,getPosition(result)+1+"");
                if (result.isRight()){
                    tv.setBackgroundResource(R.drawable.right);
                }else {
                    tv.setBackgroundResource(R.drawable.right_x);
                }
            }

            @Override
            public boolean persistInsert(QuestionResult result) {
                return false;
            }

            @Override
            public boolean persistDelete(QuestionResult result) {
                return false;
            }
        };
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onGridSelected(position);

            }
        });

        tvCircle = find(R.id.fragment_grid_circle);
        tvCircle.setOnClickListener(v -> listener.onChartFragment());
//        GenericAdapter<QuestionResult> adapter= new GenericAdapter<QuestionResult>(getContext(),
//                R.layout.grid_item,results) {
//            @Override
//            public void populate(ViewHolder viewHolder, QuestionResult result) {
//                int position=0;
//
//                viewHolder.setTextView(R.id.grid_item_tv, results.);
//
//            }
//
//            @Override
//            public boolean persistInsert(QuestionResult result) {
//                return false;
//            }
//
//            @Override
//            public boolean persistDelete(QuestionResult result) {
//                return false;
//            }
//        };
//        gv.setAdapter(adapter);
//        BaseAdapter adapter=new BaseAdapter() {
//
//            @Override
//            public int getCount() {
//                return results.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return results.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                if (convertView==null){
//                    convertView= LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent,false);
//                }
//                TextView textView=convertView.findViewById(R.id.grid_item_tv);
//                textView.setText(position+1+"");
//                QuestionResult questionResult=results.get(position);
//                if (questionResult.isRight()){
//                    textView.setBackgroundResource(R.drawable.right);
//                }else {
//                    textView.setBackgroundResource(R.drawable.right_x);
//                }
//                return convertView;
//            }
//        };
//        gv.setAdapter(adapter);

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_grid;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (OnGridSelectedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    +"必需实现OnGridSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public interface OnGridSelectedListener{
        void onGridSelected(int position);
        //Fragment切换
        void onChartFragment();
    }
}
