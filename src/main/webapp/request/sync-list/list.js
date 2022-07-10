function main(state, rc, content) {
    content.addData({title: "RC:" + rc.toString()}, "Text");
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/edit", title: "Изменить параметры"},
        icon: "edit"
    }, "AppBarActionAdd");
    content.addAppBarAction({
        onPressedData: {url: rc.url + "/add?data_uid=" + rc.getParam.uid_data, title: "Добавить задачу"},
        icon: "add"
    }, "AppBarActionAdd");

}