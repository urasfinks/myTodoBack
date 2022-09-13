function main(state, rc, content) {
    //content.setParentUI("WrapPage20");
    //content.addData({title: "RC:" + rc.toString()}, "Text");
    //content.addData({title: "STATE:" + state.toString()}, "Text");
    content.addData({title: "Группы"}, "H1Underline");
    content.addData({height: 1, color: "grey"}, "DividerCustom");

    var list = getGroupName(rc, content);
    for (var i = 0; i < list.length; i++) {
        //group_name
        var value = list[i]["group_name"];
        content.addData({
            title: value,
            icon: "add",
            onPressed: ":setAppStore(setAppStoreData)|bridgeDynamicFn(bridgeDynamicFn1)",
            setAppStoreData: {
                key: "groupName",
                value: value
            },
            bridgeDynamicFn1: {
                fn: "closeWindow"
            }
        }, "RowInkWellSelect");
        content.addData({}, "Divider");
    }

}

function getGroupName(rc, content) {
    var ret = [];
    try {
        var obj = {
            sql: "select distinct on (group_name) group_name from (\n" +
                "    select trim(d1.state_data->>'groupName') as group_name from data d1\n" +
                "    inner join tag t1 on t1.id_data = d1.id_data\n" +
                "    where t1.key_tag = ${uid_data}\n" +
                "    ) as sq1 where group_name <> ''\n" +
                "order by group_name",
            args: [
                {
                    field: 'group_name',
                    type: 'VARCHAR',
                    direction: 'COLUMN'
                },
                {
                    field: 'uid_data',
                    type: 'VARCHAR',
                    direction: 'IN',
                    value: rc.getParam.uid_data.toString()
                }
            ]
        };
        return JSON.parse(Java.type('ru.jamsys.JS').sql(JSON.stringify(obj)));
    } catch (e) {
        content.addData({title: e.toString()}, "Text");
    }
    return ret;
}