function main(state, rc, content) {
    content.setSeparated(true);
    content.setParentUI("WrapPage20");
    content.addAppBarAction({onPressedData: {url: "project/to-do/add", title: "Добавление списка"}, icon:"add"}, "AppBarActionAdd");
}