# Listener

   * ${fileTaskUploadListener} - тянет ВСЕ атачи из стартовой формы. Указывать на первой Юзертаске.  
   * ${fileTaskInheritance} - слушатель тянет по ид атача атач на юзертаску. Указывать на второй и последующих Юзертасках, перечисляя все id необходимых аттачей. 
   * ![5_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_0.jpg)
   * ${CreateDocument_UkrDoc}
   * ${GetDocument_UkrDoc}
   * ${UpdateStatusTask}
   * ${DocumentInit_iDoc}  

***
### ${SetTasks}
листенер ${SetTasks} - ставится на закрытие таски, т.е. event="complete"  **(1, 2)**  
В этом листенере мы указываем какие поля из текущего БП передать в другой БП:  
**sTaskProcessDefinition** - сюда прописываем ИД БП в который нужно пробросить данные **(3)**  
далее перечисляем обязательные поля **(5)**  
**sID_Attachment**  
**sDateRegistration**  
**sDateDoc**  
**sName_SubjectRole**  
**sDateExecution**  
**processDefinitionId**  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNG44eU1GSmlkUjg)  
Так же в параметре листенера **soData** **(4)** можно передать другие поля, необходимые в процессе в формате:

```
sContent::${sContent};;sAutorResolution::${sAutorResolution};;
```  
разделитель между переменными - две точки с запятой.

***
### ${DocumentInit_iDoc}

листенер ${DocumentInit_iDoc} - ставится на открытие таски, т.е. event="create"  
Никаких дополнительных параметров листенера ставить не нужно  
``` xml
<activiti:taskListener event="create" delegateExpression="${DocumentInit_iDoc}"></activiti:taskListener>
```
Листенер тянет из файла json данные, которые задают права определенных групп на просмотр или редактирование отдельных полей в данном бизнес-процессе.  
Файл json должен иметь такое же имя как ИД БП, в котором установлен листенер.  
Например:  
**_doc_justice_171.bpmn**  
**_doc_justice_171.json**  
Путь, где должны хранится файлы json  
**i\wf-region\src\main\resources\pattern\document**  
Рассмотрим подробнее файл джейсона  
```java
{
  "_": {
    "MJU_Dnipro_Top1_Dep1_Exec1": {			// вот этому логину
      "sName": "Контроллирующий всех этапов",
      "bWrite": true,					//даны права на редактирование полей
      "asID_Field_Read": [
        "*"						//чтение всех полей
      ],
      "asID_Field_Write": [
        "*"						//редактировоание всех полей
      ]
    },
    "MJU_Dnipro_Top1_Dep1_Exec3": {			//вот этому логину
      "sName": "Основной контролирующий",
      "bWrite": true,					//даны права на редактирование полей
      "asMask_FieldID_Read": [				//чтение всех полей
        "*",						
        "!sID_Group_Activiti",				// кроме sID_Group_Activiti, nDeepLevel
        "!nDeepLevel"
      ],
      "asMask_FieldID_Write": [
        "sDateExecution",
        "sContent"
      ]
    }
  },
  "checker": {
    "MJU_Dnipro_Top1_Dep1_Exec5": {
      "sName": "Проверяющий",
      "bWrite": false
    }
  }
}
```
***
### ${UpdateStatusTask}
обновляет статус порожденной задачи  
обязательный параметр **sID_ProcessSubjectStatus**  
Все статусы задаются в файле: _i\wf-base\src\main\resources\data\ProcessSubjectStatus.csv_  
В енаме (saStatusTask) порожденной задачи должны присутствовать только статусы из этого файла и передаваться затем в переменную sID_ProcessSubjectStatus:
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sb1J3RUx6Ti1HSGc)
***
### ${UpdateStatusTaskTreeAndCloseProcess}
Листенер прикрепляется на процесс-родитель из которого порождаются задачи. В случае, если родительский процесс закрыт, то все незакрытые порожденные задачи автозакроются со статусом “неактуально”

***


# delegateExpression

   * ${assignGroup}
   * #{setMessageFeedback_Indirectly}
   * #{fileTaskUpload} - для электронных очередей. Достает дату из объекта в переменной типа _queueData_ и передает ее в системную переменную _date_of_visit_ . Сервистаска с этим выражением должна следовать сразу за стартивентом.
   * #{MailTaskWithoutAttachment} - для отправки емейлов без  вложений
   * ![5_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_1.jpg)
   * #{MailTaskWithAttachments} - для отправки емейлов c  вложениями
   * #{MailTaskWithAttachmentsAndSMS} - для отправки емейлов смс обязательно должно быть вложение, при отсутствии вложения в поле saAttachmentsForSend должен быть пробел " "
   * #{ProcessCountTaskListener}
   * #{SendObject_Corezoid_New}
   * #{releaseTicketsOfQueue} - При создании сервистаски с таким параметром инициализируется отмена заявки и высвобождение слота  электронной очереди по инициативе сотрудника или системы 
