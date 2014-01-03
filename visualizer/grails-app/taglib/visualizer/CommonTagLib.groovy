package visualizer

class CommonTagLib {
    def grailsApplication

    static namespace = "common"

    def messages = { attrs, body ->
        out << render(template: '/taglib/common/messages', model: attrs)
    }

    def header = { attrs, body ->
        out << render(template: '/taglib/common/header', model: attrs)
    }

    def footer = { attrs, body ->
        attrs.put('', grailsApplication.config.newiron.footer.niLink)
        out << render(template: '/taglib/common/footer', model: attrs)
    }

    def nav = { attrs, body ->
        out << render(template: '/taglib/common/nav', model: attrs)
    }
}
