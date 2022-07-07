function main(state, personKey, content) {
    Java.type('ru.jamsys.JS').updateDataState(personKey, personKey, state);
    content.addData({title:"Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data:{delay: 1000}});
    content.addAction("reloadPageByUrl", {"list":["project/system/account"]});
}