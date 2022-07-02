function main(state, personKey) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    content.addData(JSON.stringify({"dataOnTap": {
            "title": "Данные аккаунта",
            "url": "project/system/account/edit",
            "dataUID": "a7d437fa-d47a-4e0f-9417-f9701ece125e"
        }}), "account");
    return content.toString();
}