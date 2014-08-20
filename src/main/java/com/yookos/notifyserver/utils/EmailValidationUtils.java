package com.yookos.notifyserver.utils;

/**
 * Created by jome on 2014/06/06.
 */
public class EmailValidationUtils {

    private static String emailHeader =  "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "<head>\n" +
            "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
            "\t<title>Header Template</title>\n" +
            "\t<style type=\"text/css\">\n" +
            "    * {\n" +
            "\t\tmargin: 0;\n" +
            "\t\tpadding: 0;\n" +
            "\t}\n" +
            "\ta {\n" +
            "\t\tcolor: #3778C7 !important;\n" +
            "\t}\n" +
            "\tbody {\n" +
            "\t\tfont-family: Helvetica, Arial, sans-serif;\n" +
            "\t\t-webkit-text-size-adjust: none;\n" +
            "\t}\n" +
            "\t/* Webkit */\n" +
            "\th1, h2, h3, h4, h5, h6, p, hr {\n" +
            "\t\t-webkit-margin-before: 0;\n" +
            "\t\t-webkit-margin-after: 0;\n" +
            "\t\t-webkit-margin-start: 0;\n" +
            "\t\t-webkit-margin-end: 0;\n" +
            "\t}\n" +
            "\t.button td {\n" +
            "\t\tbackground-image: -moz-linear-gradient(top, #75B7E3 0%, #5498D6 50%, #408BD0 51%, #387AC8 100%) !important;\n" +
            "\t\tbackground-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#75B7E3), color-stop(50%,#5498D6), color-stop(51%,#408BD0), color-stop(100%,#387AC8)) !important;\n" +
            "\t\t-webkit-box-shadow: inset 0 1px 0 #74b9de, 0 1px 3px rgba(0,0,0,.3) !important;\n" +
            "\t\t-moz-box-shadow: inset 0 1px 0 #74b9de, 0 1px 3px rgba(0,0,0,.3) !important;\n" +
            "\t}\n" +
            "\t\n" +
            "\t#logo-header-color{\n" +
            "\t\tbackground-color:#019dd8;\n" +
            "\t\theight:100px;\n" +
            "\t\twidth:100%;\n" +
            "\t}\n" +
            "\t\n" +
            "\t.avatar-image{\n" +
            "\t\tpadding:2px;\n" +
            "\t\tborder:1px solid #019dd8;\n" +
            "\t\ttext-align:left;\n" +
            "\t\tmargin-right:10px;\n" +
            "\t\twidth:76px;\n" +
            "\t\tfloat:left;\n" +
            "\t\t\t\n" +
            "\t}\n" +
            "\n" +
            "\t.header-bar{\n" +
            "\t\tpadding:0px;\n" +
            "\t\tborder:1px solid #019dd8;\n" +
            "\t\ttext-align:left;\n" +
            "\t\tmargin-right:10px;\t\n" +
            "\t}\n" +
            "\t\n" +
            "\t\n" +
            "\t</style>\n" +
            "</head>";

    private static String bodyStart ="<body style=\"width:100% !important; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none;\">\n" +
            "\n" +
            "<table cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#ffffff\" style=\"width: 90%;margin-left:5%; margin-right:5%; -moz-border-radius: 8px; -webkit-border-radius: 8px; border-radius: 8px; margin-top:10px\">\n" +
            "\t<tbody style=\"background-color:#eaeef0;\">\n" +
            "    \t<tr>\n" +
            "       \t\t<td style=\"background-color:#019dd8; border-bottom:1px solid white; padding:2px; padding-left:10px;\">\n" +
            "            \t<img src=\"http://www.yookos.com/themes/yookos6/img/yookos-mail-logonew.jpg\" alt=\"Yookos\"/>            \n" +
            "\t\t\t</td>        \n" +
            "        </tr>\n" +
            "\t\t<tr>\n" +
            "\t\t\t<td valign=\"middle\" style=\"padding-top: 10px; padding-right: 10px; padding-bottom: 20px; padding-left: 10px;\"><p style='color: #666666; margin-bottom: 20px; font-size: 14px; line-height: 20px; font-family: Helvetica, Arial, sans-serif;'>\n" +
            "Hello,\n" +
            "</p>\n" +
            "\n" +
            "<p style='color: #666666; margin-bottom: 20px; font-size: 14px; line-height: 20px; font-family: Helvetica, Arial, sans-serif;'>\n" +
            "You or someone using your email address recently asked to join Yookos on your mobile device.  For security reasons, we need to validate your email address before you gain access to Yookos.\n" +
            "</p>\n" +
            "\n" +
            "<p style='color: #666666; margin-bottom: 20px; font-size: 14px; line-height: 20px; font-family: Helvetica, Arial, sans-serif;'>\n" +
            "Just enter the number displayed below in the provided verification text box on your Yookos Mobile App.\n" +
            "<br>\n" +
            "<h2>";

    private static String bodyFooter = "</h2>\n" +
            "</p>\n" +
            "\n" +
            "\n" +
            "<p style='color: #666666; margin-bottom: 20px; font-size: 14px; line-height: 20px; font-family: Helvetica, Arial, sans-serif;'>\n" +
            "If you didn't recently ask to join Yookos, please disregard this email.\n" +
            "</p>\n" +
            "\n" +
            "<p style='color: #666666; margin-bottom: 20px; font-size: 14px; line-height: 20px; font-family: Helvetica, Arial, sans-serif;'>\n" +
            "Thanks,\n" +
            "<br>\n" +
            "The Yookos Admin\n" +
            "</p></td>\n" +
            "        </tr>\n" +
            "     </tbody>\n" +
            "</table>\n" +
            "</body>";

    public static String getEmailHeader() {
        return emailHeader;
    }

    public static void setEmailHeader(String emailHeader) {
        EmailValidationUtils.emailHeader = emailHeader;
    }

    public static String getBodyStart() {
        return bodyStart;
    }

    public static void setBodyStart(String bodyStart) {
        EmailValidationUtils.bodyStart = bodyStart;
    }

    public static String getBodyFooter() {
        return bodyFooter;
    }

    public static void setBodyFooter(String bodyFooter) {
        EmailValidationUtils.bodyFooter = bodyFooter;
    }
}
