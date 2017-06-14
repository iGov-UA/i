package org.igov.service.business.util;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Класс для описания форматов дат
 * @author inna
 *
 */
public interface DateUtilFormat {
	public final static List<String> formats = Lists.newArrayList("yyyy-MM-dd", "dd-MM-yyyy", "yyyy/MM/dd", "dd/MM/yyyy", "yyyy.MM.dd", "yyyyMMdd",
			"dd.MM.yyyy", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "EEE MMM dd HH:mm:ss zzz yyyy");
	public final static String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
    public final static String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
    public final static String DATE_FORMAT_dd_SLASH_MM_SLASH_yyyy = "dd/MM/yyyy";
}
