package org.igov.digitalsignature;

import ua.privatbank.cryptonite.CryptoniteException;

public interface ECPService {

    public byte[] signFile(byte[] content) throws CryptoniteException;

}
