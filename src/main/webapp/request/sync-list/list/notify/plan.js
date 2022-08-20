function main(state, rc, content) {
    //content.addData({title: "STATE:" + Java.type('ru.jamsys.JS').getPlanNotify(rc, state)}, "Text");
    content.addData({title: "Оповещения по порядку следования:", padding: "20,20,10,10"}, "TextPadding");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    var list = JSON.parse(Java.type('ru.jamsys.JS').getPlanNotify(rc, state));
    for (var i = 0; i < list.length; i++) {
        content.addData({data: list[i]["data"], desc: list[i]["date"]}, "ChatMsgLeft");
    }
}