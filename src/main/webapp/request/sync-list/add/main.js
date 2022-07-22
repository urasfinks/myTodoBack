function main(state, rc, content) {
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    var dialog = {
        url: rc.url + "/save",
        backgroundColor: "transparent",
        progressIndicatorColor: "#ffffff"
    };
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/share", title: "Общий список"},
        icon: "folder_shared"
    }, "AppBarActionAdd");
    content.addData({
        type: "text",
        label: "Название списка задач",
        data: "",
        name: "name",
        onSubmitted: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "TextEditAutofocus");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Группировка задач *",
        nameChecked: "autoGroup",
        getAppStoreDataChecked: {key: "autoGroup", defaultValue: true},
    }, "RowCheckSimple");
    content.addData({
        title: "Сортировка по дате добавления **",
        nameChecked: "sortTime",
        getAppStoreDataChecked: {key: "sortTime", defaultValue: true},
    }, "RowCheckSimple");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Создать",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "ButtonBlue600");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({marker: "* ", title: "Группировка - это визуальное разделение списка задач на две группы:\n1) Активные\n2) Выполненные\n"}, "TextDescription");
    content.addData({marker: "**", title: "Сортировка задач в списке осуществляется по дате добавления. Сортировка по времени от новых к старым означает, что сначала списка будут самые свежие задачи. Отключение этой опции будет сортировать данные на основе изменения данных"}, "TextDescription");
}