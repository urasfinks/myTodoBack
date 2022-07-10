function main(state, rc, content) {
    try {
        var obj = {
            sql: "select state_data from \"data\" where uid_data = ${uid_data}",
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
                    value: rc.idPerson.toString()
                }
            ]
        };
        var x = Java.type('ru.jamsys.JS').sql(JSON.stringify(obj));
        var res = JSON.parse(JSON.parse(x)[0]['state_data']);
    } catch (e) {
        var res = {
            "fio": "",
            "bday": ""
        };
    }

    //content.addData({title: x}, "Text");
    //content.addData({title: state}, "Text");
    content.addData({type: "text", label: "Имя Отчество", data: res["fio"], name: "fio"}, "TextEdit");
    content.addData({type: "datetime", label: "Дата рождения", data: res["bday"], name: "bday"}, "TextEdit");
    content.addData({height: 20, width: 0}, "SizedBox");
    content.addData({
        title: "Сохранить",
        icon: "save",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": {
            url: rc.url+"/save",
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");
    content.setParentUI("WrapPage20");
    //content.addSyncSocketDataUID("a7d437fa-d47a-4e0f-9417-f9701ece125e");
    //content.addAction("closeWindow", {});
    //content.addAction("reloadPageByUrl", {"list":["project/system/account"]});
    //content.addAction("openDialog", {data: {"title":"opa", "url": "project/system/account"}});
}