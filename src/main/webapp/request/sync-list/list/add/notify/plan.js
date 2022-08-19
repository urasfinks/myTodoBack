function main(state, rc, content) {
    content.addData({title: "STATE:" + state}, "Text");
    content.addData({title: "RC:" + rc.toString()}, "Text");
}