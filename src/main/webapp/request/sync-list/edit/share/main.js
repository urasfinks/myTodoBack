function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");

    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");

    content.addData({title: "Доступ предоставлен"}, "FirstH1-P-0-20");

    var listPerson = JSON.parse(Java.type('ru.jamsys.JS').getPersonInfoDataShared(rc, rc.getParam.uid_data));
    //Первый пользователь - всегда создатель
    var isAdmin = listPerson.length > 0 && listPerson[0]["id_person"] == rc.idPerson;
    for (var i = 0; i < listPerson.length; i++) {
        var icon = i == 0 ? "person" : (listPerson[i]["id_person"] == rc.idPerson ? "person_add" : "clear");

        content.addData({
            title: (listPerson[i]["fio"] == undefined || listPerson[i]["fio"] == null) ? "Гость" : listPerson[i]["fio"],
            desc: (listPerson[i]["bday"] == undefined || listPerson[i]["bday"] == null) ? "--" : listPerson[i]["bday"],
            src: "/avatar-get-id/" + listPerson[i]["id_person"],
            icon: icon,
            onPressed: isAdmin == true ? content.getMethod("confirm", {confirm: true}) : "",
            confirm: {
                data: "Подтвердить действие",
                onPressed: content.getMethod("openDialog", {openDialogData: true}),
                openDialogData: {
                    url: rc.url + "/remove?uid_data=" + rc.getParam.uid_data + "&TempPersonKey=" + listPerson[i]["temp_key_person"],
                    backgroundColor: "transparent",
                    progressIndicatorColor: "#ffffff"
                }
            }
        }, "PersonControl");
    }

    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Установить пароль для подключения",
        icon: "",
        onPressed: content.getMethod("openWindow", {openDialogData: true}),
        "openDialogData": {
            title: "Пароль для подключения",
            url: rc.url + "/passwd?uid_data=" + rc.getParam.uid_data
        }
    }, "ButtonBlue600");

    content.addData({height: 20, width: 10}, "SizedBox");
    //content.addData({title: "Пароль для подключения: " + res["passwd"], name: "passwd"}, "Text");
    content.addData({
        title: "Поделиться ссылкой",
        icon: "share",
        onPressed: content.getMethod("share", {shareData: true}),
        shareData: {
            data: "https://" + rc.host + "/project/" + rc.projectName + "/add/share?uid_data=" + rc.getParam.uid_data
        }
    }, "ButtonBlue600");

}