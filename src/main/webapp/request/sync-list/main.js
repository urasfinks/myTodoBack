function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setSeparated(false);
    content.setParentUI("WrapPage15");
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/add", title: "Добавить список"},
        icon: "add"
    }, "AppBarActionAdd");
    var list = [];
    try {
        var obj = {
            sql: "select d1.* from \"data\" d1 join tag t1 on t1.id_data = d1.id_data where d1.id_prj = ${id_prj} and d1.id_person = ${id_person} and t1.key_tag = 'list'",
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
        var list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
    }
    if(list.length > 0){
        content.addData({}, "GroupTop");
        for (var i = 0; i < list.length; i++) {
            var data = JSON.parse(list[i]["state_data"]);
            if(i != 0){
                content.addData({}, "Divider");
            }
            content.addData({
                title: data["name"],
                onTapData: {
                    title: data["name"],
                    dataUID: list[i]["uid_data"],
                    url: rc.url + "/list?uid_data=" + list[i]["uid_data"],
                    backgroundColor: "#f5f5f5"
                }
            }, "RowInkWell");
        }
        content.addData({}, "GroupBottom");
    } else {
        content.addData({}, "EmptyList");
        content.addData({height: 20, width: 10}, "SizedBox");
        content.addData({title: "Что это такое?"}, "H1");
        content.addData({marker: "1", title: "Вы можете создать несколько списков задач, например \"Купить в магазине\" или \"Взять с собой в отпуск\""}, "TextDescription");
        content.addData({marker: "2", title: "Возможность отмечать выполненные задачи"}, "TextDescription");
        content.addData({marker: "3", title: "История выполнения задач, а именно в какое время была выполнена задача"}, "TextDescription");
        content.addData({marker: "4", title: "Заводить задачи с установкой по времени, когда она должна быть исполнена"}, "TextDescription");
        content.addData({marker: "5", title: "Иметь общие списки задач с близкими людьми, например общая организация сбора в поход"}, "TextDescription");

    }
}