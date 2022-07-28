function main(state, rc, content) {
    var res = JSON.parse(Java.type('ru.jamsys.JS').getPersonState(rc, JSON.stringify({
        "fio": "Гость",
        "bday": "---"
    })));

    content.addData(JSON.stringify({
        "fio": res["fio"],
        "bday": res["bday"],
        "time": new Date() + "",
        "onPressedLauncher": {
            "url": "https://t.me/jamsys_bot?start="+Java.type('ru.jamsys.JS').createPersonKeyTemp(rc)
        },
        "onTapEditAccount": {
            "title": "Данные аккаунта",
            "url": rc.url + "/edit",
            "dataUID": "a7d437fa-d47a-4e0f-9417-f9701ece125e"
        },
        "onTapAvatarSet": {
            "url": "/avatar-set"
        }
    }), "account2");
}