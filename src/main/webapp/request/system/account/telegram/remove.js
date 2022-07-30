function main(state, rc, content) {
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    Java.type('ru.jamsys.JS').logout(rc);

    content.addData({title: "Opacha"}, "DialogOk");
    content.addAction("closeWindow", {data: {delay: 1000, count: 2}});
    content.addAction("reloadPageByUrl", {
        "list": [
            "/project/" + rc.projectName +"/account"
        ]
    });
}