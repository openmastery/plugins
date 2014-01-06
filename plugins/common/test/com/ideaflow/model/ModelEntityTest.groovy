package com.ideaflow.model

import org.reflections.Reflections

class ModelEntityTest extends GroovyTestCase {

	void testAllSubclasses_ShouldBeAnnotedWithEqualsAndHashcodeWithCallSuperSetToTrue() {
		Reflections reflections = new Reflections(ModelEntity.package.name)
		List<ModelEntity> subTypeInstances = reflections.getSubTypesOf(ModelEntity).collect { Class subType ->
			subType.newInstance() as ModelEntity
		}

		Date createdDate = new Date(500)
		subTypeInstances.each { ModelEntity subTypeInstance ->
			ModelEntity otherInstance = subTypeInstance.class.newInstance()

			subTypeInstance.created = createdDate
			otherInstance.created = createdDate
			assert subTypeInstance == otherInstance

			subTypeInstance.created = null
			assert subTypeInstance != otherInstance : "Ensure ${subTypeInstance.class.simpleName} is annotated with @EqualsAndHashCode(callSuper = true)"
		}
	}

}
