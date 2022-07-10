function main(state, rc, content) {
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addData({type: "text", label: "Уникальный код", data: "", name: "name_share"}, "TextEdit");
    content.addData({
        title: "Добавить",
        icon: "add",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        "openDialogData": {
            url: "project/to-do/save",
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");
}