function main(state, rc, content) {
    content.setWidgetData("title", "Добавить список");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    var dialog = {
        url: rc.url + "/save",
        backgroundColor: "transparent",
        progressIndicatorColor: "#ffffff"
    };
    /*content.addAppBarAction({
        onPressedData: {url: rc.url + "/share", title: "Общий список"},
        icon: "folder_shared"
    }, "AppBarActionAdd");*/
    content.addData({title: "Обязательно к заполнению"}, "FirstH1-P-0-20");
    content.addData({
        type: "text",
        hint: "Название списка задач",
        label: "Например \"Купить в магазине\"",
        data: "",
        name: "name",
        onSubmitted: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "TextEditAutofocus");
    //content.addData({height: 10, width: 10}, "SizedBox");
    content.addData({title: "Расширенные настройки (необзятельно)"}, "H1-P-0-20");
    content.addData({}, "GroupTop");
    content.addData({
        title: "Группировать задачи *",
        name: "autoGroup",
        selectedIndex: 0,
        value: "active",
    }, "DropdownRadio", {
        items: JSON.stringify([
            {title: "Активные/Выполненные", value: "active"},
            {title: "Принадлежность к группе", value: "tag"},
            {title: "По цвету задач", value: "color"},
            {title: "Не надо ничего группировать", value: "none"}
        ])
    });

    content.addData({
        title: "Сортировка по дате создания **",
        nameChecked: "sortTime",
        getAppStoreDataChecked: {key: "sortTime", defaultValue: true},
    }, "RowCheckSimple");
    content.addData({
        title: "Сортировка от старых к новым",
        nameChecked: "sortType",
        getAppStoreDataChecked: {key: "sortType", defaultValue: true},
    }, "RowCheckSimple");
    content.addData({}, "GroupBottom");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Создать",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "ButtonBlue600");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({marker: "* ", title: "Группировка - это визуальное разделение списка задач на группы"}, "TextDescription");
    content.addData({marker: "**", title: " У задачи может быть дата создания и дата изменения данных. Если флажёк не выделен, значит сортировка будет по дате изменения."}, "TextDescription");
}