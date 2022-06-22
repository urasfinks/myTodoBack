function main(state) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    content.setTitle("Hello");
    content.addData(JSON.stringify({title:"Opacha", test: {x:"y"}}), "test");
    //content.addData("{}", "test2");
    content.setParentUI("test2");
    content.addSyncSocketDataUID("Opa 2");

    return content.toString();
}