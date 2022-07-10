function main(state, rc, content) {
    content.addData({title: "RC:"+rc.toString()}, "Text");
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addAppBarAction({onPressedData: {url: rc.url + "/add", title: "Добавление списка"}, icon:"add"}, "AppBarActionAdd");
    var list = [];
    try {
        var obj = {
            sql: "select * from \"data\" where id_prj = ${id_prj} and id_person = ${id_person}",
            args: [
                {
                    field: 'id_data',
                    type: 'NUMBER',
                    direction: 'COLUMN'
                },
                {
                    field: 'data',
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
    for (var i = 0; i < list.length; i++) {
        var data = JSON.parse(list[i]["data"]);
        content.addData({
            title: data["name"],
            onTapData: {title: data["name"], url: rc.url + "/list?time=1"}
        }, "RowInkWell");
    }
}