import java.text.SimpleDateFormat

eventCompileStart = { msg ->

    String OS = System.getProperty('os.name')
    String cmdPrefix = ""

    if (OS && OS.toLowerCase() =~ "windows") {
        cmdPrefix = "cmd /c "
    }

    new File("grails-app/views/_version.gsp").text = (cmdPrefix + "git rev-parse HEAD").execute().text
    new File("grails-app/views/_buildDate.gsp").text = new SimpleDateFormat("MM/dd/yyyy HH:mm zzz").format(new Date())

}