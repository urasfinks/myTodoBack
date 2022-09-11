function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("title", "Добавить задачу");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    var dialog = {
        url: rc.url + "/save?uid_data=" + rc.getParam.uid_data,
        backgroundColor: "transparent",
        progressIndicatorColor: "#ffffff"
    };

    var plusAction = [
        content.getMethod("joinAppStoreData", {joinAppStoreDataData: true}),
        "bridgeDynamicFn(bridgeDynamicFn1)",
        "bridgeDynamicFn(bridgeDynamicFn2)"
    ].join("|");

    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    content.addData({title: "Обязательно к заполнению"}, "FirstH1-P-0-20");
    //content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        type: "text",
        hint: "Добавить задачу здесь",
        data: "",
        name: "name",
        onSubmitted: plusAction,
        plusAction: plusAction,
        joinAppStoreDataData: {key: "cache", "append": "${name}\n", "apply": false},
        bridgeDynamicFn1: {
            fn: "setAppStore",
            key: "name",
            value: ""
        },
        bridgeDynamicFn2: {
            fn: "focusTextField",
            name: "name"
        }
    }, "ListControl");
    content.addTemplate("ListLoop");
    content.addData({}, "ListDynamic");
    content.addData({title: "Расширенные настройки (необзятельно)"}, "H1-P-0-20");

    content.addData({
        type: "text",
        hint: "Группа",
        label: "",
        data: "",
        name: "groupName",
        onOpenModalBottomData: {
            url: rc.url + "/group-select?uid_data=" + rc.getParam.uid_data,
            bridgeState: {
                groupName: ""
            }
        }
    }, "GroupControl");

    content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({
        title: "Настроить уведомления",
        icon: "notifications_none",
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
    content.addData({
        title: "Сохранить",
        icon: "check",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "ButtonBlue600");
}