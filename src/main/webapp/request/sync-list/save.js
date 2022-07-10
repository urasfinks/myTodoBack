function main(state, rc, content) {
    Java.type('ru.jamsys.JS').addData(rc, state, []);
    content.addData({title:"Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data:{delay: 1000, url: "project/to-do"}});
    //content.addAction("reloadPageByUrl", {"list":["project/to-do"]});
}