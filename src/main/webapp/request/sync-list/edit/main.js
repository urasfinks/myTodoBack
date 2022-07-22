function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataState(rc, rc.getParam.uid_data, JSON.stringify({
        "name": "fwe",
        "autoGroup": true,
        "sortTime": true,
    })));
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addData({
        type: "text",
        label: "Название списка задач",
        data: res["name"],
        name: "name"
    }, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Группировка задач *",
        nameChecked: "autoGroup",
        getAppStoreDataChecked: {key: "autoGroup", defaultValue: res["autoGroup"]},
    }, "RowCheckSimple");
    content.addData({
        title: "Сортировка по дате добавления **",
        nameChecked: "sortTime",
        getAppStoreDataChecked: {key: "sortTime", defaultValue: res["sortTime"]},
    }, "RowCheckSimple");
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
        title: "Удалить",
        icon: "delete_forever",
        onPressed: content.getMethod("confirm", {confirm: true}),
        confirm:{
            data: "Подтвердить действие",
            onPressed: content.getMethod("openDialog", {openDialogData: true}),
            openDialogData: {
                url: rc.url + "/remove?uid_data=" + rc.getParam.uid_data,
                backgroundColor: "transparent",
                progressIndicatorColor: "#ffffff"
            }
        }
    }, "ButtonRed");
}