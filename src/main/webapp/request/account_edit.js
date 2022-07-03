function main(state, personKey) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
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
                    value: personKey
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

    //content.addData(JSON.stringify({title: x}), "Text");
    //content.addData(JSON.stringify({title: state}), "Text");
    content.addData(JSON.stringify({type: "text", label: "Имя Отчество", data: res["fio"], name: "fio"}), "TextEdit");
    content.addData(JSON.stringify({type: "datetime", label: "Дата рождения", data: res["bday"], name: "bday"}), "TextEdit");
    content.addData(JSON.stringify({height: 20, width: 0}), "SizedBox");
    content.addData(JSON.stringify({
        title: "Сохранить",
        icon: "save",
        onPressed: content.getMethod("openDialog", "{\"openDialogData\":true}"),
        "openDialogData": {
            url: "project/system/account/save",
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }), "ButtonBlue600");
    content.setParentUI("WrapPage20");
    //content.addSyncSocketDataUID("a7d437fa-d47a-4e0f-9417-f9701ece125e");
    //content.addAction("closeWindow", JSON.stringify({}));
    //content.addAction("reloadPageByUrl", JSON.stringify({"list":["project/system/account"]}));
    //content.addAction("openDialog", JSON.stringify({data: {"title":"opa", "url": "project/system/account"}}));
    return content.toString();
}