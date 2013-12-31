; var com = com || {};
com.tniswong = com.tniswong || {};
com.tniswong.jq = com.tniswong.jq || {};

(function($, document, window) {

    com.tniswong.jq.xbinputs = {

        pluginName : 'xbinputs',

        defaultConfig : {

            xbinputClass : 'xb-input',
            xbinputTextClass : 'xb-text',
            xbinputRadioClass : 'xb-radio',
            xbinputTextAreaClass : 'xb-textarea',
            xbinputCheckboxClass : 'xb-checkbox',
            xbinputCheckedClass : 'xb-checked',
            xbinputDisabledClass : 'xb-disabled',
            xbinputIndicatorClass : 'xb-checked-indicator',
            xbinputIdPrefix : 'xb-input-',
            xbinputRadioGroupPrefix : 'xb-radio-',
            xbinputPlaceholderLabelClass : 'xb-placeholder',
            xbinputErrorClass : 'xb-error',

            _xbinputType : {

                TEXT : 'TEXT',
                TEL: 'TEL',
                EMAIL: 'EMAIL',
                PASSWORD : 'PASSWORD',
                TEXTAREA : 'TEXTAREA',
                CHECKBOX : 'CHECKBOX',
                RADIO : 'RADIO',
                UNSUPPORTED : 'UNSUPPORTED'

            }

        },

        methods : {

            init : function() {

                var that = this;
                // that.config, that.default, that.$elem

                if(that._isSupported()) {
                    this._replaceOriginalElement();
                    this._bindEvents();
                    this._modernize();
                }

            },

            enable : function() {
                // TODO
            },

            disable : function() {
                // TODO
            },

            checked : function($element) {

                var that = this;

                return $element.is(':checked') || $element.hasClass(that.config.xbinputCheckedClass);

            },

            disabled : function($element) {

                var that = this;

                return $element.is(':disabled') || $element.hasClass(that.config.xbinputDisabledClass);

            },

            error : function (toggleFlag) {

                var that = this;

                if(toggleFlag) {
                    that.$elem.addClass(that.config.xbinputErrorClass);
                } else {
                    that.$elem.removeClass(that.config.xbinputErrorClass);
                }

            },

            _isSupported : function() {

                var that = this;

                var inputType = that._fetchInputType(that.$elem);

                return inputType && !(that.config._xbinputType.UNSUPPORTED === inputType);

            },

            _replaceOriginalElement : function() {

                var that = this,
                    $template = that._buildTemplate(that.$elem);

                $(that.$elem).replaceWith(function() {
                    return $template;
                });

                that.$elem = $template;

            },

            _bindEvents : function() {

                var that = this;

                if(!that.config.eventsBound) {

                    /*
                     * Mobile Safari Label Click workaround.
                     * http://stackoverflow.com/questions/7358781/tapping-on-label-in-mobile-safari
                     */
                    $('label').click(function() {});

                    $(document).on('focus', '.' + that.config.xbinputClass, function(event) {

                        var $currentTarget = $(event.currentTarget);

                        if(!event.isPropagationStopped()) {
                            $currentTarget.trigger('xb-input-focus');
                        }

                    });

                    $(document).on('focus', '.' + that.config.xbinputClass + ' input, .' + that.config.xbinputClass + ' textarea', function(event) {

                        var $currentTarget = $(event.currentTarget);

                        $currentTarget.parents('.' + that.config.xbinputClass).find('input, textarea').css('outline','none');
                        $currentTarget.parents('.' + that.config.xbinputClass).css({'outline' : 'rgb(94, 158, 215) auto 5px'});

                        event.stopPropagation()

                    });

                    $(document).on('click', '.' + that.config.xbinputClass, function(event) {

                        var $currentTarget = $(event.currentTarget);

                        switch(that._fetchInputType($currentTarget)) {
                            case that.config._xbinputType.RADIO:
                            case that.config._xbinputType.CHECKBOX:
                                event.preventDefault();
                                $currentTarget.trigger('xb-input-change');
                                break;
                            default:
                                break;
                        }

                    });

                    $(document).on('keyup', '.' + that.config.xbinputClass, function(event) {

                        var $currentTarget = $(event.currentTarget);

                        switch(that._fetchInputType($currentTarget)) {
                            case that.config._xbinputType.RADIO:
                            case that.config._xbinputType.CHECKBOX:
                                if(event.keyCode == 32) {
                                    $currentTarget.trigger('xb-input-change');
                                }
                                break;
                            case that.config._xbinputType.TEXTAREA:
                            case that.config._xbinputType.PASSWORD:
                            case that.config._xbinputType.TEXT:
                            case that.config._xbinputType.EMAIL:
                            case that.config._xbinputType.TEL:

                                // Toggle Placeholder
                                if($currentTarget.find('input, textarea').val() != "") {
                                    $currentTarget.find('.' + that.config.xbinputPlaceholderLabelClass).fadeOut();
                                }

                                break;
                            default:
                                break;
                        }

                    });

                    $(document).on('click', 'label', function(event) {

                        var $currentTarget = $(event.currentTarget);
                        var $labeledInput = $('#' + $currentTarget.attr('for'));

                        if($labeledInput && $labeledInput.attr('id') == $currentTarget.attr('for')) {

                            switch(that._fetchInputType($labeledInput)) {
                                case that.config._xbinputType.RADIO:
                                case that.config._xbinputType.CHECKBOX:

                                    event.preventDefault();

                                    $labeledInput.trigger('xb-input-change');

                                    break;
                                case that.config._xbinputType.TEXTAREA:
                                case that.config._xbinputType.PASSWORD:
                                case that.config._xbinputType.TEXT:
                                case that.config._xbinputType.EMAIL:
                                case that.config._xbinputType.TEL:

                                    event.preventDefault();

                                    $labeledInput.trigger('xb-input-focus');

                                    break;
                                default:
                                    break;
                            }
                        }

                    });

                    $(document).on('xb-input-change', '.' + that.config.xbinputClass, function(event) {

                        var $currentTarget = $(event.currentTarget);

                        switch(that._fetchInputType($currentTarget)) {
                            case that.config._xbinputType.RADIO:
                                that._toggleRadio($currentTarget);
                                break;
                            case that.config._xbinputType.CHECKBOX:
                                that._toggleCheckbox($currentTarget);
                                break;
                            default:
                                break;
                        }

                    });

                    $(document).on('xb-input-focus', '.' + that.config.xbinputClass, function(event) {

                        var $currentTarget = $(event.currentTarget);

                        switch(that._fetchInputType($currentTarget)) {
                            case that.config._xbinputType.TEXTAREA:
                            case that.config._xbinputType.PASSWORD:
                            case that.config._xbinputType.TEXT:
                            case that.config._xbinputType.EMAIL:
                            case that.config._xbinputType.TEL:

                                $currentTarget.find('input, textarea').focus();

                                break;
                        }

                    });

                    $(document).on('blur', '.' + that.config.xbinputClass, function(event) {

                        var $currentTarget = $(event.currentTarget);

                        switch(that._fetchInputType($currentTarget)) {
                            case that.config._xbinputType.TEXTAREA:
                            case that.config._xbinputType.PASSWORD:
                            case that.config._xbinputType.TEXT:
                            case that.config._xbinputType.EMAIL:
                            case that.config._xbinputType.TEL:

                                $currentTarget.css({'outline' : 'none'});

                                // Toggle Placeholder
                                if($currentTarget.find('input, textarea').val() == "") {
                                    $currentTarget.find('.' + that.config.xbinputPlaceholderLabelClass).fadeIn();
                                }

                                break;
                        }

                    });

                    that.config.eventsBound = true;

                }

            },

            _modernize : function() {
                $(function() {
                    if(Modernizr && (!Modernizr.borderradius || !Modernizr.cssgradients)) {
                        if (window.PIE) {
                            $('.xb-input').each(function() {
                                PIE.attach(this);
                            });
                        }
                    }
                });
            },

            _buildTemplate : function($element) {

                var that = this;

                var $template = null;
                var inputType = that._fetchInputType($element);

                switch(inputType) {

                    case that.config._xbinputType.TEXT:
                    case that.config._xbinputType.PASSWORD:
                    case that.config._xbinputType.TEXTAREA:
                    case that.config._xbinputType.EMAIL:
                    case that.config._xbinputType.TEL:
                        $template = that._buildTextTemplate($element);
                        break;
                    case that.config._xbinputType.CHECKBOX:
                        $template = that._buildCheckboxTemplate($element);
                        break;
                    case that.config._xbinputType.RADIO:
                        $template = that._buildRadioTemplate($element);
                        break;
                    case that.config._xbinputType.UNSUPPORTED:
                    default:
                        break;

                }

                // Add ID to template
                if ($element.attr('id')) {
                    $template.attr('id', $element.attr('id'));
                }

                if($template) {
                    $template.data('inputType', inputType);
                }

                // Copy data from $elem to $template
                $.each($element.data(), function(key, value) {
                    $template.data(key, value);
                });

                return $template;

            },

            _buildTextTemplate : function($element) {

                var that = this,
                    $template = $('<div></div>'),
                    $inputElement = that._fetch$InputElement($element),
                    placeholderText = $element.attr('title'),
                    $placeholderElement;

                // For each class, add it to the template
                that._addClassesToTemplate($template, $element);

                // Add input to template
                $template.append($inputElement);
                $template.data('$inputElement', $inputElement);

                if(placeholderText) {

                    $placeholderElement = that._fetchPlaceholder($template, placeholderText);
                    $template.prepend($placeholderElement);

                    if($inputElement.val() != "") {
                        $placeholderElement.hide();
                    }

                }

                return $template;

            },

            _buildCheckboxTemplate : function($element) {

                var that = this;
                var $template = $('<a href="#"></a>');

                // For each class, add it to the template
                that._addClassesToTemplate($template, $element);

                // Since we're using a hidden element, copy the tabindex
                that._copyTabIndexToTemplate($template, $element);

                // Add checked indicator
                that._addCheckedIndicatorToTemplate($template);

                // Add input to template if enabled and checked
                var $inputElement = that._fetch$InputElement($element);
                $template.data('$inputElement',$inputElement);

                if(!that.disabled($element) && that.checked($element)) {
                    $template.append($inputElement);
                }

                return $template;

            },

            _buildRadioTemplate : function($element) {

                var that = this;
                var $template = $('<a href="#"></a>');

                // For each class, add it to the template
                that._addClassesToTemplate($template, $element);

                // Since we're using a hidden element, copy the tabindex
                that._copyTabIndexToTemplate($template, $element);

                // Add checked indicator
                that._addCheckedIndicatorToTemplate($template);

                // Add input to template if enabled and checked
                var $inputElement = that._fetch$InputElement($element);
                $template.data('$inputElement',$inputElement);

                if(!that.disabled($element) && that.checked($element)) {
                    $template.append($inputElement);
                }

                // Store radioGroup
                var radioGroup = that._fetchRadioGroup($element);
                $template.data('radioGroup', radioGroup);

                return $template;

            },

            _addClassesToTemplate : function($template, $element) {

                var that = this,
                    classList = that._fetchClassList($element);

                if($element.attr('class')) {
                    $template.attr('class', $element.attr('class'));
                }

                $.each(classList, function(index, value) {
                    $template.addClass(value);
                });

            },

            _copyTabIndexToTemplate : function($template, $element) {
                if($element.attr('tabindex')) {
                    $template.attr('tabindex', $element.attr('tabindex'));
                }
            },

            _addCheckedIndicatorToTemplate : function($template) {

                var that = this;
                var $indicator = $('<span>&nbsp</span>');

                $indicator.addClass(that.config.xbinputIndicatorClass);

                $template.append($indicator);

            },

            _fetch$InputElement : function($element) {

                var that = this,
                    $inputElement;

                switch(that._fetchInputType($element)) {

                    case that.config._xbinputType.RADIO:
                    case that.config._xbinputType.CHECKBOX:

                        $inputElement = $('<input type="hidden">');
                        $inputElement.attr('value', $element.attr('value'));
                        $inputElement.attr('name', $element.attr('name'));

                        break;

                    default:

                        $inputElement = $element.clone();

                        // Input Element's ID should be on the replacement element
                        $inputElement.removeAttr('id');
                        $inputElement.removeAttr('class');

                        break;

                }

                if($element.attr('id')) {
                    $inputElement.attr('id', that.config.xbinputIdPrefix + $element.attr('id'));
                } else {
                    $inputElement.attr('id', that.config.xbinputIdPrefix + (new Date).getTime());
                }

                return $inputElement;

            },

            _fetchRadioGroup : function($element) {

                var that = this;
                return that.config.xbinputRadioGroupPrefix + $element.attr('name');

            },

            _fetchClassList : function($element) {

                var that = this;
                var classList = [].concat(that.config.xbinputClass);

                if(that.checked($element)) {
                    classList = classList.concat(that.config.xbinputCheckedClass);
                }

                if(that.disabled($element)) {
                    classList = classList.concat(that.config.xbinputDisabledClass);
                }

                switch(that._fetchInputType($element)) {
                    case that.config._xbinputType.TEXT:
                    case that.config._xbinputType.PASSWORD:
                    case that.config._xbinputType.EMAIL:
                    case that.config._xbinputType.TEL:
                        classList = classList.concat(that.config.xbinputTextClass);
                        break;
                    case that.config._xbinputType.TEXTAREA:
                        classList = classList.concat(that.config.xbinputTextAreaClass);
                        break;
                    case that.config._xbinputType.RADIO:
                        classList = classList.concat(that.config.xbinputRadioClass, that._fetchRadioGroup($element));
                        break;
                    case that.config._xbinputType.CHECKBOX:
                        classList = classList.concat(that.config.xbinputCheckboxClass);
                        break;
                    default:
                        break;
                }

                return classList;

            },

            _fetchInputType : function($element) {

                var that = this;
                var inputType = $element.data('inputType');

                if(!inputType) {

                    if($element.is('input')) {

                        var typeAttr = $element.attr('type');
                        var type = that.config._xbinputType.UNSUPPORTED;

                        $.each(that.config._xbinputType, function(index, value) {

                            if(typeAttr.toUpperCase() === value) {
                                type = value;
                            }

                        });

                        return type;

                    } else if($element.is('textarea')) {
                        return that.config._xbinputType.TEXTAREA;
                    } else {
                        return that.config._xbinputType.UNSUPPORTED;
                    }

                } else {
                    return inputType;
                }

            },

            _toggleCheckbox : function($element) {

                var that = this;

                if(!that.disabled($element)) {
                    $element.toggleClass(that.config.xbinputCheckedClass);

                    if(that.checked($element)) {

                        var $inputElement = $element.data('$inputElement');
                        $element.append($inputElement);

                    } else {
                        $element.find('input[type="hidden"]').remove();
                    }

                }

            },

            _toggleRadio : function($element) {

                var that = this;

                if(!that.checked($element) && !that.disabled($element)) {

                    var radioGroup = $element.data('radioGroup');

                    $('.' + radioGroup).removeClass(that.config.xbinputCheckedClass);
                    $('.' + radioGroup).find('input[type="hidden"]').remove();

                    $element.addClass(that.config.xbinputCheckedClass);

                    var $inputElement = $element.data('$inputElement');
                    $element.append($inputElement);

                }

            },

            _fetchPlaceholder : function($template, placeholderText) {

                var that = this,
                    $placeholderLabel = $('<label>' + placeholderText + '</label>');

                $placeholderLabel.addClass(that.config.xbinputPlaceholderLabelClass);
                $placeholderLabel.attr('for', $template.find('input, textarea').attr('id'));

                return $placeholderLabel;

            }

        }

    }

    $.createPlugin(com.tniswong.jq.xbinputs);

})(jQuery, document, window);