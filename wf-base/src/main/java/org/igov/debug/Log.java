/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.debug;

import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class Log {
    private final static Logger oLog = LoggerFactory.getLogger(GeneralConfig.class);
    public final static Logger oLogBig_In = LoggerFactory.getLogger(LogBig_In.class);
    public final static Logger oLogBig_Out = LoggerFactory.getLogger(LogBig_Out.class);
    
}
