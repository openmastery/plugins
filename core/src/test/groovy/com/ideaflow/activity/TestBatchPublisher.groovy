package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.client.BatchClient
import spock.lang.Specification

class TestBatchPublisher extends Specification {

	BatchPublisher batchPublisher
	File tempDir
	JSONConverter jsonConverter = new JSONConverter()

	BatchClient mockBatchClient

	void setup() {
		tempDir = new File(File.createTempFile("temp", ".txt").parentFile, "queue-dir")
		tempDir.deleteDir()
		tempDir.mkdirs()

		batchPublisher = new BatchPublisher(tempDir)

		mockBatchClient = Mock(BatchClient)
		batchPublisher.batchClient = mockBatchClient
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

	private NewEditorActivity createEditorActivity() {
		NewEditorActivity.builder()
						.taskId(1)
						.endTime(LocalDateTime.now())
						.durationInSeconds(5)
						.filePath("hello.txt")
						.isModified(true)
						.build();
	}

	private File createBatchFile() {
		NewEditorActivity editorActivity = createEditorActivity()
		File tmpFile = File.createTempFile("messages", ".log")
		tmpFile << jsonConverter.toJSON(editorActivity) + "\n"
		tmpFile
	}

	def "convertBatchFileToObject SHOULD create a Batch object that can be sent to the server"() {
		given:
		File tmpFile = createBatchFile()

		when:
		NewIFMBatch batch = batchPublisher.convertBatchFileToObject(tmpFile)

		then:
		assert batch != null
		assert batch.editorActivityList.size() == 1
	}

	def "convertBatchFileToObject SHOULD support events "() {

		given:
		NewBatchEvent batchEvent = NewBatchEvent.builder()
				.taskId(1)
				.position(LocalDateTime.now())
				.type(EventType.AWESOME)
				.comment("hello!")
				.build();


		File tmpFile = File.createTempFile("messages", ".log")
		tmpFile << jsonConverter.toJSON(batchEvent) + "\n"

		when:
		NewIFMBatch batch = batchPublisher.convertBatchFileToObject(tmpFile)

		then:
		assert batch != null
		assert batch.eventList.size() == 1
	}

	def "publishBatches SHOULD send all batches to the server and delete files"() {
		given:
		File tmpFile = createBatchFile()

		when:
		batchPublisher.commitBatch(tmpFile)
		batchPublisher.publishBatches()

		then:
		assert batchPublisher.hasSomethingToPublish() == false
		 1 * mockBatchClient.addIFMBatch(_)
	}

	def "publishBatches SHOULD mark file as failed if parsing fails"() {
		given:
		File tmpFile = File.createTempFile("messages", ".log")
		tmpFile << "illegal json"

		when:
		batchPublisher.commitBatch(tmpFile)
		batchPublisher.publishBatches()

		then:
		File[] files = tempDir.listFiles()
		assert files.length == 1
		assert files[0].name.startsWith(BatchPublisher.FAILED_FILE_PREFIX)
	}

	def "publishBatches should skip batch file which fails to publish"() {
		given:
		File tmpFile = createBatchFile()
		mockBatchClient.addIFMBatch(_) >> { throw new RuntimeException("Publication Failure") }

		when:
		batchPublisher.commitBatch(tmpFile)
		batchPublisher.publishBatches()

		then:
		assert batchPublisher.hasSomethingToPublish() == true
	}

}
