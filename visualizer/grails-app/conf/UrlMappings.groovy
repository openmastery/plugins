class UrlMappings {

	static mappings = {
		"/ideaflow/$project/$user/$taskId"(controller: 'ideaflow', action: 'save', method: 'POST')
		"/ideaflow/$project/$user/$taskId"(controller: 'ideaflow', action: 'show', method: 'GET')
		"/ideaflow/$taskId"(controller: 'ideaflow', action: 'show', method: 'GET')

		"/ideaflow/$taskId/timeline"(controller: 'ideaflow', action: 'timeline', method: 'GET')



		"/$controller/$action?/$id?" {
			constraints {
				// apply constraints here
			}
		}

		"/"(controller: "ifm", action: "view")
		"500"(view: '/error')
	}
}