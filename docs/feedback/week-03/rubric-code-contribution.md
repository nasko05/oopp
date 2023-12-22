# Code Contributions and Code Reviews

#### Focused Commits

Grade: Insufficient

Feedback: This rubric point is about individual commits representing a coherent change to the system. 
Commits should only affect a small number of files. Right now, you have a few huge commits for big changes. 

Tips:
- Commits should affect a small number of files
- Commit message should be concise and clearly summarise the changes the commit is making

#### Isolation

Grade: Insufficient

Feedback: Here we will assess how you use feature branches/merge requests to isolate individual 
features during development.
Each feature you create should have its own branch. 
We also want to see MRs for each student individually. It is great to see that you organised a coding session together 
to create a baseline for your code, but from now on you should split tasks so that everyone works on their own from their computer.

https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-57/-/merge_requests/26

Tips:
- Never commit directly to main
- MRs should not be convoluted or have a large scope

#### Reviewability

Grade: Good

Feedback: Here we will assess how easy it is to review the average MRs of the group.
You have very clear descriptions of your MRs. The changes within a MR relate to each other,
however all of them could have been split to smaller sections.

Tips:
- MRs should contain small number of commits where each change is related to each other
- Mark some MRs as drafts
- MRs should have clear descriptions and a title

#### Code Reviews

Grade: Insufficient

Feedback: Here we will assess how well you use MRs to review each other's source code contribution and provide feedback.
We aim to see actual back and forth discussions in your code reviews. We can have a bigger discussion about this during a meeting. 

Tips:
- MRs should not stay open for a long time


#### Build Server

Grade: Insufficient

Feedback: Here we assess whether the build Server was an important part of the development process.
What was nice is that whenever a build failed it was your top priority to fix it. 
Try to see if build fails locally before pushing it to Gitlab. 
Also, add 10+ checkstyle rules.

Tips:
- Builds should not fail frequently
- Commit and push frequently 

