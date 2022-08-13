function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");


    content.setSeparated(false);
    content.setParentUI("WrapPage20");

    content.addData({title: "Доступ предоставлен"}, "FirstH1-P-0-20");

    var listPerson = JSON.parse(Java.type('ru.jamsys.JS').getPersonInfoDataShared(rc, rc.getParam.uid_data));
    for (var i = 0; i < listPerson.length; i++) {
        content.addData({
            title: (listPerson[i]["fio"] == undefined || listPerson[i]["fio"] == null) ? "Гость" : listPerson[i]["fio"],
            desc: (listPerson[i]["bday"] == undefined || listPerson[i]["bday"] == null) ? "--" : listPerson[i]["bday"],
            src: "/avatar-get-id/" + listPerson[i]["id_person"],
            icon: listPerson[i]["id_person"] == rc.idPerson ? "person" : "clear",
            onPressed: listPerson[i]["id_person"] == rc.idPerson ? "" : content.getMethod("openDialog", {onPressedData: true}),
            onPressedData: {
                url: rc.url + "remove?uid_data=" + rc.getParam.uid_data + "&TempPersonKey="+listPerson[i]["temp_key_person"],
                backgroundColor: "transparent",
                progressIndicatorColor: "#ffffff"
            }
        }, "PersonControl");
    }

    content.addData({title: "Добавить нового участника"}, "H1-P-0-20");

    content.addData({type: "text", hint: "Уникальный код", data: "", name: "TempPersonKey"}, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Добавить",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": {
            url: rc.url + "save?uid_data=" + rc.getParam.uid_data,
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");
}