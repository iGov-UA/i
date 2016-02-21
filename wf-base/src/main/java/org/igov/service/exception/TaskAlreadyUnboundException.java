/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.exception;

/**
 *
 * @author bw
 */
public class TaskAlreadyUnboundException extends Exception {//private static 
    public TaskAlreadyUnboundException(String message) {
        super(message);
    }
}
