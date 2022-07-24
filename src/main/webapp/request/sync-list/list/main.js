function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");

    content.setSeparated(false);
    content.setParentUI("WrapPage15");
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/add?uid_data=" + rc.getParam.uid_data, title: "Добавить задачу"},
        icon: "playlist_add"
    }, "AppBarActionAdd");
    content.addAppBarAction({
        onPressedData: {
            url: "/project/" + rc.projectName + "/edit?uid_data=" + rc.getParam.uid_data,
            title: "Изменить настройки списка"
        },
        icon: "more_vert"
    }, "AppBarActionAdd");
    content.addSyncSocketDataUID(rc.getParam.uid_data);

    var state = getState(rc);
    var sortType = state["sortType"] != false;
    var list = getList(rc, sortType);

    content.setWidgetData("title", state["name"]);
    //content.addData({title: "STATE:" + JSON.stringify(state)}, "Text");

    //content.addData({title: "STATE:" + sortType}, "Text");

    if (list.length > 0) {
        if (state["autoGroup"] == true) {
            var listActive = [];
            var listNotActive = [];
            for (var i = 0; i < list.length; i++) {
                if (state[list[i]["uid_data"]] == true) {
                    listNotActive.push(list[i]);
                } else {
                    listActive.push(list[i]);
                }
            }
            ins(listActive, "Активные задачи", content, rc, state, sortType);
            ins(listNotActive, "Завершённые задачи", content, rc, state, sortType);
        } else {
            ins(list, "Все задачи", content, rc, state, sortType);
        }
    } else {
        content.addData({title: "Добавь новую задачу, нажав на кнопку в правом верхнем углу"}, "EmptyList55");
        content.addData({height: 20, width: 10}, "SizedBox");
        content.addData({title: "Что это такое?"}, "H1");
        content.addData({marker: "1", title: "Можно создать задачи разных типов и предназначений"}, "TextDescription");
        content.addData({marker: "2", title: "Есть возможность помечать выполненные задачи"}, "TextDescription");
        content.addData({marker: "3", title: "Вести историю выполнения задач"}, "TextDescription");
    }

}

function ins(list, title, content, rc, state, sortType) {
    if (list.length > 0) {
        var statusRed = false;
        var now = parseInt(new Date().getTime() / 1000);
        for (var i = 0; i < list.length; i++) {
            list[i]["parseStateData"] = JSON.parse(list[i]["state_data"]);
            var dl = list[i]["parseStateData"]["deadLine"];
            if (dl != undefined && dl != null && dl != "" && state[list[i]["uid_data"]] == false) { //Так как краснеют только не исполненные
                list[i]["statusRed"] = true;
                var to = toTimestamp(list[i]["parseStateData"]["deadLine"]);
                var from = list[i]["timestamp"];

                var prc = parseInt((now - from) / (to - from) * 100); //99205 / 262
                //content.addData({title: "now-from="+(now-from)+"/to-from:"+(to-from)}, "Text");
                if (prc > 100 || to - from <= 0) {
                    prc = 100;
                }
                list[i]["statusRedPrc"] = prc;
                statusRed = true;
            } else {
                list[i]["statusRed"] = false;
            }
        }
        if (state["sortTime"] == false) { //Надо по дате изменения фильтровать
            list = list.sort(function (a, b) {
                //content.addData({title: a+";"+b}, "Text");
                if (sortType == true) {
                    if (state["time_" + a["uid_data"]] < state["time_" + b["uid_data"]]) {
                        return -1;
                    }
                } else {
                    if (state["time_" + a["uid_data"]] > state["time_" + b["uid_data"]]) {
                        return -1;
                    }
                }
                return 0;
            });
        }
        content.addData({title: title, extra: list.length, offsetRight: 23}, "H1RightBlock");
        content.addData({}, "GroupTop");

        if (statusRed == true) {
            var listFirst = [];
            var listLast = [];
            for (var i = 0; i < list.length; i++) {
                if (list[i]["statusRed"] == true && list[i]["statusRedPrc"] > 50) {
                    listFirst.push(list[i]);
                }else{
                    listLast.push(list[i]);
                }
            }
            list = listFirst.concat(listLast);
        }

        for (var i = 0; i < list.length; i++) {

            if (i != 0) {
                content.addData({}, "Divider");
            }
            var color = "white";
            var extra = "";
            var titleColor = "black";
            var descColor = "rgba:0,0,0,0.37";

            if (list[i]["statusRed"] == true) {
                //prc = 60;
                if (list[i]["statusRedPrc"] >= 50) {
                    titleColor = "white";
                    descColor = "rgba:255,255,255,0.8";
                }
                var redColor = 255 - parseInt(list[i]["statusRedPrc"] * 255 / 100);
                color = "rgba:255," + redColor + "," + redColor + ",1";
                //((to-from) * procent) + from = now;
            }
            content.addData({
                title: list[i]["parseStateData"]["name"] + " ",
                color: color,
                nameChecked: list[i]["uid_data"],
                getAppStoreDataChecked: {key: list[i]["uid_data"], defaultValue: false},
                titleColor: titleColor,
                descColor: descColor,
                getAppStoreDataTime: {
                    key: "time_" + list[i]["uid_data"],
                    defaultValue: "",
                    format: "dd.MM.yyyy HH:mm:ss"
                },
                onPressedData: {
                    url: rc.url + "/edit?uid_data=" + list[i]["uid_data"] + "&parent_uid_data=" + rc.getParam.uid_data,
                    title: "Изменить настройки задачи"
                },
            }, "RowCheck");
        }
        content.addData({}, "GroupBottom");
    }
}

function toTimestamp(strDate) {
    //var datum = new Date(Date.parse(strDate));
    return parseDate(strDate).getTime() / 1000;
}

function parseDate(str) {
    var dateParts = str.split(".");
    var year = dateParts[2];
    var month = dateParts[1];
    var day = dateParts[0];
    return new Date(year, (month - 1), day);
}

function getList(rc, sortType) {
    var list = [];
    try {
        var obj = {
            sql: "select d1.*, extract(epoch from time_add_data::TIMESTAMP WITH TIME ZONE)::bigint as timestamp from \"data\" d1 join tag t1 on t1.id_data = d1.id_data where d1.id_prj = ${id_prj} and d1.id_person = ${id_person} and t1.key_tag = ${key_tag} order by d1.id_data " + (sortType == true ? 'ASC' : 'DESC'),
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
                },
                {
                    field: 'key_tag',
                    type: 'VARCHAR',
                    direction: 'IN',
                    value: rc.getParam.uid_data
                }
            ]
        };
        list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
    }
    return list;
}

function getState(rc) {
    var state = {};
    try {
        var obj = {
            sql: "select d1.* from \"data\" d1 where d1.uid_data = ${uid_data}",
            args: [
                {
                    field: 'state_data',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'uid_data',
                    type: 'VARCHAR',
                    direction: 'IN',
                    value: rc.getParam.uid_data.toString()
                }
            ]
        };
        list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
        state = JSON.parse(list[0]["state_data"]);
    } catch (e) {
    }
    return state;
}