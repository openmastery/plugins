package com.ideaflow.intellij.vcs

import com.ideaflow.controller.IFMController
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CommittedChangesProvider
import com.intellij.openapi.vcs.FilePathImpl
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.RepositoryLocation
import com.intellij.openapi.vcs.VcsListener
import com.intellij.openapi.vcs.VcsRoot
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.changes.ChangeList
import com.intellij.openapi.vcs.changes.ChangeListAdapter
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.versionBrowser.ChangeBrowserSettings
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.MessageBusConnection

/**
 * I couldn't figure out a way to register for IDEA commit messages, so this class is a workaround.
 * Basically, it listens for changes to change sets and attempts to determine if a commit was performed.
 * If so, it adds the commit message as a Note to the current idea flow map.
 */
class VcsCommitToIdeaFlowNoteAdapter extends ChangeListAdapter implements VcsListener {

	private Project project
	private IFMController controller
	private MessageBusConnection projectConnection;
	private List<VcsRoot> allVcsRoots = []
	private Map<VcsRoot, CommittedChangeList> vcsRootToLastCommitMap = [:]

	public VcsCommitToIdeaFlowNoteAdapter(Project project, IFMController controller) {
		this.project = project
		this.controller = controller
	}

	public void connect() {
		ChangeListManager.getInstance(project).addChangeListListener(this)
		projectConnection = project.getMessageBus().connect()
		projectConnection.subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, this)
	}

	public void disconnect() {
		ChangeListManager.getInstance(project).removeChangeListListener(this)
		projectConnection.disconnect()
	}

	@Override
	void directoryMappingChanged() {
		allVcsRoots = ProjectLevelVcsManager.getInstance(project).getAllVcsRoots()
		resetVcsRootToLastCommitMap()
	}

	private void resetVcsRootToLastCommitMap() {
		vcsRootToLastCommitMap = getLastCommittedChangeListForAllVcsRoots()
	}

	@Override
	public void changesAdded(Collection<Change> changes, ChangeList toList) {
		if (vcsRootToLastCommitMap.isEmpty()) {
			resetVcsRootToLastCommitMap()
		}
	}

	@Override
	void changesRemoved(Collection<Change> changes, ChangeList fromList) {
		Set<VcsRoot> affectedVcsRoots = getAffectedVcsRoots(changes)

		Map<VcsRoot, CommittedChangeList> latestVcsRootToLastCommitMap = getLastCommittedChangeListForAllVcsRoots()
		final List<String> commitMessages = affectedVcsRoots.collect { VcsRoot affectedRoot ->
			getLatestCommitMessageIfNewCommit(affectedRoot, latestVcsRootToLastCommitMap)
		}.findAll {
			it != null
		}
		vcsRootToLastCommitMap = latestVcsRootToLastCommitMap

		if (commitMessages) {
			addCommitNotes(commitMessages)
		}
	}

	private void addCommitNotes(final List<String> commitMessages) {
		final IFMController ifmController = controller

		ApplicationManager.getApplication().invokeLater(new Runnable() {
			@Override
			void run() {
				commitMessages.each { String commitMessage ->
					ifmController.addNote(project, "Commit: ${commitMessage}")
				}
			}
		})
	}

	private String getLatestCommitMessageIfNewCommit(VcsRoot vcsRoot, Map<VcsRoot, CommittedChangeList> latestVcsRootToLastCommitMap) {
		CommittedChangeList lastCommit = vcsRootToLastCommitMap[vcsRoot]
		CommittedChangeList latestCommit = latestVcsRootToLastCommitMap[vcsRoot]
		String latestCommitMessage = null

		if (latestCommit && (lastCommit?.number != latestCommit.number)) {
			latestCommitMessage = latestCommit?.comment?.trim()
		}
		latestCommitMessage
	}

	private Set<VcsRoot> getAffectedVcsRoots(Collection<Change> changes) {
		List<VirtualFile> filesRemoved = changes.collect { Change change ->
			change.virtualFile
		}

		filesRemoved.collect { VirtualFile file ->
			getVcsRootForFile(file)
		}.findAll { VcsRoot vcsRoot ->
			vcsRoot != null
		}.unique()
	}

	private VcsRoot getVcsRootForFile(VirtualFile file) {
		allVcsRoots.find { VcsRoot root ->
			file.path.startsWith(root.path.path)
		}
	}

	private Map<VcsRoot, CommittedChangeList> getLastCommittedChangeListForAllVcsRoots() {
		Map<VcsRoot, CommittedChangeList> lastCommittedChangeMap = [:]
		allVcsRoots.each { VcsRoot vcsRoot ->
			CommittedChangeList changeList = getLastCommittedChangeListForVcsRoot(vcsRoot)
			if (changeList) {
				lastCommittedChangeMap[vcsRoot] = changeList
			}
		}
		lastCommittedChangeMap
	}

	private CommittedChangeList getLastCommittedChangeListForVcsRoot(VcsRoot vcsRoot) {
		CommittedChangesProvider committedChangesProvider = vcsRoot.vcs.committedChangesProvider
		RepositoryLocation vcsRootLocation = committedChangesProvider.getLocationFor(FilePathImpl.create(vcsRoot.path))
		CommittedChangeList changeList = null

		if (vcsRootLocation) {
			ChangeBrowserSettings settings = committedChangesProvider.createDefaultSettings()
			List<CommittedChangeList> changes = committedChangesProvider.getCommittedChanges(settings, vcsRootLocation, 1)
			changeList = (changes ? changes.first() : null)
		}
		changeList

	}
}
