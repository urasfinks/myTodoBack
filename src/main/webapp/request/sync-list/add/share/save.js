function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");

    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataState(content, rc.getParam.uid_data, JSON.stringify({
        "passwd": ""
    })));

    var nameField = "passwd";
    var stateParsed = JSON.parse(state);
    if (stateParsed[nameField] != undefined && stateParsed[nameField] == res["passwd"]) {
        if (rc.getParam.uid_data != undefined) {
            Java.type('ru.jamsys.JS').addSharedPerson(rc, rc.getParam.uid_data);
        }
        content.addData({title: "Opacha"}, "DialogOk");
        content.addAction("closeWindow", {data: {delay: 1000, url: "/project/to-do"}});
        content.addAction("reloadPageByUrl", {
            "list": [
                "/project/to-do"
            ]
        });
    } else {
        content.addData({title: "Opacha"}, "DialogFail");
        content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
        content.addAction("alert", {
            data: {
                backgroundColor: "red.600",
                data: "Неверный пароль",
                duration: 3000
            }
        });
    }


}