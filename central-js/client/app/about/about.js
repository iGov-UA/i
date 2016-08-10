angular.module('about').config(function ($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
//  if (statesRepositoryProvider.isCentral()) {
    $stateProvider
      .state('index.about', {
        url: 'about',
        resolve: {
          title: function (TitleChangeService) {
            TitleChangeService.defaultTitle();
          }
        },
        views: {
          'main@': {
            templateUrl: 'app/about/about.html',
            controller: 'ServiceHistoryReportController'
          }
        }
      })
      .state('index.test', {
        url: 'test',
        views: {
          'main@': {
            templateUrl: 'app/about/test.html',
            controller: 'TestController'
          }
        }
      });
//  }
});

angular.module('about').controller('aboutController', function ($scope, $http) {

  var oGroups =  {
      sName: "IT-волонтери iGov",
      a:[{
          sName: "Регіональні волонтери iGov",
          a:[{
              sID:"volyn",
              sName: "Волинська область",
                a:[{
                  sID:"lutsk",
                  sName: "Луцьк",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
          },
            {
              sID:"dnipro",
              sName: "Дніпропетровська область",
                a: [{
                  sID: "dnipro",
                  sName: "Дніпро",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"kyiv",
              sName: "Київська область",
                a:[{
                  sID: "kyiv",
                  sName: "Київ",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "vyshgorod",
                  sName: "Вишгород",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "bucha",
                  sName: "Буча",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "bila-tserkva",
                  sName: "Біла Церква",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "makarov",
                  sName: "Макаров",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"kharkiv",
              sName: "Харківська область",
                a: [{
                  sID: "kharkiv",
                  sName: "Харків",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"lviv",
              sName: "Львівська область",
                a: [{
                  sID: "lviv",
                  sName: "Львів",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "chervonograd",
                  sName: "Червоноград",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"odesa",
              sName: "Одеська область",
                a: [{
                  sID: "odesa",
                  sName: "Одеса",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"mykolaiv",
              sName: "Миколаївська область",
                a: [{
                  sID: "mykolaiv",
                  sName: "Миколаїв",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"chernigiv",
              sName: "Чернігівська область",
                a: [{
                  sID: "chernigiv",
                  sName: "Чернігів",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"donetsk",
              sName: "Донецька область",
                a: [{
                  sID: "donetsk",
                  sName: "Донецьк",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"sumy",
              sName: "Сумська область",
                a: [{
                  sID: "sumy",
                  sName: "Суми",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "gluhiv",
                  sName: "Глухів",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"zhytomyr",
              sName: "Житомирська область",
                a: [{
                  sID: "zhytomyr",
                  sName: "Житомир",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"ternopil",
              sName: "Тернопільська область",
                a: [{
                  sID: "ternopil",
                  sName: "Тернопіль",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"ivano-frankivsk",
              sName: "Івано-Франківська область",
                a: [{
                  sID: "ivano-frankivsk",
                  sName: "Івано-Франківськ",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "kalush",
                  sName: "Калуш",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"cherkasy",
              sName: "Черкаська область",
                a: [{
                  sID: "cherkasy",
                  sName: "Черкаси",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"poltava",
              sName: "Полтавська область",
                a: [{
                  sID: "poltava",
                  sName: "Полтава",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "myrgorod",
                  sName: "Миргород",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"rivne",
              sName: "Рівненська область",
                a: [{
                  sID: "rivne",
                  sName: "Рівне",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "varash",
                  sName: "Вараш",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "ostrog",
                  sName: "Острог",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"kherson",
              sName: "Херсонська область",
                a: [{
                  sID: "kherson",
                  sName: "Херсон",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"zaporizhya",
              sName: "Запорізька область",
                a: [{
                  sID: "zaporizhya",
                  sName: "Запоріжжя",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"lugansk",
              sName: "Луганська область",
                a: [{
                  sID: "severodonetsk",
                  sName: "Сєвєродонецьк",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "novopskov",
                  sName: "Новопсков",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"zakarpattya",
              sName: "Закарпатська область",
                a: [{
                  sID: "uzhgorod",
                  sName: "Ужгород",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"vinnytsia",
              sName: "Вінницька область",
                a: [{
                  sID: "vinnytsia",
                  sName: "Вінниця",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"khmelnytskiy",
              sName: "Хмельницька область",
                a: [{
                  sID: "khmelnytskiy",
                  sName: "Хмельницький",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"kirovograd",
              sName: "Кіровоградська область",
                a: [{
                  sID: "kirovograd",
                  sName: "Кіровоград",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                }]
            },
            {
              sID:"chernivtsi",
              sName: "Чернівецька область",
              a: [{
                sID: "chernivtsi",
                sName: "Чернівці",
                sCityPassportURL:"",
                sNewsGroupURL:"",
                sVolunteersGroupURL:""
              }]
            },
            {
              sID:"other",
              sName: "Інші регіони",
                a: [{
                  sID: "zurich",
                  sName: "Цюріх",
                },
                {
                  sID: "singapore",
                  sName: "Сингапур",
                },
                {
                  sID: "copenhagen",
                  sName: "Копенгаген",
                },
                {
                  sID: "none",
                  sName: "",
                }]
            }
          ]
        }]
  };

    var aSubject = [
        {
          "sFIO":"Білявцев Володимир",
          "sURL":"https://www.facebook.com/profile.php?id=100001410629235",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Дубілет Дмитро",
          "sURL":"https://www.facebook.com/dubilet",
          "sPhoto" : "https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11406895_10153457089428552_7046977239850441618_n.jpg?oh=da0b48378043d6a27b385ec916710239&oe=5857AFE6",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Туренко (Кузьмінова) Ольга",
          "sURL":"https://www.linkedin.com/in/olga-turenko-65860999",
          "sPhoto" : "https://media.licdn.com/mpr/mpr/shrinknp_200_200/p/8/005/0a3/010/2873119.jpg",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Шимків Дмитро",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Мерило Яніка",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c1.0.160.160/p160x160/10620554_10204503241759783_5791407653897428363_n.jpg?oh=c75d263d5ab1d1f920b42d4aa10b9e9c&oe=585B9330",
          "sURL":"https://www.facebook.com/jaanika.merilo",
          "sCity":"Київ"
        },
        {
          "sFIO":"Курбацький Павло",
          "sURL":"https://www.facebook.com/kyrbatsky",
          "sPhoto" : "https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/14948_717710254976581_8444143412921572127_n.jpg?oh=eadeb8ea15640de0d90ec54477e6737e&oe=582A0FB6",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Кельнер Ірина",
          "sURL":"https://www.facebook.com/IraKelner",
          "sPhoto" : "https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.0.160.160/p160x160/12642654_1012837938763164_3246634087482743284_n.jpg?oh=edc2d0da483bd6d0a187fc1a6ec5053c&oe=581165DA",
          "sCity":"Тернопіль"
        },
        {
          "sFIO":"Заболотній Дмитро",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Боровик Дмитро",
          "sURL":"https://www.facebook.com/dmitry.borovik.39",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c32.0.160.160/p160x160/10599674_678591012230665_7844095113786179561_n.jpg?oh=f96e99efd0e18c178bbf70f4314dde29&oe=58180A80",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Домаш Олексій",
          "sPhoto":"https://avatars2.githubusercontent.com/u/1484619?v=3&s=460",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Забрудський Дмитро",
          "sURL":"https://www.facebook.com/dmitrij.zabrudskij",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c37.56.465.465/s160x160/48134_10201214484668290_1678936328_n.jpg?oh=776e6b7d79db6671ebfd1e7dd673cdc1&oe=582393CE",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Жиган Роман",
          "sURL":"https://www.facebook.com/roman.zhigan.1",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11891155_1482232562095454_7875554608302750847_n.jpg?oh=00f42b7f50927134427a36381867078c&oe=5854A23F",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Грек Дар'я",
          "sURL":"https://www.facebook.com/darja.grek",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12046987_969763873087888_5887983526328644721_n.jpg?oh=ccdc6260583f719fa7f61ff1b13ab31b&oe=5824EFAC",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Свідрань Максим",
          "sURL":"https://www.facebook.com/maksim.svidran",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.40.160.160/p160x160/1655880_10203085452998391_1162874664_n.jpg?oh=8979ec2ee3494428820cfd8e2a434ea9&oe=5824A775",
          "sCity":"Дніпро"
        },
  		{
          "sFIO":"Ставицький Валерій",
          "sURL":"https://www.facebook.com/valery.stavitsky",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11224861_870685936332468_2045742132617793708_n.jpg?oh=1b29b4b5d3d9047fc949a7fa671c7153&oe=5812E599",
          "sCity":"Харків"
        },
  		{
          "sFIO":"Золотова Тетяна",
          "sURL":"https://www.facebook.com/agraell",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13669845_1756498947958107_6398366836813663529_n.jpg?oh=8c8ca823c9cb770b5107363f86824b13&oe=5850F618",
          "sCity":"Дніпро"
        },
  		{
          "sFIO":"Смоктій Кирило",
          "sURL":"https://www.facebook.com/smoktii",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12933003_567888636711685_9122203894998038314_n.jpg?oh=2f60ddab0abb663ddb2ae7b4af823cd1&oe=58126829",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Продан Юлія",
          "sURL":"https://www.facebook.com/klimkovichy",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10268438_1477001455911660_5539510016470161228_n.jpg?oh=3791f2651b792617423001a164cfa986&oe=581F4D6A",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Столбова Ганна",
          "sURL":"https://www.facebook.com/insanniyou",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/1743652_594673260603466_1655652539_n.jpg?oh=57bdc0968e8b24ffc6b83a6eaf1bcb0d&oe=5823E139",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Герман Август",
          "sURL":"https://www.facebook.com/profile.php?id=100005136618550",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Куліш Андрій",
          "sURL":"https://www.facebook.com/andriy.kylish",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c28.28.352.352/s160x160/421576_111660178962768_1443552158_n.jpg?oh=da3c9218fcc6b4a34980b32009f3ac24&oe=5829C6E1",
          "sCity":"Дніпро"
        },
        {
          "sFIO":"Данілевич Софія",
          "sURL":"https://www.facebook.com/sophia.danylevich",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13315586_1051012694987834_4701805118846809487_n.jpg?oh=98e4dcfe7e578c77da8a6f6476400d8d&oe=58597FC5",
          "sCity":"Тернопіль"
        },
        
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Боборицкий Денис",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Вадим Волос",
          "sURL":"https://www.facebook.com/vadymvolos",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/969077_563076740410432_913138478_n.jpg?oh=5816e4ea9ae61095dbd571ae33763ada&oe=581E1A89",
          "sCity":"Київ"
        },
        
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Руднев Вадим",
          "sURL":"",
          "sCity":"Київ"
        },
      
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Кащенко Александр",
          "sURL":"",
          "sCity":"Київ"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Гулич Вадим",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"kharkiv.kharkiv",
          "sFIO":"Большуткин Владимир",
          "sURL":"",
          "sCity":"Харків"
        },
        {
          "sID_Group":"lviv.chervonograd",
          "sFIO":"Квасній Артур",
          "sURL":"",
          "sCity":"Червоноград"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Корчагин Павел",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"odesa.odesa",
          "sFIO":"Сафроненков Андрей",
          "sURL":"",
          "sCity":"Одеса"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Будник Дмитрий",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Ладик Денис",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro;",
          "sFIO":"Терещенко Максим",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Великотский Вячеслав",
          "sURL":"",
          "sCity":"Київ"
        },
        {
          "sID_Group":"odesa.odesa",
          "sFIO":"Лупашко Богдан",
          "sURL":"",
          "sCity":"Одеса"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Воронюк Евгений",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"mykolaiv.mykolaiv",
          "sFIO":"Лущан Владислав",
          "sURL":"",
          "sCity":"Миколаїв"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Феденишин Андрей",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"lviv.lviv",
          "sFIO":"Глеб Жебраков",
          "sURL":"",
          "sCity":"Львів"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Макаренко Валерий",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Дашенко Инни",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"kharkiv.kharkiv",
          "sFIO":"Педич Максим",
          "sURL":"",
          "sCity":"Харків"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Зора Борис",
          "sURL":"",
          "sCity":"Київ"
        },
        {
          "sID_Group":"lviv.lviv",
          "sFIO":"Янов Андрей",
          "sURL":"",
          "sCity":"Львів"
        },
        {
          "sID_Group":"chernigiv.chernigiv",
          "sFIO":"Дубина Владимир",
          "sURL":"",
          "sCity":"Чернігів"
        },
        {
          "sID_Group":"mykolaiv.mykolaiv",
          "sFIO":"Жмурков Александр",
          "sURL":"",
          "sCity":"Миколаїв"
        },
        {
          "sID_Group":"odesa.odesa",
          "sFIO":"Соловьев Александр",
          "sURL":"https://ua.linkedin.com/in/%D0%B0%D0%BB%D0%B5%D0%BA%D1%81%D0%B0%D0%BD%D0%B4%D1%80-%D1%81%D0%BE%D0%BB%D0%BE%D0%B2%D1%8C%D0%B5%D0%B2-a2108085",
          "sPhoto":"https://media.licdn.com/mpr/mpr/shrinknp_200_200/p/7/005/059/1eb/2db7911.jpg",
          "sCity":"Одеса"
        },
        {
          "sID_Group":"sumy.sumy",
          "sFIO":"Дубинка Юрій",
          "sURL":"https://ua.linkedin.com/pub/yurii-dubinka/b9/16/45b",
          "sPhoto":"https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAALQAAAAJDY1YWIxNGRlLTEyZmItNGY4OC1iOWIxLWFlYTFjOTQ3MWU5Nw.jpg",
          "sCity":"Суми"
        },
        {
          "sID_Group":"chernigiv.chernigiv",
          "sFIO":"Голуб Олексій",
          "sURL":"",
          "sCity":"Чернігів"
        },
        {
          "sID_Group":"lviv.lviv",
          "sFIO":"Гуцуляк Олександр",
          "sURL":"",
          "sCity":"Львів"
        },
        {
          "sID_Group":"chernigiv.chernigiv",
          "sFIO":"Олександр Скосир",
          "sURL":"",
          "sCity":"Чернігів"
        },
        {
          "sID_Group":"sumy.sumy",
          "sFIO":"Олексій Сердюк",
          "sURL":"",
          "sCity":"Суми"
        },
        {
          "sID_Group":"zhytomyr.zhytomyr",
          "sFIO":"Ольга Прилипко",
          "sURL":"",
          "sCity":"Житомир"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Майданюк Дмитро",
          "sURL":"",
          "sCity":"Київ"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Туренко Сергей",
          "sURL":"https://www.linkedin.com/in/sergey-turenko-b8708a94",
          "sPhoto":"https://media.licdn.com/mpr/mpr/shrinknp_200_200/p/7/005/0a3/012/1f8218c.jpg",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Волобуев Дмитрий",
          "sURL":"https://www.facebook.com/profile.php?id=100007298923596",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Горбань Сергей",
          "sURL":"https://github.com/sergeygorban",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Марченко Игорь",
          "sURL":"https://github.com/djekildp",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Нижник Олександр",
          "sURL":"https://www.facebook.com/profile.php?id=100009085920321",
          "sCity":"Дніпро"
        },
       
        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk",
          "sFIO":"Озорович Андрій",
          "sURL":"https://www.facebook.com/a.ozorovych",
          "sCity":"Івано-Франківськ"
        },
        {
          "sID_Group":"kyiv.vyshgorod.rada",
          "sFIO":"Сокиржинський Олександр",
          "sURL":"https://www.facebook.com/alex.sokirjinskiy",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13092069_1028917970489685_2653187308818494451_n.jpg?oh=5388c4c374e7365b2230af346eb68876&oe=582491CD",
          "sCity":"Вишгород"
        },
        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Глибочко Олександр",
          "sURL":"https://www.facebook.com/AlexanderGlybo",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12928395_1163331457033988_678737066450901642_n.jpg?oh=51454d57b8aa2c33eff91e6d2d1543cc&oe=58163A65",
          "sCity":"Черкаси"
        },
        {
          "sID_Group":"poltava.myrgorod.rada",
          "sFIO":"Слємзін Олександр",
          "sURL":"https://www.facebook.com/alexandr.slemzin",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/13269_10205607206771207_8344419548296787003_n.jpg?oh=fabef51ac001b6f626726d5f6ca0d2ee&oe=5855E1E9",
          "sCity":"Миргород"
        },
        {
          "sID_Group":"rivne.varash.rada",
          "sFIO":"Поремчук Євгеній",
          "sURL":"https://www.facebook.com/e.poremchuk?fref=ts",
          "sCity":"Вараш"
        },
        {
          "sID_Group":"kyiv.kyiv.rada",
          "sFIO":"Шарапов Єгор",
          "sURL":"https://www.facebook.com/egorsha",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.10.160.160/p160x160/1966850_10203490579085516_348411029_n.jpg?oh=7a30398a86e7b0cddcfd23a7ca13238d&oe=581915B3",
          "sCity":"Київ"
        },
        {
          "sID_Group":"sumy.gluhiv.rada",
          "sFIO":"Павловець Ілля",
          "sURL":"https://www.facebook.com/Freedom.hl",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10955798_832812253442342_8854510801972688533_n.jpg?oh=c0c7f062f3e78d9960bb31fda11f47c9&oe=5820C928",
          "sCity":"Глухів"
        },
        {
          "sID_Group":"odesa.odesa.rada",
          "sFIO":"Юрченко Роман",
          "sURL":"https://www.facebook.com/iroyur?fref=ts",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12308646_839362439509936_8503452484623107341_n.jpg?oh=ff3e189508953adc99011562674c0ccf&oe=58208485",
          "sCity":"Одеса"
        },
        {
          "sID_Group":"kherson.kherson.rada",
          "sFIO":"Кулик Павло",
          "sURL":"https://www.facebook.com/kylikpavel",
          "sCity":"Херсон"
        },
        {
          "sID_Group":"kyiv.bucha.rada",
          "sFIO":"Лисенко Іван",
          "sURL":"https://www.facebook.com/Lysenko.Ivan",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10441011_939516459428532_3278611361190235130_n.jpg?oh=fa81c99c8c17200f4744b59ef5cdcb19&oe=585B76A5",
          "sCity":"Буча"
        },
        {
          "sID_Group":"zaporizhya.zaporizhya.rada",
          "sFIO":"Соловйов Максим",
          "sURL":"https://www.facebook.com/maksim.solovyov.12",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.0.160.160/p160x160/1654480_462168640578627_1688838187_n.jpg?oh=ee1aa15df9961314f5c04ac2deca8cbe&oe=58589B6B",
          "sCity":"Запоріжжя"
        },
        {
          "sID_Group":"lugansk.severodonetsk.rada",
          "sFIO":"Малеванець Олексій",
          "sURL":"https://www.facebook.com/malevanec",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13938389_1072424572833636_7000621191111350534_n.jpg?oh=243827ba07364e597756c121af22c903&oe=581B1239",
          "sCity":"Сєвєродонецьк"
        },
        {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Горкуша Михайло",
          "sURL":"https://www.facebook.com/mgorkusha",
          "sCity":"Суми"
        },
        {
          "sID_Group":"lviv.lviv.rada",
          "sFIO":"Стець Ольга",
          "sURL":"https://www.facebook.com/olga.kuk.7?fref=ts",
          "sCity":"Львів"
        },
        {
          "sID_Group":"kyiv.bila-tserkva.rada",
          "sFIO":"Лаврусь Павел",
          "sURL":"https://www.facebook.com/p.lavrus",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12196019_522416387923078_4001051597931043315_n.jpg?oh=b61b518dbef7bf4d63248767b51e5b3a&oe=5854F567",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Савінова Ванда",
          "sURL":"https://www.facebook.com/profile.php?id=100001353020712",
          "sCity":"Житомир"
        },
        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Дячок Дарина",
          "sURL":"https://www.facebook.com/profile.php?id=100004683825896",
          "sCity":"Черкаси"
        },
        {
          "sID_Group":"zakarpattya.uzhgorod.rada",
          "sFIO":"Лук'янчук Микола",
          "sURL":"https://www.facebook.com/profile.php?id=100007826441900",
          "sCity":"Ужгород"
        },
        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Корнійчук Володимир",
          "sURL":"https://www.facebook.com/profile.php?id=100010892283159",
          "sCity":"Житомир"
        },
        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Колодіч Олена",
          "sURL":"https://www.facebook.com/profile.php?id=100011166150937",
          "sCity":"Черкаси"
        },
        {
          "sID_Group":"rivne.ostrog.rada",
          "sFIO":"Галич Сергій",
          "sURL":"https://www.facebook.com/serhiy.halich",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11903710_1016467491743026_2145437931467083079_n.jpg?oh=cd457b4bc836e270fb9f3a89082691fe&oe=5813239B",
          "sCity":"Острог"
        },
        {
          "sID_Group":"lugansk.novopskov.rada",
          "sFIO":"Малетин Виктор",
          "sURL":"https://www.facebook.com/v.maletin",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c170.50.621.621/s160x160/314832_161708137251710_572795340_n.jpg?oh=1281bc05c206e60ce5130bdee326c731&oe=58566C61",
          "sCity":"Новопсков"
        },
        {
          "sID_Group":"vinnytsia.vinnytsia.rada",
          "sFIO":"Мукомол Вадим",
          "sURL":"https://www.facebook.com/vadim.munin",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13322177_1128005340575202_2101175329914658142_n.jpg?oh=5f1cc91ede0ea2075becc8e315b435f4&oe=5818D3A5",
          "sCity":"Вінниця"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Вербицький Олександр",
          "sURL":"https://www.facebook.com/verbitsky.aleks",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"ivano-frankivsk.kalush.rada",
          "sFIO":"Маліборський Віталій",
          "sURL":"https://www.facebook.com/weres.ua",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12144677_439030329635978_6939953702971035626_n.jpg?oh=d5032677cc3f4997ed2658a1cdd0ab38&oe=582818AD",
          "sCity":"Калуш"
        },
        {
          "sID_Group":"poltava.poltava.rada",
          "sFIO":"Городчаніна Юлія",
          "sURL":"https://www.facebook.com/yuliya.bobir",
          "sCity":"Полтава"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Пляцик Юрій",
          "sURL":"https://www.facebook.com/yura.plyacik",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13537696_1156377111049092_6910064597484651978_n.jpg?oh=0016c0346f7df21238e551d4867182f8&oe=58285ABB",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"rivne.rivne.rada",
          "sFIO":"Дюг Юрій",
          "sURL":"https://www.facebook.com/yuriy.dyug",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10923531_1000018706694178_455606146826939393_n.jpg?oh=92dd846cda22cb5df1c9bd86efbb6131&oe=5822EDA5",
          "sCity":"Рівне"
        },
        {
          "sID_Group":"kyiv.makarov.rada",
          "sFIO":"Татьяна Гончарова",
          "sURL":"https://www.facebook.com/tanja.goncharova",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.41.160.160/p200x200/11891269_952001978206576_6268696842288680632_n.jpg?oh=19929ba17c9fa69635b0fbf1d76bc6a2&oe=58163EB2",
          "sCity":"Макаров"
        },
        {
          "sID_Group":"other.zurich",
          "sFIO":"Самсонюк Юрій",
          "sURL":"",
          "sCity":"Цюріх"
        },
        {
          "sID_Group":"other.singapore",
          "sFIO":"Московцов Даниил",
          "sURL":"",
          "sCity":"Сингапур"
        },
        {
          "sID_Group":"other.none",
          "sFIO":"Яценко Антон",
          "sURL":"",
          "sCity":""
        },
        {
          "sID_Group":"other.copenhagen",
          "sFIO":"Катерина Бутенко",
          "sURL":"https://www.facebook.com/ekaterina.butenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10888637_1009507575729643_6435584388863763113_n.jpg?oh=095e9eeef3fbe35039473674d2976a2f&oe=5825C574",
          "sCity":"Копенгаген"
        }
      ];

  var oAllVolunteers = {
    aTop : [],
    aVolunteers : []
  };
  angular.forEach(aSubject, function(volunteer) {

    //check for pics in fb accounts
    if(volunteer.sURL) {
      var id = volunteer.sURL.split('id=')[1];
      if(id) {
        volunteer.sPhoto = 'https://graph.facebook.com/' + id + '/picture?type=large';
      }
    }
    
    // TOP
    if(!volunteer.sID_Group) {
      oAllVolunteers.aTop.push(volunteer);
    } else {
      var found = false;
      var options = volunteer.sID_Group.split('.');
      var count = 0;

      // volunteers
      for(var i=0; i<oAllVolunteers.aVolunteers.length; i++) {
        if(oAllVolunteers.aVolunteers[i].sID === options[0]) {
          angular.forEach(oAllVolunteers.aVolunteers[i].a, function (city) {
            count++;
            if(city.sID === options[1]) {
              city.a.push(volunteer);
              found = true;
              count = 0;
            } else if(count === oAllVolunteers.aVolunteers[i].a.length) {
              count = 0;
              angular.forEach(oGroups.a[0].a, function(group) {
                if(options[0] === group.sID) {
                  angular.forEach(group.a, function(city) {
                    if(options[1] === city.sID) {
                      oAllVolunteers.aVolunteers[i].a.push({
                          "sName" : city.sName,
                          "sID" : city.sID,
                          "sCityPassportURL" : city.sCityPassportURL,
                          "sNewsGroupURL" : city.sNewsGroupURL,
                          "sVolunteersGroupURL" : city.sVolunteersGroupURL,
                          "a" : [volunteer]
                      });
                      found = true;
                    }
                  })
                }
              })
            }
          })
        }
      }

      if (!found && volunteer.sID_Group != "") {
        angular.forEach(oGroups.a[0].a, function(group) {
          if(options[0] === group.sID) {
            angular.forEach(group.a, function(city) {
              if(options[1] === city.sID) {
                oAllVolunteers.aVolunteers.push({
                  "sName" : group.sName,
                  "sID" : group.sID,
                  "nOrder" : group.nOrder,
                  a : [{
                    "sName" : city.sName,
                    "sID" : city.sID,
                    "sCityPassportURL" : city.sCityPassportURL,
                    "sNewsGroupURL" : city.sNewsGroupURL,
                    "sVolunteersGroupURL" : city.sVolunteersGroupURL,
                    "a" : [volunteer]
                  }]
                })
              }
            })
          }
        })
      }
    }
  });

  $scope.volunteers = oAllVolunteers;

  $scope.check = function (item) {
   if(item && item.split('.')[2] === 'rada') {
     return true;
   }
  };

  $scope.checkForEmpty = function () {
    var el = document.querySelectorAll('.content-ul');
    for(var i=0; i<el.length; i++) {
      if(el[i].childElementCount === 0) {
        el[i].parentNode.style.display = 'none'
      } else {
        el[i].parentNode.style.display = 'block'
      }
    }
  };
});
