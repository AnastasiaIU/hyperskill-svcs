# hyperskill-svcs
Simple version control system for JetBrains Academy.

Implemented commands at this project:

--help - prints the help page;  
config - sets or outputs the name of a commit author;  
add - adds a file to the list of tracked files or outputs this list;  
log - shows commit logs;
commit - saves file changes and the author name;  
checkout - allows you to switch between commits and restore a previous file state.  

Objectives for the project:

Stage 1.

Take one argument as a command. If an argument is missing or it is --help, print the entire help page. If a command exists, the program should output a description of the command. If the command is wrong, print '[passed argument]' is not a SVCS command.

Stage 2.

You need to work on the following commands:

config - should allow the user to set their own name or output an already existing name. If a user wants to set a new name, the program must overwrite the old one.
add - should allow the user to set the name of a file that they want to track or output the names of tracked files. If the file does not exist, the program should inform a user that the file does not exist.

Stage 3.

Implement the following commands:

commit - must be passed to the program along with a message. Save all changes. Each commit must be assigned a unique id. If there were no changes since the last commit, do not create a new commit.
log - should show all the commits in reverse order.

Stage 4.

The checkout command must be passed to the program together with the commit ID to indicate which commit should be used. If a commit with the given ID exists, the contents of the tracked file should be restored in accordance with this commit.
