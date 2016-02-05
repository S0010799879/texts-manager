package com.aat

import groovy.json.JsonSlurper

class DownloadText {

    TextPluginExtension textPlugin
    String ws

    DownloadText() {
        ws = textPlugin.ws
        textPlugin.languages.each {
            loadTextWithLang(it)
        }
    }

    public static void loadTextWithLang(def lang) {
        def json = new JsonSlurper().parseText(ws.getText(
            requestProperties: [Accept: 'application/json', language: lang, translateKey: '%_$s']
        ))
        def dir = "values";
        if (!lang.equals("en")) {
            dir = "values-" + lang
        }
        File myDir = new File(dir);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(dir + File.separator + "strings.xml")
        file.write "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
        file << "<!-- DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED BY groovy script-->\n"
        file << "<resources>\n"
        json.data.texts.each { myText ->
            if (myText.id != null && myText.value != null) {
                if (myText.value.contains("&")) {
                    myText.value = myText.value.replaceAll("&", "&amp;")
                } else if (myText.value.contains("'")) {
                    myText.value = myText.value.replaceAll("'", "\\\\'")
                }
                file << "    <string name=\"$myText.key\">$myText.value</string>\n"
                // println myText.key + ' ' + myText.value
            }
        }
        file << "    <!-- other resources (not present in /text/static WS) -->\n"
        file << "    <string name=\"app_name\">eCab</string>\n"
        file << "    <string name=\"empty_time\">--</string>\n"
        // file << "    <string name=\"fb_id\">368852519902944</string>\n"
        file << "    <string name=\"zero\">0</string>\n"
        file << "    <string name=\"percent\" formatted=\"false\">%</string>\n"
        file << "    <string name=\"percent\" formatted=\"false\">%</string>\n"
        file << "    <string name=\"ten_percent\" formatted=\"false\">10%</string>\n"
        file << "    <string name=\"fifteen_percent\" formatted=\"false\">15%</string>\n"
        file << "    <string name=\"twenty_percent\" formatted=\"false\">20%</string>\n"
        file << "    <!-- missing keys for account payment -->\n"
        file << "    <string name=\"subscriptions\">subscriptions</string>\n"
        file << "    <string name=\"enter_login_password\">enter_login_password</string>\n"
        file << "    <string name=\"password_account_payment\">password_account_payment</string>\n"
        file << "    <string name=\"set_default_account_payment\">set_default_account_payment</string>\n"
        file << "</resources>"
        println "We've done with " + lang
    }
}
