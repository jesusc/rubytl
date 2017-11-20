package gts.eclipse.core.resources;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

public class BuilderUtil {

    /**
     * Adds a builder to the build spec for the given project.
     */
    public static boolean addToBuildSpec(IProject project, String builderId) throws CoreException {

        IProjectDescription description = project.getDescription();
        int commandIndex = getCommandIndex(description.getBuildSpec(), builderId);

        if (commandIndex == -1) {

            // Add a Java command to the build spec
            ICommand command = description.newCommand();
            command.setBuilderName(builderId);
            setCommand(project, description, command, builderId);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the given builder from the build spec for the given project.
     */
    public static void removeFromBuildSpec(IProject project, String builderId) throws CoreException {

        IProjectDescription description = project.getDescription();
        ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(builderId)) {
                ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                description.setBuildSpec(newCommands);
                project.setDescription(description, null);
                return;
            }
        }
    }
    
    
    /**
     * Find the specific command amongst the given build spec and return
     * its index or -1 if not found.
     */
    private static int getCommandIndex(ICommand[] buildSpec, String builderId) {

        for (int i = 0; i < buildSpec.length; ++i) {
            if (buildSpec[i].getBuilderName().equals(builderId)) { return i; }
        }
        return -1;
    }
    
    /**
     * Update the command in the build spec (replace existing one if
     * present, add one first if none).
     */
    private static void setCommand(IProject project, IProjectDescription description, ICommand newCommand, String builderId)
            throws CoreException {

        ICommand[] oldBuildSpec = description.getBuildSpec();
        int oldCommandIndex = getCommandIndex(oldBuildSpec, builderId);
        ICommand[] newCommands;

        if (oldCommandIndex == -1) {
            // Add a build spec before other builders (1FWJK7I)
            newCommands = new ICommand[oldBuildSpec.length + 1];
            System.arraycopy(oldBuildSpec, 0, newCommands, 1, oldBuildSpec.length);
            newCommands[0] = newCommand;
        } else {
            oldBuildSpec[oldCommandIndex] = newCommand;
            newCommands = oldBuildSpec;
        }

        // Commit the spec change into the project
        description.setBuildSpec(newCommands);
        project.setDescription(description, null);
    }    
}
