function main(state, rc, content) {
    var res = JSON.parse(Java.type('ru.jamsys.JS').getPersonState(rc, JSON.stringify({
        "fio": "",
        "bday": ""
    })));

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
}