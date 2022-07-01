function main(state) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    content.setTitle("Аккаунт");
    content.addData(JSON.stringify({title:"Привет", test: {x:"y"}}), "test");
    //content.addData("{}", "test2");
    content.setParentUI("test2");
    content.addSyncSocketDataUID("Opa 2");

    return content.toString();
}