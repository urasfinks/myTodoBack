function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("title", "Обновить приложение");
    content.setSeparated(false);
    content.setParentUI("WrapPage15");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.addData({title: "Время пришло"}, "FirstH1-P-0-20");
    if (rc.getPlatform() == "ios") {
        content.addData({
            title: "Обновить в AppStore",
            icon: "get_app",
            onPressed: ":launcher(onPressedLauncher)",
            onPressedLauncher: {
                "url": "https://apps.apple.com/ru/app/mytodo/id1639810855"
            }
        }, "ButtonBlue600");
    }
    if (rc.getPlatform() == "android") {
        content.addData({
            title: "Скачать приложение",
            icon: "get_app",
            onPressed: ":launcher(onPressedLauncher)",
            onPressedLauncher: {
                "url": "https://jamsys.ru/apk/myTODO.apk"
            }
        }, "ButtonBlue600");
        content.addData({height: 20, width: 10}, "SizedBox");
        content.addData({
            marker: "* ",
            title: "Мне очень жаль, Google Play маркет не дал мне возможность зарегистрировать аккаунт разработчика, для того, что бы я выложил вам это бесплатное приложение"
        }, "TextDescription");
    }
}