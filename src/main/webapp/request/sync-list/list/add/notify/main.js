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
        value: "none",
        margin: "5,0,5,0"
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Нет", value: "none"},
            {title: "Стандартное", value: "standard"},
            {title: "Единоразовое", value: "once"},
            {title: "Циклическое", value: "cycle"},
            {title: "Выборочное", value: "custom"}
        ])
    });
    content.addData({}, "GroupBottom");
    content.addData({height: 10, width: 10}, "SizedBox");

    dead(state, rc, content);
    standard(state, rc, content);
    once(state, rc, content);
    cycle(state, rc, content);
    custom(state, rc, content);

    content.addData({height: 10, width: 10}, "SizedBox");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Показать план уведомлений",
        desc: "",
        descColor: "grey",
        titleColor: "black",
        color: "transparent",
        onTapData: {
            title: "План уведомлений",
            url: rc.url + "/plan",
            backgroundColor: "#f5f5f5",
            bridgeState: {
                notify: "standard"
            }
        },
        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["standard", "once", "cycle", "custom"],
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "RowInkWell", "NotifyStandard");
    content.addData({}, "GroupBottom");
}

function cycle(state, rc, content){
    content.addData({
        height: 10,
        width: 10,
        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["standard", "once", "cycle"],
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "SizedBox", "NotifyStandard");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Интервал",
        name: "interval",
        selectedIndex: 0,
        value: "hour",
        margin: "5,0,5,0",

        arg: "standard",
        standard: {
            "key": "notify",
            "value": "cycle",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Час", value: "hour"},
            {title: "День", value: "day"},
            {title: "Неделя", value: "week"},
            {title: "Месяц", value: "month"},
            {title: "Год", value: "year"}
        ])
    }, "NotifyStandard");
    content.addData({}, "GroupBottom");

    hour(state, rc, content);
    day(state, rc, content);
    week(state, rc, content);
    month(state, rc, content);

    content.addData({
        title: "Количество повторений:",
        padding: "12,15,0,15",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "cycle",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");

    content.addData({
        type: "number",
        hint: "Кол-во повторений",
        label: "",
        data: "",
        name: "countRetry",

        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["hour", "day", "week", "month", "year"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextEdit", "NotifyStandard");

    content.addData({
        title: "Начиная с выбранной даты, будут приходить оповещения в указанное время через выбранный интервал. Если не указывать количество повторений, то оповещения будут проиходить на протяжениии всего времени существования)",
        padding: "10,17,10,10",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "cycle",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");
}

function once(state, rc, content){
    content.addData({
        title: "Единоразовое напоминание просто оповестит в указанную дату и время",
        padding: "10,17,10,10",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "once",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");
}

function standard(state, rc, content){
    content.addData({
        title: "Стандартное напоминание включает в себя до 4-х напоминаний в зависомости от периода выбранной даты. Напоминание будет за месяц, неделю, сутки и 2 часа до срока завешения. Если период очень короткий (меньше 4-х часов), напоминание будет срок в срок",
        padding: "10,17,10,10",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "standard",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");
}

function hour(state, rc, content){
    content.addData({
        title: "Оповещать каждые:",
        padding: "12,15,0,15",
        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["hour"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");

    content.addData({
        type: "time",
        hint: "Оповещать каждые",
        label: "",
        data: "01:00",
        name: "interval_hour",

        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["hour"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextEdit", "NotifyStandard");
}

function day(state, rc, content){
    content.addData({
        height: 10,
        width: 10,
        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["day"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "SizedBox", "NotifyStandard");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Каждые/й",
        name: "interval_day",
        selectedIndex: 0,
        value: "1day",
        margin: "5,0,5,0",

        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["day"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "День", value: "1day"},
            {title: "Полтора дня", value: "1_5day"},
            {title: "2 дня", value: "2day"},
            {title: "3 дня", value: "3day"},
            {title: "4 дня", value: "4day"},
            {title: "5 дней", value: "5day"},
            {title: "6 дней", value: "6day"},
            {title: "7 дней", value: "7day"},
            {title: "8 дней", value: "8day"},
            {title: "9 дней", value: "9day"},
            {title: "10 дней", value: "10day"},
            {title: "11 дней", value: "11day"},
            {title: "12 дней", value: "12day"},
            {title: "13 дней", value: "13day"},
            {title: "14 дней", value: "14day"},
            {title: "15 дней", value: "15day"},
            {title: "16 дней", value: "16day"},
            {title: "17 дней", value: "17day"},
            {title: "18 дней", value: "18day"},
            {title: "19 дней", value: "19day"},
            {title: "20 дней", value: "20day"},
            {title: "21 дня", value: "21day"},
            {title: "22 дня", value: "22day"},
            {title: "23 дня", value: "23day"},
            {title: "24 дня", value: "24day"},
            {title: "25 дней", value: "25day"},
            {title: "26 дней", value: "26day"},
            {title: "27 дней", value: "27day"},
            {title: "28 дней", value: "28day"},
            {title: "29 дней", value: "29day"},
            {title: "30 дней", value: "30day"}
        ])
    }, "NotifyStandard");
    content.addData({}, "GroupBottom");
}

function week(state, rc, content){
    content.addData({
        height: 10,
        width: 10,
        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["week"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "SizedBox", "NotifyStandard");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Каждую/ые",
        name: "interval_week",
        selectedIndex: 0,
        value: "1week",
        margin: "5,0,5,0",

        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["week"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Неделю", value: "1week"},
            {title: "Полторы недели", value: "1_5week"},
            {title: "2 недели", value: "2week"},
            {title: "3 недели", value: "3week"}
        ])
    }, "NotifyStandard");
    content.addData({}, "GroupBottom");
}

function month(state, rc, content){
    content.addData({
        height: 10,
        width: 10,
        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["month"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "SizedBox", "NotifyStandard");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Каждый/ые",
        name: "interval_month",
        selectedIndex: 0,
        value: "1month",
        margin: "5,0,5,0",

        arg: "standard",
        standard: {
            "valueGroup": {
                g1: {
                    key: "notify",
                    list: ["cycle"],
                    condition: "and"
                },
                g2: {
                    key: "interval",
                    list: ["month"],
                    condition: "and"
                }
            },
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Месяц", value: "1month"},
            {title: "Полтора месяца", value: "1_5month"},
            {title: "2 месяца", value: "2month"},
            {title: "3 месяца", value: "3month"},
            {title: "4 месяца", value: "4month"},
            {title: "5 месяцев", value: "5month"},
            {title: "6 месяцев", value: "6month"},
            {title: "7 месяцев", value: "7month"},
            {title: "8 месяцев", value: "8month"},
            {title: "9 месяцев", value: "9month"},
            {title: "10 месяцев", value: "10month"},
            {title: "11 месяцев", value: "11month"}
        ])
    }, "NotifyStandard");
    content.addData({}, "GroupBottom");
}

function dead(state, rc, content){
    content.addData({
        title: "Дата:",
        padding: "12,10,0,15",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["standard", "once", "cycle", "custom"],
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");

    content.addData({
        type: "datetime",
        hint: "",
        label: "",
        data: "",
        name: "deadLineDate",

        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["standard", "once", "cycle", "custom"],
            "trueCondition": "true",
            "falseCondition": "false"
        }

    }, "TextEdit", "NotifyStandard");

    content.addData({
        title: "Время:",
        padding: "12,15,0,15",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["standard", "once", "cycle", "custom"],
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");

    content.addData({
        type: "time",
        hint: "",
        label: "",
        data: "",
        name: "deadLineTime",

        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["standard", "once", "cycle", "custom"],
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextEdit", "NotifyStandard");

}

function custom(state, rc, content){
    content.addData({
        height: 30,
        width: 10,
        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["custom"],
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "SizedBox", "NotifyStandard");

    content.addData({
        title: "Добавить",
        icon: "add",
        onPressed: content.getMethod("joinAppStoreData", {joinAppStoreDataData: true}),
        joinAppStoreDataData: {key: "custom_date", "append": "${deadLineDate} ${deadLineTime}\n"},
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "custom",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "ButtonBlue600", "NotifyStandard");

    content.addData({
        height: 30,
        width: 10,
        arg: "standard",
        standard: {
            "key": "notify",
            "value": ["custom"],
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "SizedBox", "NotifyStandard");

    content.addData({
        type: "multiline",
        hint: "",
        label: "",
        data: "",
        name: "custom_date",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "custom",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextEditAutofocusAppStore", "NotifyStandard");

    content.addData({
        title: "Выбирай произвольные даты и время - оповестим!!!",
        padding: "10,17,10,10",
        arg: "standard",
        standard: {
            "key": "notify",
            "value": "custom",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "TextPadding", "NotifyStandard");
}