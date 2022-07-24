function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var dialog = {
        url: rc.url + "/save?uid_data=" + rc.getParam.uid_data,
        backgroundColor: "transparent",
        progressIndicatorColor: "#ffffff"
    };
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addData({title: "Обязательно к заполнению"}, "H1-P-0-20");
    content.addData({
        type: "multiline",
        label: "Название задачи (например \"Купить пельмешей\")",
        data: "",
        name: "name",
        onSubmitted: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "TextEditAutofocusAppStore");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Добавить ещё задачу",
        icon: "add",
        onPressed: content.getMethod("joinAppStoreData", {joinAppStoreDataData: true}),
        joinAppStoreDataData: {key: "name", "append": "\n"}
    }, "ButtonMin");
    content.addData({title: "Расширенные настройки (необзятельно)"}, "H1-P-0-20");
    content.addData({title: "Это поле заполняй, только в том случаи, если эту задачу надо выполнить к какому-то определённому времени"}, "Text");
    content.addData({type: "datetime", label: "Дата исполнения", data: "", name: "deadLine"}, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Создать",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "ButtonBlue600");
}