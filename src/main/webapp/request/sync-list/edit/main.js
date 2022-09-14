function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("title", "Изменить настройки списка");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataState(rc.getParam.uid_data, JSON.stringify({
        "name": "fwe",
        "autoGroup": "active",
        "sortTime": true,
        "sortType": true,
    })));
    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    content.addAppBarAction({
        onPressed: ":openWindow(onPressedData)",
        onPressedData: {url: rc.url + "/share?uid_data=" + rc.getParam.uid_data, title: "Общий список"},
        icon: "share"
    }, "AppBarActionAddSmall");

    content.addData({title: "Обязательно к заполнению"}, "FirstH1-P-0-20");
    content.addData({
        type: "text",
        hint: "Название списка задач",
        data: res["name"],
        name: "name"
    }, "TextEdit");

    content.addData({title: "Расширенные настройки (необзятельно)"}, "H1-P-0-20");

    content.addData({}, "GroupTop");
    content.addData({
        title: "Группировать задачи *",
        name: "autoGroup",
        selectedIndex: 0,
        value: res["autoGroup"],
        margin: "0,0,0,0"
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Активные/Выполненные", value: "active"},
            {title: "Принадлежность к группе", value: "tag"},
            {title: "По цвету задач", value: "color"},
            {title: "Не надо ничего группировать", value: "none"}
        ])
    });

    content.addData({
        title: "Сортировать по созданию **",
        nameChecked: "sortTime",
        getAppStoreDataChecked: {key: "sortTime", defaultValue: res["sortTime"]},
    }, "RowCheckSimple");
    content.addData({
        title: "Сортировка от старых к новым",
        nameChecked: "sortType",
        getAppStoreDataChecked: {key: "sortType", defaultValue: res["sortType"]},
    }, "RowCheckSimple");
    content.addData({}, "GroupBottom");

    content.addData({height: 20, width: 10}, "SizedBox");
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
        title: "Удалить список и задачи",
        icon: "delete_forever",
        onPressed: content.getMethod("confirm", {confirm: true}),
        confirm: {
            data: "Подтвердить действие",
            onPressed: content.getMethod("openDialog", {openDialogData: true}),
            openDialogData: {
                url: rc.url + "/remove?uid_data=" + rc.getParam.uid_data,
                backgroundColor: "transparent",
                progressIndicatorColor: "#ffffff"
            }
        }
    }, "ButtonRed");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        marker: "* ",
        title: "Группировка - это визуальное разделение списка задач на группы"
    }, "TextDescription");
    content.addData({
        marker: "**",
        title: " У задачи может быть дата создания и дата изменения данных. Если флажок не выделен, значит сортировка будет по дате изменения."
    }, "TextDescription");
}