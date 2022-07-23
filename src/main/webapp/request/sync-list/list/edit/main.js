function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataState(rc, rc.getParam.uid_data, JSON.stringify({
        "name": "fwe",
        "deadLine": ""
    })));
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addData({
        type: "text",
        label: "Название задачи",
        data: res["name"],
        name: "name"
    }, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({type: "datetime", label: "Дата исполнения", data: res["deadLine"], name: "deadLine"}, "TextEdit");
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
                url: rc.url + "/remove?uid_data=" + rc.getParam.uid_data + "&parent_uid_data="+rc.getParam.parent_uid_data,
                backgroundColor: "transparent",
                progressIndicatorColor: "#ffffff"
            }
        }
    }, "ButtonRed");
}