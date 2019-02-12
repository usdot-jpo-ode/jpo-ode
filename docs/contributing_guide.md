
# Contributing Guide

Welcome to the JPO ODE contributing guide. Please read this guide to learn about our development process, how to propose pull requests and improvements, and how to build and test your changes to this project. 


## Open source license

By contributing to the US Department of Transportation Joint Program office (JPO) Operational Data Environment (ODE), you agree that your contributions will be licensed under its Apache License 2.0 license.

## Style Guide

[Code Standards](https://usdotjpoode.atlassian.net/wiki/spaces/ODTD/pages/8234945/Code+Standards)


## Miscellaneous Documentation

[JPO-ODE GitHub page](https://github.com/usdot-jpo-ode/jpo-ode)

[Confluence Wiki](https://usdotjpoode.atlassian.net/wiki/)

[ODE Architecture](https://github.com/usdot-jpo-ode/jpo-ode/blob/develop/docs/JPO%20ODE%20Architecture.docx)

[ODE User Guide](https://github.com/usdot-jpo-ode/jpo-ode/blob/develop/docs/JPO_ODE_UserGuide.docx)

[ODE REST API Guide](usdot-jpo-ode.github.io)

[ODE Smoke Tests](https://github.com/usdot-jpo-ode/jpo-ode/wiki/JPO-ODE-QA-Documents)

## Tools

### Issue tracker
Contributors will utilize Github's issue tracking system to record and manage issues that are reported by users of the ODE in the field. These may include performance requests, found bugs, and new requests. The follow operating procedure highlights how the ODE development team will address and respond to reported issues.

Issue Repository: [https://github.com/usdot-jpo-ode/jpo-ode/issues](https://github.com/usdot-jpo-ode/jpo-ode/issues)

### Pull requests

[JPO-ODE GitHub Pull Request Page](https://github.com/usdot-jpo-ode/jpo-ode/pulls)

All software development teams, regardless of being a member of the repository or external contributor, are required to submit their changes in a Pull Request (PR). The `master` branch is protected against direct commits. All commits to `master` branch would have to be processed through a PR. Changes must be committed to a separate branch, pushed to GitHub and PR created to merge the branch to `master`. The `dev` branch has been set aside for daily updates and will contain the latest commits but does not necessarily contain a `stable` build. The `dev` branch is work in progress until the changes have been reviewed in a PR and approve, at which point, the branch can be merged to `master`.

All pull requests will be reviewed by the JPO-ODE team. The team member will either request for additional details, merge it to the baseline, request changes to it, or close it with an explanation. For major changes, the reviewer may require additional support from the team, which could cause some delay. We'll do our best to provide updates and feedback throughout the process. Feel free to open pull requests, and the ODE team will provide feedback.
**Before submitting a pull request**, please make sure the following is done:
	
	1.	External contributors must first fork the repository in their own personal or organization.
	2.	All contributors must create a branch from `master`, `dev` or any other branch desired.
	3.	If you've added code that should be tested, add tests!
	4.	Ensure the tests pass. Our target is 90% coverage
	5.	Update the documentation.
		- User QA procedures are documented within the Github Wiki
		- Architecture and user guide documentation should be included in the word document under the `docs/` folder
		- Please contact the ODE with qny questions
	6.	Format your code as outlined in the style guide

## Contributor Covenant Code of Conduct
### Our Pledge
In the interest of fostering an open and welcoming environment, we as contributors and maintainers pledge to making participation in our project and our community a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, gender identity and expression, level of experience, nationality, personal appearance, race, religion, or sexual identity and orientation.

### Our Standards
Examples of behavior that contributes to creating a positive environment include:
	
	-	Using welcoming and inclusive language
	-	Being respectful of differing viewpoints and experiences
	-	Gracefully accepting constructive criticism
	-	Focusing on what is best for the community
	-	Showing empathy towards other community members

Examples of unacceptable behavior by participants include:

	-	The use of sexualized language or imagery and unwelcome sexual attention or advances
	-	Trolling, insulting/derogatory comments, and personal or political attacks
	-	Public or private harassment
	-	Publishing others' private information, such as a physical or electronic address, without explicit permission
	-	Other conduct which could reasonably be considered inappropriate in a professional setting

### Our Responsibilities
Project maintainers are responsible for clarifying the standards of acceptable behavior and are expected to take appropriate and fair corrective action in response to any instances of unacceptable behavior.
Project maintainers have the right and responsibility to remove, edit, or reject comments, commits, code, wiki edits, issues, and other contributions that are not aligned to this Code of Conduct, or to ban temporarily or permanently any contributor for other behaviors that they deem inappropriate, threatening, offensive, or harmful.

### Scope
This Code of Conduct applies both within project spaces and in public spaces when an individual is representing the project or its community. Examples of representing a project or community include using an official project e-mail address, posting via an official social media account, or acting as an appointed representative at an online or offline event. Representation of a project may be further defined and clarified by project maintainers.
Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be reported by contacting the project team at Ariel.Gold@dot.gov. All complaints will be reviewed and investigated and will result in a response that is deemed necessary and appropriate to the circumstances. The project team is obligated to maintain confidentiality with regard to the reporter of an incident. Further details of specific enforcement policies may be posted separately.
Project maintainers who do not follow or enforce the Code of Conduct in good faith may face temporary or permanent repercussions as determined by other members of the project's leadership.
