function main(state, rc, content) {
    var list = [];
    try {
        var obj = {
            sql: "select * from \"prj\"",
            args: [
                {
                    field: 'key_prj',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'title_prj',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                }
            ]
        };
        var list = JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
    }
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addData({title: "RC:"+rc.toString()}, "Text");
    for (var i = 0; i < list.length; i++) {
        content.addData({
            title: list[i]["title_prj"],
            onTapData: {title: list[i]["title_prj"], url: "project/" + list[i]["key_prj"]}
        }, "RowInkWell");
    }
}