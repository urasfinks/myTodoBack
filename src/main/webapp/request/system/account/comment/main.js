function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    content.addData({title: "Напиши нам что-нибудь"}, "FirstH1-P-0-20");
    content.addData({
        type: "multiline",
        hint: "Комментарий / предложение",
        data: "",
        name: "comment",
        minLines: 3
    }, "TextEdit");
    content.addData({height: 20, width: 10}, "SizedBox");
    content.addData({
        title: "Отправить",
        icon: "save",
        onPressed: content.getMethod("openDialog", {openDialogData: true}),
        openDialogData: {
            url: rc.url + "/save",
            backgroundColor: "transparent",
            progressIndicatorColor: "#ffffff"
        }
    }, "ButtonBlue600");
}