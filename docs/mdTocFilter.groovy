/**
 * mdTocFilter.groovy
 *
 * This Groovy script is called by ./indexconv.sh script.
 * This script is designed to filter a "index_.md" file with
 * Table Of Contents (TOC) section generated by Pandoc.
 *
 * The Pandoc has a small bug. It generates a TOC section with
 * href="someurl#foo_bar", but it should rather be
 * href="someurl#foo-bar".
 *
 * This mdTocFilter script replaces "_" to "-" to workaround this shortage.
 */
import java.util.regex.Pattern
import java.util.regex.Matcher
Pattern pattern = Pattern.compile('^(.*)\\(#_(.*)$')
def stdin = System.in.newReader()
String line
while ((line = stdin.readLine()) != null) {
    Matcher m = pattern.matcher(line)
    if (m.matches()) {
        /*
        println "does match"
        println "groupCount=" + m.groupCount()
        for (int i = 0; i <= m.groupCount(); i++) {
          println "group[" + i + "]=" + m.group(i)
        }
        */
        println m.group(1) + '(#' + m.group(2).replace('_', '-')
    } else {
        //println "no match"
        println line
    }
}