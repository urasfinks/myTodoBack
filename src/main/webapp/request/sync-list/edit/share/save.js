function main(state, rc, content) {
    if (state != undefined && state != "") {
        var stateParsed = JSON.parse(state);
        if(stateParsed["TempPersonKey"] != undefined && stateParsed["TempPersonKey"].split(" ").join("") != ""){
            Java.type('ru.jamsys.JS').addSharedPerson(rc, stateParsed["TempPersonKey"], rc.getParam.uid_data);
            content.addData({title: "Opacha"}, "DialogOk");
            content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
            content.setWidgetData("parentRefresh", true);
        }else{
            content.addData({title: "Opacha"}, "DialogFail");
            content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
            content.addAction("alert", {
                data: {
                    backgroundColor: "red.600",
                    data: "Код пользователя должен быть заполнен",
                    duration: 3000
                }
            });
        }
    }else{
        content.addData({title: "Opacha"}, "DialogOk");
        content.addAction("closeWindow", {data: {delay: 1000, count: 1}});
        content.setWidgetData("parentRefresh", true);
    }
}