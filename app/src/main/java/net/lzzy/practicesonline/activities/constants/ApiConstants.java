package net.lzzy.practicesonline.activities.constants;

import net.lzzy.practicesonline.activities.utils.AppUtils;

import java.net.URL;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/15.
 * Description:
 */
public class ApiConstants {
    //"http://10.88.91.102:8888"
    private static final String IP= AppUtils.loadServerSetting(AppUtils.getContext()).first;
    private static final String PORT=AppUtils.loadServerSetting(AppUtils.getContext()).second;
    private static final String PROTOCOL="http://";
    //API地址
    public static final String URL_API=PROTOCOL.concat(IP).concat(":").concat(PORT);

    //practices地址
    private static final String ACTION_PRACTICES="/api/practices";
    public static final String URL_PRACTICES= URL_API.concat(ACTION_PRACTICES);

    //question地址
    private static final String ACTION_QUESTIONS="/api/pquestions?practiceid=";
    public static final String URL_QUESTIONS= URL_API.concat(ACTION_QUESTIONS);

    //提交结果
    private static final String ACTION_RESULT="/api/result/PracticeResult";
    public static final String URL_RELT=URL_API.concat(ACTION_RESULT);

    //practice的json标签
    public static final String JSON_PRACTICE_API_ID="Id";
    public static final String JSON_PRACTICE_NAME="Name";
    public static final String JSON_PRACTICE_OUTLINES="OutLines";
    public static final String JSON_PRACTICE_QUESTION_COUNT="QuestionCount";

    public  static final String JSON_QUESTION_ANALYSIS="Analysis";
    public  static final String JSON_QUESTION_CONTENT="Content";
    public  static final String JSON_QUESTION_TYPE="QuestionType";
    public  static final String JSON_QUESTION_OPTIONS="Options";
    public  static final String JSON_QUESTION_ANSWER="Answers";

    public  static final String JSON_OPTION_CONTENT="Content";
    public  static final String JSON_OPTION_LABEL="Label";
    public  static final String JSON_OPTION_API_ID="Id";
    public  static final String JSON_ANSWER_OPTION_ID="OptionId";

    public static final String JSON_RESULT_API_ID="PracticeID";
    public static final String JSON_RESULT_SCORE_RATIO="ScroreRatio";
    public static final String JSON_RESULT_WRONG_IDS="WrongQuestionIds";
    public static final String JSON_RESULT_PERSON_INFO="PhoneNo";

}
