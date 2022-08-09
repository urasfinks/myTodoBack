function main(state, rc, content) {
    var list = getList(rc, content);
    Java.type('ru.jamsys.JS').clearUnreadChatMessage(rc);
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    //content.addData({title: JSON.stringify(list)}, "Text");
    //content.addData({data: "Ашта-лашта"}, "ChatMsgRight");
    //content.addData({data: "Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка Шаломка "}, "ChatMsgLeft");
    for (var i = 0; i < list.length; i++) {
        var sys = list[i]["id_person_from"] == undefined || list[i]["id_person_from"] == null || list[i]["id_person_from"] == "";
        content.addData({data: list[i]["data_chat"], desc: list[i]["timestamp"]}, sys ? "ChatMsgLeft" :"ChatMsgRight");
    }
}

function getList(rc, content) {
    var list = [];
    try {
        var obj = {
            sql: "select id_person_from, id_person_to, data_chat, to_char(time_add_chat, 'MM.dd.yyyy HH24:MI') as timestamp from chat where id_person_to = ${id_person} OR id_person_from = ${id_person} order by id_chat desc",
            args: [
                {
                    field: 'id_person',
                    type: 'NUMBER',
                    direction: 'IN',
                    value: rc.idPerson.toString()
                },
                {
                    field: 'id_person_from',
                    type: 'NUMBER',
                    direction: 'COLUMN'
                },
                {
                    field: 'id_person_to',
                    type: 'NUMBER',
                    direction: 'COLUMN'
                },
                {
                    field: 'data_chat',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'timestamp',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                }

            ]
        };
        list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
        content.addData({title: e.toString()}, "Text");
    }
    return list;
}