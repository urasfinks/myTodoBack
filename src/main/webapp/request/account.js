function main(state, personKey) {
    var content = Java.type('ru.jamsys.ContentOutput').newInstance();
    content.addData(JSON.stringify({
        "time": new Date()+"",
        "dataOnTap": {
            "title": "Данные аккаунта",
            "url": "project/system/account/edit",
            "dataUID": "a7d437fa-d47a-4e0f-9417-f9701ece125e"
        },
        "dataOnOpenGallery":{
            "url": "avatar-set"
        }
    }), "account2");
    return content.toString();
}