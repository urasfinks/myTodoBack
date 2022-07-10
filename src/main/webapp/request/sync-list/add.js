function main(state, rc, content) {
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addAppBarAction({onPressedData: {url: rc.url + "/share", title: "Общий список"}, icon:"folder_shared"}, "AppBarActionAdd");
    content.addData({type: "text", label: "Название списка", data: "", name: "name"}, "TextEdit");
    content.addData({
        title: "Создать",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": {
            url: rc.url + "/save",
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");
}