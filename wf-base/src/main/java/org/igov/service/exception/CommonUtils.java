package org.igov.service.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtils {

    public static String getStringStackTrace( Exception oException ){
	String sStack = null;
	if ( oException != null ) {
	    StringWriter errors = new StringWriter();
	    oException.printStackTrace(new PrintWriter(errors));
	    sStack = errors.toString();
	}

	return sStack;
    }
}
