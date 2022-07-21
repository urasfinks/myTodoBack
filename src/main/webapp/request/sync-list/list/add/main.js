function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var dialog = {
        url: rc.url + "/save?uid_data=" + rc.getParam.uid_data,
        backgroundColor: "transparent",
        progressIndicatorColor: "#ffffff"
    };
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addData({
        type: "text",
        label: "Название задачи",
        data: "",
        name: "name",
        onSubmitted: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: dialog
    }, "TextEditAutofocus");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Создать",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": dialog
    }, "ButtonBlue600");
}