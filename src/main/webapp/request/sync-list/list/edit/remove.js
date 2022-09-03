function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    if (rc.getParam.uid_data != undefined) {
        if (rc.getParam.parent_uid_data != undefined) {
            var data = {};
            data["_"+rc.getParam.uid_data] = null;
            Java.type('ru.jamsys.JS').updateDataState(rc, rc.getParam.parent_uid_data, JSON.stringify(data));
        }
        Java.type('ru.jamsys.JS').removeData(rc, rc.getParam.uid_data);
    }

    content.addData({title: "Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
    content.addAction("reloadPageByUrl", {
        "list": [
            "/project/" + rc.projectName +"/list",
            "/project/" + rc.projectName
        ]
    });
    Java.type('ru.jamsys.JS').socketReload(rc, rc.getParam.parent_uid_data);
}