var com = com || {};
com.tniswong = com.tniswong || {};
com.tniswong.jq = com.tniswong.jq || {};

(function ($, doc, win) {

    "use strict";

    com.tniswong.jq.PluginFactory = {

        plugins : {},

        createPlugin : function (pluginName, defaultConfig, methods) {

            this.plugins[pluginName] = new com.tniswong.jq.PluginDelegate(pluginName, defaultConfig);

            $.each(methods, function (key, value) {
                com.tniswong.jq.PluginFactory.plugins[pluginName].PluginContext.prototype[key] = value;
            });

            $.fn[pluginName] = function (method) {

                var returnValue,
                    delegate = com.tniswong.jq.PluginFactory.plugins[pluginName],
                    args = arguments;

                if (com.tniswong.jq.PluginFactory._single$ElementAndMethodIsString(this, method)) {
                    returnValue = delegate.methodCall($(this[0]), method, Array.prototype.slice.call(args, 1));
                } else {

                    this.each(function (index, element) {
                        delegate.methodCall($(element), method, Array.prototype.slice.call(args, 1));
                    });

                    returnValue = this;

                }

                return returnValue;

            };

        },

        _single$ElementAndMethodIsString : function ($element, method) {
            return $element.length === 1 && typeof method === "string";
        }

    };

    com.tniswong.jq.PluginDelegate = function (name, defaultConfig) {

        this.name = name;

        this.methodCall = function ($elem, method) {

            var delegate, pluginContext = $elem.data(this.name);

            if (this._pluginContextUndefinedAnd_methodUndefinedOrMethodIsConfig(pluginContext, method)) {

                delegate = com.tniswong.jq.PluginFactory.plugins[this.name];

                pluginContext = new delegate.PluginContext($elem, defaultConfig, method);
                $elem.data(this.name, pluginContext);

                pluginContext.init();

            } else {

                if (typeof method === "string" && method[0] !== "_") {
                    return pluginContext[method].apply(pluginContext, Array.prototype.slice.call(arguments, 2)[0]);
                }

            }

        };

        this._pluginContextUndefinedAnd_methodUndefinedOrMethodIsConfig = function (pluginContext, method) {
            return !pluginContext && (!method || $.isPlainObject(method));
        };

        this.PluginContext = function ($elem, defaultConfig, config) {

            this.$elem = $elem;

            this.config = $.extend(defaultConfig, config);

        };

    };

    $.createPlugin = function (name, defaultConfig, methods) {
        if (typeof name === "string") {
            com.tniswong.jq.PluginFactory.createPlugin(name, defaultConfig, methods);
        } else if ($.isPlainObject(name) && !defaultConfig && !methods) {
            com.tniswong.jq.PluginFactory.createPlugin(name.pluginName, name.defaultConfig, name.methods);
        }
    };

}(jQuery, document, window));