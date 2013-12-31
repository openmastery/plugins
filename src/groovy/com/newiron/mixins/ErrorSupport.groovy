package com.newiron.mixins

import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec
import org.springframework.context.MessageSource
import org.springframework.validation.Errors

class ErrorSupport {

    /**
     * Helper for displaying errors to users.
     *
     * @param errors Instance errors from a @Validatable class, (Domain class or Command Object)
     * @param messageSource MessageSource, you should use the Spring Injected MessageSource
     * @param locale Locale, defaults to null
     *
     * @return a Map keyed by field name, valued by a list of messages.
     * Message Values are retrieved from the given MessageSource and are
     * HTML encoded to prevent XSS attacks as they are often displayed in a view.
     */
    private Map<String, List<String>> errorsAsMap(Errors errors, MessageSource messageSource, Locale locale = null) {

        Map<String, List<String>> errorsMap = [:]

        for (fieldErrors in errors) {

            String fieldName = fieldErrors.fieldError.field
            List<String> messages = []

            for (error in fieldErrors.allErrors) {
                messages.add(HTMLCodec.encode(messageSource.getMessage(error, locale)))
            }

            errorsMap.put(fieldName, messages)

        }

        return errorsMap

    }

}
