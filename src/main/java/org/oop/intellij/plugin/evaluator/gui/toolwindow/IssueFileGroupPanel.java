/*
 * Copyright 2021 Yu Junyang
 * https://github.com/lowkeyfish
 *
 * This file is part of Sonar Intellij plugin.
 *
 * Sonar Intellij plugin is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Sonar Intellij plugin is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Sonar Intellij plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.oop.intellij.plugin.evaluator.gui.toolwindow;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.BoxLayout;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import org.oop.intellij.plugin.evaluator.common.IdeaUtils;
import org.oop.intellij.plugin.evaluator.core.AbstractIssue;
import org.oop.intellij.plugin.evaluator.core.DuplicatedBlocksIssue;
import org.oop.intellij.plugin.evaluator.core.Issue;
import org.oop.intellij.plugin.evaluator.gui.common.UIUtils;
import org.oop.intellij.plugin.evaluator.resources.ResourcesLoader;
import org.oop.intellij.plugin.evaluator.service.ProblemCacheService;

public class IssueFileGroupPanel extends JBPanel {
	private Project project;
	private PsiFile psiFile;
	private List<AbstractIssue> issues;
	private int issueCount;
	private JBTextArea titleTextArea;

	public IssueFileGroupPanel(PsiFile psiFile, List<AbstractIssue> issues) {
		this.project = psiFile.getProject();
		this.psiFile = psiFile;
		this.issues = issues;
		init();
	}

	private void init() {
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBorder(JBUI.Borders.empty(0, 5));

		List<DuplicatedBlocksIssue> duplicatedBlocksIssues = issues.stream()
				.filter(n -> n instanceof DuplicatedBlocksIssue).map(n -> (DuplicatedBlocksIssue) n).collect(Collectors.toList());
		List<Issue> normalIssues = issues.stream().filter(n -> n instanceof Issue).map(n -> (Issue) n).collect(Collectors.toList());
		issueCount = (duplicatedBlocksIssues.size() > 0 ? 1 : 0) + normalIssues.size();
		addTitleTextArea(issueCount);
		add(Box.createVerticalStrut(2));

		if (duplicatedBlocksIssues.size() > 0) {
			addIssue(duplicatedBlocksIssues);
		}
		normalIssues.forEach(n -> addIssue(n));
		add(Box.createVerticalStrut(10));
	}

	private void addTitleTextArea(int count) {
		titleTextArea = UIUtils.createWrapLabelLikedTextArea(ResourcesLoader.getString("report.fileSummary", IdeaUtils.getPath(psiFile), count));
		titleTextArea.setForeground(Color.GRAY);
		titleTextArea.setAlignmentX(LEFT_ALIGNMENT);
		add(titleTextArea);
	}

	private void updateTitleTextArea(int count) {
		titleTextArea.setText(ResourcesLoader.getString("report.fileSummary", IdeaUtils.getPath(psiFile), count));
	}

	private void addIssue(List<DuplicatedBlocksIssue> issues) {
		IssueItemPanel panel = new IssueItemPanel(issues);
		Consumer<IssueItemPanel> resolveCallback = this::issueItemPanelResolved;
		panel.putClientProperty("RESOLVE_CALLBACK", resolveCallback);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		add(panel);
		add(Box.createVerticalStrut(5));
	}

	private void addIssue(Issue issue) {
		IssueItemPanel panel = new IssueItemPanel(issue);
		Consumer<IssueItemPanel> resolveCallback = this::issueItemPanelResolved;
		panel.putClientProperty("RESOLVE_CALLBACK", resolveCallback);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		add(panel);
		add(Box.createVerticalStrut(5));
	}

	private void addIssue(AbstractIssue issue) {
		IssueItemPanel panel = new IssueItemPanel(issue);
		Consumer<IssueItemPanel> resolveCallback = this::issueItemPanelResolved;
		panel.putClientProperty("RESOLVE_CALLBACK", resolveCallback);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		add(panel);
		add(Box.createVerticalStrut(5));
	}

	private void issueItemPanelResolved(IssueItemPanel issueItemPanel) {
		Set<String> filters = ProblemCacheService.getInstance(project).getFilters();
		if (filters.contains("UNRESOLVED") && !filters.contains("RESOLVED")) {
			if (issues.stream().filter(n -> !n.isFixed()).count() > 0) {
				Component[] components = getComponents();
				for (int i = 0; i < components.length; i++) {
					Component component = components[i];
					if (component.equals(issueItemPanel)) {
						remove(issueItemPanel);
						remove(components[i + 1]);
						issueCount--;
						updateTitleTextArea(issueCount);
						break;
					}
				}
				revalidate();
			} else {
				((Consumer<IssueFileGroupPanel>) getClientProperty("RESOLVE_CALLBACK")).accept(this);
			}
		}
	}
}
