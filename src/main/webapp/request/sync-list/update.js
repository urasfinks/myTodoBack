function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("title", "Обновить приложение");
    content.setSeparated(false);
    content.setParentUI("WrapPage15");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.addData({title: "Время пришло"}, "FirstH1-P-0-20");
    if(rc.getPlatform() == "ios"){
        content.addData({
            title: "Обновить в AppStore",
            icon: "get_app",
            onPressed: ":launcher(onPressedLauncher)",
            onPressedLauncher: {
                "url": "https://apps.apple.com/ru/app/mytodo/id1639810855"
            }
        }, "ButtonBlue600");
    }
}