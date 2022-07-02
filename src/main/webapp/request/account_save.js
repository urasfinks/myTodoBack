function main(state, personKey) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    Java.type('ru.jamsys.JS').updateDataState(personKey, personKey, state);
    //content.addData(JSON.stringify({title: state}), "Text");
    content.addData(JSON.stringify({title:"Opacha"}), "DialogOk");
    content.addAction("closeWindow", JSON.stringify({data:{delay: 1000}}));
    return content.toString();
}