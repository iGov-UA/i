package org.igov.io.db.kv.temp.exception;

/**
 * Ошибки хранилища "инмемори"
 *
 * @author BW
 *
 */
public class RecordInmemoryException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Статус ошибки
     */
    public static final String STATE_ERROR = "ERROR";

    
    /**
     * Код ошибки - кривой входящий запрос
     */
    public static final String CODE_INVALID_REQUEST = "INVALID_REQUEST";
    /**
     * Код ошибки - нет данных
     */
    public static final String CODE_EMPTY_DATA = "EMPTY_DATA";
    /**
     * Код обработки - ошибка обработки
     */
    public static final String CODE_PROCESS_ABORTED = "PROCESS_ABORTED";

    /**
     * Код ошибки -ошибка сервиса
     */
    public static final String CODE_UNKNOWN_EXCEPTION = "UNKNOWN_EXCEPTION";
    /**
     * Код ошибки - ключ не найден
     */
    public static final String CODE_KEY_NOT_FOUND = "KEY_NOT_FOUND";

    
    /**
     *
     */
    private String sState;
    private String sCode;
    private String sMessageCustom;

    public RecordInmemoryException() {
        super();
    }

    public RecordInmemoryException(String sMessageCustom) {
        super(sMessageCustom);
        this.sMessageCustom = sMessageCustom;
    }

    public RecordInmemoryException(String sCode, String sMessageCustom) {
        super(sMessageCustom);
        this.sState = STATE_ERROR;
        this.sCode = sCode;
        this.sMessageCustom = sMessageCustom;
    }

    public RecordInmemoryException(String prState, String sCode, String sMessageCustom) {
        super(sMessageCustom);
        this.sState = prState;
        this.sCode = sCode;
        this.sMessageCustom = sMessageCustom;
    }

    public RecordInmemoryException(String sCode, String sMessageCustom, Throwable cause) {
        super(sMessageCustom, cause);
        this.sState = STATE_ERROR;
        this.sCode = sCode;
        this.sMessageCustom = sMessageCustom;
    }

    @Override
    public String getMessage() {
        return "[" + this.getCode() + "] " + this.getMessageCustom();
    }

    /**
     * @return the sState
     */
    public String getState() {
        return sState;
    }

    /**
     * @param prState the sState to set
     */
    public void setState(String prState) {
        this.sState = prState;
    }

    /**
     * @return the sCode
     */
    public String getCode() {
        return sCode;
    }

    /**
     * @param sCode the sCode to set
     */
    public void setCode(String sCode) {
        this.sCode = sCode;
    }

    /**
     * @return the sMessage
     */
    public String getMessageCustom() {
        return sMessageCustom;
    }

    /**
     * @param sMessageCustom the sMessage to set
     */
    public void setMessageCustom(String sMessageCustom) {
        this.sMessageCustom = sMessageCustom;
    }

}
