function main(state, rc, content) {
    content.setWidgetData("title", "Просмотр задачи");
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");

    var res = JSON.parse(Java.type('ru.jamsys.JS').getDataInformation(rc.getParam.uid_data));
    content.addData({title: "Наименование задачи"}, "FirstH1-P-0-20");
    content.addData({hint: "", data: res["name"]}, "TextEditReadOnly");
    content.addData({title: "Время создания задачи"}, "H1-P-0-20");
    content.addData({hint: "", data: res["time"]}, "TextEditReadOnly");
    content.addData({title: "Автор задачи"}, "H1-P-0-20");
    content.addData({hint: "", data: res["author"]}, "TextEditReadOnly");

}