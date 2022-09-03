function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");

    content.setWidgetData("title", "Подключение общего списка");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");

    content.addData({title: "Пароль для подключения к списку:"}, "H1-P-0-20");

    content.addData({type: "text", hint: "Пароль для подключения", data: "", name: "passwd"}, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Подключить",
        icon: "",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": {
            url: rc.url + "/save?uid_data=" + rc.getParam.uid_data,
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");

}