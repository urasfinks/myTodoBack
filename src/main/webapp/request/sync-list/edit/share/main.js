function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");

    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");

    content.addData({title: "Доступ предоставлен"}, "FirstH1-P-0-20");

    var listPerson = JSON.parse(Java.type('ru.jamsys.JS').getPersonInfoDataShared(rc, rc.getParam.uid_data));
    //Первый пользователь - всегда создатель

    for (var i = 0; i < listPerson.length; i++) {
        var icon = i == 0 ? "person" : (listPerson[i]["id_person"] == rc.idPerson ? "person_add" : "clear");
        content.addData({
            title: (listPerson[i]["fio"] == undefined || listPerson[i]["fio"] == null) ? "Гость" : listPerson[i]["fio"],
            desc: (listPerson[i]["bday"] == undefined || listPerson[i]["bday"] == null) ? "--" : listPerson[i]["bday"],
            src: "/avatar-get-id/" + listPerson[i]["id_person"],
            icon: icon,
            onPressed: icon == "clear" ? content.getMethod("confirm", {confirm: true}) : "",
            confirm: {
                data: "Подтвердить действие",
                onPressed: content.getMethod("openDialog", {openDialogData: true}),
                openDialogData: {
                    url: rc.url + "remove?uid_data=" + rc.getParam.uid_data + "&TempPersonKey="+listPerson[i]["temp_key_person"],
                    backgroundColor: "transparent",
                    progressIndicatorColor: "#ffffff"
                }
            }
        }, "PersonControl");
    }

    content.addData({title: "Добавить нового участника"}, "H1-P-0-20");

    content.addData({type: "text", hint: "Временный код пользователя", data: "", name: "TempPersonKey"}, "TextEdit");
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

    content.addData({height: 20, width: 10}, "SizedBox");

    content.addData({title: "Где взять временный код пользователя?"}, "H1");
    content.addData({marker: "1", title: "Пользователь, которого мы хотим подключить к общему списку должен зайти в вкладку \"Аккаунт\" и нажать скопировать временный код"}, "TextDescription");
    content.addData({marker: "2", title: "Далее этот временный код, скопированный в буфер обмена, он должен нам как-то передать. Как вариант вставить в смс или telegram, заскриншотить экран и также послать, но если ничего не получается, просто продиктовать)"}, "TextDescription");
    content.addData({marker: "3", title: "Далее надо вставить этот код в поле и нажать \"Добавить\""}, "TextDescription");
}