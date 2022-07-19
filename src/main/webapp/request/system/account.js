function main(state, rc, content) {
    var res = JSON.parse(Java.type('ru.jamsys.JS').getPersonState(rc, JSON.stringify({
        "fio": "",
        "bday": ""
    })));

    content.addData(JSON.stringify({
        "fio": res["fio"],
        "bday": res["bday"],
        "time": new Date() + "",
        "dataGroup": [
            {
                "flutterType": "Container",
                "width": "infinity",
                "padding": "15,10,10,10",
                "child": {
                    "flutterType": "Text",
                    "data": "Основная"
                }
            },
            {
                "flutterType": "Divider",
                "thickness": 1,
                "color": "#f5f5f5"
            },
            {
                "flutterType": "Container",
                "padding": "15,10,10,10",
                "child": {
                    "flutterType": "Text",
                    "data": "Вторичная"
                }
            }
        ],
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