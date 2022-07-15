function main(state, rc, content) {
    content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/edit", title: "Изменить параметры"},
        icon: "edit"
    }, "AppBarActionAdd");
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/add?uid_data=" + rc.getParam.uid_data, title: "Добавить задачу"},
        icon: "add"
    }, "AppBarActionAdd");
    content.addSyncSocketDataUID(rc.getParam.uid_data);

    var list = [];
    try {
        var obj = {
            sql: "select d1.* from \"data\" d1 join tag t1 on t1.id_data = d1.id_data where d1.id_prj = ${id_prj} and d1.id_person = ${id_person} and t1.key_tag = ${key_tag}",
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
                },
                {
                    field: 'key_tag',
                    type: 'VARCHAR',
                    direction: 'IN',
                    value: rc.getParam.uid_data
                }
            ]
        };
        var list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
    }
    for (var i = 0; i < list.length; i++) {
        var data = JSON.parse(list[i]["state_data"]);
        content.addData({
            title: data["name"],
            nameChecked: list[i]["uid_data"],
            getAppStoreDataChecked: {key: list[i]["uid_data"], defaultValue: false},
            getAppStoreDataTime: {key: "time_" + list[i]["uid_data"], defaultValue: ""}
        }, "RowCheck");
    }

}