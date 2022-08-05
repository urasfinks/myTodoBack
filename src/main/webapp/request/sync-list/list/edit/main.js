function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataState(content, rc.getParam.uid_data, JSON.stringify({
        "name": "",
        "deadLineDate": "",
        "deadLineTime": "",
        "tagColor": "",
        "groupName": ""
    })));
    /*var data = JSON.parse(Java.type('ru.jamsys.JS').getData(rc, rc.getParam.uid_data, JSON.stringify({
        "time_add_data": ""
    })));*/

    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    content.addData({title: "Обязательно к заполнению"}, "FirstH1-P-0-20");
    content.addData({
        type: "text",
        label: "Название задачи",
        data: res["name"],
        name: "name"
    }, "TextEdit");
    content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({title: "Расширенные настройки (необзятельно)"}, "H1-P-0-20");
    content.addData({
        type: "text",
        label: "Группа",
        data: res["groupName"],
        name: "groupName"
    }, "TextEdit");
    //content.addData({title: "Эти поля необходимо заполнять, только в том случаи, если задачу надо выполнить к определённой дате/времени"}, "Text");

    content.addData({type: "datetime", label: "Дата исполнения", data: res["deadLineDate"], name: "deadLineDate"}, "TextEdit");
    content.addData({
        type: "time",
        label: "Время исполнения",
        data: res["deadLineTime"],
        name: "deadLineTime"
    }, "TextEdit");

    content.addData({height: 20, width: 10}, "SizedBox");
    //content.addData({title: "Цветная метка"}, "H1-P-0-20");
    content.addData({
        red: {
            "key":"tagColor",
            "value": "red",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        blue: {
            "key":"tagColor",
            "value": "blue",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        green: {
            "key":"tagColor",
            "value": "green",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        orange: {
            "key":"tagColor",
            "value": "orange",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        brown: {
            "key":"tagColor",
            "value": "brown",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        black: {
            "key":"tagColor",
            "value": "black",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        }
    }, "TagColor");

    content.addData({height: 30, width: 10}, "SizedBox");
    //content.addData({title: "Дата создания задачи: " + data["time_add_data"]}, "Text");
    //content.addData({height: 20, width: 10}, "SizedBox");

    content.addData({
        title: "Сохранить",
        icon: "save",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: {
            url: rc.url + "/save?uid_data=" + rc.getParam.uid_data,
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Удалить задачу",
        icon: "delete_forever",
        onPressed: content.getMethod("confirm", {confirm: true}),
        confirm: {
            data: "Подтвердить действие",
            onPressed: content.getMethod("openDialog", {openDialogData: true}),
            openDialogData: {
                url: rc.url + "/remove?uid_data=" + rc.getParam.uid_data + "&parent_uid_data=" + rc.getParam.parent_uid_data,
                backgroundColor: "transparent",
                progressIndicatorColor: "#ffffff"
            }
        }
    }, "ButtonRed");
}