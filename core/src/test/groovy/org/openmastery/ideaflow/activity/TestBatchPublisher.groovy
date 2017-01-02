package org.openmastery.ideaflow.activity

import com.bancvue.rest.exception.NotFoundException
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
		File tmpFile = batchPublisher.createActiveFile("message")
		tmpFile << "some stuff"

		when:
		batchPublisher.commitActiveFiles()

		then:
		assert batchPublisher.hasSomethingToPublish()
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
		File tmpFile = batchPublisher.createActiveFile("file")
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
		createBatchFile()

		when:
		batchPublisher.commitActiveFiles()
		batchPublisher.publishBatches()

		then:
		assert batchPublisher.hasSomethingToPublish() == false
		 1 * mockBatchClient.addIFMBatch(_)
	}

	def "publishBatches SHOULD mark file as failed if parsing fails"() {
		given:
		File file = batchPublisher.createActiveFile("file")
		file << "illegal json"

		when:
		batchPublisher.commitActiveFiles()
		batchPublisher.publishBatches()

		then:
		File[] files = batchPublisher.failedDir.listFiles()
		assert files.length == 1
	}

	def "publishBatches should skip batch file which fails to publish"() {
		given:
		createBatchFile()
		mockBatchClient.addIFMBatch(_) >> { throw new RuntimeException("Publication Failure") }

		when:
		batchPublisher.commitActiveFiles()
		batchPublisher.publishBatches()

		then:
		assert batchPublisher.hasSomethingToPublish() == true
	}

	def "publishBatches should set aside batches where the task cannot be found and resume on next session"() {
		given:
		createBatchFile()
		mockBatchClient.addIFMBatch(_) >> { throw new NotFoundException("task not found") }

		when:
		batchPublisher.commitActiveFiles()
		batchPublisher.publishBatches()

		then:
		assert batchPublisher.hasSomethingToPublish() == false

		and:
		assert batchPublisher.retryNextSessionDir.listFiles().length == 1

		when:
		batchPublisher.setBatchClient(mockBatchClient)

		then:
		assert batchPublisher.hasSomethingToPublish() == true
	}

}
