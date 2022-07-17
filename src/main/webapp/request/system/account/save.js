function main(state, rc, content) {
    Java.type('ru.jamsys.JS').updateDataState(rc, rc.idPerson.toString(), state);
    content.addData({title:"Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data:{delay: 1000}});
    content.addAction("reloadPageByUrl", {"list":["/project/system/account"]});
}