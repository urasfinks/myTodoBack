function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var stateParsed = JSON.parse(state);
    if(stateParsed["name"] != undefined && stateParsed["name"] != "" && stateParsed["name"].split(" ").join("").split("\n").join("") != ""){
        if (rc.getParam.uid_data != undefined) {
            var listTask = stateParsed["name"].split("\n");
            for(var i=0;i<listTask.length;i++){
                var name = listTask[i].trim();
                if(name != ""){
                    stateParsed["name"] = name;
                    var newDataUid = Java.type('ru.jamsys.JS').addData(rc, JSON.stringify(stateParsed), [rc.getParam.uid_data]);
                    var data = {};
                    data[newDataUid] = false;
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
    }else{
        content.addData({title: "Opacha"}, "DialogFail");
        content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
        content.addAction("alert", {data: {backgroundColor: "red.600", data: "Имя задачи не может быть пустым", duration: 3000}});
    }
}