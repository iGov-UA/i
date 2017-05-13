package org.igov.service.business.util;

import java.util.regex.Pattern;

public interface CustomRegexPattern {
    static final Pattern TAG_PAYMENT_BUTTON_LIQPAY = Pattern
            .compile("\\[paymentButton_LiqPay(.*?)\\]");
    static final Pattern TAG_sPATTERN_CONTENT_CATALOG = Pattern
            .compile("[a-zA-Z]+\\{\\[(.*?)\\]\\}");
    static final Pattern TAG_PATTERN_PREFIX = Pattern
            .compile("_[0-9]+");
    static final Pattern TAG_PATTERN_DOUBLE_BRACKET = Pattern
            .compile("\\{\\[(.*?)\\]\\}");
    static final String TAG_CANCEL_TASK = "[cancelTask]";
    static final String TAG_CANCEL_TASK_SIMPLE = "[cancelTaskSimple]";
    static final String TAG_nID_Protected = "[nID_Protected]";
    static final String TAG_sID_Order = "[sID_Order]";
    static final String TAG_nID_SUBJECT = "[nID_Subject]";
    static final String TAG_sDateCreate = "[sDateCreate]";
    // private static final String TAG_sURL_SERVICE_MESSAGE =
    // "[sURL_ServiceMessage]";
    static final Pattern TAG_sURL_SERVICE_MESSAGE = Pattern
            .compile("\\[sURL_ServiceMessage(.*?)\\]");
    static final Pattern TAG_sURL_FEEDBACK_MESSAGE = Pattern
            .compile("\\[sURL_FeedbackMessage(.*?)\\]");
    static final Pattern TAG_sPATTERN_CONTENT_COMPILED = Pattern
            .compile("\\[pattern/(.*?)\\]");
    static final String TAG_Function_AtEnum = "enum{[";
    static final String TAG_Function_To = "]}";
    static final String PATTERN_MERCHANT_ID = "sID_Merchant%s";
    static final String PATTERN_SUM = "sSum%s";
    static final String PATTERN_CURRENCY_ID = "sID_Currency%s";
    static final String PATTERN_DESCRIPTION = "sDescription%s";
    static final String PATTERN_EXPIRED_PERIOD_HOUR  = "nExpired_Period_Hour%s";
    static final String PATTERN_SUBJECT_ID = "nID_Subject%s";
    static final Pattern TAG_PATTERN_JSON_BRACKET = Pattern
            .compile("\\{.*?\\}");
    static final char[] CYR_ABC = {' ', '_', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 
        'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 
        'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 
        'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 
        'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 
        'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'ї', 'Ї', 'ґ', 'Ґ', 'і', 'І', '\'', 'є', 'Є'};
    static final String[] LAT_ABC = {" ", "_", "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", 
        "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B", "V",
        "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", 
        "Ts", "Ch", "Sh", "Sch", "", "I", "", "E", "Ju", 
        "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", 
        "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", 
        "S", "T", "U", "V", "W", "X", "Y", "Z", "ji", "Ji", "g", "G", "i", "I", "\'", "je", "Je"};
}
