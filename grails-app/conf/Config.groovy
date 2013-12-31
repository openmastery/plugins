// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = false // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
                        html: ['text/html','application/xhtml+xml'],
                        xml: ['text/xml', 'application/xml'],
                        text: 'text/plain',
                        js: 'text/javascript',
                        rss: 'application/rss+xml',
                        atom: 'application/atom+xml',
                        css: 'text/css',
                        csv: 'text/csv',
                        all: '*/*',
                        json: ['application/json','text/json'],
                        form: 'application/x-www-form-urlencoded',
                        multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

String logDir = (Environment.warDeployed ? System.getProperty('catalina.base') + '/logs' : 'target')
String logFile = "$logDir/${appName}.log"

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    appenders {

        // Gets rid of stacktrace.log
        'null' name: 'stacktrace'

        console name: 'stdout',
                layout: pattern(conversionPattern: '%d{MM-dd-yyyy HH:mm:ss,SSS} [%t] [%p] %c{2} %X{trackingInfo}-- %m%n')

        rollingFile name: 'appLog',
                    file: logFile,
                    maxFileSize: "5MB",
                    maxBackupIndex: 10,
                    'append': true,
                    layout: pattern(conversionPattern: '%d{MM-dd-yyyy HH:mm:ss,SSS} [%t] [%p] %c{2} %X{trackingInfo}-- %m%n')

    }

    root {
        info 'appLog'
    }

    // Preserves console output in development mode
    environments {
        development {
            root {
                info 'appLog', 'stdout'
            }
        }
        local {
            root {
                info 'appLog', 'stdout'
            }
        }
    }

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
          'org.codehaus.groovy.grails.web.pages', //  GSP
          'org.codehaus.groovy.grails.web.sitemesh', //  layouts
          'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
          'org.codehaus.groovy.grails.web.mapping', // URL mapping
          'org.codehaus.groovy.grails.commons', // core / classloading
          'org.codehaus.groovy.grails.plugins', // plugins
          'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache',
          'grails.plugin.webxml.WebxmlGrailsPlugin',
          'grails.util.GrailsUtil',
          'org.codehaus.groovy.grails.scaffolding',
          'org.grails.plugin.resource.module',
          'org.codehaus.groovy.grails.web.context',
          'org.apache.coyote.http11',
          'org.apache.catalina.core',
          'org.apache.catalina.startup'


    info  'grails.app', 'com.newiron'

}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'com.newiron.auth.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'com.newiron.auth.UserRole'
grails.plugins.springsecurity.authority.className = 'com.newiron.auth.Role'
grails.plugins.springsecurity.requestMap.className = 'com.newiron.auth.RequestMap'
grails.plugins.springsecurity.securityConfigType = 'Requestmap'
grails.plugins.springsecurity.rememberMe.persistentToken.domainClassName = 'com.newiron.auth.RememberMeToken'
grails.plugins.springsecurity.rememberMe.persistent = true
grails.plugins.springsecurity.rememberMe.useSecureCookie = true
grails.plugins.springsecurity.useSessionFixationPrevention = true
grails.plugins.springsecurity.password.algorithm = 'bcrypt'
grails.plugins.springsecurity.successHandler.alwaysUseDefault = true
grails.plugins.springsecurity.successHandler.defaultTargetUrl = "/"
grails.plugins.springsecurity.rejectIfNoRule = true