function main(state, rc, content) {
    var nameField = "passwd";
    var stateParsed = JSON.parse(state);
    if (stateParsed[nameField] != undefined && stateParsed[nameField] != "" && stateParsed[nameField].split(" ").join("") != "") {
        if (rc.getParam.uid_data != undefined) {
            Java.type('ru.jamsys.JS').updateDataState(rc, rc.getParam.uid_data, state);
        }
        content.addData({title: "Opacha"}, "DialogOk");
        content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
        content.addAction("reloadPageByUrl", {
            "list": [
                "/project/" + rc.projectName + "/edit/share",
            ]
        });
    } else {
        content.addData({title: "Opacha"}, "DialogFail");
        content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
        content.addAction("alert", {
            data: {
                backgroundColor: "red.600",
                data: "Пароль не может быть пустым",
                duration: 3000
            }
        });
    }
}