## Использование таймеров
Для использования таймера с целью приостановки процесса, используем стандартный элемент **TimerCatchingEvent** (самостоятельный элемент схемы) который приостанавливает процесс до срабатывания таймера.

Для настройки эскалации или автопроброса процесса дальше на этап используем элемент **TimerBoundaryEvent** (крепится на юзертаску).  
Обязательно изменить автоматически создаваемый ID этого элемента  “boundarytimer1 ”на  id="escalationTimer1"  

[формат даты/времени](https://en.wikipedia.org/wiki/ISO_8601#Durations), задаваемый  на срабатывание таймера. 
-общие шаблоны в указанном стандарте:  
P[n]Y[n]M[n]DT[n]H[n]M[n]S  
P[n]W   
`P<date>T<time>  `

Период указывается в соответствующем теге :

Установим таймер на **конкретное дату и время** срабатывания
```xml
<boundaryEvent id="escalationTimer" name="Timer" attachedToRef="usertask1" cancelActivity="true">
  		<timerEventDefinition>`
    			<timeDate>2011-03-11T12:13:14</timeDate>
  		</timerEventDefinition>
	</boundaryEvent>
```
Установим таймер на срабатывание  через **период**
```xml
<boundaryEvent id="escalationTimer" name="Timer" attachedToRef="usertask1" cancelActivity="true">
  		<timerEventDefinition>
    			<timeDuration>PT5S</timeDuration>
  		</timerEventDefinition>
	</boundaryEvent>
```


