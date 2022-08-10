function main(state, rc, content) {
    content.setWidgetData("title", "Главная");
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setSeparated(false);
    content.setParentUI("WrapPage15");
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/add", title: "Добавить список"},
        icon: "playlist_add"
    }, "AppBarActionAdd");
    var list = getList(rc);
    if (list.length > 0) {
        content.addData({title: "Списки", extra: list.length, offsetRight: 17}, "H1RightBlock");
        content.addData({}, "GroupTop");
        for (var i = 0; i < list.length; i++) {
            var data = JSON.parse(list[i]["state_data"]);
            if (i != 0) {
                content.addData({}, "Divider");
            }
            var countActive = 0;
            var countComplete = 0;
            for (var k in data) {
                if (k == "time_autoGroup" || k == "tome_name" || k == "time_sortTime" || k == "time_sortType") {
                    continue;
                }
                if (k.startsWith("time_")) {
                    if (data[k.split("time_")[1]] == true) {
                        countActive++;
                    }
                    if (data[k.split("time_")[1]] == false) {
                        countComplete++;
                    }
                }
            }
            //content.addData({title: list[i]["state_data"]}, "Text");
            content.addData({
                title: data["name"],
                badge1: countComplete + "",
                badge2: countActive + "",
                onTapData: {
                    title: data["name"],
                    dataUID: list[i]["uid_data"],
                    url: rc.url + "/list?uid_data=" + list[i]["uid_data"],
                    backgroundColor: "#f5f5f5",
                    config: {
                        parentRefreshOnChangeStateData: true
                    }
                }
            }, "RowInkWellBadge2");
        }
        content.addData({}, "GroupBottom");

        prepareRed(rc, content);

    } else {
        content.addData({title: "Создай новый список задач, нажав на кнопку в правом верхнем углу"}, "EmptyList5");
        content.addData({height: 20, width: 10}, "SizedBox");
        content.addData({title: "Что это такое?"}, "H1");
        content.addData({
            marker: "1",
            title: "Можно создать несколько списков задач, например \"Купить в магазине\" или \"Взять с собой в отпуск\""
        }, "TextDescription");
        content.addData({
            marker: "2",
            title: "Возможность вести совместные списки с близкими людьми, например общая организация сбора в поход"
        }, "TextDescription");
        content.addData({marker: "3", title: "Контролировать исполнение не погружаясь в детали"}, "TextDescription");
    }
}

function getList(rc) {
    var list = [];
    try {
        var obj = {
            sql: "select d1.* from \"data\" d1 join tag t1 on t1.id_data = d1.id_data where d1.id_prj = ${id_prj} and d1.id_person = ${id_person} and t1.key_tag = 'list' order by d1.id_data desc",
            args: [
                {
                    field: 'uid_data',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'state_data',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'id_prj',
                    type: 'NUMBER',
                    direction: 'IN',
                    value: rc.idProject.toString()
                },
                {
                    field: 'id_person',
                    type: 'NUMBER',
                    direction: 'IN',
                    value: rc.idPerson.toString()
                }
            ]
        };
        list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
    }
    return list;
}

function getListRed(rc, content) {
    var list = [];
    try {
        var obj = {
            sql: "select d1.state_data, d2.uid_data, extract(epoch from d1.time_add_data::TIMESTAMP WITH TIME ZONE)::bigint as timestamp, d2.state_data->>'name' as parent_name from \"data\" d1 \n" +
                "join tag t1 on t1.id_data = d1.id_data\n" +
                "join \"data\" d2 on d2.uid_data = t1.key_tag\n" +
                "where d1.id_prj = ${id_prj} \n" +
                "and d1.id_person = ${id_person} \n" +
                "and t1.key_tag <> 'list'\n" +
                "and length(d1.state_data->>'deadLineDate') > 0\n" +
                "and d2.state_data->>d1.uid_data = 'false'\n" +
                "order by date(d1.state_data->>'deadLineDate') asc",
            args: [
                {
                    field: 'uid_data',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'state_data',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'timestamp',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'parent_name',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'id_prj',
                    type: 'NUMBER',
                    direction: 'IN',
                    value: rc.idProject.toString()
                },
                {
                    field: 'id_person',
                    type: 'NUMBER',
                    direction: 'IN',
                    value: rc.idPerson.toString()
                }
            ]
        };
        list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
        content.addData({title: e}, "Text");
    }
    return list;
}

function toTimestamp(strDate, strTime) {
    var dateTimestamp = parseDate(strDate).getTime() / 1000;
    if (strTime != undefined && strTime != null && strTime != "") {
        var exp = strTime.split(":");
        dateTimestamp = dateTimestamp + parseInt(exp[0]) * 3600 + parseInt(exp[1]) * 60;
    }
    return dateTimestamp;
}

function parseDate(str) {
    var dateParts = str.split(".");
    var year = dateParts[2];
    var month = dateParts[1];
    var day = dateParts[0];
    return new Date(year, (month - 1), day);
}

function prepareRed(rc, content) {
    var list = getListRed(rc, content);
    if (list.length > 0) {
        var listRed = [];
        var now = new Date().getTime() / 1000;
        for (var i = 0; i < list.length; i++) {
            list[i]["parseStateData"] = JSON.parse(list[i]["state_data"]);
            var dl = list[i]["parseStateData"]["deadLineDate"];
            if (dl != undefined && dl != null && dl != "") { //Так как краснеют только не исполненные
                var to = toTimestamp(list[i]["parseStateData"]["deadLineDate"], list[i]["parseStateData"]["deadLineTime"]);

                var from = list[i]["timestamp"];
                var prc = parseInt((now - from) / (to - from) * 100);
                if (prc > 100 || to - from <= 0) {
                    prc = 100;
                }

                if(prc >= 50){
                    list[i]["statusRedPrc"] = prc;
                    listRed.push(list[i]);
                }
            }
        }
        if (listRed.length > 0) {
            content.addData({title: "Приближается срок", extra: listRed.length, offsetRight: 17}, "H1RightBlock");
            content.addData({}, "GroupTop");
            for (var i = 0; i < listRed.length; i++) {
                var data = JSON.parse(listRed[i]["state_data"]);
                if (i != 0) {
                    content.addData({}, "Divider");
                }
                var titleColor = "black";
                var descColor = "rgba:0,0,0,0.37";

                if (listRed[i]["statusRedPrc"] >= 50) {
                    titleColor = "white";
                    descColor = "rgba:255,255,255,0.8";
                }
                var opacity = listRed[i]["statusRedPrc"]/100;
                var color = opacity > 0.1 ? ("rgba:30,136,229,"+opacity.toFixed(2)) : "white";
                content.addData({
                    title: listRed[i]["parent_name"]+"/"+data["name"],
                    desc: listRed[i]["parseStateData"]["deadLineDate"] +" "+listRed[i]["parseStateData"]["deadLineTime"],
                    descColor: descColor,
                    titleColor: titleColor,
                    color: color,
                    onTapData: {
                        title: listRed[i]["parent_name"],
                        dataUID: listRed[i]["uid_data"],
                        url: rc.url + "/list?uid_data=" + listRed[i]["uid_data"],
                        backgroundColor: "#f5f5f5",
                        config: {
                            parentRefreshOnChangeStateData: true
                        }
                    }
                }, "RowInkWellDescription");
            }
            content.addData({}, "GroupBottom");
        }
    }

}