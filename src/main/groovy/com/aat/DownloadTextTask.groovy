package com.aat

import com.android.build.gradle.api.ApplicationVariant
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT

class DownloadTextTask extends DefaultTask {

    ApplicationVariant applicationVariant
    String variantName
    TextPluginExtension textPluginExt
    def ws

    DownloadTextTask() {
        super()
    }

    @TaskAction
    def load() throws IOException {
        textPluginExt = project.texts
        if (textPluginExt.ws) {
            initWsUrl()
            textPluginExt.languages.add(textPluginExt.defaultLanguage)
            textPluginExt.languages.each {
                loadTextWithLang(it.toLowerCase())
            }
        }
    }

    public void loadTextWithLang(String lang) {
        println "WS : " + ws
        def content2 = new HTTPBuilder(ws.toString()).request(GET, TEXT) { req ->
            headers.'accept' = 'application/json'
            headers.'language' = lang
            headers.'translateKey' = '%_$s'

            response.success = { resp, reader ->
                // println reader.text
                reader.text
            }
        }
        if (content2) {
            def json = new JsonSlurper().parseText(content2)
            def dir = 'values';
            if (!lang.equals(textPluginExt.defaultLanguage)) {
                dir = 'values-' + lang
            }
            String currentDir = new File(".").getAbsoluteFile().getParent()
            currentDir = currentDir + '/app/src/main/res/'

            File myDir = new File(currentDir + dir);
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File file = new File(myDir.getAbsolutePath() + File.separator + 'strings.xml')
            file.write '<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n'
            file << '<!-- DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED BY groovy script made by aat -->\n\n'
            file << "<resources>\n"

            def texts = json.data.texts
            if (textPluginExt.alphabeticallySort) {
                texts.sort {
                    it.key.toLowerCase()
                }
            }
            if (textPluginExt.removeDuplicate) {
                texts.unique {
                    it.key.trim()
                }
            }

            texts.each { myText ->
                if (myText.id != null && myText.value != null) {
                    if (!myText.key.matches("\\d.*")) {  // key must not start with a digit
                        if (myText.value.contains("&")) {
                            myText.value = myText.value.replaceAll("&", "&amp;")
                        }
                        if (myText.value.contains("'")) {
                            myText.value = myText.value.replaceAll("'", "\\\\'")
                        }

                        // Add formatted="false" if text contains %
                        // But we do not handle %1$s
                        def pattern = /.*%[0-9]\$.*/
                        if (myText.value.contains('%') && !(myText.value ==~ pattern)) {
                            myText.key = myText.key + '" formatted="false'
                        }
                        file << "    <string name=\"${myText.key.trim()}\">$myText.value</string>\n"
                    }
                }
            }

            // Theses keys will be addded by customer later
            if (lang == textPluginExt.defaultLanguage && textPluginExt.missingKeys != null) {
                file << '    <!-- Keys added by user -->\n'
                def values = textPluginExt.missingKeys.tokenize('\n')
                // Add indentation of 4 spaces
                values.each { missingKey ->
                    file << '    ' + missingKey.trim() + '\n'
                }
                // file << textPluginExt.missingKeys
            }
            file << '</resources>'
            println "We've done with [" + lang + ']'
        } else {
            println 'content is null or empty'
        }
    }

    private void initWsUrl() {
        String wsUrl = textPluginExt.ws
        if (textPluginExt.variantToWs) {
            if (textPluginExt.variantToWs[variantName]) {
                wsUrl = textPluginExt.variantToWs[variantName]
            }
        }
        ws = new URL(wsUrl)
    }
}

