function main(state, rc, content) {
    content.setWidgetData("title", "Просмотр задачи");
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    content.setWidgetData("backgroundColor", "#f5f5f5");
    content.setSeparated(false);
    content.setParentUI("WrapPage20");
    var res = Java.type('ru.jamsys.JS').getDataInformation(rc.getParam.uid_data);
    content.addData({title: res}, "Text");

}