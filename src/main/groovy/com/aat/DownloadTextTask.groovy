package com.aat

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DownloadTextTask extends DefaultTask {

    TextPluginExtension textPlugin
    def ws

    DownloadTextTask() {
        super()
    }

    @TaskAction
    def load() throws IOException {
        textPlugin = project.texts
        ws = new URL(textPlugin.ws)
        textPlugin.languages.each {
            println it
            // loadTextWithLang(it)
        }
    }

    public void loadTextWithLang(def lang) {
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
        file << "<!-- DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED BY groovy script made by aat -->\n"
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

        // Theses keys will be addded by customer later
        if (lang == textPlugin.defaultLanguage) {
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
            file << "    <string name=\"connectivity_problem\">Erreur de connexion</string>\n"
            file << "    <string name=\"no_network\">Aucune connexion réseau, veuillez patienter.</string>\n"
            file << "    <string name=\"enter_professional_account\">COMPTE PROFESSIONEL</string>\n"
            file << "    <string name=\"update_service_level_unavailable\">Cette fonction n\'est pas dispo pour ce partenaire</string>\n"
            file << "    <string name=\"select_your_favorite_partner\">Choisissez votre partenaire référentiel</string>\n"
            file << "    <string name=\"nearest_cab\">Le taxi le plus proche</string>\n"
            file << "    <string name=\"confirm_delete_billing_address\">Are you sure you want to delete this billing address ?</string>\n"
            file << "    <string name=\"sponsor_code_label\">Saisir le code de votre parrain</string>\n"
            file << "    <string name=\"sponsor_code_not_recognized\">Sponsor code pas reconnu</string>\n"
            file << "    <string name=\"your_promo_code\">Votre code promo :</string>\n"
            file << "    <string name=\"replace_promo_code\">Remplacer</string>\n"
            file << "    <string name=\"add_promo_code_for_next_ride\">Ajoutez un code promo pour votre prochaine course</string>\n"
            file << "    <string name=\"only_one_promo_code\">Un seul code promo est permis</string>\n"
            file << "    <string name=\"confirm_to_replace_promo_code\">Etes vous sûr de vouloir remplacer ce code promo par un autre ?</string>\n"
            file << "    <string name=\"problem_order\">Une commande n\'a pas été payée. Veuillez procéder au règlement pour pouvoir effectuer des nouvelles commandes.</string>\n"
            file << "    <string name=\"cancel_order\">cancel_order</string>\n"
            file << "    <string name=\"modify_order\">modify_order</string>\n"
            file << "    <string name=\"rate_app_title\">Noter eCab</string>\n"
            file << "    <string name=\"rate_app_content\">Si vous apréciez eCab, notez l\'application. Merci de votre intérêt</string>\n"
            file << "    <string name=\"rate_agree\">Noter</string>\n"
            file << "    <string name=\"rate_later\">Plus tard</string>\n"
            file << "    <string name=\"rate_disagree\">Non merci</string>\n"
            file << "    <string name=\"scan\">Scanner</string>\n"
        }
        file << "</resources>"
        println "We've done with " + lang
    }
}

