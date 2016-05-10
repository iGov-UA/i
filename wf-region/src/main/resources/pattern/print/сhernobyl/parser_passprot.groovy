import java.util.regex.Matcher
import java.util.regex.Pattern

/* константные переменные для регулярных выражений*/
def regSeriaStr = "(([Сс]ерия|СЕРИЯ|[Сс]ерія|СЕРІЯ).\\s*)?"
def regPassoprt = "(([Пп]аспорт|ПАСПОРТ).\\w?.\\s*)?"
def regNumberStr = "(([Нн]омер|НОМЕР).\\s*)?"

def regSeriaVal = "[ЄІЇҐА-ЯA-Z]{2}"
def regNumberVal = "[0-9]{6}"
def regFrom = "(([Вв]ід|[Оо]т|ВІД|ОТ)\\D{0,4})?"
def regYearVal = "[0-9]{2}\\W[0-9]{2}\\W(19|20|21)?[0-9]{2}"

def patSerNum = Pattern.compile(regSeriaStr + "\\D{0,4}"
+ regNumberStr + "\\D{0,4}"
+ regPassoprt + "\\D{0,4}"
+ regSeriaVal + "\\D{0,4}"
+ regNumberStr + "\\D{0,4}"
+ regPassoprt + "\\D{0,4}" 
+ regNumberVal + "\\s*" /**/
)

def patDate = Pattern.compile(
regFrom + "\\s*" + 
regYearVal +"\\s*"
)

def str0 = execution.getVariable("bankIdPassport")

def strSerial = ""
def strNumber = ""
def strDate = ""
def strIssuedBy = ""

def matSerNum = patSerNum.matcher(str0)


if(matSerNum.find()){
    strSerialNumber = matSerNum.group()
    str0 = str0.replaceAll(strSerialNumber, "")
   /* */
strSerialNumber = strSerialNumber.replaceAll(regPassoprt, "")
strSerialNumber = strSerialNumber.replaceAll(regSeriaStr, "")
strSerialNumber = strSerialNumber.replaceAll(regNumberStr, "")
 /**/   

    def matSer = Pattern.compile(regSeriaVal).matcher(strSerialNumber)
    if(matSer.find()){
        strSerial = matSer.group()
    }

    def matNum = Pattern.compile(regNumberVal).matcher(strSerialNumber)
    if(matNum.find()){
        strNumber = matNum.group()
    }  
}

def matDate = patDate.matcher(str0)

if(matDate.find()){
    strDate = matDate.group()
    strIssuedBy = str0.replaceAll(strDate, "")
}

execution.setVariable("sPassportSeria",strSerial)
execution.setVariable("sPassportNumber",strNumber)
execution.setVariable("sPassportIssuedBy",strIssuedBy)
execution.setVariable("dPassportDate",strDate)
