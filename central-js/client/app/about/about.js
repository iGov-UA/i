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

angular.module('about').controller('aboutController', function ($scope) {

  var oGroups =  {
      sName: "IT-волонтери iGov",
      a:[{
          sName: "Регіональні волонтери iGov",
          a:[
            {
              sID:"dnipro",
              sName: "Дніпропетровська область",
                a: [{
                  sID: "dnipro",
                  sName: "м. Дніпро",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1W9hv0Mc5OKSlyHG7aTQgX6lSxY1NINLmz5k8FWe2bYM/edit?ts=56570e49#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovdnepr/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/512450418935430/"
                },
                {
                  sID: "govtivodi",
                  sName: "м. Жовті Води",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1RZSqK7MkvKT5oRpOCZwuWTDly6m2ouYK3toy1Ia4lDQ/edit?ts=56b2284c#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovzv/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/igovzhovtivody/"
                },
                {
                  sID: "nikopol",
                  sName: "м. Нікополь",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1pEQzgwXL9JWVd5sl8m1h2Z27l3gXQ6LaX1xfp6BGf20/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovnikopol/",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "vilnogirsk",
                  sName: "м. Вільногірськ",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "kamenske",
                  sName: "м. Кам'янське",
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
                  sName: "м. Хмельницький",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1frBfJxhSAF9z5SRp1zYgHP5E_T7a5tTRMgdBDzSXHts/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/igovkhmelnitskiy/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/volonteryigovkhmelnitsky/"
               },
               {
                 sID: "netishin",
                 sName: "м. Нетішин",
                 sCityPassportURL:"https://docs.google.com/spreadsheets/d/1bTXdJ5gn5vRGstFYPCe61QGOygV4edZd36shwVOSGdc/edit#gid=1409053144",
                 sNewsGroupURL:"",
                 sVolunteersGroupURL:""
               },
               {
                 sID: "shepetivka",
                 sName: "м. Шепетівка",
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
                  sName: "м. Київ",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1Rx-WbP8hQVQ_kTAWgTsEjLP3zaFuaDiv5MgZVikDmVA/edit?ts=56614e79#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovkyiv/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/517099548452702/"
                },
                {
                  sID: "vyshgorod",
                  sName: "м. Вишгород",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/17OJB14niYhyJb-SLfIm7oEBEiJVWiRqUXApRfMGDdj0/edit?ts=566b384a#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/iGov.VishGorod/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/149432808752447/"
                },
                {
                  sID: "bila-tserkva",
                  sName: "м. Біла Церква",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1VhaiwnALoNxvdhEJwt8dPAME5QdTQEhdNdX_7OfsV-c/edit#gid=1409053144&vpid=A1",
                  sNewsGroupURL:"https://www.facebook.com/groups/igov.bila.tserkva/",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "bucha",
                  sName: "м. Буча",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/13tEr1bhzkcTbQOFpCV2O52xUTB9aLI6ocPmJ6Mi4ti4/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/1745766712310379/",
                  sVolunteersGroupURL:""
                },

                {
                  sID: "brovari",
                  sName: "м. Бровари",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1tV5_9k5pch1KPxVNZi_Tg5R1yQC3wfx4PWNv4dj4CVE/edit#gid=0",
                  sNewsGroupURL:"https://www.facebook.com/igovbrovary/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/892876970830043/"
                },
                {
                  sID: "boyarka",
                  sName: "м. Боярка",
                  sCityPassportURL:"",
                  sNewsGroupURL:"https://www.facebook.com/groups/403499903170828/?fref=ts",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "vishneve",
                  sName: "м. Вишневе",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/14aVbtSd-x0nXYOl_lsocvDEtVyqKK-L_aIzMfxm6cMY/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/e.vyshneve/",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "irpin",
                  sName: "м. Ірпінь",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "makariv",
                  sName: "смт. Макарів",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1reX3MxK_ATvs3h18tCaX6CfRUcfaH4WQ-Injr5zBI4Y/edit#gid=1628611752",
                  sNewsGroupURL:"https://www.facebook.com/groups/894811883965243/",
                  sVolunteersGroupURL:""
                }]
            },

            {
              sID:"ternopil",
              sName: "Тернопільська область",
                a: [{
                  sID: "ternopil",
                  sName: "м. Тернопіль",
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
                  sName: "м. Рівне",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1_3EVPgBiBkvF9jlRmN8BIzAGIZBnGx5M84nfOC6my6o/edit?ts=56632cbc#gid=1409053144&vpid=A1",
                  sNewsGroupURL:"https://www.facebook.com/igovrivne/",
                  sVolunteersGroupURL:""
               },
               {
                  sID: "varash",
                  sName: "м. Вараш",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1Xl9eSA8XIuBTfzINOONs5ja05HPyQ3auOxdaiXk5TuM/edit?ts=567480c9#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/IGov-%D0%9A%D1%83%D0%B7%D0%BD%D0%B5%D1%86%D0%BE%D0%B2%D1%81%D1%8C%D0%BA-1520126318285493/info/?tab=page_info&section=long_desc&view",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/198879173786815/"
               },
               {
                  sID: "ostrog",
                  sName: "м. Острог",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1gnNLhs316837JDAv9btl--572RlzIH6fXHOJUxL7PII/edit#gid=0",
                  sNewsGroupURL:"https://www.facebook.com/groups/469918746528216/",
                  sVolunteersGroupURL:""
                   }]
            },
            {
                sID:"poltava",
                sName: "Полтавська область",
                a: [{
                  sID: "poltava",
                  sName: "м. Полтава",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/10TPLqRLlMF4qoxhCnB3fhiTZwbfTqPj8FYSkV9j9bmc/edit#gid=0",
                  sNewsGroupURL:"https://www.facebook.com/groups/1716236748590982/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/844645305657268/"
                 },
                {
                  sID: "myrgorod",
                  sName: "м. Миргород",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1rNOJUTAuPQM20D8EtJuNF0sF5GrSChf0jPQOikRIEoc/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igov.myrhorod/",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "lubni",
                  sName: "м. Лубни",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1KhbkWZLQKSK4AqqaQQSCqvIpq8O1L2A4O0VzRTThsJg/edit?ts=566b04dc#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovLubny",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/723277194472511/"
                },
                {
                  sID: "kobelyaki",
                  sName: "м. Кобеляки",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1EzYxuoechzNGRM8J4ySjwJz7HPXG4dEsx5aMVcHOr1A/edit?copiedFromTrash#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/%D0%A1%D0%B8%D0%BB%D0%B0-%D0%B3%D1%80%D0%BE%D0%BC%D0%B0%D0%B4%D0%B8-2015-1024381444241586/",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "kremenchug",
                  sName: "м. Кременчуг",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1vDczKGfX0nIjGa9z7fsJUA5Fcl_yvjw762h3E7FLXhY/edit",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                  }]
              },

{
              sID:"odesa",
              sName: "Одеська область",
                a: [{
                  sID: "odesa",
                  sName: "м. Одеса",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1j-PQUvkB2Mt1NutJxkObBsXpJStOl--CP4GKt8Taxfo/edit#gid=1409053144",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:"https://groups.google.com/forum/#!forum/igov-odessa"
                }]
            },

                    {
              sID:"kherson",
              sName: "Херсонська область",
                a: [{
                  sID: "kherson",
                  sName: "м. Херсон",
                  sCityPassportURL:"",
                  sNewsGroupURL:"https://www.facebook.com/IGOV-%D0%9F%D0%BE%D1%80%D1%82%D0%B0%D0%BB-%D0%94%D0%B5%D1%80%D0%B6%D0%B0%D0%B2%D0%BD%D0%B8%D1%85-%D0%9F%D0%BE%D1%81%D0%BB%D1%83%D0%B3-%D0%A5%D0%B5%D1%80%D1%81%D0%BE%D0%BD-1732115370404292/?pnref=story",
                  sVolunteersGroupURL:"https://groups.google.com/forum/?hl=ru#!forum/igov-kherson"
                }]
            },
            {
              sID:"mykolaiv",
              sName: "Миколаївська область",
                a: [{
                  sID: "mykolaiv",
                  sName: "м. Миколаїв",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/133au9afsHrvLc3h6gPwWBnts0RQx75-mYSGD19uXThw/edit#gid=1770670897",
                  sNewsGroupURL:"https://www.facebook.com/groups/iGov.Mykolaiv/",
                  sVolunteersGroupURL:"https://groups.google.com/forum/#!forum/igov-nikolaev"
                 },

            {

                  sID: "pervomaysk",
                  sName: "м. Первома́йськ",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1nFqPqX2hCoo7I9lPgOFBT4HxY8AbEvrfCrOC8upEaYE/edit?ts=56744b75#gid=0",
                  sNewsGroupURL:"https://www.facebook.com/groups/568838149949187/?ref=bookmarks",
                  sVolunteersGroupURL:""
                }]
            },

            {
              sID:"kharkiv",
              sName: "Харківська область",
                a: [{
                  sID: "kharkiv",
                  sName: "м. Харків",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1TzmtKpHiOeW9XLu7r40OYpzNVUo1kSyP_jULF2oGPzs/edit#gid=15276565",
                  sNewsGroupURL:"https://www.facebook.com/groups/igov.kharkiv.volunteers/",
                  sVolunteersGroupURL:""
                },

                {sID: "kharkivrayon",
                  sName: "Харківський район",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1TiRS4iCl5br-J1tBTfqU8uYHIxcwvhaHBZgbX_Wfl5A/edit#gid=0",
                  sNewsGroupURL:"https://www.facebook.com/groups/1521306591499452/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/475242739331487/"
                  },

                {sID: "balakleya",
                  sName: "м. Балаклія",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1Xmb6IB6ibk_t6DPjRypoK6S5iPIyHJjDSR14nTtSuwQ/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/143909895971350/",
                  sVolunteersGroupURL:"https://groups.google.com/forum/#!topic/igov-balakliya-kharkiv/"

                   },

                {sID: "bogoduhiv",
                  sName: "м. Богодухів",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1VoFsqZIjdgDcOzFQFdyWaEshAVu3fvYvikyBZFNNnmk/edit#gid=0",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/iGov.Bohodukhiv/"
              }]
            },


            {
              sID:"lviv",
              sName: "Львівська область",
                a: [{
                  sID: "lviv",
                  sName: "м. Львів",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1WJfk3RP4rZVdPYobECePzuDZkTh2BFRSJiCDUlde1Fo/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/IGov-Lviv-801495843309925/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1034876896574452/"
                },
                {
                  sID: "chervonograd",
                  sName: "м. Червоноград",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                  },
                {
                  sID: "ctriy",
                  sName: "м. Стрий",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1CljcwEbEhVCBvu1nShzGMriBglkjtEpHOanPhi-K2_0/edit?ts=56dac750#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/IGovStryi/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/467415000125415/"
                }]
            },

            {
              sID:"vinnytsia",
              sName: "Вінницька область",
                a: [{
                  sID: "vinnytsia",
                  sName:"м. Вінниця",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1lXzaAnDLB7YpMroevHmX6DOJ_LxzB9wVuIEu0uRpjxo/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/IGov-%D0%92%D1%96%D0%BD%D0%BD%D0%B8%D1%86%D1%8F-933138393390720/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1531056573877513/"
                }]
            },

            {
              sID:"sumy",
              sName: "Сумська область",
                a: [{
                  sID: "sumy",
                  sName: "м. Суми",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1JInu_kMhMYshfrYLMXF2HiOZqtomxUcnfef7UKgTuNQ/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovsumy",
                  sVolunteersGroupURL:"https://groups.google.com/forum/m/?hl=ru#!forum/igov-sumy"
                },

                {
                  sID: "ohtirka",
                  sName: "м. Охтирка",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1UHyTH4Bu-Lu9uexin9m6B8CyQYArue7yfzJ_vfWei1A/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovsumy",
                  sVolunteersGroupURL:"https://groups.google.com/forum/m/?hl=ru#!forum/igov-sumy"
                },

                {
                  sID: "gluhiv",
                  sName: "м. Глухів",
                  sCityPassportURL:"https://www.facebook.com/igovsumy",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:"https://groups.google.com/forum/#!forum/igov--hlukhiv"
                  },

                  {
                  sID: "shostka",
                  sName: "м. Шостка",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1is0yFCnTkZR32afHjTqrNWRVxvhlvtEV7zjM-j8TH2c/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovsumy",
                  sVolunteersGroupURL:"https://groups.google.com/forum/m/?hl=ru#!forum/igov-sumy"

                }]
            },

            {
              sID:"cherkasy",
              sName: "Черкаська область",
                a: [{
                  sID: "cherkasy",
                  sName: "м. Черкаси",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1BsigXNrIKqX8H2Lc5V_velHkAeWlJuxaPvjXRIc4IUg/edit#gid=1279585403",
                  sNewsGroupURL:"https://www.facebook.com/groups/306944482973785/?fref=ts",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1674001846217834/?fref=ts"
                  },

                  {
                  sID: "smila",
                  sName: "м. Сміла",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1DBeX2cU5DHup7ZYOuP9TCIn5mTbKwm1eK54j1raTx1s/edit#gid=1409053144",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:"https://groups.google.com/forum/?utm_medium=email&utm_source=footer#!forum/igov-smila"
                  },

                  {
                  sID: "zvenigorodka",
                  sName: "м. Звенигоро́дка",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/10WnX6qpaK6BYeMVEZfEAzNlnzJf_DbqVEsp6GeXKaDk/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/1075102902500536/",
                  sVolunteersGroupURL:"https://vk.com/club109424225"
                }]
            },

 {
              sID:"lugansk",
              sName: "Луганська область",
                a: [{
                  sID: "severodonetsk",
                  sName: "м. Сєвєродонецьк",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/13wq5KihuzfXHuv0UOGwHNsjjE5e_Zpciudkuduyh7AI/edit?ts=565c6996#gid=1409053144&vpid=A1",
                  sNewsGroupURL:"https://www.facebook.com/groups/1680758882211363/1680760022211249/?notif_t=like",
                  sVolunteersGroupURL:""
                },
                {
                  sID: "novopskov",
                  sName: "м. Новопсков",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1DSSttdmOJzFR2PFng6VfUZCyBjrCdpdEGJxYIgKpPfI/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/IGov-Новопсков-459227737605173/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/477483609120197/"
                }]
            },

            {
              sID:"zhytomyr",
              sName: "Житомирська область",
                a: [{
                  sID: "zhytomyr",
                  sName: "м. Житомир",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1acKIS8CRk9kl5zgtAQS_23q5gNcyy90yeRXL5AfySXQ/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/IGov-Житомир-550656265085515",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1558206471136024"
                  },

                  {

                  sID: "berduchiv",
                  sName: "м. Бердичів",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1QoxQRYrADuZh9Jvj1FuJZqtD5mY3QkITOacY72FYcg8/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovbrd/",
                  sVolunteersGroupURL:"https://groups.google.com/forum/#!forum/igovbrd"
                  },

                  {
                  sID: "korosten",
                  sName: "м. Коростень",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1mNyMsL9XLNnmjuSEJZroZHUFkQNslFBwVxSioJbV4Rg/edit?pref=2&pli=1#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/profile.php?id=100000939614292",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1691306134421067/"
                  }
                ]
            },

            {
              sID:"volyn",
              sName: "Волинська область",
                a:[{
                  sID:"lutsk",
                  sName: "м. Луцьк",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1YlN8xHWlTncX6Y3ZijxgoyxDYYH5htcT57pBoHKY_jA/edit?ts=565e045a#gid=1409053144&vpid=A1",
                  sNewsGroupURL:"https://www.facebook.com/groups/iGov.Lutsk/",
                  sVolunteersGroupURL:"https://groups.google.com/forum/#!forum/igov-lutsk"
                }]
          },

           {
              sID:"donetsk",
              sName: "Донецька область",
                a: [{
                  sID: "donetsk",
                  sName: "м. Донецьк",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:""
                  },

                  {
                  sID: "bahmut",
                  sName: "м. Бахмут",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1ijA3wPRbwX8MagP1STqqOk6RySVRqHynf1NvKgszFTQ/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovbahmut/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/igovbahmut/"
                  },

                  {
                  sID: "mangush",
                  sName: "м. Мангуш",
                  sCityPassportURL:"",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1687256011512309/"
                }]
            },


            {
              sID:"chernigiv",
              sName: "Чернігівська область",
                a: [{
                  sID: "chernigiv",
                  sName: "Чернігів",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1SeDnxAXXvDsQTmO9bbaDHBXClO6-KX5X3IFYmKmXKLk/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igovchernihiv/",
                  sVolunteersGroupURL:""
                  },

                  {
                  sID: "negin",
                  sName: "м. Ніжин",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1bkv6quMnDrDLTS-aG8XeJzcWrzrRog5C2XnUOZ5rsCk/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/igov.nizhyn/",
                  sVolunteersGroupURL:"https://groups.google.com/forum/#!forum/igov-nizhyn"
                  },

                  {
                  sID: "mena",
                  sName: "м. Мена",
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
                sName: "м. Чернівці",
                sCityPassportURL:"https://docs.google.com/spreadsheets/d/1m5JOxJ6coZyCNdVBHdfGtBYgEgg5r5v_dmvsLYBZqfE/edit?ts=566eb1f0#gid=1409053144",
                sNewsGroupURL:"https://www.facebook.com/igov.chernivtsi",
                sVolunteersGroupURL:"https://groups.google.com/forum/#!forum/igov-chernivtsy"
              }]
            },

            {
              sID:"ivano-frankivsk",
              sName: "Івано-Франківська область",
                a: [{
                  sID: "ivano-frankivsk",
                  sName: "м. Івано-Франківськ",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1iSzC0mJMlZ4zlGdMgplLSQsLHoU4TsVKY49lOUhz7HQ/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/727176644079090/",
                  sVolunteersGroupURL:"https://docs.google.com/spreadsheets/d/1iSzC0mJMlZ4zlGdMgplLSQsLHoU4TsVKY49lOUhz7HQ/edit#gid=1409053144"
                },
                {
                  sID: "kalush",
                  sName: "м. Калуш",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1_q3FAOtCn7agqeY0Nbf48jHkONzt85CRFI-N8vfHZbQ/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/groups/514345798738262/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1525342057760444/"
                  },

                  {
                  sID: "kolomia",
                  sName: "м. Коломия",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1P5KXpHEMJI2qhtpOj0GI-aRRSq6BusrC2KYOm0yOhy4/edit#gid=1409053144",
                  sNewsGroupURL:"https://www.facebook.com/iGov-%D0%9A%D0%BE%D0%BB%D0%BE%D0%BC%D0%B8%D1%8F-1538469346473515/",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/igovkolomyya"

                }]
            },

            {
              sID:"zaporizhya",
              sName: "Запорізька область",
                a: [{
                  sID: "zaporizhya",
                  sName: "м. Запоріжжя",
                  sCityPassportURL:"https://docs.google.com/spreadsheets/d/1Nz6-W5ezC1DCzm-vejnROkcm6KMckOKVtP6U_hF8rxM/edit#gid=1409053144",
                  sNewsGroupURL:"",
                  sVolunteersGroupURL:"https://www.facebook.com/groups/1667139696885838/"
                }]
            },

            {
              sID:"zakarpattya",
              sName: "Закарпатська область",
                a: [{
                  sID: "uzhgorod",
                  sName: "м. Ужгород",
                  sCityPassportURL:"",
                  sNewsGroupURL:"https://www.facebook.com/groups/1544798579164442/",
                  sVolunteersGroupURL:"https://groups.google.com/forum/?hl=en#!forum/igov-zakarpattya"
                }]
            },


            {
              sID:"kirovograd",
              sName: "Кіровоградська область",
                a: [{
                  sID: "kirovograd",
                  sName: "м. Кіровоград",
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
                  sName: "Цюріх"
                },
                {
                  sID: "singapore",
                  sName: "Сингапур"
                },
                {
                  sID: "copenhagen",
                  sName: "Копенгаген"
                },
                {
                  sID: "none",
                  sName: ""
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
          "sFIO":"Столбова Анна",
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
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Корчагін Павло",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Боборицький Денис",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Феденішин Андрій",
          "sURL":"",
          "sCity":"Дніпро"
        },
  		{
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Туренко Сергій",
          "sURL":"https://www.linkedin.com/in/sergey-turenko-b8708a94",
          "sPhoto":"https://media.licdn.com/mpr/mpr/shrinknp_200_200/p/7/005/0a3/012/1f8218c.jpg",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Волобуєв Дмитро",
          "sURL":"https://www.facebook.com/profile.php?id=100007298923596",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Горбань Сергій",
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
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Будник Дмитро",
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
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Гуліч Вадим",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Макаренко Валерій",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Дашенко Інна",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.dnipro",
          "sFIO":"Воронюк Євген",
          "sURL":"",
          "sCity":"Дніпро"
        },
        {
          "sID_Group":"dnipro.govtivodi.rada",
          "sFIO":"Гончар Володимир",
          "sURL":"https://www.facebook.com/gonvf",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12512535_944078765672226_2942303036351434166_n.jpg?oh=68ad81a88b8125219cbe6c20b2ea40e3&oe=581E4051",
          "sCity":"Жовті Води",
       	  "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"dnipro.govtivodi.rada",
          "sFIO":"Усов Олександр",
          "sCity":"Жовті Води"
        },
        {
          "sID_Group":"dnipro.govtivodi.rada",
          "sFIO":"Чорний Дмитро",
          "sURL":"https://www.facebook.com/bm.ukraine",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11987147_10203560073880837_2297829304350196503_n.jpg?oh=a99c19c8573621d6473cc6aa2291a158&oe=581BE5DA",
          "sCity":"Жовті Води"
        },
        {
          "sID_Group":"dnipro.nikopol.rada",
          "sFIO":"Ященко Ірина",
          "sURL":"https://www.facebook.com/iyaschenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12122659_999176800103778_2978037555071706320_n.jpg?oh=2b99d91e6d28576c577342528394f56d&oe=585CFF0A",
          "sCity":"Нікополь",
       	  "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"dnipro.nikopol.rada",
          "sFIO":"Колодіч Євген",
          "sCity":"Нікополь"
        },
        {
          "sID_Group":"dnipro.vilnogirsk.rada",
          "sFIO":"Глиняна Тетяна",
          "sURL":"https://www.facebook.com/profile.php?id=100009432326885",
          "sCity":"Вільногірськ"
        },
        {
          "sID_Group":"dnipro.kamenske.rada",
          "sFIO":"Кравцов Михайло",
          "sCity":"Кам'янське"
        },

        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Вербіцький Олександр",
          "sURL":"https://www.facebook.com/verbitskyi.aleksandr",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/21496_912289115556824_4265952439936957757_n.jpg?oh=480d850b7c82768e0de1f6ee2ccbd680&oe=581812A3",
          "sCity":"Хмельницький",
      	  "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Пляцик Юрій",
          "sURL":"https://www.facebook.com/yura.plyacik",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13537696_1156377111049092_6910064597484651978_n.jpg?oh=5fa96595b0b21d8fa5d1c0e59ab4bc4d&oe=584FE7BB",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Крупа Олександр",
          "sURL":"https://www.facebook.com/krupa.oleksandr",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12088209_138336699854420_3667079446770424011_n.jpg?oh=dbc83be27c7398bbb66a55c02c2ece53&oe=585AC0B6",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Вітішина Ельза",
          "sURL":"https://www.facebook.com/elya.vityshyna",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c33.33.413.413/s160x160/184899_1823507345840_3197773_n.jpg?oh=eb4d2d862fdd17431a7945670611fd50&oe=5851FD20",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Матвійчук Леся",
          "sURL":"https://www.facebook.com/profile.php?id=100010267147641",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12299380_168501876835383_8965072070346545787_n.jpg?oh=8317f590c69dd15093f6889b37944b02&oe=584EDD63",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Примуш Роман",
          "sURL":"https://www.facebook.com/prymush",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10439398_1580661178854407_8730334301039309476_n.jpg?oh=37cdd5754481a1265ff7686f8ada7f22&oe=5820D748",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Коваль Альона",
          "sURL":"https://www.facebook.com/profile.php?id=100001725485289",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13872806_1110687748998731_5868744183942959645_n.jpg?oh=55d7f48bd15615d1f51c003d5c005046&oe=58149AF4",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Попик Андрій",
          "sURL":"https://www.facebook.com/andriipopyk",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11825980_10207458546131064_9126739607132861522_n.jpg?oh=9791012f44f2770bc11685d06ffe02a6&oe=5821ED93",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy.rada",
          "sFIO":"Погребняк Ольга",
          "sURL":"https://www.facebook.com/olialedi",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13692492_1060862787323381_6849013182041158535_n.jpg?oh=bf4f9a4f8edd547c6e94b10622f8c6f8&oe=584DA0C3",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy",
          "sFIO":"Валентюк Сніжана",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy",
          "sFIO":"Щавінський Антон",
          "sURL":"https://www.facebook.com/anton.shchavinsky",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.0.160.160/p160x160/13062426_1014266421986184_3428012010300932484_n.jpg?oh=ea3441c4ab62dbb9c4ae3adf402966c7&oe=581EE783",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy",
          "sFIO":"Іванюк Валерій",
          "sURL":"https://www.facebook.com/valerij.ivanjuk",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13239108_1006830926032309_4047307760447517617_n.jpg?oh=33c3cf1a5e58947b8e8a03fb9b808846&oe=585EB5CB",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.khmelnytskiy",
          "sFIO":"Вавринюк Юрій",
          "sURL":"https://www.facebook.com/yurij.vavrynyuk",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13631404_297693547251722_3777643342925475082_n.jpg?oh=077c15e02ce040caa30c6423bcb25d81&oe=584DDCF0",
          "sCity":"Хмельницький"
        },
        {
          "sID_Group":"khmelnytskiy.netishin.rada",
          "sFIO":"Матросова Олена",
          "sURL":"https://www.facebook.com/olena.matrosova",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12274290_1653296344913498_2188353526296878343_n.jpg?oh=dac4869999d25d17ad5b93a3439c863d&oe=58513EAE",
          "sCity":"Нетішин",
      	  "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"khmelnytskiy.netishin.rada",
          "sFIO":"Рудомський Руслан",
          "sURL":"https://www.facebook.com/rudomskyi",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13939507_10207154762569908_1710901039836317726_n.jpg?oh=c3f76e9968aecabc3dbb119f8c4e5310&oe=5852FDB5",
          "sCity":"Нетішин"
        },
        {
          "sID_Group":"khmelnytskiy.shepetivka.rada",
          "sFIO":"Котенко Павло",
          "sURL":"https://www.facebook.com/pavlo.kotenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13238889_1327250380623737_7488932593139122889_n.jpg?oh=b46b4667fee24a5a168dbea42320fe8d&oe=585135C8",
          "sCity":"Шепетівка"
        },
        {
          "sID_Group":"khmelnytskiy.shepetivka.rada",
          "sFIO":"Чайка Ігор",
          "sURL":"https://www.facebook.com/igor.chayka.1",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c52.40.500.500/s160x160/292201_116092371859904_1118593307_n.jpg?oh=40d7cd605dca6a2cc30dfdef34b59918&oe=58106C84",
          "sCity":"Шепетівка"
        },
        {
          "sID_Group":"kyiv.kyiv.rada",
          "sFIO":"Шарапов Єгор",
          "sURL":"https://www.facebook.com/egorsha",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.10.160.160/p160x160/1966850_10203490579085516_348411029_n.jpg?oh=7a30398a86e7b0cddcfd23a7ca13238d&oe=581915B3",
          "sCity":"Київ",
          "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"kyiv.kyiv.rada",
          "sFIO":"Калініченко Ірина",
          "sURL":"https://www.facebook.com/irin.kalinichenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13498_856418117766324_769692464734795539_n.jpg?oh=7b8e326ffa4933f41ced888ce7190e9b&oe=585BC6A1",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv.rada",
          "sFIO":"Вадим Волос",
          "sURL":"https://www.facebook.com/vadymvolos",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/969077_563076740410432_913138478_n.jpg?oh=5816e4ea9ae61095dbd571ae33763ada&oe=581E1A89",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Зора Борис",
          "sURL":"",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Майданюк Дмитро",
          "sURL":"",
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
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Великотский Вячеслав",
          "sURL":"",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Наталія Гуран",
          "sURL":"https://www.facebook.com/nata.guran",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12654289_1021099344596205_3061116787172874678_n.jpg?oh=22042f798df097d1065ffe1a0b420a16&oe=585474CF",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Корженко Лариса",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Мелентьєва Людмила",
          "sURL":"https://www.facebook.com/ludmila.melentyeva",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.31.160.160/p160x160/19748_101184356578801_2557586_n.jpg?oh=2b8c6e5d587a3a75508d9b3fd37febb6&oe=581DF302",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Пастушенко Олексій",
          "sURL":"https://www.facebook.com/alex.past.771",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12289670_773935012732583_9055905272953874483_n.jpg?oh=aacb776d995496c1eee25b08a64981cf&oe=5850B9CD",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Пастушенко Алексей",
          "sURL":"https://www.facebook.com/alex.past.771",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12289670_773935012732583_9055905272953874483_n.jpg?oh=aacb776d995496c1eee25b08a64981cf&oe=5850B9CD",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Бернасовська Ольга",
          "sURL":"https://www.facebook.com/bernasovskaya",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/1780676_1489358851318502_6168472148616755825_n.jpg?oh=976b206625f089d276e7a5e306514597&oe=5821487B",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Андрей Домашенко",
          "sURL":"https://www.facebook.com/andrej.domashenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13102692_1055814591151636_2724676959100200991_n.jpg?oh=68c9ba9e0441d44bedee7db086684d67&oe=581541BA",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Лаврик Ирина",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.kyiv",
          "sFIO":"Коваленко Ігорь",
          "sCity":"Київ"
        },
        {
          "sID_Group":"kyiv.vyshgorod.rada",
          "sFIO":"Сокиржинський Олександр",
          "sURL":"https://www.facebook.com/alex.sokirjinskiy",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13092069_1028917970489685_2653187308818494451_n.jpg?oh=5388c4c374e7365b2230af346eb68876&oe=582491CD",
          "sCity":"Вишгород",
          "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"kyiv.vyshgorod.rada",
          "sFIO":"Кочелісова Марина",
          "sCity":"Вишгород"
        },
        {
          "sID_Group":"kyiv.vyshgorod.rada",
          "sFIO":"Пироженко Інна",
          "sURL":"https://www.facebook.com/inna.pyrozhenko",
          "sCity":"Вишгород"
        },
        {
          "sID_Group":"kyiv.vyshgorod.rada",
          "sFIO":"Титарчук Юлія",
          "sCity":"Вишгород"
        },
        {
          "sID_Group":"kyiv.vyshgorod.rada",
          "sFIO":"Шулежко Наталя",
          "sCity":"Вишгород"
        },

        {
          "sID_Group":"kyiv.bila-tserkva.rada",
          "sFIO":"Лаврусь Павел",
          "sURL":"https://www.facebook.com/p.lavrus",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12196019_522416387923078_4001051597931043315_n.jpg?oh=b61b518dbef7bf4d63248767b51e5b3a&oe=5854F567",
          "sCity":"Біла Церква",
          "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"kyiv.bila-tserkva.rada",
          "sFIO":"Лємєнов Олександр",
          "sURL":"https://www.facebook.com/o.lemenov",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-9/1441412_655782784473295_1700401491_n.jpg?oh=5feb1cf81cddce873d48cb6878170c52&oe=585B0945",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bila-tserkva",
          "sFIO":"Іванчук Руслана",
          "sURL":"https://www.facebook.com/profile.php?id=100008934434293",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bila-tserkva",
          "sFIO":"Скотар Юлія",
          "sURL":"https://www.facebook.com/yuliya.skotar",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-9/11062919_914443645306884_2021877743840497063_n.jpg?oh=319b502483a9c06c32b0805185c40f53&oe=585DF46E",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bila-tserkva",
          "sFIO":"Зозуля Роман",
          "sURL":"https://www.facebook.com/romnikzoz",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c12.17.147.147/166121_133020440090637_6416690_n.jpg?oh=c30c97f20e932a7c2fda6399fb37fd5e&oe=58100052",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bila-tserkva",
          "sFIO":"Нестеренко Юрій",
          "sURL":"https://www.facebook.com/profile.php?id=100003188277207",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bila-tserkva",
          "sFIO":"Ларюк Сергій",
          "sURL":"https://www.facebook.com/serg.si.7",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c18.17.218.218/s160x160/942803_141312672722377_105158731_n.jpg?oh=3d7dc2ba14e5c5c8f777b3f2ee74d57e&oe=584F8BDC",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bila-tserkva",
          "sFIO":"Степанчук Сергій",
          "sURL":"https://www.facebook.com/profile.php?id=100001276321735",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bila-tserkva",
          "sFIO":"Львова Світлана",
          "sURL":"https://www.facebook.com/svetlana.lvova.18",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11026017_637848019675066_4643291714744605501_n.jpg?oh=5e2ef77634959ebcfe30565dab79739d&oe=5810142B",
          "sCity":"Біла Церква"
        },
        {
          "sID_Group":"kyiv.bucha.rada",
          "sFIO":"Лисенко Іван",
          "sURL":"https://www.facebook.com/Lysenko.Ivan",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10441011_939516459428532_3278611361190235130_n.jpg?oh=fa81c99c8c17200f4744b59ef5cdcb19&oe=585B76A5",
          "sCity":"Буча",
      	  "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"kyiv.bucha.rada",
          "sFIO":"П'ядик Ірина",
          "sURL":"https://www.facebook.com/profile.php?id=100005781762917",
          "sCity":"Буча"
        },
        {
          "sID_Group":"kyiv.brovari.rada",
          "sFIO":"Ретта Наталія",
          "sURL":"https://www.facebook.com/natalia.retta.7",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11219023_887687687980795_7062814664090434318_n.jpg?oh=4f0f08889a6158ec904e7e462f4f04d7&oe=585D0FD2",
          "sCity":"Бровари",
          "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"kyiv.brovari.rada",
          "sFIO":"Пушкарьов Сергій",
          "sURL":"https://www.facebook.com/sergey.pushkarev.92",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c34.34.431.431/s160x160/575324_106361619501425_1350610443_n.jpg?oh=4eda6750d7445361227b29659376740c&oe=58591B9B",
          "sCity":"Бровари"
        },
        {
          "sID_Group":"kyiv.brovari",
          "sFIO":"Дяченко Аліна",
          "sURL":"https://www.facebook.com/alina.dyachenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12341353_1085997878090995_4747460443686081515_n.jpg?oh=a674581b766fff1affc356fe2677304c&oe=585411A9",
          "sCity":"Бровари"
        },
        {
          "sID_Group":"kyiv.brovari",
          "sFIO":"Пушкарьова Олена",
          "sURL":"https://www.facebook.com/profile.php?id=100001399363397",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11261203_928022097254427_8147402047381063345_n.jpg?oh=db83fcc475a00a949737d32fed764299&oe=58174287",
          "sCity":"Бровари"
        },
        {
          "sID_Group":"kyiv.brovari",
          "sFIO":"Талалай Вікторія",
          "sCity":"Бровари"
        },
        {
          "sID_Group":"kyiv.brovari",
          "sFIO":"Сергій Іллюхін",
          "sURL":"https://www.facebook.com/sergii.illiukhin",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12049169_10207763983279340_8325553375466709535_n.jpg?oh=f4f7015f8b9a5fe46bd2070299acac23&oe=5819595B",
          "sCity":"Бровари"
        },
        {
          "sID_Group":"kyiv.brovari",
          "sFIO":"Улітенко Ірина",
          "sURL":"https://www.facebook.com/iulitenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13775543_1079760695435896_9138038083246565957_n.jpg?oh=3a5e36263982e75e5f7904e73e954c8c&oe=58164622",
          "sCity":"Бровари"
        },
        {
          "sID_Group":"kyiv.makariv.rada",
          "sFIO":"Гончарова Тетяна",
          "sURL":"https://www.facebook.com/tanja.goncharova",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.41.160.160/p200x200/11891269_952001978206576_6268696842288680632_n.jpg?oh=19929ba17c9fa69635b0fbf1d76bc6a2&oe=58163EB2",
          "sCity":"Макарів"
        },
        {
          "sID_Group":"kyiv.boyarka.rada",
          "sFIO":"Гужва Арсен",
          "sURL":"https://www.facebook.com/arsen.guzhva",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12802793_1752583634961900_8668635582533018800_n.jpg?oh=2885454a4aeeac160a2b9360411375c6&oe=5816C953",
          "sCity":"Боярка"
        },
        {
          "sID_Group":"kyiv.boyarka.rada",
          "sFIO":"Кириленко Марія",
          "sURL":"https://www.facebook.com/M.Iv.Kyrylenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.27.160.160/p160x160/34589_115485685163875_579632_n.jpg?oh=6b9531389676f2bf9eae38ff176d36f6&oe=58569E1D",
          "sCity":"Боярка"
        },
        {
          "sID_Group":"kyiv.vishneve.rada",
          "sFIO":"Пєшков Павло",
          "sURL":"https://www.facebook.com/Paule.HAN",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.48.336.336/s160x160/165709_194586083889282_3404844_n.jpg?oh=8bc54e93c6fb453db65a7cc85e0223a5&oe=5814CDF5",
          "sCity":"Вишневе"
        },
        {
          "sID_Group":"kyiv.vishneve.rada",
          "sFIO":"Бойко Павло",
          "sURL":"https://www.facebook.com/p.boiko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.43.160.160/p160x160/11988303_942273712486925_9031769201948679345_n.jpg?oh=632c73a76c3bfdac659612f57dd18c07&oe=58222490",
          "sCity":"Вишневе"
        },
        {
          "sID_Group":"kyiv.irpin.rada",
          "sFIO":"Білоконь Віктор",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Ірпінь"
        },
        {
          "sID_Group":"ternopil.ternopil.rada",
          "sFIO":"Данилевич Софія",
          "sURL":"https://www.facebook.com/sophia.danylevich",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13315586_1051012694987834_4701805118846809487_n.jpg?oh=98e4dcfe7e578c77da8a6f6476400d8d&oe=58597FC5",
          "sCity":"Тернопіль"
        },
        {
          "sID_Group":"ternopil.ternopil.rada",
          "sFIO":"Кондратюк Катерина",
          "sURL":"https://www.facebook.com/kondrkatya",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12391777_993246580749114_1136134211307250107_n.jpg?oh=65acbe9a5682de04996442840c6f1300&oe=5850E319",
          "sCity":"Тернопіль"
        },
        {
          "sID_Group":"rivne.rivne.rada",
          "sFIO":"Дюг Юрій",
          "sURL":"https://www.facebook.com/yuriy.dyug",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10923531_1000018706694178_455606146826939393_n.jpg?oh=92dd846cda22cb5df1c9bd86efbb6131&oe=5822EDA5",
          "sCity":"Рівне",
          "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"rivne.rivne.rada",
          "sFIO":"Киричук Олександр",
          "sURL":"https://www.facebook.com/oleksandr.kyrylchuk",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12552519_1018604581533558_5324102037207454303_n.jpg?oh=d9d86434cea13bbe7e9768ca067b4478&oe=585C26A2",
          "sCity":"Рівне"
        },
        {
          "sID_Group":"rivne.rivne",
          "sFIO":"Федчук Олександр",
          "sURL":"https://www.facebook.com/profile.php?id=100002148663785",
          "sPhoto":"e=https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10302098_752938611454421_8342659531263729684_n.jpg?oh=393b5928b6a39a42654bb298e425d550&oe=581D2C7D",
          "sCity":"Рівне"
        },
        {
          "sID_Group":"rivne.rivne",
          "sFIO":"Федорук Анна",
          "sURL":"https://www.facebook.com/ana.slipcenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/22309_825562030849318_3764224607233062134_n.jpg?oh=b18c9ed0547442d4f44053d2cf14338a&oe=58565A0C",
          "sCity":"Рівне"
        },
        {
          "sID_Group":"rivne.rivne",
          "sFIO":"Някшу Вероніка",
          "sURL":"https://www.facebook.com/veronika.kobrina",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13096248_10208303533758576_7482633870385518245_n.jpg?oh=c6ea1d480ec5a70a3a23379f0ad72c92&oe=58555737",
          "sCity":"Рівне"
        },
        {
          "sID_Group":"rivne.rivne",
          "sFIO":"Рембовська Арміне",
          "sURL":"https://www.facebook.com/armine.rembovskaya",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11428725_1455218131438537_3689246994590755199_n.jpg?oh=900cf392729c2f62929b1bdcf6811e16&oe=581ACA16",
          "sCity":"Рівне"
        },
        {
          "sID_Group":"rivne.rivne.rada",
          "sFIO":"Назарчук Юлія",
          "sURL":"https://www.facebook.com/julia.nazarchuk.92",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13938620_1748593062020106_8746100073451884611_n.jpg?oh=df268ca256ff294318cbd17979146f9d&oe=581D41CE",
          "sCity":"Рівне"
        },
        {
          "sID_Group":"rivne.varash.rada",
          "sFIO":"Поремчук Євгеній",
          "sURL":"https://www.facebook.com/e.poremchuk",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11866439_10206601466262607_2800248533852732931_n.jpg?oh=2dfad633f47ada22b9677862b52628ef&oe=584C2EF6",
          "sCity":"Вараш",
       	  "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"rivne.varash.rada",
          "sFIO":"Радюк Тетяна",
          "sURL":"https://www.facebook.com/tatiana.yakubovich.1",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11029895_970261326341199_1621442036029963678_n.jpg?oh=d719cdcbadde505f9fe1abb80ee69519&oe=5855D191",
          "sCity":"Вараш"
       	},
  		{
          "sID_Group":"rivne.ostrog.rada",
          "sFIO":"Галич Сергій",
          "sURL":"https://www.facebook.com/serhiy.halich",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11903710_1016467491743026_2145437931467083079_n.jpg?oh=cd457b4bc836e270fb9f3a89082691fe&oe=5813239B",
          "sCity":"Острог",
       	  "sInfo":"Координатор міста"
        },
  	    {
          "sID_Group":"poltava.poltava.rada",
          "sFIO":"Городчаніна Юлія",
          "sURL":"https://www.facebook.com/yuliya.bobur",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13310494_1800267660195875_4333125623057871212_n.jpg?oh=50394e7533a4d91c1c06399a781f014c&oe=5815EACA",
          "sCity":"Полтава",
          "sInfo":"Координатор міста"
        },
        {
          "sID_Group":"poltava.poltava.rada",
          "sFIO":"Гончаренко Альона",
          "sURL":"https://www.facebook.com/AlenaV.Goncharenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12191060_1242068505819208_8508880021504014101_n.jpg?oh=e160cedada87fed81f31e1a33eae74c7&oe=5818EA92",
          "sCity":"Полтава"
        },
        {
          "sID_Group":"poltava.poltava.rada",
          "sFIO":"Пустовіт Сергій",
          "sURL":"https://www.facebook.com/pustovitsv",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13439203_10205360697251118_7629907667338368564_n.jpg?oh=f7bc27289e5a03e8737a315e731acb08&oe=582146E0",
          "sCity":"Полтава"
        },
        {
          "sID_Group":"poltava.poltava.rada",
          "sFIO":"Захаров Тарас",
          "sURL":"https://www.facebook.com/taras.zakharov.9",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c1.0.160.160/p160x160/13083249_1703496926567997_1155642791542906971_n.jpg?oh=aa722fc3b9297f6b0ef2949577afc421&oe=585B3659",
          "sCity":"Полтава"
        },
        {
          "sID_Group":"poltava.poltava.rada",
          "sFIO":"Ямщиков Вадим",
          "sURL":"https://www.facebook.com/vadim.yamshchikov",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12036618_1653097091641581_5799162348914427574_n.jpg?oh=143b7f08f24fdb3eb98573fd05ba4ff9&oe=5819FE18",
          "sCity":"Полтава"
        },

        {
          "sID_Group":"poltava.myrgorod.rada",
          "sFIO":"Слємзін Олександр",
          "sURL":"https://www.facebook.com/alexandr.slemzin",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/13269_10205607206771207_8344419548296787003_n.jpg?oh=fabef51ac001b6f626726d5f6ca0d2ee&oe=5855E1E9",
          "sCity":"Миргород",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"poltava.myrgorod.rada",
          "sFIO":"Ковтун Татьяна",
          "sURL":"https://www.facebook.com/TaniqueK",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.14.160.160/p160x160/11200835_871751776234701_9018613554521976127_n.jpg?oh=c69ecc0f766057776e6bcf1edb418413&oe=58568B5C",
          "sCity":"Миргород"
        },

        {
          "sID_Group":"poltava.lubni.rada",
          "sFIO":"Богаєвський Юрій",
          "sURL":"https://www.facebook.com/profile.php?id=100006495822797",
          "sCity":"Лубни",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"poltava.lubni.rada",
          "sFIO":"Ляшенко Валентина",
          "sURL":"https://www.facebook.com/profile.php?id=100007737824290",
          "sCity":"Лубни"
        },

        {
          "sID_Group":"poltava.lubni.rada",
          "sFIO":"Грищенко Андрій",
          "sURL":"https://www.facebook.com/profile.php?id=100001990108532",
          "sCity":"Лубни"
        },

        {
          "sID_Group":"poltava.lubni.rada",
          "sFIO":"Гузь Анатолій",
          "sURL":"https://www.facebook.com/anatoly.guz",
          "sCity":"Лубни"
        },

         {
          "sID_Group":"poltava.lubni.rada",
          "sFIO":"Ладика Руслан",
          "sURL":"https://www.facebook.com/profile.php?id=100001276076777",
          "sCity":"Лубни"
        },

        {
          "sID_Group":"poltava.lubni.rada",
          "sFIO":"Павловський Ілля",
          "sURL":"https://www.facebook.com/profile.php?id=100003296871105",
          "sCity":"Лубни"
        },

        {
          "sID_Group":"poltava.kobelyaki.rada",
          "sFIO":"Безкібальний Олег",
          "sURL":"https://www.facebook.com/bezkibalniy",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c15.0.160.160/p160x160/10478141_653076371450483_7688422733698352628_n.jpg?oh=bd12e8543d18f87b507c955538a578c5&oe=581499AD",
          "sCity":"Кобеляки",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"poltava.kobelyaki.rada",
          "sFIO":"Пелюхня Ігор",
          "sURL":"https://www.facebook.com/igor.pelyuhnya",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10801819_413970195428055_1638372654564524042_n.jpg?oh=0e8e9c6d852e38b81a87a06efbfb42f6&oe=5857D596",
          "sCity":"Кобеляки"
        },

        {
          "sID_Group":"poltava.kremenchug.rada",
          "sFIO":"Курченко-Гай Валентин",
          "sURL":"https://www.facebook.com/theo10000",
          "sCity":"Кременчук",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"poltava.kremenchug.rada",
          "sFIO":"Немченко Валентина",
          "sURL":"https://www.facebook.com/valentina.nemtchenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/10685543_670319569730693_2776910852201495032_n.jpg?oh=2f6ba8770b10d0a0fdb9f18817c67e51&oe=584A368F",
          "sCity":"Кременчук"
        },

        {
          "sID_Group":"odesa.odesa.rada",
          "sFIO":"Юрченко Роман",
          "sURL":"https://www.facebook.com/iroyur?fref=ts",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12308646_839362439509936_8503452484623107341_n.jpg?oh=ff3e189508953adc99011562674c0ccf&oe=58208485",
          "sCity":"Одеса"
        },

                {
          "sID_Group":"odesa.odesa",
          "sFIO":"Сафроненков Андрей",
          "sURL":"",
          "sCity":"Одеса"
        },


        {
          "sID_Group":"odesa.odesa",
          "sFIO":"Лупашко Богдан",
          "sURL":"",
          "sCity":"Одеса"
        },

        {
          "sID_Group":"odesa.odesa",
          "sFIO":"Соловьев Александр",
          "sURL":"https://ua.linkedin.com/in/%D0%B0%D0%BB%D0%B5%D0%BA%D1%81%D0%B0%D0%BD%D0%B4%D1%80-%D1%81%D0%BE%D0%BB%D0%BE%D0%B2%D1%8C%D0%B5%D0%B2-a2108085",
          "sPhoto":"https://media.licdn.com/mpr/mpr/shrinknp_200_200/p/7/005/059/1eb/2db7911.jpg",
          "sCity":"Одеса"
        },

        {
          "sID_Group":"kherson.kherson.rada",
          "sFIO":"Кулик Павло",
          "sURL":"https://www.facebook.com/kylikpavel",
           "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13903355_1127327543980103_2086857622526032794_n.jpg?oh=fc5f4bb561330af067aa882fce59457f&oe=5818978F",
          "sCity":"Херсон"
        },

         {
          "sID_Group":"mykolaiv.mykolaiv.rada",
          "sFIO":"Решетнік Юрій",
          "sURL":"https://www.facebook.com/jurij.reshetnik?fref=ts",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c10.0.160.160/p160x160/390787_250501421672958_1960030092_n.jpg?oh=0823eeb98a87edb00a90f2f0df9b917e&oe=5818BB0A",
          "sCity":"Миколаїв"
        },

        {
          "sID_Group":"mykolaiv.mykolaiv.rada",
          "sFIO":"Казимир Олена",
          "sURL":"https://www.facebook.com/elena.kazimir",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13331101_970823463013528_6076238489650790624_n.jpg?oh=fd4662d08fd3aba22253951b5f6c2b48&oe=5810258F",
          "sCity":"Миколаїв"
        },

         {
          "sID_Group":"mykolaiv.mykolaiv",
          "sFIO":"Жмурков Александр",
          "sURL":"",
          "sCity":"Миколаїв"
        },

        {
          "sID_Group":"mykolaiv.pervomaysk.rada",
          "sFIO":"Булгакова Тетяна",
          "sURL":"https://www.facebook.com/tanya.bulgakova.1",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/10696394_750915754993753_6353623982169031743_n.jpg?oh=d735a91baf7fdfdc19490e15af94305b&oe=585ABC52",
          "sCity":"Первомайськ"
        },

        {
          "sID_Group":"mykolaiv.pervomaysk.rada",
          "sFIO":"Дементьєва Марія",
          "sURL":"https://www.facebook.com/profile.php?id=100008312552131",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c18.18.925.925/s160x160/10986908_1596486893971714_1320803372531555273_n.jpg?oh=d4036fd7b8fded2eb1505d69539b5c7e&oe=585AA7E5",
          "sCity":"Первомайськ"
        },

        {
          "sID_Group":"mykolaiv.pervomaysk.rada",
          "sFIO":"Кухаренко Всеволод",
          "sURL":"https://www.facebook.com/profile.php?id=100001700035596",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c142.54.675.675/s160x160/199980_409589752441022_1256854001_n.jpg?oh=a4ebb70a5dd4ae22b83888aa18b814a4&oe=585D0C40",
          "sCity":"Первомайськ"
        },

        {
          "sID_Group":"mykolaiv.pervomaysk.rada",
          "sFIO":"Донченко Сергій",
          "sURL":"https://www.facebook.com/donchenko.sergey?fref=hovercard",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c49.0.160.160/p160x160/10590531_10204371410146590_3737003999679112870_n.jpg?oh=7282dde4b34df96355c2ff80f5b87a04&oe=5850CCE9",
          "sCity":"Первомайськ"
        },

        {
          "sID_Group":"mykolaiv.pervomaysk.rada",
          "sFIO":"Корой Віталій",
          "sURL":"https://www.facebook.com/vitalii.koroi",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c44.298.552.552/s160x160/75056_461672333894241_1916982663_n.jpg?oh=a1360ff58629c884cfc0032fe9ad9090&oe=5816C4ED",
          "sCity":"Первомайськ"
        },

        {
          "sID_Group":"kharkiv.kharkiv",
          "sFIO":"Большуткин Владимир",
          "sURL":"",
          "sCity":"Харків"
        },

        {
          "sID_Group":"kharkiv.kharkiv.rada",
          "sFIO":"Черкашин Сергій",
          "sURL":"https://www.facebook.com/boomer33x",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c170.49.619.619/s160x160/305556_110861075713255_980388014_n.jpg?oh=387932a3476d882494353f1d3add67e1&oe=584B19E7",
          "sCity":"Харків"
        },

        {
          "sID_Group":"kharkiv.kharkiv.rada",
          "sFIO":"Болгова Оксана",
          "sURL":"https://www.facebook.com/profile.php?id=100004862344397",
          "sCity":"Харків"
        },

        {
          "sID_Group":"kharkiv.kharkiv.rada",
          "sFIO":"Голуб Марина",
          "sURL":"https://www.facebook.com/marina.gollub",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11036420_822540414504237_6462367547555957037_n.jpg?oh=7bf9ef9090776e0e715d0831ef2f1ec1&oe=58543DDC",
          "sCity":"Харків"
        },

        {
          "sID_Group":"kharkiv.kharkiv.rada",
          "sFIO":"Борхович Анатолій",
          "sURL":"https://www.facebook.com/a.borkhovych",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12065816_10207769442979352_7925749401844117913_n.jpg?oh=6f02cd40c2610a84d89d470fd97660a1&oe=58535E54",
          "sCity":"Харків"
        },

        {
          "sID_Group":"kharkiv.kharkiv.rada",
          "sFIO":"Черевко Кирило",
          "sURL":"https://www.facebook.com/Cherevko.kirill",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12247067_1022647361124893_1157524489600301380_n.jpg?oh=5872f4d602b4edeae89ab5c6955e3828&oe=585EB2A4",
          "sCity":"Харків"
        },

        {
          "sID_Group":"kharkiv.kharkiv.rada",
          "sFIO":"Михайличенко Олена",
          "sURL":"https://www.facebook.com/elena.mykhaylichenko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.0.160.160/p160x160/10599198_744036318971325_9195847072957001424_n.jpg?oh=83ddc9c20eb8a7815f65f774b9a34453&oe=584F6BC6",
          "sCity":"Харків"
        },

            {
          "sID_Group":"kharkiv.kharkiv",
          "sFIO":"Педич Максим",
          "sURL":"",
          "sCity":"Харків"
        },

         {
          "sID_Group":"kharkiv.kharkivrayon.rada",
          "sFIO":"Костецький Артем",
          "sURL":"https://www.facebook.com/dobravolya.kharkov",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12243166_917113878358148_5679464966402234614_n.jpg?oh=6e2225138641ac1cce4f11ab34d376b2&oe=581D29CA",
          "sCity":"Харківський район"
        },

        {
          "sID_Group":"kharkiv.kharkivrayon.rada",
          "sFIO":"Калмиков Дмитро",
          "sURL":"https://www.facebook.com/dimakainfo",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13592307_1060150010688766_8461665128658238096_n.jpg?oh=d5d2a0dba2a00afc64aa0adb03be0fae&oe=584F8079",
          "sCity":"Харківський район"
        },

        {
          "sID_Group":"kharkiv.kharkivrayon.rada",
          "sFIO":"Кукліна Юлія",
          "sURL":"https://www.facebook.com/julia.kuklean",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12241501_456896611167180_2170675904355798612_n.jpg?oh=2ee8fe9c22355ec6fbcf55584755c7e9&oe=584ABD9F",
          "sCity":"Харківський район"
        },


        {
          "sID_Group":"kharkiv.balakleya.rada",
          "sFIO":"Жарко Віталій",
          "sURL":"https://www.facebook.com/vzharko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c44.140.439.439/s160x160/148105_119110354817739_3999174_n.jpg?oh=8d8fdff668b815bd150bc1e7929c7aa3&oe=58503C37",
          "sCity":"Балаклія"
        },

        {
          "sID_Group":"kharkiv.balakleya.rada",
          "sFIO":"Чавир Павло",
          "sURL":"https://www.facebook.com/pavelchavyr",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10425030_421508464679485_5292986022012250297_n.jpg?oh=6528a22b1ccaa91ab9608addbf443208&oe=581776DE",
          "sCity":"Балаклія"
        },


        {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Лященко Володимир",
          "sURL":"https://www.facebook.com/UgaqFHkVC",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10923199_798210820234224_5052169545311231817_n.jpg?oh=ef403f3d518c58a53854d89a65ce33a1&oe=5817DACB",
          "sCity":"Богодухів"
        },

       {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Шмаков Іван",
          "sURL":"https://www.facebook.com/profile.php?id=100001124686957",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10415679_1014693445244802_351137081710861196_n.jpg?oh=8c6f9bc8c071b7bc31822f85c22db914&oe=581BADC5",
          "sCity":"Богодухів"
        },

        {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Оксенич Валентин",
          "sURL":"https://www.facebook.com/profile.php?id=100010448146484",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c151.0.733.733/s160x160/12321608_160212291003708_2154323811760212833_n.jpg?oh=040295987bb918d3f3d4be43f573288a&oe=58178A4A",
          "sCity":"Богодухів"
        },

         {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Коваленко Андрій",
          "sURL":"https://www.facebook.com/profile.php?id=100001691240388",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/550732_443438539055850_140973306_n.jpg?oh=ab3f677dfad0d2e0cfa727d12d2e7b20&oe=5812D755",
          "sCity":"Богодухів"
        },

        {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Кириченко Ольга",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Богодухів"
        },

        {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Скирда Олександр",
          "sURL":"https://www.facebook.com/profile.php?id=100000662492943",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/1939912_859776474054428_7359486192117554923_n.jpg?oh=71ca7654208937b779eb66ec9f1ebb0a&oe=585AACA0",
          "sCity":"Богодухів"
        },

         {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Ворвуль Володимир",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Богодухів"
        },

         {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Міщенко Ольга",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Богодухів"
        },

         {
          "sID_Group":"kharkiv.bogoduhiv.rada",
          "sFIO":"Бабенко Оксана",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Богодухів"
        },

        {
          "sID_Group":"lviv.lviv.rada",
          "sFIO":"Стець Ольга",
          "sURL":"https://www.facebook.com/olga.kuk.7?fref=ts",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13118949_1090622227657248_4317645524507437859_n.jpg?oh=f0c3d5f3088405f3749a349ef9bb04e6&oe=581BC784",
          "sCity":"Львів",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"lviv.lviv.rada",
          "sFIO":"Ємельянов Андрій",
          "sURL":"https://www.facebook.com/andriy.emelyanov?fref=ts",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12348008_10206321727654894_12572916702785949_n.jpg?oh=f7da99b2f9e33032d8df2ea675225fbe&oe=584D9EEC",
          "sCity":"Львів"
        },

        {
          "sID_Group":"lviv.lviv.rada",
          "sFIO":"Мочан Анна",
          "sURL":"https://www.facebook.com/anna.mochan?fref=ts",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13876370_1745329249041968_2174109669241323848_n.jpg?oh=4ca9f37e6610d7b05cd4eb3b32674417&oe=581B3E37",
          "sCity":"Львів"
        },

        {
          "sID_Group":"lviv.lviv.rada",
          "sFIO":"Драбик Ігор ",
          "sURL":"https://www.facebook.com/ihor.drabyk?fref=pb_other",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c40.0.160.160/p160x160/10363399_675226872559570_7903489327376029821_n.jpg?oh=42583f01faa83c932816502f86661d0d&oe=581DAE6D",
          "sCity":"Львів"
        },

        {
          "sID_Group":"lviv.lviv",
          "sFIO":"Жебраков Глеб",
          "sURL":"",
          "sCity":"Львів"
        },

        {
          "sID_Group":"lviv.lviv",
          "sFIO":"Янов Андрей",
          "sURL":"",
          "sCity":"Львів"
        },

  {
          "sID_Group":"lviv.lviv",
          "sFIO":"Гуцуляк Олександр",
          "sURL":"",
          "sCity":"Львів"
        },

        {
          "sID_Group":"lviv.chervonograd.rada",
          "sFIO":"Квасній Артур",
          "sURL":"https://www.facebook.com/artur.kvasnii?fref=ts",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c34.0.133.133/58409_157035217656511_6964268_n.jpg?oh=179867c9a2114cf6a336e0d129b8b8fa&oe=5812CCB7",
          "sCity":"Червоноград",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"lviv.ctriy.rada",
          "sFIO":"Тиченко Євген",
          "sURL":"https://www.facebook.com/profile.php?id=100002165937951",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12065719_887088831373306_4523486344892046187_n.jpg?oh=462d4a5de01c44616869804e7f888fa2&oe=584E6AF8",
          "sCity":"Стрий"
        },


        {
          "sID_Group":"lviv.ctriy.rada",
          "sFIO":"Сікора Олександр",
          "sURL":"https://www.facebook.com/Aleksandr.Sikora",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c6.0.160.160/p160x160/10441202_10203658374090326_4714475090803473129_n.jpg?oh=ffd889a2d372ac5b48345ca35f03099a&oe=584F148E",
          "sCity":"Стрий"
        },

        {
          "sID_Group":"vinnytsia.vinnytsia.rada",
          "sFIO":"Мукомол Вадим",
          "sURL":"https://www.facebook.com/vadim.munin",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13322177_1128005340575202_2101175329914658142_n.jpg?oh=5f1cc91ede0ea2075becc8e315b435f4&oe=5818D3A5",
          "sCity":"Вінниця"
        },

        {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Горкуша Михайло",
          "sURL":"https://www.facebook.com/mgorkusha",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c2.0.565.565/s160x160/10505527_970637849630121_6526722851659838624_n.jpg?oh=367c93a7a8f8c8380f5d63e264ac5dd5&oe=581622CD",
          "sCity":"Суми",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Бузок Лана",
          "sURL":"https://www.facebook.com/lana.buzok",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c127.37.466.466/s160x160/215757_102699656485327_2355595_n.jpg?oh=f9d9f3c1fb081ae70369c14dd78f5b3a&oe=585679E8",
          "sCity":"Суми"
        },

         {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Летюга Юлія",
          "sURL":"https://www.facebook.com/profile.php?id=100004160319984",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13529112_680275798787758_1602275812019151041_n.jpg?oh=03fe97ecb4647dc5c06cbc46925d28eb&oe=5857DAA1",
          "sCity":"Суми"
        },

         {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Кузьмичов Євген",
          "sURL":"https://www.facebook.com/ekuzmichov",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13895484_1086443681448198_7218521654132330425_n.jpg?oh=1ed6aa95a93906fec1361cb919ab2065&oe=5852E391",
          "sCity":"Суми"
        },

         {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Сахно Валентина",
          "sURL":"https://www.facebook.com/valenta.sakhno",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c121.118.229.229/s160x160/13626961_1058169447603344_948595910807164841_n.jpg?oh=9e79d9a18e32b4b6e32d3286261ea208&oe=585B4705",
          "sCity":"Суми"
        },

        {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Сидоренко Наталія",
          "sURL":"https://www.facebook.com/natalya.sydorenko.5",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/10629740_752913151447449_6676359151058605651_n.jpg?oh=088b6e9560b47e9aa5ef369a6b3ad847&oe=58149FC4",
          "sCity":"Суми"
        },

         {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Бєломар Віктор",
          "sURL":"https://www.facebook.com/testovy.zapys",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/10410814_623836591048905_358311601518934968_n.jpg?oh=03f415738e290aeb38a6fd05ef53fd00&oe=5819DC49",
          "sCity":"Суми"
        },

         {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Губа Антон",
          "sURL":"https://www.facebook.com/anton.guba.988?fref=ufi",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c0.40.160.160/p160x160/13321836_1553826794924853_7488945860080742_n.jpg?oh=0319ec6cfbdfc8cc5474f0b2590b9fb0&oe=584F9E14",
          "sCity":"Суми"
        },

        {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Шкурат Іван",
          "sURL":"https://www.facebook.com/profile.php?id=100007617124826",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13658953_1696985720565316_3094432700811893823_n.jpg?oh=9d9813305e1dd5bf9bbcafa248d00133&oe=584C91D6",
          "sCity":"Суми"
        },

        {
          "sID_Group":"sumy.sumy.rada",
          "sFIO":"Бунаас Людмила",
          "sURL":"https://www.facebook.com/liudmyla.bounaas",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c65.0.160.160/p160x160/375713_640394686024997_1866618190_n.jpg?oh=873c0cfc72f5685d5190ccdbfa0b215e&oe=585C8AB2",
          "sCity":"Суми"
        },

                {
          "sID_Group":"sumy.sumy",
          "sFIO":"Дубинка Юрій",
          "sURL":"https://ua.linkedin.com/pub/yurii-dubinka/b9/16/45b",
          "sPhoto":"https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAALQAAAAJDY1YWIxNGRlLTEyZmItNGY4OC1iOWIxLWFlYTFjOTQ3MWU5Nw.jpg",
          "sCity":"Суми"
        },

        {
          "sID_Group":"sumy.sumy",
          "sFIO":"Олексій Сердюк",
          "sURL":"https://ua.linkedin.com/in/alexey-serdiuk-994a07101",
          "sPhoto": "https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAANJAAAAJDgxMjM2MTYzLWFlODgtNGU0NC1iMDg4LTE4MDM0MzVhMjBhMg.jpg",
          "sCity":"Суми"
        },

        {
          "sID_Group":"sumy.ohtirka.rada",
          "sFIO":"Горкуша Михайло",
          "sURL":"https://www.facebook.com/mgorkusha",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c2.0.565.565/s160x160/10505527_970637849630121_6526722851659838624_n.jpg?oh=367c93a7a8f8c8380f5d63e264ac5dd5&oe=581622CD",
          "sCity":"Охтирка",
          "sInfo":"Координатор міста"
        },

         {
          "sID_Group":"sumy.ohtirka.rada",
          "sFIO":"Сидоренко Наталія",
          "sURL":"https://www.facebook.com/natalya.sydorenko.5",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/10629740_752913151447449_6676359151058605651_n.jpg?oh=088b6e9560b47e9aa5ef369a6b3ad847&oe=58149FC4",
          "sCity":"Охтирка"
        },

        {
          "sID_Group":"sumy.ohtirka.rada",
          "sFIO":"Марченко Руслан",
          "sURL":"https://www.facebook.com/ruslan.marchenko.5",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12994398_681239742015638_999233168024556913_n.jpg?oh=5e9d3472e07b34ae77e7eb95a3be2701&oe=5857F879",
          "sCity":"Охтирка"
        },

        {
          "sID_Group":"sumy.gluhiv.rada",
          "sFIO":"Павловець Ілля",
          "sURL":"https://www.facebook.com/Freedom.hl",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10955798_832812253442342_8854510801972688533_n.jpg?oh=095a285238be1dee3f04fce4bee726f3&oe=58485628",
          "sCity":"Глухів",
          "sInfo":"Координатор міста"
        },


        {
          "sID_Group":"sumy.shostka.rada",
          "sFIO":"Горкуша Михайло",
          "sURL":"https://www.facebook.com/mgorkusha",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c2.0.565.565/s160x160/10505527_970637849630121_6526722851659838624_n.jpg?oh=367c93a7a8f8c8380f5d63e264ac5dd5&oe=581622CD",
          "sCity":"Шостка",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"sumy.shostka.rada",
          "sFIO":"Сидоренко Наталія",
          "sURL":"https://www.facebook.com/natalya.sydorenko.5",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/10629740_752913151447449_6676359151058605651_n.jpg?oh=088b6e9560b47e9aa5ef369a6b3ad847&oe=58149FC4",
          "sCity":"Шостка"
        },

        {
          "sID_Group":"sumy.shostka.rada",
          "sFIO":"Москаленко Галина",
          "sURL":"https://www.facebook.com/halyna.moskalenko",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/1525098_424658800995496_376116867_n.jpg?oh=968b84cf3150d83fd3ea4b2a65a67744&oe=581E810F",
          "sCity":"Шостка"
        },

        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Колодіч Олена",
          "sURL":"https://www.facebook.com/profile.php?id=100011166150937",
          "sCity":"Черкаси"
        },

        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Глибочко Олександр",
          "sURL":"https://www.facebook.com/AlexanderGlybo",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12928395_1163331457033988_678737066450901642_n.jpg?oh=51454d57b8aa2c33eff91e6d2d1543cc&oe=58163A65",
          "sCity":"Черкаси"
        },

        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Дячок Дарина",
          "sURL":"https://www.facebook.com/profile.php?id=100004683825896",
          "sCity":"Черкаси"
        },

        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Коваленко Віктор",
          "sURL":"https://www.facebook.com/profile.php?id=100009884892039",
          "sCity":"Черкаси"
        },

        {
          "sID_Group":"cherkasy.cherkasy.rada",
          "sFIO":"Карманник Роман",
          "sURL":"https://www.facebook.com/roman.karmannik?fref=ts",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11817190_1716989245108104_8288166504827133096_n.jpg?oh=8a6f9dd839594ad3d95f1e8d8d1aafa3&oe=5851AEE6",
          "sCity":"Черкаси"
        },

        {
          "sID_Group":"cherkasy.smila.rada",
          "sFIO":"Хівренко Вадим",
          "sURL":"https://www.facebook.com/rezhissser",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c34.34.431.431/s160x160/427047_3462371203517_1788723060_n.jpg?oh=a479da1dfe8be819b8fa36b2682aaf58&oe=584DC530",
          "sCity":"Сміла"
        },

        {
          "sID_Group":"cherkasy.zvenigorodka.rada",
          "sFIO":"Насадчук Андрій",
          "sURL":"https://www.facebook.com/andriy.nasa",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11887878_1607195519554325_8947948008351283675_n.jpg?oh=18b2f1325c5f404011b759af9bef5ea6&oe=5818B10E",
          "sCity":"Звенигоро́дка",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"cherkasy.zvenigorodka.rada",
          "sFIO":"Ревенко Оля",
          "sURL":"https://www.facebook.com/olia.revenko.ua",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13962762_1142283669170607_3332757661793358989_n.jpg?oh=856458100b876fe007627986691287a3&oe=58551112",
          "sCity":"Звенигоро́дка"
        },

        {
          "sID_Group":"cherkasy.zvenigorodka.rada",
          "sFIO":"Кармазін Сергій",
          "sURL":"https://www.facebook.com/karmazinsv",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c0.0.160.160/p160x160/10414552_1423153121344086_6349469452415992360_n.jpg?oh=981ab0e547ad2795722e24915056eb4d&oe=58497269",
          "sCity":"Звенигоро́дка"
        },

        {
          "sID_Group":"cherkasy.zvenigorodka.rada",
          "sFIO":"Крицький Кирило",
          "sURL":"https://www.facebook.com/vosvobodazven",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12195933_431486590309504_2579224181610587466_n.jpg?oh=860cfabcfc9831d8388d848d64e160fa&oe=5845D6BB",
          "sCity":"Звенигоро́дка"
        },

        {
          "sID_Group":"lugansk.severodonetsk.rada",
          "sFIO":"Малеванець Олексій",
          "sURL":"https://www.facebook.com/malevanec",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13938389_1072424572833636_7000621191111350534_n.jpg?oh=243827ba07364e597756c121af22c903&oe=581B1239",
          "sCity":"Сєвєродонецьк",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"lugansk.severodonetsk.rada",
          "sFIO":"Мосалов Артем",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Сєвєродонецьк"
        },

        {
          "sID_Group":"lugansk.severodonetsk.rada",
          "sFIO":"Щеглов Олександр",
          "sURL":"https://www.facebook.com/alex.shcheglow",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/l/t1.0-1/p160x160/12993502_1069605753062420_4352450246132597828_n.jpg?oh=ee7581613b861d77a8cfdfd2ac68c8c3&oe=584EF31C",
          "sCity":"Сєвєродонецьк"
        },

        {
          "sID_Group":"lugansk.severodonetsk.rada",
          "sFIO":"Золошко Валентина",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Сєвєродонецьк"
        },

        {
          "sID_Group":"lugansk.severodonetsk.rada",
          "sFIO":"Дивенок Костянтин",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Сєвєродонецьк"
        },

        {
          "sID_Group":"lugansk.novopskov.rada",
          "sFIO":"Малєтін Віктор",
          "sURL":"https://www.facebook.com/v.maletin",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c170.50.621.621/s160x160/314832_161708137251710_572795340_n.jpg?oh=8f004b301b43d91ce7d764f58dd5320d&oe=58566C61",
          "sCity":"Новопсков",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"lugansk.novopskov.rada",
          "sFIO":"Соколов В'ячеслав",
          "sURL":"https://www.facebook.com/profile.php?id=100010309479613",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/12348116_178947449125570_5029610496308752986_n.jpg?oh=a511ce5834f8f98d505b02c9d58b2840&oe=585C34AF",
          "sCity":"Новопсков"
        },


        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Савінова Ванда",
          "sURL":"https://www.facebook.com/profile.php?id=100001353020712",
          "sCity":"Житомир",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Корнійчук Володимир",
          "sURL":"https://www.facebook.com/profile.php?id=100010892283159",
          "sCity":"Житомир"
        },

        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Рафаловський Павло",
          "sURL":"",
          "sCity":"Житомир"
        },

        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Ведіщев Максим",
          "sURL":"",
          "sCity":"Житомир"
        },

        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Май Михайло",
          "sURL":"",
          "sCity":"Житомир"
        },

        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Срібненко Ірина",
          "sURL":"https://www.facebook.com/irina.sribnenko",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13177150_10208375879407540_4858862059362588573_n.jpg?oh=3b09abc888f27ddeb9344e15d26de2e7&oe=585947D3",
          "sCity":"Житомир"
        },

         {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Горбовий Андрій",
          "sURL":"https://www.facebook.com/profile.php?id=100008570803653",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/1510824_1381371408825206_4499816260900112429_n.jpg?oh=ba19f5d296a3382b6eafe0659f08006a&oe=584FFADA",
          "sCity":"Житомир"
        },

         {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Шпаківський Андрій",
          "sURL":"https://www.facebook.com/profile.php?id=100004291328682",
          "sCity":"Житомир"
        },

        {
          "sID_Group":"zhytomyr.zhytomyr.rada",
          "sFIO":"Воликова Викторія",
          "sURL":"https://www.facebook.com/v.volikova",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c77.45.566.566/s160x160/168947_185132804859586_6523188_n.jpg?oh=eaa7b49f0d7ffa1273f43bced85c99ea&oe=581721B7",
          "sCity":"Житомир"
        },

        {
          "sID_Group":"zhytomyr.zhytomyr",
          "sFIO":"Прилипко Ольга",
          "sURL":"",
          "sCity":"Житомир"
        },

        {
          "sID_Group":"zhytomyr.berduchiv.rada",
          "sFIO":"Тростянський Олексій",
          "sURL":"https://www.facebook.com/altrost",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12188960_951790888218417_1794667916496361734_n.jpg?oh=0cd87331376800e83d93b4d7a3a8867b&oe=5812E3F7",
          "sCity":"Бердичів",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"zhytomyr.berduchiv.rada",
          "sFIO":"Побережний Олексій",
          "sURL":"https://www.facebook.com/poberezhnyi",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12088489_1152647488083041_2370649604893029326_n.jpg?oh=581d22b9308de30c1982a292a84f0009&oe=585CE710",
          "sCity":"Бердичів"
        },

        {
          "sID_Group":"zhytomyr.korosten.rada",
          "sFIO":"Боровков Володимир",
          "sURL":"https://www.facebook.com/profile.php?id=100000939614292",
          "sCity":"Коростень",
          "sInfo":"Координатор міста"
        },


         {
          "sID_Group":"zhytomyr.korosten.rada",
          "sFIO":"Яблонський Павло",
          "sURL":"https://www.facebook.com/profile.php?id=100001959023340",
          "sPhoto":"https://scontent-fra3-1.xx.fbcdn.net/v/t1.0-1/c0.28.160.160/p160x160/12106892_902115183197107_3407239165810275896_n.jpg?oh=44dd999f5df5b80fcba9c0cba411851d&oe=58464BA8",
          "sCity":"Коростень",
          "sInfo":"Координатор міста"
        },


        {
          "sID_Group":"lutsk.lutsk.rada",
          "sFIO":"Ожема Роман",
          "sURL":"https://www.facebook.com/roman.ozhema",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/71466_674117079300793_878206001_n.jpg?oh=9165f17ed194672fba88b83fc70b6ed6&oe=584B8F50",
          "sCity":"Луцьк",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"lutsk.lutsk.rada",
          "sFIO":"Іжик Олександр",
          "sURL":"https://www.facebook.com/alexander.izhyk",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/1544993_628567023858321_90375393_n.jpg?oh=6309ffdcdd91bb40c0a6c207a1aea7db&oe=585A7C1B",
          "sCity":"Луцьк"
        },

        {
          "sID_Group":"lutsk.lutsk.rada",
          "sFIO":"Струк Віталій",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Луцьк"
        },

         {
          "sID_Group":"lutsk.lutsk.rada",
          "sFIO":"Пус Богдан",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Луцьк"
        },

        {
          "sID_Group":"lutsk.lutsk.rada",
          "sFIO":"Лішук Людмила",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Луцьк"
        },

        {
          "sID_Group":"donetsk.bahmut.rada",
          "sFIO":"Букрей Антон",
          "sURL":"https://www.facebook.com/anton.bukrey",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c23.0.160.160/p160x160/1979615_817563818287271_566741374064899893_n.jpg?oh=c5a0872ccb00a1a2f61333e49476eb77&oe=58171BC7",
          "sCity":"Бахмут",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"donetsk.bahmut.rada",
          "sFIO":"Бойко Павло",
          "sURL":"https://www.facebook.com/dogmator",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12391234_1542476309377825_4544382214282457468_n.jpg?oh=1c32e773f3f9f3e49fc788d179dfa054&oe=5815EE7C",
          "sCity":"Бахмут"
        },


        {
          "sID_Group":"donetsk.bahmut.rada",
          "sFIO":"Бокова Ганна",
          "sURL":"https://www.facebook.com/profile.php?id=100008132412510",
          "sCity":"Бахмут"
        },

        {
          "sID_Group":"donetsk.mangush.rada",
          "sFIO":"Потапова Наталія",
          "sURL":"https://www.facebook.com/profile.php?id=100004909877173",
          "sCity":"Мангуш",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"donetsk.mangush.rada",
          "sFIO":"Чіпчева Олена",
          "sURL":"https://www.facebook.com/alewtena",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10408780_1583097661966556_5701603915804963368_n.jpg?oh=921bbce0773416c8554c76f6929b8d51&oe=5848FFC0",
          "sCity":"Мангуш"
        },

        {
          "sID_Group":"chernigiv.chernigiv.rada",
          "sFIO":"Дубина Владимир",
          "sURL":"https://www.facebook.com/vdubyna",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11149241_1133428103339365_5558092930375075183_n.jpg?oh=c57ffe2f3e94761f619f08066b65e5ab&oe=5845FADA",
          "sCity":"Чернігів",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"chernigiv.chernigiv.rada",
          "sFIO":"Скосир Олександр",
          "sURL":"https://www.facebook.com/oleksandr.skosyr",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12316117_915488398500687_7677369442130610113_n.jpg?oh=84ea10a00f2cf84d32358fea0b5371f0&oe=5812B1F1",
          "sCity":"Чернігів"
        },

        {
          "sID_Group":"chernigiv.chernigiv",
          "sFIO":"Голуб Олексій",
          "sURL":"",
          "sCity":"Чернігів"
        },

        {
          "sID_Group":"chernigiv.negin.rada",
          "sFIO":"Кедров Андрій",
          "sURL":"https://www.facebook.com/andre.kedrov",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10407675_899453400094736_5899670826252535902_n.jpg?oh=fb6437f4f951fffbca28b32bc9bcc2a5&oe=585362F7",
          "sCity":"Ніжин",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"chernigiv.negin.rada",
          "sFIO":"Середа Дмитро",
          "sURL":"https://www.facebook.com/dmitry.sereda",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13880409_1275467425804362_6751011138514585790_n.jpg?oh=a06b71caac890a35fe0359e561280610&oe=58119290",
          "sCity":"Ніжин"
        },

        {
          "sID_Group":"chernigiv.mena.rada",
          "sFIO":"Лавський Сергій",
          "sURL":"https://www.facebook.com/sergiy.lavskiy",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/993581_1069987896391303_3066229769705823061_n.jpg?oh=8504276a74fb8135ee58a8b7fddb3b59&oe=584E64F4",
          "sCity":"Мена",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"chernigiv.mena.rada",
          "sFIO":"Мороз Олексій",
          "sURL":"",
          "sPhoto":"",
          "sCity":"Мена"
        },

        {
          "sID_Group":"chernivtsi.chernivtsi.rada",
          "sFIO":"Даналакі Василій",
          "sURL":"https://www.facebook.com/vasiliy.danalaki",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c118.0.741.741/s160x160/12243534_978038138922840_3502722115413844447_n.jpg?oh=65028608142085b073a3d8e374c91dca&oe=5810EC1F",
          "sCity":"Чернівці",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"chernivtsi.chernivtsi.rada",
          "sFIO":"Литвинова Тетяна",
          "sURL":"https://www.facebook.com/golub1ka",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13521995_10208762155078970_8628113840475794858_n.jpg?oh=d656f1aa50ba1e9214562c7f444d0f70&oe=585CB9F0",
          "sCity":"Чернівці"

        },

        {
          "sID_Group":"chernivtsi.chernivtsi.rada",
          "sFIO":"Шумєйко Вадім",
          "sURL":"https://www.facebook.com/vadim.shumejko",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13087616_1114273908633381_1874407463961732209_n.jpg?oh=31c5657f7685e748b8927aeda1403d54&oe=5849434E",
          "sCity":"Чернівці"

        },

        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk.rada",
          "sFIO":"Озорович Андрій",
          "sURL":"https://www.facebook.com/a.ozorovych",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12644999_10205482425011273_4853192322731669289_n.jpg?oh=8e43279e814091334a2b795912f31622&oe=58196C80",
          "sCity":"Івано-Франківськ",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk.rada",
          "sFIO":"Сворак Ольга",
          "sURL":"https://www.facebook.com/olga.metel.1",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12524026_992186644177498_8027844815207364273_n.jpg?oh=d2e80919816f302a8030db7dc4a419ed&oe=585A9EAB",
          "sCity":"Івано-Франківськ"
        },

        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk.rada",
          "sFIO":"Сліпенчук Ростислав",
          "sURL":"https://www.facebook.com/ross104",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11058053_866310446770689_3685441560946392024_n.jpg?oh=aefe90b30d50a0826b43383e4672fa3c&oe=58178E01",
          "sCity":"Івано-Франківськ"
        },

        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk.rada",
          "sFIO":"Квасов Денис",
          "sURL":"https://www.facebook.com/denisblack.photo",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12282_924735467602412_7437690410755375683_n.jpg?oh=e928145e59fed1cbc72bd5e50ec131ef&oe=58447602",
          "sCity":"Івано-Франківськ"
        },

        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk.rada",
          "sFIO":"Шевченко Сергій",
          "sURL":"https://www.facebook.com/shevchenko.sb",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13174118_1099134966816244_2085987104033680941_n.jpg?oh=fd79abc28317d8274edf463484157dd1&oe=584A0A11",
          "sCity":"Івано-Франківськ"
        },

        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk.rada",
          "sFIO":"Вербовий Віталій",
          "sURL":"https://www.facebook.com/profile.php?id=100007173287490",
          "sCity":"Івано-Франківськ"
        },

        {
          "sID_Group":"ivano-frankivsk.ivano-frankivsk.rada",
          "sFIO":"Вівчар Володимир",
          "sURL":"https://www.facebook.com/volodimir.vivcar",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/10846286_772543469478119_8022794003890473372_n.jpg?oh=0d060f075ef6b66d8f0f0b7dcb5c8b63&oe=584C2343",
          "sCity":"Івано-Франківськ"
        },

         {
          "sID_Group":"ivano-frankivsk.kalush.rada",
          "sFIO":"Маліборський Віталій",
          "sURL":"https://www.facebook.com/weres.ua",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12144677_439030329635978_6939953702971035626_n.jpg?oh=9ac26c7ade7472929ec3523930733331&oe=584FA5AD",
          "sCity":"Калуш",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"ivano-frankivsk.kolomia.rada",
          "sFIO":"Білоус Володимир",
          "sURL":"https://www.facebook.com/bilousvolodymyr",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c4.0.160.160/p160x160/1014129_730878076923102_932274137_n.jpg?oh=f930ed09cefaf6a7c3846eeeabbd70cf&oe=585C2489",
          "sCity":"Коломия",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"ivano-frankivsk.kolomia.rada",
          "sFIO":"Слижук Андрій",
          "sURL":"https://www.facebook.com/sluva2707",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/13220807_1156449611052804_4297313136236421863_n.jpg?oh=e25161ed8354a18b5d62b9984b385493&oe=58126A14",
          "sCity":"Коломия"
        },

        {
          "sID_Group":"zaporizhya.zaporizhya.rada",
          "sFIO":"Соловйов Максим",
          "sURL":"https://www.facebook.com/maksim.solovyov.12",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c0.0.160.160/p160x160/1654480_462168640578627_1688838187_n.jpg?oh=ee1aa15df9961314f5c04ac2deca8cbe&oe=58589B6B",
          "sCity":"Запоріжжя",
          "sInfo":"Координатор міста"
        },

        {
          "sID_Group":"zaporizhya.zaporizhya.rada",
          "sFIO":"Семененко Наталія",
          "sURL":"https://www.facebook.com/natali.semenenko.7",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/11053111_973704062680359_5930411105580993115_n.jpg?oh=1f1cd02868841db723c70e76f7d71559&oe=584B177A",
          "sCity":"Запоріжжя"
        },

        {
          "sID_Group":"zaporizhya.zaporizhya.rada",
          "sFIO":"Микал Олена",
          "sURL":"https://www.facebook.com/alona.mykal?fref=hovercard",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/p160x160/12189529_897424843680141_1910380645282979523_n.jpg?oh=ea5e22fed4d8dbdb0248a4a5f4c272b7&oe=584C104A",
          "sCity":"Запоріжжя"
        },

        {
          "sID_Group":"zakarpattya.uzhgorod.rada",
          "sFIO":"Лук'янчук Микола",
          "sURL":"https://www.facebook.com/profile.php?id=100007826441900",
          "sCity":"Ужгород"
        },

         {
          "sID_Group":"zakarpattya.uzhgorod.rada",
          "sFIO":"Федор Михайло",
          "sURL":"https://www.facebook.com/mykhaylo.fedor",
          "sPhoto":"https://scontent-frt3-1.xx.fbcdn.net/v/t1.0-1/c30.13.164.164/s160x160/281420_4150995247938_320432746_n.jpg?oh=273b8ad0b6249285a80e4f9a883ea98e&oe=5816CBA9",
          "sCity":"Ужгород"
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
