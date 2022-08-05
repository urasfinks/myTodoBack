function main(state, rc, content) {
    var res = JSON.parse(Java.type('ru.jamsys.JS').getPersonState(rc, JSON.stringify({
        "fio": "Гость",
        "bday": "---"
    })));

    var accountColor = "red";
    var accountIcon = "cancel";
    var titleTelegram = "Синхронизация с Telegram";
    if(Java.type('ru.jamsys.JS').isAuth(rc)){
        accountColor = "black";
        accountIcon = "check_circle";
        titleTelegram = "Синхронизованно с Telegram!";
    }

    content.setWidgetData("title", "Аккаунт");
    content.setWidgetData("backgroundColor", "blue.600");
    content.setWidgetData("pullToRefreshBackgroundColor", "blue.600");
    content.setWidgetData("progressIndicatorBackgroundColor", "#ffffff");

    content.addData(JSON.stringify({
        "accountColor": accountColor,
        "accountIcon": accountIcon,
        "titleTelegram": titleTelegram,
        "fio": res["fio"],
        "bday": res["bday"],
        "time": new Date() + "",

        "onPressedDonat": ":launcher(onPressedLauncherDonat)",
        "onPressedLauncherDonat": {
            "url": "https://pay.cloudtips.ru/p/2bda8e55"
        },
        "onPressedTelegram": ":openWindow(onPressedLauncher)",
        "onPressedLauncher": {
            "title": "Синхронизация с Telegram",
            "url": rc.url + "/telegram"
        },
        /*"onPressedTelegram": ":launcher(onPressedLauncher)",
        "onPressedLauncher": {
            "url": "https://t.me/jamsys_bot?start="+Java.type('ru.jamsys.JS').getTempKeyPerson(rc)
        },*/
        "onTapEditAccount": {
            "title": "Данные аккаунта",
            "url": rc.url + "/edit"
        },
        "onTapAvatarSet": {
            "url": "/avatar-set"
        }
    }), "account2");
}