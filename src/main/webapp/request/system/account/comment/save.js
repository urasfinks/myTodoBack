function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    var stateParsed = JSON.parse(state);
    if (stateParsed["comment"] != undefined && stateParsed["comment"] != ""){
        Java.type('ru.jamsys.JS').comment(rc, stateParsed["comment"]);
    }
    content.addData({title: "Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
}