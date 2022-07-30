function main(state, rc, content) {
    content.setParentUI("WrapPage20");

    var res = JSON.parse(Java.type('ru.jamsys.JS').getPersonState(rc, JSON.stringify({
        "fio": "",
        "bday": "",
        "gender": 0
    })));

    content.addData({title: "Заполни информацию о себе"}, "H1-P-0-20");

    content.addData({type: "text", label: "Имя Отчество", data: res["fio"], name: "fio"}, "TextEdit");
    content.addData({type: "datetime", label: "Дата рождения", data: res["bday"], name: "bday"}, "TextEdit");
    content.addData({height: 20, width: 0}, "SizedBox");

    content.addData({
        name: "gender",
        getAppStoreDataSegmentControl: {key: "gender", defaultValue: res["gender"]}
    }, "SegmentControl");
    content.addData({height: 20, width: 0}, "SizedBox");
    content.addData({
        title: "Сохранить",
        icon: "save",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": {
            url: rc.url + "/save",
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");

    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({title: "Для чего это всё?"}, "H1-P-0-20");
    content.addData({
        marker: "1",
        title: "Для проектов, когда будет несколько участников, нам необходимо будет тебя как-то представить им, наверное будет некорректно представлять тебя как \"Гость\""
    }, "TextDescription");
    content.addData({
        marker: "2",
        title: "Гендерная принадлежность нужна так-же для обращений"
    }, "TextDescription");
    content.addData({
        marker: "3",
        title: "Что касается даты рождения - нам всё равно! Страница была слишком пустой)"
    }, "TextDescription");
}