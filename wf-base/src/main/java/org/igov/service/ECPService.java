package org.igov.service;

import ua.privatbank.cryptonite.CryptoniteException;

public interface ECPService {

    public byte[] signFile(byte[] content) throws CryptoniteException;

}
