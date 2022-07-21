function main(state, rc, content) {
    var dataUID = Java.type('ru.jamsys.JS').addData(rc, state, ["list"]);
    var stateParsed = JSON.parse(state);
    content.addData({title: "Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
    content.addAction("reloadPageByUrl", {"list": ["/project/" + rc.projectName]});
    content.addAction("openWindow", {
        data:{
            delay: 1500,
            title: stateParsed["name"],
            dataUID: dataUID,
            url: "/project/"+rc.projectName + "/list?uid_data=" + dataUID,
            backgroundColor: "#f5f5f5",
            config: {
                parentUpdateOnChangeStateData: true
            }
        }
    });
}