var NodeCache = require("node-cache" );
//var uuid = require("uuid");
var crypto = require('crypto');

var adminKeysCache = new NodeCache();
var cacheKey = 'admin-keys-map';
// ��� �������
var aAdminInn = [
	'3119325858'
	,'2943209693' //Белявцев
	,'3167410996' //Забрудский
        ,'2817305057' //Свидрань
        ,'3075311805' //Грек
        ,'3432314483' //БА Федько Анастасия Сергеевна оператор БО
        ,'3100218921' //БА Магоня Ирина Александровна оператор БО
        ,'3007605949' //БА Зимовец Полина Владимировна оператор БО
        ,'3118718406' //БА Салимова Лейли Александровна оператор БО
        ,'3347002733' //БА Божко Владимир Сергеевич супервизор
        ,'3274107439' //Реутов Сергій Валерійович Загран КрРог
        ,'2490717289' //Бурлака Валентина Вячеславівна Загран КрРог
        ,'2701508303' //Бура Ганна Вячеславівна Загран КрРог
        ,'3227221389' //Легкошерст Ю.С. Загран КрРог
        ,'2745115749' //Людмила Довгинцівський РВ Загран КрРог
        ,'3354912955' //Дмитро Олегович Загран Кременець
        ,'3145719001' //Шмига Тетяна Віталіївна Загран Кременець
        ,'3298407213' //Невстокай Олександр Сергійович оператор БО
        ,'2506700205' //Соколова Ирина Георгиевна оператор БО
        ,'3303004002' //Мельничук Анжелика Игоревна оператор БО
        ,'3340312504' //Романькова Юлия Валериевна оператор ФО
        ,'3199006657' //Горб Игорь Валентинович оператор ФО
        ,'3139206241' //Голубь Ольга Вячеславовна оператор ФО
        ,'2794700544' //Момот Ольга Вячеславовна оператор ФО
        ,'3318210825' //Донець Олена Миколаївна оператор ФО
        ,'2947005183' //Гапоненко Ольга Вікторовна оператор ФО
        ,'3440714805' //София Данилевич БА Тернополь
        ,'3221220890' //Яковлев Максим загран КрРог
        ,"3409307027" //СИМОН АЛЕКСАНДРА СЕРГЕЕВНА ДМС ДнепрРайон
        ,"2811011402" //УСЕНКО ВАЛЕНТИНА НИКОЛАЕВНА ДМС ДнепрРайон
        ,"2612400336" //Август
        ,"2520400407" //Перебора Елена Николаевна оператор ФО
        ,"2209413811" //Яланський Алім Олександрович оператор ФО
        ,"3494104123" //Вискворцова Світлана Юріївна оператор ФО
        ,"2831100257" //Смоктий Кирилл Викторович БА
        ,"3101717771" //Жиган Роман Сергеевич БА
        ,"2541900347" //Гермаш Анжела Володимирівна ЗАГС
        ,"3049907728" //Чигринець Юлія Юріївна ЗАГС
        ,"2249500648" //Хоменко Наталія Миколаївна ЗАГС
        ,"2778600188" //Граб Наталя Вікторівна ЗАГС
        ,"2539216269" //Кіча Оксана Семенівна ЗАГС
        ,"3332702409" //Шапіро Яна Андріївна ЗАГС
        ,"2664017580" //Сагайдак Валерія Геннадіївна ЗАГС
        ,"3274415665" //Мартиросян Яна Робертівна ЗАГС
        ,"3443403546" //Шоломіцька Катерина Олександрівна ЗАГС
        ,"2650822029" //Пряхіна Оксана Юріївна ЗАГС
        ,"3189309384" //Ляпченко Любов Олексіївна ЗАГС
        ,"2309401607" //Цяпало Наталія Михайлівна
        ,"3017805509" //Сидоренко Катерина Геннадіївна
        ,"2895119507" //Шеян Ганна Олександрівна
        ,"3273818166" //Слюсаренко Ірина Петрівна
        ,"2963801608" //Жилка Тетяна Павлівна
        ,"3139808662" //Бордюг Ганна Сергіївна
        ,"3099807585" //Ніколаєнко Ольга Миколаївна
        ,"3203800421" //Носова Ганна Анатоліївна
        ,"3191819764" //Дацько Катерина Сергіївна
        ,"2954217947" //Євтушенко Леся Федорівна Психлікарня
        ,"3171200740" //Слободян Леся Антонівна Психлікарня
        ,"3241718226" //Золотова Татьяна Александровна БА
        ,"3007204044" //Кушіль Ірина Володимирівна  ЗАГС
        ,"3051311394" //Концевий Ярослав Михайлович  волонтер УПСЗН м.Херсон
        ,"3351117004" //Заводовська Анастасія Ігорівна ЗАГС
        ,"2235509967" //Третяк Ольга Петрівна  ЗАГС
        ,"3038024701" //Алєксєєва Олена Миколаївна  ЗАГС
        ,"3165800702" //Жариченко Юлія Василівна ЗАГС
        ,"2170410148" //Залевська Наталія Леонідівна  ЗАГС
        ,"2956103047" //Цивата Мар’яна Володимирівна  ЗАГС
        ,"2322517781" //Майхват Алла Василівна ЗАГС
        ,"3202900661" //Колесник Дарина Олегівна  ЗАГС
        ,"3085300444" //Медяник Вікторія Сергіївна ЗАГС
        ,"3421208522" //Грань Марія Андріївна ЗАГС
        ,"3351117004" //Заводовська Анастасія Ігорівна  ЗАГС
        ,"3271016443" //Чичиркина Марія Миколаївна  ЗАГС
        ,"2982303227" //Зеркаль Лілія Анатоліївна ЗАГС
        ,"2999519965" //Вербовська Катерина Василівна ЗАГС
        ,"3072400084" //Макогон Ірина Володимирівна ЗАГС
        ,"3156715068" //Літвінова Тетяна Сергіївна ЗАГС
        ,"2969708583" //Кошарова Оксана Олександрівна ЗАГС
        ,"3151717264" //Молчанова Наталія Олексіївна ЗАГС
        ,"3187520923" //Саєнко Наталя Юріївна ЗАГС
        ,"3163127189" //Сковородка Надія Олександрівна ЗАГС
        ,"2796300242" //Король Олена Михайлівна ЗАГС
        ,"3018006729" //Карпенко Ольга Олександрівна ЗАГС
        ,"3177515189" //Остапенко Наталія Володимирівна ЗАГС
        ,"3293108543" //Денисюк Олена Миколаївна ЗАГС
        ,"3210220627" //Пронько Людмила Володимирівна ЗАГС
        ,"3420112300" //Суханова Вероніка Євгеніївна ЗАГС
        ,"3094419782" //Ткач Олеся Геннадіївна ЗАГС
        ,"3151522969" //Сербаєва Неля Анатоліївна ЗАГС
        ,"3085517147" //Рогожникова Есміра Шахмірівна ЗАГС
        ,"2762905506" //Коваленко Ліна Анатоліївна ЗАГС
        ,"3148901683" //Кортікова Надія Віталіївна ЗАГС
        ,"2652612209" //Богомолова Людмила Миколаївна ЗАГС
        ,"3119700382" //Воробйова Олена Ігорівна ЗАГС
        ,"3180400301" //Іщенко Юлія Павлівна ЗАГС
        ,"3001108528" //Постоєнко Людмила Вікторівна  ЗАГС
        ,"3099604989" //Явтушенко Ольга Анатоліївна  ЗАГС
        ,"3390200942" //Балаба Валерія Андріївна ЗАГС
        ,"2769004407" //Гольденберг Алла Анатоліївна ЗАГС
        ,"3046508482" //Клепікова Катерина Павлівна ЗАГС
        ,"3176104789" //Кучеренко Лариса Миколаївна ЗАГС
        ,"2936610461" //Абальмасова Лариса Миколаївна ЗАГС
        ,"3377701921" //Рябініна Марія Миколаїівна ЗАГС
        ,"2923514989" //Єфіменко Юлія Олександрівна ЗАГС
        ,"3148514529" //Смірницька Тамара Валентинівна ЗАГС
        ,"2947019068" //Безвезюк Алла Володимирівна ЗАГС
        ,"3126905423" //Золотова Ольга Леонідівна ЗАГС
        ,"2850606927" //Касимова Тамара Володимирівна ЗАГС
        ,"2900902409" //Шевченко Тетяна  Михайлівна ЗАГС
        ,"2870722027" //Шевченко Олена Пилипівна ЗАГС
        ,"2976303363" //Вікуліна Тетяна Вікторівна ЗАГС
        ,"3267701229" //Олефір Яна Петрівна ЗАГС
        ,"2776304328" //Коваленко Ангеліна Володимирівна ЗАГС
        ,"3155500189" //Шевченко Вікторія Олександрівна  ЗАГС
        ,"3148318821" //Колісник Яна Олександрівна  ЗАГС
        ,"3365712981" //Франщук Вікторія Сергіївна  ЗАГС
        ,"2808607440" //Баганець Антоніна Борисівна  ЗАГС
        ,"3003804605" //Сабєльнікова Людмила Михайлівна ЗАГС
        ,"3033400971" //Маковецький Михайло Павлович ЗАГС
        ,"3057000486" //Підгайна Тетяна Михайлівна ЗАГС
        ,"3406403065" //Лисиця Анна Дмитрівна ЗАГС
        ,"3054700220" //Колот Тетяна Миколаївна ЗАГС
        ,"2942005330" //Аксьонов Юрій Володимирович  ЗАГС
        ,"2946216060" //Назаренко Леся Володимирівна ЗАГС
        ,"3207802703" //Іванченко Дар’я Олександрівна ЗАГС
        ,"3152720101" //Симонович Юлія Олександрівна  ЗАГС
        ,"3350300062" //Новицька Яна Леонідівна ЗАГС
        ,"3417101600" //Макарчук Анна Олександрівна ЗАГС
        ,"3050113509" //Стрічка Олена Олександрівна ЗАГС
        ,"3139601404" //Таран Альона Сергіївна ЗАГС
        ,"2240920007" //Акулова Олена Володимирівна   ЗАГС
        ,"2762412587" //Папченко Світлана Михайлівна  ЗАГС
        ,"3374913647" //Дмитріва Анна Степанівна ЗАГС
        ,"2344008365" //Іосипова Наталя Сергіївна ЗАГС
        ,"2647107661" //Береза Тетяна Григорівна ЗАГС
        ,"2779107920" //Соколенко Анжела Іванівна ЗАГС
        ,"2413108167" //Потарська Світлана Ярославівна ЗАГС
        ,"3272805622" //Катериніч Юлія Миколаївна ЗАГС
        ,"3369213003" //Кукса Оксана Олегівна ЗАГС
        ,"2670806559" //Кіріченко Світлана Іванівна ЗАГС
        ,"2679706167" //Шамбарова Світлана Василівна  ЗАГС
        ,"3149218849" //Голомідова Тетяна Євгеніївна  ЗАГС
        ,"3398608488" //Аранська Катерина Олександрівна  ЗАГС
        ,"2971012825" //Швайко Олена Миколаївна ЗАГС
        ,"2391615327" //Турченко Валентина Іванівна ЗАГС
        ,"3210801703" //Федоренко Альона Анатоліївна ЗАГС
        ,"2560407220" //Богданова Альона Степанівна  ЗАГС
        ,"3102119981" //Баранова Катерина Іванівна  ЗАГС
        ,"2988609701" //Титикало Олександра Юріївна  ЗАГС
        ,"3275313322" //Золотар Анна Миколаївна  ЗАГС
        ,"2754414187" //Кислова Вікторія Петрівна  ЗАГС
        ,"3207318822" //Овдієнко Тетяна Володимирівна  ЗАГС
        ,"2963909182" //Солоха Олена Олексіївна  ЗАГС
        ,"2201109749" //Грушина Тетяна Петрівна  ЗАГС
        ,"3459703741" //Свириденко Діана Веніамінівна ЗАГС
        ,"3456711522" //Якименко Анастасія Юріївна ЗАГС
        ,"2886305246" //Алієва Олена Віталіївна  ЗАГС
        ,"3343412280" //Богуславська Вікторія Михайлівна ЗАГС
        ,"2561606741" //Ромашкіна Оксана Олександрівна ЗАГС
        ,"2933216960" //Бережнова Тетяна Леонідівна  ЗАГС
        ,"3236521424" //Валіахметова Тетяна Олександрівна ЗАГС
        ,"2523307061" //Колкунова Тетяна Іванівна ЗАГС
        ,"3107209021" //Пазіна Людмила Миколаївна ЗАГС
        ,"3285713568" //Моренець Ольга Вікторівна ЗАГС
        ,"3377701921" //Рябініна Марія Олександрівна ЗАГС
        ,"3313916847" //Караченцева Анастасия Вікторівна ЗАГС
        ,"2350117007" //Сідєльцева Людмила Віталіївна ЗАГС
        ,"3109116104" //Водоп'янова Юлія Вікторівна ЗАГС
        ,"3414206545" //Різник Марина Миколаївна ЗАГС
        ,"3292516420" //Павленко Юлия iGov
        ,"3069121049" //Розискул Алла Володимирівна ЦНАП Олешківський рн
        ,"2321612243" //Петренко Світлана Іванівна ЦНАП Запоріжжя
        ,"3308103967" //Повищая Тетяна Олексіївна ЦНАП Запоріжжя
        ,"2499808349" //Заєць Ольга Юлиановна Каховка міська рада
        ,"3214509257" //Кулиш Андрей Юрійович ПМ
        ,"3087516425" //Золотухіна Оксана Сергіївна Херсон
        
             
];

var getAdminKeys = function () {
	var result = adminKeysCache.get(cacheKey);
	if (!result) {
		result = {};
		setAdminKeys(result);
	}
	return result;
};

var setAdminKeys = function (value) {
	adminKeysCache.set(cacheKey, value);
};

var generateAdminToken = function (inn) {
	var unhashed = inn + (new Date()).toString();
	var result = crypto.createHash('sha1').update(unhashed).digest('hex'); //uuid.v1();
	var keys = getAdminKeys();
	keys[inn] = result;
	setAdminKeys(keys);
	return result;
};

var isAdminInn = function(inn) {
	return aAdminInn.indexOf(inn) > -1;
};

var Admin = function() {

};

Admin.generateAdminToken = generateAdminToken;
Admin.isAdminInn = isAdminInn;

module.exports = Admin;
