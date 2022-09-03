function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    if (rc.getParam.uid_data != undefined) {
        var listRemoveDataUID = getRemoveDataUID(rc.getParam.uid_data, content, rc);
        //content.addData({title: "REMOVE:" + JSON.stringify(listRemoveDataUID)}, "Text");
        if (listRemoveDataUID.length > 0) {
            var state = JSON.parse(Java.type('ru.jamsys.JS').getDataStateAll(rc.getParam.uid_data));
            var updState = {};
            for (var i = 0; i < listRemoveDataUID.length; i++) {
                var curDataUID = "_" + listRemoveDataUID[i]["uid_data"];
                if (state[curDataUID] != undefined) {
                    updState[curDataUID] = null;
                }
            }
            //content.addData({title: "NEW STATE:" + JSON.stringify(updState)}, "Text");
            Java.type('ru.jamsys.JS').updateDataState(rc, rc.getParam.uid_data, JSON.stringify(updState));
        }
        Java.type('ru.jamsys.JS').removeData(rc, rc.getParam.uid_data);
    }
    content.addData({title: "Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data: {delay: 1000, count: 3}});
    content.addAction("reloadPageByUrl", {
        "list": [
            "/project/" + rc.projectName
        ]
    });
}

function getRemoveDataUID(dataUID, content, rc) {
    var list = [];
    try {
        var obj = {
            sql: "select d1.uid_data from data d1\n" +
                "inner join tag t1 on t1.id_data = d1.id_data\n" +
                "where (d1.uid_data = ${uid_data_1} or t1.key_tag = ${uid_data_1})\n" +
                "and d1.id_prj = ${id_prj}\n" +
                "and d1.id_person = ${id_person}",
            args: [
                {
                    field: 'uid_data',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'uid_data_1',
                    type: 'VARCHAR',
                    direction: 'IN',
                    value: dataUID
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