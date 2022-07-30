function main(state, rc, content) {
    var stateParsed = JSON.parse(state);
    if(stateParsed["name"] != undefined && stateParsed["name"] != "" && stateParsed["name"].split(" ").join("") != ""){
        Java.type('ru.jamsys.JS').addData(rc, state, ["list"]);
        content.addData({title: "Opacha"}, "DialogOk");
        content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
        content.addAction("reloadPageByUrl", {"list": ["/project/" + rc.projectName]});
    }else{
        content.addData({title: "Opacha"}, "DialogFail");
        content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
        content.addAction("alert", {data: {backgroundColor: "red.600", data: "Имя списка не может быть пустым", duration: 3000}});
    }
}