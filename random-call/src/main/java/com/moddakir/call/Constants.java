package com.moddakir.call;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final float DEFAULT_TEXT_SIZE = 13.0f;
    public static String ONLINE = "1";
    public static String BUSY = "2";
    public static String OFFLINE = "3";
    public static String apikey = " bc037aec-4d8b-4945-9d91-df6824adaf49";
    public static int PAGE_SIZE = 20;
    public static String ENV = "dev-"; //Dev
//        public static String ENV = "stage-"; //Stage
//    public static String ENV = ""; //Production

    //        public static String BaseURL = "https://" + ENV + "api.moddakir.com/v9/api/";//production
//    public static String BASE_URL = "https://" + ENV + "api.moddakir.com/v5/api/";//dev
//    public static String SOCKET_URL = "https://" + ENV + "ws-io.moddakir.com";


    //Production v3

    public static String MECCA_TIME_ZONE = "Asia/Riyadh";
    public static String TERMS_Teacher = "https://moddakir.com/teacher-terms-conditions/";
    public static String TERMS_Student = "https://moddakir.com/student-terms-conditions/";

    public static final String SINCH_ENVIRONMENT = "clientapi.sinch.com";


    public static String invitationCode = null;
    public static String invitingID = null;
    public static String paymentTransactionId = "";


    public static final List<String> ARABIC_DAYS = Arrays.asList("السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعه");
    public static final List<String> ARABIC_MONTH = Arrays.asList("يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر");
    public static final List<String> ENGLISH_MONTH = Arrays.asList("January ", "February", "March ", "April ", "May", "June ", "July ", "August", "September", "October", "November", "December");
    public static final List<String> ENGLISH_DAYS = Arrays.asList("SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI");
    public static final String KEY_TYPE = "type";
    public static final String KEY_SIGN_UP = "sign_up";
    public static String CHANNEL_No_ID = "moddakir";
    public static String CHANNEL_Name = "moddakir_push_notification";
    public static String CHANNEL_Desc= "Moddakir Push Notification Android O or Above";

    public static String CHANNEL_ID = "Sinch Push Notification Channel";

    public static String CALL_IDS = "callIds";
    public static  String childData = "childData";



}
