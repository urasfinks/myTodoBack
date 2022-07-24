function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var stateParsed = JSON.parse(state);
    if (stateParsed["name"] != undefined && stateParsed["name"] != "" && stateParsed["name"].split(" ").join("") != "") {
        if (rc.getParam.uid_data != undefined) {
            Java.type('ru.jamsys.JS').updateDataState(rc, rc.getParam.uid_data, state);
        }
        content.addData({title: "Opacha"}, "DialogOk");
        content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
        content.addAction("reloadPageByUrl", {
            "list": [
                "/project/" + rc.projectName + "/list",
                "/project/" + rc.projectName
            ]
        });
    } else {
        content.addData({title: "Opacha"}, "DialogFail");
        content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
        content.addAction("alert", {
            data: {
                backgroundColor: "red.600",
                data: "Имя задачи не может быть пустым",
                duration: 30000
            }
        });
    }
}