package net.lzzy.practicesonline.activities.fragments;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;
import net.lzzy.practicesonline.activities.views.BarChartView;

import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/17.
 * Description:
 */
public class BarFragment extends BaseFragment {
    private List<QuestionResult> results;

    @Override
    protected void populate() {
        BarChartView barChartView=find(R.id.chart_bcv);
        int reght=0,extra=0,wrong=0,miss=0;
        for (QuestionResult questionResult:results){
            switch (questionResult.getType()){
                case RIGHT_OPTIONS:
                    reght++;
                    break;
                case EXTRA_OPTIONS:
                    extra++;
                    break;
                case WRONG_OPTIONS:
                    wrong++;
                    break;
                case MISS_OPTIONS:
                    miss++;
                    break;
            }
        }
        float[] DATA={reght,extra,wrong,miss};
        String[] AXIS={"正确", "多选","错选", "少选"};
        barChartView.setHorizontalAxis(AXIS);
        barChartView.setDataList(DATA,10);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bar;
    }

    @Override
    public void search(String kw) {

    }
}
