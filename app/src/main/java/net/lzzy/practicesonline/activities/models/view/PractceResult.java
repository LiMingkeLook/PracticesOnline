package net.lzzy.practicesonline.activities.models.view;

import android.os.Build;

import androidx.annotation.RequiresApi;

import net.lzzy.practicesonline.activities.constants.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/8.
 * Description:
 */
public class PractceResult {
    public static final String STRING = ",";
    private List<QuestionResult> results;
    private int id;
    private String info;
    public PractceResult(List<QuestionResult> results,int apiId,String info){
        this.id=apiId;
        this.info=info;
        this.results=results;
    }

    public List<QuestionResult> getResults() {
        return results;
    }

    public void setResults(List<QuestionResult> results) {
        this.results = results;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private double getRation(){
        //算分数
        int rightCount=0;
        for (QuestionResult result:results){
            if (result.isRight()){
                rightCount++;
            }
        }
        return rightCount*1.0/results.size();
}

    private String getWrongOrders(){
        //错误题目的序号，比如1、2、3
        int i=0;
        String ids="";
        for (QuestionResult result:results){
            i++;
            if (!result.isRight()){
              ids=ids.concat(i+ STRING);
            }
        }
        if (ids.endsWith(STRING)){
            ids=ids.substring(0,ids.length()-1);
        }
        return ids;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json=new JSONObject();
        //调用put方法赋值
        json.put(ApiConstants.JSON_RESULT_API_ID,id);
        json.put(ApiConstants.JSON_RESULT_PERSON_INFO,info);
        json.put(ApiConstants.JSON_RESULT_SCORE_RATIO,
                new DecimalFormat("#.00").format(getRation()));
        json.put(ApiConstants.JSON_RESULT_WRONG_IDS,getWrongOrders());
        return json;
    }
}
