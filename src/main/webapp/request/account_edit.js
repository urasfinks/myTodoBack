function main(state) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    content.addData(JSON.stringify({label:"Имя Отчество", data: "", name: "fio"}), "TextEdit");
    content.addData(JSON.stringify({label:"Дата рождения", data: "", name: "bday"}), "TextEdit");
    content.setParentUI("WrapPage20");
    content.addSyncSocketDataUID("a7d437fa-d47a-4e0f-9417-f9701ece125e");
    return content.toString();
}