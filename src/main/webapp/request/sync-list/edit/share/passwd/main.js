function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");

    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");

    content.addData({title: "Пароль для подключения к списку:"}, "H1-P-0-20");
    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataState(rc.getParam.uid_data, JSON.stringify({
        "passwd": ""
    })));

    content.addData({type: "text", hint: "Пароль для подключения", data: res["passwd"], name: "passwd"}, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Сохранить",
        icon: "",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": {
            url: rc.url + "/save?uid_data=" + rc.getParam.uid_data,
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");

}