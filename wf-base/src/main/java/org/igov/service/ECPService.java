package org.igov.service;


public interface ECPService {

    public byte[] signFile(byte[] content) throws Exception;

    public byte[] signFileByCustomSign(byte[] content, byte[] keyFile, byte[] certFile, String sPassword) throws Exception;

}
