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

package org.oop.intellij.plugin.evaluator.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.oop.intellij.plugin.evaluator.core.AnalyzeState;
import org.oop.intellij.plugin.evaluator.messages.MessageBusManager;
import org.oop.intellij.plugin.evaluator.resources.ResourcesLoader;
import org.oop.intellij.plugin.evaluator.service.ProblemCacheService;
import org.jetbrains.annotations.NotNull;

public class ClearAndCloseToolWindowAnalyzeAction extends AbstractAction {
	@Override
	public void updateImpl(
			@NotNull AnActionEvent e,
			@NotNull Project project,
			@NotNull ToolWindow toolWindow,
			@NotNull AnalyzeState state) {
		final boolean enable = state.isIdle();

		e.getPresentation().setEnabled(enable);
		e.getPresentation().setVisible(true);
		e.getPresentation().setText(ResourcesLoader.getString("action.clear"));
	}


	@Override
	public void actionPerformedImpl(
			@NotNull AnActionEvent e,
			@NotNull Project project,
			@NotNull ToolWindow toolWindow,
			@NotNull AnalyzeState state) {
		MessageBusManager.publishClear(project);
	}
}
