includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsArgParsing")

target(main: "Updates Application Name and resets Version to 0.1") {

    depends(parseArguments)

    metadata['app.name'] = argsMap.params[0]
    metadata['app.version'] = '0.1'
    metadata.persist()

}

setDefaultTarget(main)
