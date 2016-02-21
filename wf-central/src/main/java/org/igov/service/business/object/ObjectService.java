/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.object;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ObjectService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ObjectService.class);
    
    
    //возвращает true, если аргументов нет
    public static boolean isArgsNull(Object... args) {
        boolean result = true;

        for (Object obj : args) {
            if (obj != null) {
                result = false;
                break;
            }
        }
        return result;
    }

 //выясняет совпадает ли значение аргумента с регулярным выражением 
    public static boolean isMatchSID(String sID, String regex) {
        Pattern pattern1 = Pattern.compile(regex);
        Matcher matcher = pattern1.matcher(sID);
        return matcher.matches();
    }

    //проверяет корректность введения единицы измерения

    public static boolean isMeasureCorrect(String sMeasure) {
        for (String str : measures) {
            if (sMeasure.equals(str)) {
                return true;
            }
        }
        return false;
    }    
    
    public static final String sid_pattern1 = "^\\d\\d\\d\\d(\\s\\d\\d){0,3}$";
    private static final String[] measures = {
        "кг",
        "брутто-реєстр.т",
        "вантажпідйом.метрич.т",
        "г",
        "г поділ.ізотоп",
        "кар",
        "кв.м",
        "кг N",
        "кг KOH",
        "кг NaOH",
        "кг K2O",
        "кг H2O2",
        "кг P2O5",
        "кг C5H14ClNO",
        "кг сух.90% реч",
        "кг U",
        "куб.м",
        "Ki",
        "л",
        "л 100% спирт",
        "м",
        "пар",
        "100 шт",
        "тис.куб.м",
        "тис.л",
        "тис.шт",
        "тис.кВт-год",
        "шт",
        "-"
    };    
}
