function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Тип",
        name: "notify",
        selectedIndex: 0,
        value: "standard",
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Стандартное", value: "standard"},
            {title: "Единоразовое", value: "once"},
            {title: "Циклическо", value: "cycle"},
            {title: "Выборочное", value: "custom"}
        ])
    });
    content.addData({height: 10, width: 10}, "SizedBox");
    content.addData({
        standard: {
            "key": "notify",
            "value": "standard",
            "trueCondition": "true",
            "falseCondition": "false"
        }
    }, "NotifyStandard");
}