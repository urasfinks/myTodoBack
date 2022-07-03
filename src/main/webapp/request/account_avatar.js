function main(state, personKey) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    content.setWidgetData("title", "Hello");
    return content.toString();
}