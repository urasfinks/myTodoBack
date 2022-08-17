function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setSeparated(false);
    content.setParentUI("WrapPage15");
    content.addData({height: 25, width: 10}, "SizedBox");
    content.addData({}, "GroupTop");
    content.addData({
        title: "Тип оповещения",
        name: "notify",
        selectedIndex: 0,
        value: "standard",
        margin: "5,0,5,0"
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Стандартное", value: "standard"},
            {title: "Единоразовое", value: "once"},
            {title: "Циклическо", value: "cycle"},
            {title: "Выборочное", value: "custom"}
        ])
    });
    content.addData({}, "GroupBottom");
    //content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({
        type: "datetime",
        hint: "Дата исполнения",
        label: "",
        data: "", name: "deadLineDate",

        arg: "standard",
        standard: {
            "key": "notify",
            "value": "standard",
            "trueCondition": "true",
            "falseCondition": "false"
        }

    }, "TextEdit", "NotifyStandard");

    //content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({
        type: "time",
        hint: "Время исполнения",
        label: "",
        data: "",
        name: "deadLineTime",

        arg: "standard",
        standard: {
            "key": "notify",
            "value": "standard",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextEdit", "NotifyStandard");

    //content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({
        title: "Стандартное напоминание включает в себя до 4-х напоминаний в зависомости от периода выбранной даты. Напоминание будет за месяц, неделю, сутки и 2 часа до срока завешения. Если период очень короткий (меньше 4-х часов), напоминание будет срок в срок",
        padding: "10",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "standard",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");

    content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Показать план уведомлений",
        desc: "",
        descColor: "grey",
        titleColor: "black",
        color: "white",
        onTapData: {
            title: "План уведомлений",
            url: rc.url + "/notify/plan",
            backgroundColor: "#f5f5f5",
            bridgeState: {
                notify: "standard"
            }
        }
    }, "RowInkWell");
    content.addData({}, "GroupBottom");
}