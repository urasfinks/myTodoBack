function main(state, rc, content) {
    if (state != undefined && state != "") {
        var stateParsed = JSON.parse(state);
        Java.type('ru.jamsys.JS').removeSharedPerson(rc, rc.getParam.TempPersonKey, rc.getParam.uid_data);
    }

    content.addData({title: "Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
    content.setWidgetData("parentRefresh", true);
}