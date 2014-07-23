git filter-branch --tree-filter 'rm -rf `cat /home/saggezza/IdeaProjects/large_files.txt | cut -d " " -f 2` ' --prune-empty master
