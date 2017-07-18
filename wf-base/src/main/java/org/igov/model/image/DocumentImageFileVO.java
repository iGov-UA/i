package org.igov.model.image;

/**
 *
 * @author Kovylin
 */
public class DocumentImageFileVO {

    public DocumentImageFileVO() {
    }
    
    private String sURL;
    
    private Long nID;
    
    private String sSecret;
    
    private String sHash;
    
    private String sKey_File;
    
    private String sID_FileStorage;
    
    private String sFileType;

    private String sFileExtension;
    
    private Long nBytes;
    
    private String sDateSave;
    
    //aDocumentImageFileSign //массив объектов DocumentImageFileSign, которые связаны с DocumentImageFile (в каждом объекте должен быть под-объект oSignType, связанный по nID_SignType)

    public String getsURL() {
        return sURL;
    }

    public Long getnID() {
        return nID;
    }

    public String getsSecret() {
        return sSecret;
    }

    public String getsHash() {
        return sHash;
    }

    public String getsKey_File() {
        return sKey_File;
    }

    public String getsID_FileStorage() {
        return sID_FileStorage;
    }

    public String getsFileType() {
        return sFileType;
    }

    public String getsFileExtension() {
        return sFileExtension;
    }

    public Long getnBytes() {
        return nBytes;
    }

    public String getsDateSave() {
        return sDateSave;
    }

    public void setsURL(String sURL) {
        this.sURL = sURL;
    }

    public void setnID(Long nID) {
        this.nID = nID;
    }

    public void setsSecret(String sSecret) {
        this.sSecret = sSecret;
    }

    public void setsHash(String sHash) {
        this.sHash = sHash;
    }

    public void setsKey_File(String sKey_File) {
        this.sKey_File = sKey_File;
    }

    public void setsID_FileStorage(String sID_FileStorage) {
        this.sID_FileStorage = sID_FileStorage;
    }

    public void setsFileType(String sFileType) {
        this.sFileType = sFileType;
    }

    public void setsFileExtension(String sFileExtension) {
        this.sFileExtension = sFileExtension;
    }

    public void setnBytes(Long nBytes) {
        this.nBytes = nBytes;
    }

    public void setsDateSave(String sDateSave) {
        this.sDateSave = sDateSave;
    }
    
}
