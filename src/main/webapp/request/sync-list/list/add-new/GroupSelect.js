function main(state, rc, content) {
    //content.setParentUI("WrapPage20");
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    //content.addData({title: "STATE:" + state.toString()}, "Text");
    content.addData({title: "Ваши группы для списка"}, "H1Underline");
    content.addData({}, "Divider");
    content.addData({
        title: "Хоба",
        icon: "add",
        onPressed: ":setAppStore(setAppStoreData)|bridgeDynamicFn(bridgeDynamicFn1)",
        setAppStoreData: {
            key: "groupName",
            value: "Хоба"
        },
        bridgeDynamicFn1: {
            fn: "closeWindow"
        }
    }, "RowInkWellSelect");
    content.addData({}, "Divider");

}