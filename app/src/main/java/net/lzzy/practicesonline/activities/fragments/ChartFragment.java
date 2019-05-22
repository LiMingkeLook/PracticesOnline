package net.lzzy.practicesonline.activities.fragments;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.DragAndDropPermissions;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;
import net.lzzy.practicesonline.activities.models.view.WrongType;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class ChartFragment extends BaseFragment {

    public static final String RESULT = "result";
    private List<QuestionResult> results;
    private OnChartSelectedListener listener;
    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;
    private String[] titles=new String[]{"正确与错误的比例（单位：%）","题目阅读量统计","题目错误类型统计"};
    private Chart[] charts;
    private int reght=0;
    private float touchX1;
    private int chartIndex=0;


    public static ChartFragment newInstance( List<QuestionResult> results){
        ChartFragment fragment=new ChartFragment();
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

        for (QuestionResult questionResult:results){
            if (questionResult.isRight()){
                reght++;
            }
        }

    }
    @Override
    protected void populate() {
        TextView tvCircle=find(R.id.fragment_chart_circle);
        tvCircle.setOnClickListener(v -> {
            if (listener!=null){
                listener.onGridFragment();
            }
        });
        initCharts();
        configPieChart();
        displayPieChart();
        configBarLineChart(lineChart);
        configBarLineChart(barChart);
        displayBarChart();
        displayLineChart();


        pieChart.setVisibility(View.VISIBLE);
        View dot1=find(R.id.fragment_chart_dot1);
        View dot2=find(R.id.fragment_chart_dot2);
        View dot3=find(R.id.fragment_chart_dot3);
        View[] dots=new View[]{dot1,dot2,dot3};
        find(R.id.fragment_chart_pie).setOnTouchListener(new ViewUtils.AbstractTouchHandler() {
            private static final float MIN_DISTANCE = 100;

            @Override
            public boolean handleTouch(MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    touchX1 = event.getX();
                }
                if (event.getAction()==MotionEvent.ACTION_UP){
                    float touchX2=event.getX();
                    if (Math.abs(touchX2-touchX1)>MIN_DISTANCE){
                        if (touchX2<touchX1){
                            if (chartIndex<charts.length-1){
                                chartIndex++;
                            }else {
                                chartIndex=0;
                            }
                        }else {
                            if (chartIndex>0){
                                chartIndex--;
                            }else {
                                chartIndex=charts.length-1;
                            }
                        }
                        switchChart();
                    }
                }
                return true;
            }

            private void switchChart() {
                for (int i=0;i<charts.length;i++){
                    if (chartIndex==i){
                        charts[i].setVisibility(View.VISIBLE);
                        dots[i].setBackgroundResource(R.drawable.dot_fill_style);
                    }else {
                        charts[i].setVisibility(View.GONE);
                        dots[i].setBackgroundResource(R.drawable.dot_style);
                    }
                }
            }
        });


    }
    //显示折线图
    private void displayLineChart() {
        ValueFormatter xFrmatter=new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Q."+(int)value;
            }
        };
        lineChart.getXAxis().setValueFormatter(xFrmatter);
        List<Entry> entries=new ArrayList<>();
        for (int i=0;i<results.size();i++){
            int times= UserCookies.getInstance().getReadCount(results.get(i).getQuestionId().toString());
            entries.add(new Entry(i+1,times));
        }
        LineDataSet dataSet=new LineDataSet(entries,"");
        dataSet.setColor(Color.RED);
        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setValueTextSize(9f);
        LineData data=new LineData(dataSet);
        lineChart.setData(data);


    }
    //显示柱状图
    private void displayBarChart() {
        ValueFormatter xFormatter=new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return WrongType.getInstance((int)value).toString();
            }
        };
        barChart.getXAxis().setValueFormatter(xFormatter);


        int right=0,miss=0,extra=0,wrong=0;
        for (QuestionResult questionResults:results){
            switch (questionResults.getType()){
                case RIGHT_OPTIONS:
                    right++;
                    break;
                case MISS_OPTIONS:
                    miss++;
                    break;
                case EXTRA_OPTIONS:
                    extra++;
                    break;
                case WRONG_OPTIONS:
                    wrong++;
                    break;
                default:
                    break;
            }
        }
        List<BarEntry> entries=new ArrayList<>();
        entries.add(new BarEntry(0,right));
        entries.add(new BarEntry(1,miss));
        entries.add(new BarEntry(2,extra));
        entries.add(new BarEntry(3,wrong));

        BarDataSet dataSet=new BarDataSet(entries,"查看类型");
        dataSet.setColors(Color.GREEN,Color.GRAY,Color.DKGRAY,Color.LTGRAY);
        ArrayList<IBarDataSet> dataSets=new ArrayList<>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.8f);
        barChart.setData(data);
        barChart.invalidate();

    }

    //配置条线图
    private void configBarLineChart(BarLineChartBase chart) {
        XAxis xAxis=chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(11f);
        xAxis.setGranularity(1f);

        /**  Y 轴 **/
        YAxis yAxis=chart.getAxisLeft();
        yAxis.setLabelCount(8,false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextSize(11f);
        yAxis.setGranularity(1f);
        yAxis.setAxisMinimum(0);

        /** chart属性 **/
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setPinchZoom(false);

    }

    //饼图显示
    private void displayPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(reght,"正确"));
        entries.add(new PieEntry(results.size()-reght,"错误"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        dataSet.setColors(Color.GREEN,Color.RED);
        dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        pieChart.highlightValues(null);
        pieChart.invalidate();


    }
    //配置饼图
    private void configPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
    }

    private void initCharts() {
        pieChart = find(R.id.fragment_charts_pie);
        barChart = find(R.id.fragment_charts_bar);
        lineChart = find(R.id.fragment_charts_line);
        charts = new Chart[]{pieChart,lineChart,barChart};
        int i=0;
        for (Chart chart:charts){
            chart.setTouchEnabled(false);
            chart.setVisibility(View.GONE);
            Description desc=new Description();
            desc.setText(titles[i++]);
            chart.setDescription(desc);
            chart.setNoDataText("数据获取中......");
            chart.setExtraOffsets(5,10,5,25);
        }

    }


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_chart;
    }

    @Override
    public void search(String kw) {

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (ChartFragment.OnChartSelectedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    +"必需实现OnChartSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public interface OnChartSelectedListener{
        void onGridFragment();
    }
}
