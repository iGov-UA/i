package org.igov.service.business.access;

public interface AccessDataService {

    public String setAccessData(String content);

    public String setAccessData(byte[] content);

    public String getAccessData(String sKey);

    public boolean removeAccessData(String sKey);
}
