package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.client.ActivityClient
import spock.lang.Specification


class TestBatchPublisher extends Specification {

	BatchPublisher batchPublisher
	File tempDir
	JSONConverter jsonConverter = new JSONConverter()

	ActivityClient mockActivityClient

	void setup() {
		tempDir = new File(File.createTempFile("temp", ".txt").parentFile, "queue-dir")
		tempDir.mkdirs()



		batchPublisher = new BatchPublisher(tempDir)

		mockActivityClient = Mock(ActivityClient)
		batchPublisher.activityClient = mockActivityClient
//		DateTimeUtils.setCurrentMillisFixed(NOW)
//
//		messageLogger = new InMemoryMessageLogger()
//		MessageQueue activityQueue = new MessageQueue(controller, messageLogger)
//		handler = new ActivityHandler(controller, activityQueue)
//
//		controller.getActiveTask() >> new Task(id: 1)
//		controller.isRecording() >> true
	}

	def cleanup() {
		tempDir.delete()

		List<File> batchFiles = batchPublisher.findAllBatchesAndSort()
		batchFiles.each { File file ->
			file.delete()
		}
	}

	def "commitBatch SHOULD create stuff to publish"() {
		given:
		File tmpFile = File.createTempFile("messages", ".log")
		tmpFile << "some stuff"

		when:
		batchPublisher.commitBatch(tmpFile)

		then:
		assert batchPublisher.hasSomethingToPublish()

	}

	def "findAllBatches SHOULD sort from earliest to latest"() {
		given:
		File tmpFile = File.createTempFile("messages", ".log")
		tmpFile << "some stuff"

		when:
		batchPublisher.commitBatch(tmpFile)
		Thread.sleep(1000)
		tmpFile << "some stuff"
		println tmpFile.exists()
		batchPublisher.commitBatch(tmpFile)

		then:
		List<File> files = batchPublisher.findAllBatchesAndSort()
		println files.size()
		files.each { File file ->
			println file.name
		}

	}

	def "convertBatchFileToObject SHOULD create a Batch object that can be sent to the server"() {

		given:
		NewEditorActivity editorActivity = NewEditorActivity.builder()
				.taskId(1)
				.endTime(LocalDateTime.now())
				.durationInSeconds(5)
				.filePath("hello.txt")
				.isModified(true)
				.build();


		File tmpFile = File.createTempFile("messages", ".log")
		tmpFile << jsonConverter.toJSON(editorActivity) + "\n"

		when:
		NewActivityBatch batch = batchPublisher.convertBatchFileToObject(tmpFile)

		then:
		assert batch != null
		assert batch.editorActivityList.size() == 1
	}


	def "publishBatches SHOULD send all batches to the server and delete files"() {
		given:
		NewEditorActivity editorActivity = NewEditorActivity.builder()
				.taskId(1)
				.endTime(LocalDateTime.now())
				.durationInSeconds(5)
				.filePath("hello.txt")
				.isModified(true)
				.build();


		File tmpFile = File.createTempFile("messages", ".log")
		tmpFile << jsonConverter.toJSON(editorActivity) + "\n"

		when:
		batchPublisher.commitBatch(tmpFile)
		batchPublisher.publishBatches()

		then:
		assert batchPublisher.hasSomethingToPublish() == false
		 1 * mockActivityClient.addActivityBatch(_)
	}
}
