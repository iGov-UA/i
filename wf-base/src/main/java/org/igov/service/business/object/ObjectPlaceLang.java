package org.igov.service.business.object;

public enum ObjectPlaceLang {
    UAN("УКРАЇНСЬКА"), RUS("РУССКИЙ"), ENG("АНГЛИЙСКИЙ"), KAT("ГРУЗИНСКИЙ"), LAV("ЛАТЫШСКИЙ");

    private String descr;

    private ObjectPlaceLang(String descr) {
        this.descr = descr;
    }

    public String getDescription() {
        return this.descr;
    }    
}
