function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setParentUI("WrapPage20");
    content.addData({title: "Самое приятное в этой истории"}, "H1");
    content.addData({
        marker: "*",
        title: "Синхронизация нужна только тебе. Все функции приложения бесплатные и доступны без синхронизации с Telegram. Подключить или отключить синхронизацию с Telegram можно в любое время."
    }, "TextDescription");
    content.addData({title: "Для чего это всё?"}, "H1");
    content.addData({
        marker: "1",
        title: "Все данные, будут привязаны к Telegram аккаунту, это означает, что данные можно будет всегда восстановить"
    }, "TextDescription");
    content.addData({
        marker: "2",
        title: "Синхронизация позволит подключиться с нескольких устройств"
    }, "TextDescription");
    content.addData({
        marker: "3",
        title: "Всё, что тебе будет необходимо мы пришлём в TODO чат Telegram"
    }, "TextDescription");

    if (Java.type('ru.jamsys.JS').isAuth(rc)) {
        content.addData({height: 20, width: 10}, "SizedBox");
        content.addData({
            title: "Остановить синхронизацию с Telegram",
            icon: "pause",
            onPressed: content.getMethod("confirm", {confirm: true}),
            confirm: {
                data: "Подтвердить действие",
                actionTitle: "Остановить",
                onPressed: content.getMethod("openDialog", {openDialogData: true}),
                openDialogData: {
                    url: rc.url + "/remove",
                    backgroundColor: "transparent",
                    progressIndicatorColor: "#ffffff"
                }
            }
        }, "ButtonRed");
    } else {
        content.addData({title: "Как включить синхронизацию?"}, "H1");
        content.addData({
            marker: "1",
            title: "Нажать на кнопку \"Синхронизация с Telegram\". Это действие должно открыть чат TODO в Telegram."
        }, "TextDescription");
        content.addData({marker: "2", title: "Нажать на кнопку в Telegram: СТАРТ (START)"}, "TextDescription");
        content.addData({marker: "3", title: "Всё"}, "TextDescription");
        content.addData({
            marker: "*",
            title: "Если удалить в Telegram чат TODO с остановкой, со временем синхронизация в нашем приложении остановится"
        }, "TextDescription");

        content.addData({height: 20, width: 10}, "SizedBox");
        content.addData({
            title: "Синхронизация с Telegram",
            icon: "telegram",
            onPressed: ":launcher(onPressedLauncher)",
            onPressedLauncher: {
                "url": "https://t.me/jamsys_bot?start="+Java.type('ru.jamsys.JS').getTempKeyPerson(rc)
            },
            confirm: {
                data: "Подтвердить действие",
                actionTitle: "Остановить",
                onPressed: content.getMethod("openDialog", {openDialogData: true}),
                openDialogData: {
                    url: rc.url + "/remove",
                    backgroundColor: "transparent",
                    progressIndicatorColor: "#ffffff"
                }
            }
        }, "ButtonBlue600");
    }
}