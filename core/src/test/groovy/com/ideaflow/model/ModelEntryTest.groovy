package com.ideaflow.model

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime
import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class ModelEntryTest extends Specification {

	void testAllSubclasses_ShouldBeAnnotedWithEqualsAndHashcodeWithCallSuperSetToTrue() {
        given:
		List<ModelEntry> subTypeInstances = getModelEntitySubClassInstances()

        expect:
		DateTime createdDate = new DateTime(500)
		subTypeInstances.each { ModelEntry subTypeInstance ->
			ModelEntry otherInstance = subTypeInstance.class.newInstance()

			subTypeInstance.created = createdDate
			otherInstance.created = createdDate
			assert subTypeInstance == otherInstance

			assert subTypeInstance.class.getAnnotation(EqualsAndHashCode): "Ensure ${subTypeInstance.class.simpleName} is annotated @EqualsAndHashCode(callSuper = true)"
			subTypeInstance.created = null
			assert subTypeInstance != otherInstance: "Ensure ${subTypeInstance.class.simpleName} is annotated with @EqualsAndHashCode(callSuper = true)"
		}
	}

}
