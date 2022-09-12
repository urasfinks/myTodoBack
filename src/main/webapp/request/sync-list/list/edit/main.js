function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");

    content.setWidgetData("title", "Изменить настройки задачи");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.loadState(rc.getParam.uid_data);
    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataState(rc.getParam.uid_data, JSON.stringify({
        "name": "",
        "notify": "",
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
        hint: "Название задачи",
        data: res["name"],
        name: "name"
    }, "TextEdit");

    content.addData({title: "Расширенные настройки (необзятельно)"}, "H1-P-0-20");
    content.addData({
        type: "text",
        hint: "Группа",
        label: "",
        data: res["groupName"],
        name: "groupName",
        onOpenModalBottomData: {
            url: "/project/" + rc.projectName + "/list/add-new/group-select?uid_data=" + rc.getParam.parent_uid_data,
            bridgeState: {
                groupName: ""
            }
        }
    }, "GroupControl");

    //content.addData({title: "Эти поля необходимо заполнять, только в том случаи, если задачу надо выполнить к определённой дате/времени"}, "Text");
    content.addData({height: 10, width: 10}, "SizedBox");

    var notify = res["notify"] != undefined && res["notify"] != "" && res["notify"] != "none";

    content.addData({
        title: notify ? "Редактировать уведомления" : "Настроить уведомления",
        icon: notify ? "notifications_active" : "notifications_none",
        onPressed: content.getMethod("openWindow", {onTapData: true}),
        onTapData: {
            title: "Настройка оповещений",
            url: "/project/" + rc.projectName + "/list/notify",
            backgroundColor: "#f5f5f5",
            bridgeState: {
                notify: "none",
                name: "",
                interval: "hour",
                countRetry: "",
                interval_hour: "01:00",
                interval_day: "1day",
                interval_week: "1week",
                interval_month: "1month",
                deadLineDate: "",
                deadLineTime: "",
                custom_date: ""
            }
        }
    }, "ButtonMin");


    if (rc.getParam.shared != undefined && rc.getParam.shared == "shared") {
        var personChange = Java.type('ru.jamsys.JS').getPersonInformationWhoChangeDataState(rc, rc.getParam.uid_data);
        if (personChange != undefined && personChange != null && personChange != "") {
            content.addData({title: "Изменил состояние"}, "H1-P-0-20");
            content.addData({hint: "", data: personChange}, "TextEditReadOnly");
        }
        content.addData({height: 10, width: 10}, "SizedBox");
    } else {
        content.addData({height: 10, width: 10}, "SizedBox");
    }

    //content.addData({title: "Цветная метка"}, "H1-P-0-20");
    content.addData({
        red: {
            "key": "tagColor",
            "value": "red",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        blue: {
            "key": "tagColor",
            "value": "blue",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        green: {
            "key": "tagColor",
            "value": "green",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        orange: {
            "key": "tagColor",
            "value": "orange",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        brown: {
            "key": "tagColor",
            "value": "brown",
            "trueCondition": "check_circle",
            "falseCondition": "brightness_1"
        },
        black: {
            "key": "tagColor",
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
            url: rc.url + "/save?uid_data=" + rc.getParam.uid_data + "&parent_uid_data=" + rc.getParam.parent_uid_data,
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