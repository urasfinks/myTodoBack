function main(state) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    content.addData(JSON.stringify({}), "account");
    return content.toString();
}