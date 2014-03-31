package com.ideaflow.model

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class ModelEntityTest extends GroovyTestCase {

	void testAllSubclasses_ShouldBeAnnotedWithEqualsAndHashcodeWithCallSuperSetToTrue() {
		List<ModelEntity> subTypeInstances = getModelEntitySubClassInstances()

		DateTime createdDate = new DateTime(500)
		subTypeInstances.each { ModelEntity subTypeInstance ->
			ModelEntity otherInstance = subTypeInstance.class.newInstance()

			subTypeInstance.created = createdDate
			otherInstance.created = createdDate
			assert subTypeInstance == otherInstance

			assert subTypeInstance.class.getAnnotation(EqualsAndHashCode) : "Ensure ${subTypeInstance.class.simpleName} is annotated @EqualsAndHashCode(callSuper = true)"
			subTypeInstance.created = null
			assert subTypeInstance != otherInstance : "Ensure ${subTypeInstance.class.simpleName} is annotated with @EqualsAndHashCode(callSuper = true)"
		}
	}

}
