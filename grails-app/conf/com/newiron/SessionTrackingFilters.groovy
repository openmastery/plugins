package com.newiron

import org.apache.log4j.MDC

class SessionTrackingFilters {

    def springSecurityService

    def filters = {

        all(controller:'*', action:'*') {

            before = {

                String sessionId = request.session.id
                List trackingInfo = []

                if (springSecurityService.isLoggedIn()) {
                    trackingInfo.add(springSecurityService.getPrincipal().id)
                } else {
                    trackingInfo.add('ANONYMOUS')
                }

                if (sessionId) {
                    trackingInfo.add(sessionId)
                }

                // add trackingInfo to Logger pattern
                MDC.put('trackingInfo', trackingInfo.toString() + " ")

                String queryString = request.queryString ? "?${request.queryString}" : ""
                if (!"GET".equals(request.method.toUpperCase())) {
                    queryString = " DATA: " + request.parameterMap.toString()
                }

                log.info("${request.method.toUpperCase()} ${response?.status ?: '???'} ${request.forwardURI}${queryString}")

            }

            after = { Map model ->

            }

            afterView = { Exception e ->

                MDC.remove('trackingInfo')

            }

        }
    }
}
