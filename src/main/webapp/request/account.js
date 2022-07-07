function main(state, personKey, content) {
    personKey = "a7d437fa-d47a-4e0f-9417-f9701ece125e";
    try {
        var obj = {
            sql: "select state_data from \"data\" where uid_data = ${uid_data}",
            args: [
                {
                    field: 'state_data',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'uid_data',
                    type: 'VARCHAR',
                    direction: 'IN',
                    value: personKey
                }
            ]
        };
        var x = Java.type('ru.jamsys.JS').sql(JSON.stringify(obj));
        var res = JSON.parse(JSON.parse(x)[0]['state_data']);
    } catch (e) {
        var res = {
            "fio": "",
            "bday": ""
        };
    }

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
            "url": "project/system/account/edit",
            "dataUID": "a7d437fa-d47a-4e0f-9417-f9701ece125e"
        },
        "onTapAvatarSet": {
            "url": "avatar-set"
        }
    }), "account2");
}