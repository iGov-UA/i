package org.igov.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import org.igov.service.exception.CRCInvalidException;

public class ToolLuna {

    private static final Logger LOG = LoggerFactory.getLogger(ToolLuna.class);

    private static int getLastDigit(Long inputNumber) {
        return (int) (inputNumber % 10);
    }

    private static int getCheckSumLastDigit(Long inputNumber) {
        int sum = sumDigitsByLuna(inputNumber);
        return sum % 10;//sumDigits(sum);
    }

    private static int sumDigitsByLuna(Long inputNumber) {
        int factor = 1;
        int sum = 0;
        int addend;
        while (inputNumber != 0) {
            addend = (int) (factor * (inputNumber % 10));
            factor = (factor == 2) ? 1 : 2;
            addend = addend > 9 ? addend - 9 : addend;
            sum += addend;
            inputNumber /= 10;
        }
        return sum;
    }

    public static long getProtectedNumber(long inputNumber) {
        return getCheckSumLastDigit(inputNumber) + inputNumber * 10;
    }

    public static long getOriginalNumber(long protectedNumber) {
        return protectedNumber / 10;
    }

    public static boolean checkProtectedNumber(Long inputNumber) {
        long originalNumber = getOriginalNumber(inputNumber);

        LOG.info("inputNumber / 10={}", originalNumber);
        LOG.info("(inputNumber={})", inputNumber);
        LOG.info("getLastDigit(inputNumber)={}", getLastDigit(inputNumber));
        LOG.info("getCheckSumLastDigit(inputNumber / 10)={}", getCheckSumLastDigit(originalNumber));
        return getCheckSumLastDigit(originalNumber) == getLastDigit(inputNumber);
    }

    public static void validateProtectedNumber(Long inputNumber) throws CRCInvalidException {
        if (!checkProtectedNumber(inputNumber)) {
            throw new CRCInvalidException();
        }
    }

    public static void validateProtectedNumber(Long inputNumber, String errorMessage) throws CRCInvalidException {
        if (!checkProtectedNumber(inputNumber)) {
            throw new CRCInvalidException(errorMessage);
        }
    }
    
    public static String getProtectedString(Long inputNumber, Long nID_subject, String sID_status) {
        return "" + inputNumber;
    }

    public static long getValidatedOriginalNumber(long protectedNumber) throws CRCInvalidException {
        validateProtectedNumber(protectedNumber);
        return getOriginalNumber(protectedNumber);
    }

    public static void main(String[] args) {
        Random random = new Random();
        int arrSize = 20;
        long[] testArray = new long[arrSize];
        long[] testProtectedArray = new long[arrSize];
        long currValue;
        System.out.println("Long.MAX_VALUE=" + Long.MAX_VALUE);
        for (int i = 0; i < arrSize; i++) {
            currValue = random.nextLong();
            while (currValue < 0 || currValue > Long.MAX_VALUE / 10) {
                currValue = random.nextLong();
            }
            testArray[i] = currValue;
            testProtectedArray[i] = getProtectedNumber(testArray[i]);
            System.out.println(">>test " + i
                    + ":   nID=" + testArray[i]
                    + ",   checkSum=" + getCheckSumLastDigit(testArray[i])
                    + ",   controlDigit=" + getCheckSumLastDigit(testArray[i] * 10)
                    + ",   nID_Protected=" + testProtectedArray[i]
                    + ",   checkResult=" + checkProtectedNumber(testProtectedArray[i]));
        }
    }
}
