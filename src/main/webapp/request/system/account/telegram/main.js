function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setParentUI("WrapPage20");

    content.setWidgetData("refreshOnResume", true);
    content.setWidgetData("parentRefresh", true);

    content.addData({title: "Приятное в этой истории"}, "H1");
    content.addData({
        marker: "*",
        title: "Синхронизация нужна только тебе. Все функции приложения - бесплатные и доступны без синхронизации с Telegram. Подключить или отключить синхронизацию с Telegram можно в любое время."
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
        title: "Если тебе понадобятся напоминания, мы отправим их в Telegram. Собственных уведомлений у приложения не планируется"
    }, "TextDescription");

    if (Java.type('ru.jamsys.JS').isAuth(rc)) {
        content.addData({title: "У тебя подключена синхронизация, всё Огонь!"}, "H1");
        content.addData({
            marker: "*",
            title: "Удаление с остановкой TODO бота, приведёт к отключению синхронизации"
        }, "TextDescription");
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
        /*content.addData({title: "Внимание!!!"}, "H1");
        content.addData({
            marker: "1",
            title: "Если ты примешь решение отключить синхронизацию Telegram, а потом сделаешь синхронизацию с другим Telegram аккаунтом, вся информация текущего аккаунта мигрирует к новову аккаунту"
        }, "TextDescription");
        content.addData({
            marker: "2",
            title: "Отключение синхронизации не приведёт к потере информации, просто она будет принадлежать только твоему устройству"
        }, "TextDescription");*/

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