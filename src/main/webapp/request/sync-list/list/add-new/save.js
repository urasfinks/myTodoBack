function main(state, rc, content) {
    //content.addData({title: "RC:" + state}, "Text");
    var stateParsed = JSON.parse(state);
    var listTask = [];
    checkAndAdd(stateParsed, "name", listTask);
    for(var key in stateParsed){
        if(key.startsWith("LoopField")){
            checkAndAdd(stateParsed, key, listTask);
        }
    }
    if (listTask.length > 0) {
        if (rc.getParam.uid_data != undefined) {
            for (var i = 0; i < listTask.length; i++) {
                var name = listTask[i].trim();
                if (name != "") {
                    stateParsed["name"] = name;
                    var newDataUid = Java.type('ru.jamsys.JS').addData(rc, JSON.stringify(stateParsed), [rc.getParam.uid_data]);
                    var data = {};
                    data["_" + newDataUid] = false;
                    Java.type('ru.jamsys.JS').updateDataState(rc, rc.getParam.uid_data, JSON.stringify(data));
                }
            }
        }
        content.addData({title: "Opacha"}, "DialogOk");
        content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
        content.addAction("reloadPageByUrl", {
            "list": [
                "/project/" + rc.projectName + "/list",
                "/project/" + rc.projectName
            ]
        });
        Java.type('ru.jamsys.JS').socketReload(rc, rc.getParam.uid_data);
    } else {
        content.addData({title: "Opacha"}, "DialogFail");
        content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
        content.addAction("alert", {
            data: {
                backgroundColor: "red.600",
                data: "Имя задачи не может быть пустым",
                duration: 3000
            }
        });
    }
}

function checkAndAdd(data, key, arr){
    if(data[key] != undefined && data[key] != "" && data[key].split(" ").join("").split("\n").join("") != ""){
        arr.push(data[key]);
    }
}