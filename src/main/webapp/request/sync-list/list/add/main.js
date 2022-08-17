function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("title", "Добавить задачу");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    var dialog = {
        url: rc.url + "/save?uid_data=" + rc.getParam.uid_data,
        backgroundColor: "transparent",
        progressIndicatorColor: "#ffffff"
    };
    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    content.addData({title: "Обязательно к заполнению"}, "FirstH1-P-0-20");
    content.addData({
        type: "multiline",
        hint: "Название задачи",
        label: "Например \"Купить пельмешей\"",
        data: "",
        name: "name",
        onSubmitted: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "TextEditAutofocusAppStore");
    content.addData({height: 10, width: 10}, "SizedBox");
    content.addData({
        title: "Добавить ещё задачу",
        icon: "add",
        onPressed: content.getMethod("joinAppStoreData", {joinAppStoreDataData: true}),
        joinAppStoreDataData: {key: "name", "append": "\n"}
    }, "ButtonMin");
    content.addData({title: "Расширенные настройки (необзятельно)"}, "H1-P-0-20");
    content.addData({
        type: "text",
        hint: "Группа",
        label: "",
        data: "",
        name: "groupName"
    }, "TextEdit");
    content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Настроить уведомления",
        desc: "",
        descColor: "grey",
        titleColor: "black",
        color: "white",
        onTapData: {
            title: "Настройка оповещений",
            url: rc.url + "/notify",
            backgroundColor: "#f5f5f5",
            bridgeState: {
                notify: "standard"
            }
        }
    }, "RowInkWell");
    content.addData({}, "GroupBottom");

    /*
    content.addData({
        type: "multiline",
        hint: "--",
        label: "--",
        data: "",
        name: "notify",
        onSubmitted: ""
    }, "TextEditAutofocusAppStore");
    */
    /*
    content.addData({
        type: "datetime",
        hint: "Дата исполнения",
        label: "",
        data: "", name: "deadLineDate"
    }, "TextEdit");
    content.addData({height: 10, width: 10}, "SizedBox");
    content.addData({
        type: "time",
        hint: "Время исполнения",
        label: "",
        data: "",
        name: "deadLineTime"
    }, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");*/

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
    content.addData({
        title: "Создать",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "ButtonBlue600");
}