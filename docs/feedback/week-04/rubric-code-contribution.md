# Code Contributions and Code Reviews


#### Focused Commits

Grade: Good

Feedback: The repository has a good amount of commits.
Most commit messages are concise one liners, which clearly summarize the change.
The source code does not contain big pieces of commented code.

Commits should only affect a small number of files and aggregate a coherent change to the system.
I saw that you had a lot of "fix / update " commits. You could try using following git command:
git commit –amend.
Google what it does :)


#### Isolation

Grade: Good/Very Good

Feedback:  The group uses feature branches/merge requests to isolate individual features during development.
Even small changes like fixing something, should not be done on Main. (Especially if it also fails the build)
Always branch out!

https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-57/-/commit/03f72a36349933eae9042571e077e9042d306f23

Besides that you have a very good amount of successfully integrated MRs.



#### Reviewability

Grade: Very Good

Feedback: MRs always have a clear focus that becomes clear from the title and the description. 
Most MRs contain a low number of formatting changes. MRs usually only cover small number of commits


#### Code Reviews

Grade: Good/Very Good 

Feedback: Comments in the MRs are constructive and goal oriented:
https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-57/-/merge_requests/31
Code reviews are an actual discussion with a back and forth of questions and answers:
https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-57/-/merge_requests/43
The reviews actually lead to iterative improvements of the code:
https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-57/-/merge_requests/30

I would like to see even more discussion from all members :) 

#### Build Server

Grade: Good

Feedback: You should select 10+ checkstyle rules.
https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-57/-/blob/main/checkstyle.xml
You don't seem to have many failing build. 


